package inventory.desktop.http;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import inventory.desktop.security.SessionManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.StringJoiner;

/**
 * HTTP client dùng chung cho JavaFX Desktop App.
 * Tự serialize/deserialize JSON bằng Gson và tự gắn JWT vào mọi request đã đăng nhập.
 */
public class ApiClient {
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(20);

    private final HttpClient httpClient;
    private final SessionManager sessionManager;
    private final Gson gson;
    private String baseUrl;

    public ApiClient(String baseUrl, SessionManager sessionManager) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(REQUEST_TIMEOUT)
                .build();
        this.gson = new Gson();
        this.baseUrl = trimTrailingSlash(baseUrl);
        this.sessionManager = sessionManager;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = trimTrailingSlash(baseUrl);
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String resolvePublicUrl(String resourcePath) {
        if (resourcePath == null || resourcePath.trim().isEmpty()) {
            return "";
        }
        String trimmedPath = resourcePath.trim();
        if (trimmedPath.startsWith("http://") || trimmedPath.startsWith("https://")) {
            return trimmedPath;
        }
        String rootUrl = baseUrl.endsWith("/api") ? baseUrl.substring(0, baseUrl.length() - 4) : baseUrl;
        return rootUrl + normalizePath(trimmedPath);
    }

    public String login(String userName, String password) throws IOException, InterruptedException, ApiException {
        LoginRequest request = new LoginRequest(userName, password);
        ApiResult result = send("POST", "/api/auth/login", gson.toJson(request), false);
        ensureSuccess(result);

        String token = extractAccessToken(result.body);
        if (token == null || token.isEmpty()) {
            throw new ApiException(500, "Máy chủ không trả về token đăng nhập.");
        }

        sessionManager.startSession(token, userName);
        return token;
    }

    public void logout() {
        sessionManager.clear();
    }

    public <T> T get(String path, Class<T> responseType) throws IOException, InterruptedException, ApiException {
        return get(path, null, responseType);
    }

    public <T> T get(String path, Map<String, String> query, Class<T> responseType) throws IOException, InterruptedException, ApiException {
        ApiResult result = get(path, query);
        return readData(result.body, responseType);
    }

    public <T> T get(String path, Map<String, String> query, Type responseType) throws IOException, InterruptedException, ApiException {
        ApiResult result = get(path, query);
        return readData(result.body, responseType);
    }

    public <T> T post(String path, Object requestBody, Class<T> responseType) throws IOException, InterruptedException, ApiException {
        ApiResult result = post(path, gson.toJson(requestBody));
        return readData(result.body, responseType);
    }

    public <T> T post(String path, Object requestBody, Type responseType) throws IOException, InterruptedException, ApiException {
        ApiResult result = post(path, gson.toJson(requestBody));
        return readData(result.body, responseType);
    }

    public <T> T put(String path, Object requestBody, Class<T> responseType) throws IOException, InterruptedException, ApiException {
        ApiResult result = put(path, gson.toJson(requestBody));
        return readData(result.body, responseType);
    }

    public <T> T put(String path, Object requestBody, Type responseType) throws IOException, InterruptedException, ApiException {
        ApiResult result = put(path, gson.toJson(requestBody));
        return readData(result.body, responseType);
    }

    public <T> T patch(String path, Object requestBody, Class<T> responseType) throws IOException, InterruptedException, ApiException {
        String json = requestBody == null ? null : gson.toJson(requestBody);
        ApiResult result = send("PATCH", path, json, true);
        ensureSuccess(result);
        return readData(result.body, responseType);
    }

    public void deleteForNoContent(String path) throws IOException, InterruptedException, ApiException {
        delete(path);
    }

    public ApiResult get(String path, Map<String, String> query) throws IOException, InterruptedException, ApiException {
        ApiResult result = send("GET", path + queryString(query), null, true);
        ensureSuccess(result);
        return result;
    }

    public ApiResult post(String path, String json) throws IOException, InterruptedException, ApiException {
        ApiResult result = send("POST", path, json, true);
        ensureSuccess(result);
        return result;
    }

    public ApiResult put(String path, String json) throws IOException, InterruptedException, ApiException {
        ApiResult result = send("PUT", path, json, true);
        ensureSuccess(result);
        return result;
    }

    public ApiResult delete(String path) throws IOException, InterruptedException, ApiException {
        ApiResult result = send("DELETE", path, null, true);
        ensureSuccess(result);
        return result;
    }

