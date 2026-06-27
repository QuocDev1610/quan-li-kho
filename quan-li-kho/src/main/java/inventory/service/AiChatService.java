package inventory.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AiChatService {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.ai.api-url}")
    private String apiUrl;

    @Value("${app.ai.api-key}")
    private String apiKey;

    @Value("${app.ai.model}")
    private String model;

    @Value("${app.ai.fallback-models:}")
    private String fallbackModels;

    public String ask(String userMessage) {
        if (apiKey == null || apiKey.isBlank() || apiKey.startsWith("PASTE_")) {
            throw new AiChatException(
                    503,
                    "Trợ lý AI chưa được cấu hình. Hãy nhập Gemini API key trong config/application-secrets.properties rồi khởi động lại backend."
            );
        }

        AiChatException lastRetryableError = null;
        for (String candidateModel : configuredModels()) {
            try {
                return askWithModel(userMessage, candidateModel);
            } catch (AiChatException ex) {
                if (!ex.isRetryable()) {
                    throw ex;
                }
                lastRetryableError = ex;
            }
        }

        throw new AiChatException(
                503,
                "Dịch vụ AI đang bận. Hệ thống đã thử các model dự phòng nhưng chưa nhận được phản hồi, vui lòng thử lại sau.",
                lastRetryableError
        );
    }

    private String askWithModel(String userMessage, String candidateModel) {
        try {
            Map<String, Object> body = Map.of(
                    "model", candidateModel,
                    "temperature", 0.3,
                    "messages", List.of(
                            Map.of(
                                    "role", "system",
                                    "content", systemPrompt()
                            ),
                            Map.of(
                                    "role", "user",
                                    "content", userMessage
                            )
                    )
            );

            String jsonBody = objectMapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            ensureSuccessfulResponse(response.statusCode(), response.body(), candidateModel);

            JsonNode root = objectMapper.readTree(response.body());
            return root.path("choices")
                    .path(0)
                    .path("message")
                    .path("content")
                    .asText("Xin lỗi, tôi chưa thể trả lời câu hỏi này.");

        } catch (AiChatException ex) {
            throw ex;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new AiChatException(503, "Yêu cầu tới AI đã bị gián đoạn. Vui lòng thử lại.", ex);
        } catch (Exception ex) {
            throw new AiChatException(503, "Không thể kết nối tới AI. Vui lòng thử lại sau.", ex);
        }
    }

    private List<String> configuredModels() {
        Set<String> models = new LinkedHashSet<>();
        addModel(models, model);
        if (fallbackModels != null) {
            for (String fallbackModel : fallbackModels.split(",")) {
                addModel(models, fallbackModel);
            }
        }
        return new ArrayList<>(models);
    }

    private void addModel(Set<String> models, String candidate) {
        if (candidate != null && !candidate.isBlank()) {
            models.add(candidate.trim());
        }
    }

    private void ensureSuccessfulResponse(int statusCode, String responseBody, String candidateModel) {
        if (statusCode >= 200 && statusCode < 300) {
            return;
        }
        if (statusCode == 401) {
            throw new AiChatException(
                    503,
                    "Gemini API key không hợp lệ hoặc đã hết hiệu lực. Hãy kiểm tra GEMINI_API_KEY."
            );
        }
        String providerMessage = extractProviderMessage(responseBody);
        if (statusCode == 400 && providerMessage.toLowerCase().contains("valid api key")) {
            throw new AiChatException(
                    503,
                    "Gemini API key trong config/application-secrets.properties không hợp lệ. Hãy tạo key mới từ Google AI Studio."
            );
        }
        if (statusCode == 404) {
            throw new AiChatException(
                    503,
                    "Model AI '" + candidateModel + "' không tồn tại hoặc không khả dụng với API key hiện tại."
            );
        }
        if (statusCode == 429) {
            throw new AiChatException(
                    503,
                    "Model AI '" + candidateModel + "' đang giới hạn yêu cầu.",
                    true
            );
        }
        if (statusCode >= 500) {
            throw new AiChatException(
                    503,
                    "Model AI '" + candidateModel + "' đang tạm thời quá tải.",
                    true
            );
        }
        String message = providerMessage.isBlank()
                ? "Dịch vụ AI tạm thời trả lỗi " + statusCode + "."
                : "Dịch vụ AI từ chối yêu cầu: " + providerMessage;
        throw new AiChatException(502, message);
    }

    private String extractProviderMessage(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            return "";
        }
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode errorContainer = root.isArray() && !root.isEmpty() ? root.path(0) : root;
            return errorContainer.path("error")
                    .path("message")
                    .asText("");
        } catch (Exception ignored) {
            return "";
        }
    }

    private String systemPrompt() {
        return """
                Bạn là trợ lý AI của hệ thống Quản lý kho.
                Hãy trả lời bằng tiếng Việt, ngắn gọn, dễ hiểu.
                Chỉ hướng dẫn người dùng sử dụng phần mềm quản lý kho.
                Các chức năng chính gồm:
                - Quản lý danh mục
                - Quản lý sản phẩm
                - Nhập kho
                - Xuất kho
                - Tồn kho
                - Quản lý người dùng
                - Xuất Excel
                Nếu người dùng hỏi ngoài phạm vi hệ thống, hãy lịch sự nói rằng bạn chỉ hỗ trợ nghiệp vụ quản lý kho.
                """;
    }
}
