package inventory.desktop.http;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.ConnectException;
import java.net.http.HttpConnectTimeoutException;
import java.util.Map;

public final class ApiErrorParser {
    private static final Gson GSON = new Gson();

    private ApiErrorParser() {
    }

    public static String friendlyMessage(int statusCode, String body) {
        String message = extractMessage(body);
        if (message == null || message.trim().isEmpty()) {
            message = defaultMessage(statusCode);
        }
        return translate(statusCode, message);
    }

    public static String friendlyException(Throwable throwable) {
        if (throwable instanceof ApiException) {
            return throwable.getMessage();
        }
        if (throwable instanceof ConnectException || throwable instanceof HttpConnectTimeoutException) {
            return "Không kết nối được tới máy chủ. Vui lòng kiểm tra backend đã chạy và đúng địa chỉ API.";
        }
        if (throwable instanceof NumberFormatException) {
            return "Dữ liệu số không hợp lệ. Vui lòng kiểm tra số lượng và giá tiền.";
        }
        return "Có lỗi xảy ra. Vui lòng thử lại hoặc kiểm tra log ứng dụng.";
    }

    private static String extractMessage(String body) {
        try {
            if (body == null || body.trim().isEmpty()) {
                return null;
            }
            JsonObject object = JsonParser.parseString(body).getAsJsonObject();
            if (object.has("message")) {
                return object.get("message").getAsString();
            }
            if (object.has("errors")) {
                JsonElement errors = object.get("errors");
                if (errors.isJsonObject()) {
                    StringBuilder builder = new StringBuilder();
                    for (Map.Entry<String, JsonElement> entry : errors.getAsJsonObject().entrySet()) {
                        if (builder.length() > 0) {
                            builder.append("\n");
                        }
                        builder.append(entry.getKey()).append(": ").append(entry.getValue().getAsString());
                    }
                    return builder.toString();
                }
                return GSON.toJson(errors);
            }
        } catch (Exception ignored) {
            return null;
        }
        return null;
    }

    private static String defaultMessage(int statusCode) {
        switch (statusCode) {
            case 400:
                return "Dữ liệu nhập chưa hợp lệ.";
            case 401:
                return "Phiên đăng nhập không hợp lệ hoặc đã hết hạn.";
            case 403:
                return "Bạn không có quyền thực hiện thao tác này.";
            case 404:
                return "Không tìm thấy dữ liệu yêu cầu.";
            case 500:
                return "Máy chủ đang gặp lỗi nội bộ.";
            default:
                return "Yêu cầu không thành công. Mã lỗi: " + statusCode;
        }
    }

    private static String translate(int statusCode, String message) {
        String normalized = message.toLowerCase();
        if (normalized.contains("password") || normalized.contains("mat khau")) {
            return "Mật khẩu không chính xác.";
        }
        if (normalized.contains("tai khoan khong ton tai") || normalized.contains("not found")) {
            return statusCode == 401 ? "Tài khoản không tồn tại." : "Không tìm thấy dữ liệu.";
        }
        if (normalized.contains("locked") || normalized.contains("khoa")) {
            return "Tài khoản đã bị khóa.";
        }
        if (normalized.contains("validation")) {
            return "Dữ liệu nhập chưa hợp lệ. Vui lòng kiểm tra các trường bắt buộc.";
        }
        return message;
    }
}