    public byte[] download(String path) throws IOException, InterruptedException, ApiException {
        HttpRequest.Builder builder = baseRequest(path)
                .header("Accept", "*/*")
                .GET();
        addAuthorizationHeader(builder);

        HttpResponse<byte[]> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            String body = new String(response.body(), StandardCharsets.UTF_8);
            throw new ApiException(response.statusCode(), ApiErrorParser.friendlyMessage(response.statusCode(), body));
        }
        return response.body();
    }

    public <T> T readData(String json, Class<T> responseType) throws ApiException {
        if (responseType == Void.class || responseType == Void.TYPE) {
            return null;
        }
        JsonElement data = extractDataElement(json);
        if (data == null || data.isJsonNull()) {
            return null;
        }
        return gson.fromJson(data, responseType);
    }

    public <T> T readData(String json, Type responseType) throws ApiException {
        JsonElement data = extractDataElement(json);
        if (data == null || data.isJsonNull()) {
            return null;
        }
        return gson.fromJson(data, responseType);
    }

    public <T> T readRaw(String json, Class<T> responseType) {
        return gson.fromJson(json, responseType);
    }

    public <T> T readRaw(String json, Type responseType) {
        return gson.fromJson(json, responseType);
    }

    private ApiResult send(String method, String path, String body, boolean authenticated) throws IOException, InterruptedException {
        HttpRequest.Builder builder = baseRequest(path)
                .header("Accept", "application/json");

        if (authenticated) {
            addAuthorizationHeader(builder);
        }

        if (body == null) {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        } else {
            builder.header("Content-Type", "application/json; charset=UTF-8");
            builder.method(method, HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8));
        }

        HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return new ApiResult(response.statusCode(), response.body());
    }

    private HttpRequest.Builder baseRequest(String path) {
        return HttpRequest.newBuilder(URI.create(resolveUrl(path)))
                .timeout(REQUEST_TIMEOUT);
    }

    private void addAuthorizationHeader(HttpRequest.Builder builder) {
        sessionManager.authorizationHeader().ifPresent(token -> builder.header("Authorization", token));
    }

    private void ensureSuccess(ApiResult result) throws ApiException {
        if (!result.isSuccess()) {
            throw new ApiException(result.statusCode, ApiErrorParser.friendlyMessage(result.statusCode, result.body));
        }
    }

    private String extractAccessToken(String body) throws ApiException {
        JsonElement data = extractDataElement(body);
        if (data == null || !data.isJsonObject()) {
            return null;
        }
        JsonObject dataObject = data.getAsJsonObject();
        return dataObject.has("accessToken") && !dataObject.get("accessToken").isJsonNull()
                ? dataObject.get("accessToken").getAsString()
                : null;
    }

    private JsonElement extractDataElement(String json) throws ApiException {
        try {
            JsonElement root = JsonParser.parseString(json);
            if (!root.isJsonObject()) {
                return root;
            }
            JsonObject object = root.getAsJsonObject();
            if (object.has("success") && !object.get("success").getAsBoolean()) {
                String message = object.has("message") ? object.get("message").getAsString() : "Yêu cầu không thành công.";
                throw new ApiException(400, message);
            }
            return object.has("data") ? object.get("data") : root;
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApiException(500, "Không đọc được dữ liệu JSON từ máy chủ.");
        }
    }

    private String queryString(Map<String, String> query) {
        if (query == null || query.isEmpty()) {
            return "";
        }
        StringJoiner joiner = new StringJoiner("&", "?", "");
        query.forEach((key, value) -> {
            if (value != null && !value.trim().isEmpty()) {
                joiner.add(URLEncoder.encode(key, StandardCharsets.UTF_8) + "=" + URLEncoder.encode(value, StandardCharsets.UTF_8));
            }
        });
        return joiner.toString().equals("?") ? "" : joiner.toString();
    }

    private String normalizePath(String path) {
        if (path == null || path.trim().isEmpty()) {
            return "/";
        }
        return path.startsWith("/") ? path : "/" + path;
    }

    private String resolveUrl(String path) {
        String normalizedPath = normalizePath(path);
        if (baseUrl.endsWith("/api") && normalizedPath.startsWith("/api/")) {
            normalizedPath = normalizedPath.substring(4);
        }
        return baseUrl + normalizedPath;
    }

    private String trimTrailingSlash(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "http://localhost:8080/api";
        }
        while (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }

    public static class ApiResult {
        public final int statusCode;
        public final String body;

        public ApiResult(int statusCode, String body) {
            this.statusCode = statusCode;
            this.body = body;
        }

        public boolean isSuccess() {
            return statusCode >= 200 && statusCode < 300;
        }
    }

    private static class LoginRequest {
        private final String userName;
        private final String password;

        private LoginRequest(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }
    }
}
