# System Architecture - Inventory Management Desktop Client + Spring Boot REST API

## 1. Tổng Quan Kiến Trúc

Dự án Quản lý Kho được tách thành 2 hệ thống độc lập:

- **Backend:** Spring Boot REST API, Hibernate/JPA, MySQL, JWT Stateless Security.
- **Frontend:** JavaFX Desktop App, gọi API qua `java.net.http.HttpClient`, parse JSON bằng Gson, hiển thị dữ liệu bằng JavaFX UI.

Mô hình tổng thể:

```text
User
  |
  | Click / Input
  v
JavaFX View + Controller
  |
  | Background Task / CompletableFuture
  v
JavaFX Api Service
  |
  | HttpClient + Gson + JWT Header
  v
Spring Boot REST Controller
  |
  v
Service Layer
  |
  v
Repository / DAO / Hibernate
  |
  v
MySQL Database
```

### Design Pattern Sử Dụng

- **Client - Server:** JavaFX và Spring Boot chạy độc lập, giao tiếp qua HTTP/JSON.
- **MVC trên JavaFX:** FXML là View, Controller xử lý sự kiện UI, Service/API xử lý logic gọi mạng.
- **Layered Architecture trên Backend:** Controller -> Service -> Repository/DAO -> Database.
- **Singleton:** `ApiClient` dùng một instance duy nhất để quản lý `HttpClient`, base URL và JWT token.
- **DTO Pattern:** Backend trả DTO thay vì Entity Hibernate để tránh vòng lặp JSON và giảm dữ liệu thừa.
- **Background Task Pattern:** JavaFX gọi API trên thread nền, không gọi mạng trực tiếp trên JavaFX Application Thread.

---

## 2. Data Flow & Sequence

Ví dụ hành động: Người dùng bấm nút **Lấy danh sách sản phẩm**.

### Step-by-step

1. Người dùng bấm nút `refreshButton` hoặc mở màn hình Products.

2. `ProductUIController` nhận sự kiện click trên **JavaFX Application Thread**.

3. Controller tạo một `Task<List<ProductDto>>`.

4. `Task.call()` chạy trên **Background Thread**.

5. Trong background thread, `ProductUIController` gọi:

   ```java
   productApiService.getAllProducts();
   ```

6. `ProductApiService` không biết gì về UI. Lớp này chỉ đóng gói endpoint và gọi:

   ```java
   apiClient.get("/api/products", queryParams, pageType);
   ```

7. `ApiClient` tạo HTTP request:

   - Method: `GET`
   - URL: `http://localhost:8080/api/products?page=1&size=100`
   - Header:

     ```http
     Accept: application/json
     Authorization: Bearer <jwt-token>
     ```

8. Spring Boot nhận request tại `ProductRestController`.

9. Controller gọi `ProductService`.

10. Service gọi Repository/DAO/Hibernate để lấy dữ liệu từ MySQL.

11. Backend map Entity sang `ProductDto`.

12. Backend trả JSON chuẩn:

    ```json
    {
      "success": true,
      "message": "success",
      "data": {
        "items": [
          {
            "id": 1,
            "code": "SP001",
            "name": "Laptop Dell",
            "categoryId": 2,
            "categoryName": "Electronics",
            "imgUrl": "/upload/laptop.png",
            "activeFlag": 1
          }
        ],
        "currentPage": 1,
        "recordPerPage": 100,
        "totalPages": 1,
        "totalRows": 1
      }
    }
    ```

13. `ApiClient` nhận response, kiểm tra status code.

14. `ApiClient` parse JSON bằng Gson và lấy phần `data`.

15. `Task` trả về danh sách `ProductDto`.

16. Khi task thành công, JavaFX gọi `setOnSucceeded`.

17. Controller cập nhật TableView trên **JavaFX Application Thread**:

    ```java
    Platform.runLater(() -> tableView.setItems(...));
    ```

18. Nếu API lỗi `400/401/403/500`, controller không hiển thị JSON thô. Lỗi được parse thành thông báo thân thiện rồi hiển thị bằng `Alert` hoặc `JOptionPane`.

### Luồng Thread

```text
JavaFX Application Thread
  - Nhận click
  - Disable button / show loading
  - Start Task
  - Update TableView sau khi có kết quả

Background Thread
  - Gọi ProductApiService
  - Gọi ApiClient
  - Gửi HTTP request
  - Parse JSON
  - Trả dữ liệu về UI thread
```

Nguyên tắc quan trọng:

- Không gọi API trực tiếp trong event handler UI.
- Không cập nhật `TableView`, `Label`, `Button` từ background thread.
- Mọi cập nhật UI phải chạy trên JavaFX Application Thread.

---

## 3. Folder Structure Đề Xuất Cho JavaFX Frontend

```text
desktop-client/
  src/main/java/inventory/desktop/
    MainApp.java

    config/
      AppConfig.java
      ApiConfig.java

    context/
      AppContext.java

    security/
      SessionManager.java

    http/
      ApiClient.java
      ApiException.java
      ApiErrorParser.java

    model/
      ApiResponse.java
      ApiPage.java
      ProductDto.java
      CategoryDto.java
      UserDto.java

    service/
      ProductApiService.java
      CategoryApiService.java
      UserApiService.java
      AuthApiService.java

    controller/
      MainLayoutController.java
      LoginController.java

    controller/module/
      ProductUIController.java
      CategoryUIController.java
      UserUIController.java

    navigation/
      ViewNavigator.java

    ui/
      StatusBadgeTableCell.java
      ProductImageTableCell.java
      TableExcelExporter.java

  src/main/resources/inventory/desktop/
    view/
      MainLayout.fxml
      LoginView.fxml
      ProductView.fxml
      CategoryView.fxml

    styles/
      global-style.css
```

### Quy Ước Trách Nhiệm

| Package | Trách nhiệm |
|---|---|
| `controller` | Nhận event UI, gọi service, cập nhật giao diện |
| `service` | Gói logic gọi API theo từng module |
| `http` | Xử lý HTTP, JWT, JSON, lỗi mạng |
| `model` | DTO nhận từ backend hoặc gửi lên backend |
| `ui` | Custom TableCell, Dialog, Exporter, component dùng chung |
| `navigation` | Chuyển màn hình FXML |
| `security` | Lưu JWT trong RAM phiên đăng nhập |

---

## 4. Code Template - Backend Spring Boot

### ProductRestController.java

```java
package inventory.controller;

import inventory.api.ApiResponse;
import inventory.api.PageResponse;
import inventory.api.dto.ProductDto;
import inventory.api.request.ProductRequest;
import inventory.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST Controller chỉ nhận request và trả JSON.
 * Không trả về tên file HTML, không dùng Model/ModelAndView.
 */
@RestController
@RequestMapping("/api/products")
@Validated
public class ProductRestController {
    private final ProductService productService;

    public ProductRestController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * GET /api/products?page=1&size=100
     * Trả danh sách sản phẩm dạng PageResponse.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductDto>>> getAllProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "100") int size
    ) {
        PageResponse<ProductDto> result = productService.findProducts(page, size);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    /**
     * GET /api/products/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> getProductById(@PathVariable Integer id) {
        ProductDto product = productService.findProductById(id);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Không tìm thấy sản phẩm."));
        }
        return ResponseEntity.ok(ApiResponse.ok(product));
    }

    /**
     * POST /api/products
     * Body JSON ví dụ:
     * {
     *   "code": "SP001",
     *   "name": "Laptop Dell",
     *   "categoryId": 2,
     *   "description": "Laptop văn phòng",
     *   "imgUrl": "/upload/laptop.png"
     * }
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ProductDto>> createProduct(
            @Valid @RequestBody ProductRequest request
    ) {
        ProductDto created = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Thêm sản phẩm thành công.", created));
    }

    /**
     * PUT /api/products/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> updateProduct(
            @PathVariable Integer id,
            @Valid @RequestBody ProductRequest request
    ) {
        ProductDto updated = productService.updateProduct(id, request);
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Không tìm thấy sản phẩm."));
        }
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật sản phẩm thành công.", updated));
    }

    /**
     * DELETE /api/products/{id}
     * Có thể xóa mềm bằng activeFlag = 0.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Integer id) {
        boolean deleted = productService.deleteProduct(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Không tìm thấy sản phẩm."));
        }
        return ResponseEntity.ok(ApiResponse.ok("Xóa sản phẩm thành công.", null));
    }
}
```

### ProductRequest.java

```java
package inventory.api.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ProductRequest {
    @NotBlank(message = "Mã sản phẩm không được để trống.")
    private String code;

    @NotBlank(message = "Tên sản phẩm không được để trống.")
    private String name;

    @NotNull(message = "Danh mục không được để trống.")
    private Integer categoryId;

    private String description;
    private String imgUrl;

    // Getter/Setter
}
```

---

## 5. Code Template - Core/Utility JavaFX

### ApiClient.java

```java
package inventory.desktop.http;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
 * Singleton ApiClient:
 * - Quản lý HttpClient dùng chung.
 * - Quản lý baseUrl.
 * - Tự động gắn JWT Authorization header.
 * - Parse JSON bằng Gson.
 *
 * Lý do thiết kế:
 * UI Controller không nên tự tạo HttpRequest.
 * Service chỉ cần gọi apiClient.get/post/put/delete.
 */
public final class ApiClient {
    private static final ApiClient INSTANCE = new ApiClient();

    private final HttpClient httpClient;
    private final Gson gson;

    private String baseUrl = "http://localhost:8080";
    private String jwtToken;

    private ApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
        this.gson = new Gson();
    }

    public static ApiClient getInstance() {
        return INSTANCE;
    }

    public void setBaseUrl(String baseUrl) {
        if (baseUrl != null && !baseUrl.isBlank()) {
            this.baseUrl = trimTrailingSlash(baseUrl);
        }
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public void clearToken() {
        this.jwtToken = null;
    }

    public <T> T get(String path, Map<String, String> query, Type responseType)
            throws IOException, InterruptedException {
        HttpRequest request = baseRequest(path + queryString(query))
                .GET()
                .build();

        String body = send(request);
        return readData(body, responseType);
    }

    public <T> T post(String path, Object bodyObject, Type responseType)
            throws IOException, InterruptedException {
        String json = gson.toJson(bodyObject);
        HttpRequest request = baseRequest(path)
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        String body = send(request);
        return readData(body, responseType);
    }

    public <T> T put(String path, Object bodyObject, Type responseType)
            throws IOException, InterruptedException {
        String json = gson.toJson(bodyObject);
        HttpRequest request = baseRequest(path)
                .header("Content-Type", "application/json; charset=UTF-8")
                .PUT(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        String body = send(request);
        return readData(body, responseType);
    }

    public void delete(String path) throws IOException, InterruptedException {
        HttpRequest request = baseRequest(path)
                .DELETE()
                .build();

        send(request);
    }

    private HttpRequest.Builder baseRequest(String path) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + normalizePath(path)))
                .timeout(Duration.ofSeconds(20))
                .header("Accept", "application/json");

        if (jwtToken != null && !jwtToken.isBlank()) {
            builder.header("Authorization", "Bearer " + jwtToken);
        }

        return builder;
    }

    private String send(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("API lỗi HTTP " + response.statusCode() + ": " + response.body());
        }

        return response.body();
    }

    /**
     * Backend trả wrapper:
     * {
     *   "success": true,
     *   "message": "success",
     *   "data": ...
     * }
     *
     * Hàm này lấy riêng field data để map về DTO.
     */
    private <T> T readData(String json, Type responseType) {
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();

        if (root.has("success") && !root.get("success").getAsBoolean()) {
            String message = root.has("message") ? root.get("message").getAsString() : "Yêu cầu thất bại.";
            throw new RuntimeException(message);
        }

        JsonElement data = root.has("data") ? root.get("data") : root;
        return gson.fromJson(data, responseType);
    }

    private String queryString(Map<String, String> query) {
        if (query == null || query.isEmpty()) {
            return "";
        }

        StringJoiner joiner = new StringJoiner("&", "?", "");
        query.forEach((key, value) -> {
            if (value != null && !value.isBlank()) {
                joiner.add(
                        URLEncoder.encode(key, StandardCharsets.UTF_8)
                                + "="
                                + URLEncoder.encode(value, StandardCharsets.UTF_8)
                );
            }
        });
        return joiner.toString();
    }

    private String normalizePath(String path) {
        if (path == null || path.isBlank()) {
            return "/";
        }
        return path.startsWith("/") ? path : "/" + path;
    }

    private String trimTrailingSlash(String value) {
        while (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }
}
```

---

## 6. Code Template - JavaFX Service Layer

### ProductApiService.java

```java
package inventory.desktop.service;

import com.google.gson.reflect.TypeToken;
import inventory.desktop.http.ApiClient;
import inventory.desktop.model.ApiPage;
import inventory.desktop.model.ProductDto;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Service tầng client.
 *
 * Nhiệm vụ:
 * - Biết endpoint Product nằm ở đâu.
 * - Biết request/response type.
 * - Không chứa code TableView, Button, Label.
 */
public class ProductApiService {
    private final ApiClient apiClient = ApiClient.getInstance();

    public List<ProductDto> getAllProducts() throws Exception {
        Type pageType = TypeToken.getParameterized(ApiPage.class, ProductDto.class).getType();

        ApiPage<ProductDto> page = apiClient.get(
                "/api/products",
                Map.of("page", "1", "size", "100"),
                pageType
        );

        return page == null || page.getItems() == null
                ? List.of()
                : page.getItems();
    }

    public ProductDto createProduct(ProductDto product) throws Exception {
        Type productType = ProductDto.class;
        return apiClient.post("/api/products", product, productType);
    }

    public ProductDto updateProduct(Integer id, ProductDto product) throws Exception {
        Type productType = ProductDto.class;
        return apiClient.put("/api/products/" + id, product, productType);
    }

    public void deleteProduct(Integer id) throws Exception {
        apiClient.delete("/api/products/" + id);
    }
}
```

---

## 7. Code Template - JavaFX UI Controller

### ProductUIController.java

```java
package inventory.desktop.controller.module;

import inventory.desktop.model.ProductDto;
import inventory.desktop.service.ProductApiService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

/**
 * Controller gắn với ProductView.fxml.
 *
 * Nhiệm vụ:
 * - Nhận event click từ UI.
 * - Gọi ProductApiService trên background thread.
 * - Cập nhật TableView trên JavaFX Application Thread.
 *
 * Controller không tự tạo HttpRequest.
 */
public class ProductUIController {
    @FXML
    private TableView<ProductDto> productTable;

    @FXML
    private TableColumn<ProductDto, Integer> idColumn;

    @FXML
    private TableColumn<ProductDto, String> codeColumn;

    @FXML
    private TableColumn<ProductDto, String> nameColumn;

    @FXML
    private TableColumn<ProductDto, String> categoryColumn;

    @FXML
    private Button refreshButton;

    private final ProductApiService productApiService = new ProductApiService();

    @FXML
    private void initialize() {
        configureTable();
        loadProducts();
    }

    private void configureTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));

        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    private void loadProducts() {
        refreshButton.setDisable(true);

        Task<List<ProductDto>> task = new Task<>() {
            @Override
            protected List<ProductDto> call() throws Exception {
                // Đoạn này chạy ở Background Thread.
                // Không được cập nhật UI tại đây.
                return productApiService.getAllProducts();
            }
        };

        task.setOnSucceeded(event -> {
            List<ProductDto> products = task.getValue();

            // setOnSucceeded đã chạy trên JavaFX Application Thread.
            // Có thể update UI trực tiếp, hoặc dùng Platform.runLater để rõ ý đồ.
            Platform.runLater(() -> {
                productTable.setItems(FXCollections.observableArrayList(products));
                refreshButton.setDisable(false);
            });
        });

        task.setOnFailed(event -> Platform.runLater(() -> {
            refreshButton.setDisable(false);
            showError(task.getException());
        }));

        Thread thread = new Thread(task, "product-load-task");
        thread.setDaemon(true);
        thread.start();
    }

    private void showError(Throwable throwable) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Không tải được danh sách sản phẩm");
        alert.setContentText(toFriendlyMessage(throwable));
        alert.showAndWait();
    }

    private String toFriendlyMessage(Throwable throwable) {
        if (throwable == null) {
            return "Đã xảy ra lỗi không xác định.";
        }

        String message = throwable.getMessage();
        if (message == null || message.isBlank()) {
            return "Không thể kết nối tới máy chủ.";
        }

        if (message.contains("401")) {
            return "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.";
        }

        if (message.contains("403")) {
            return "Bạn không có quyền thực hiện chức năng này.";
        }

        if (message.contains("500")) {
            return "Máy chủ đang gặp lỗi. Vui lòng thử lại sau.";
        }

        return message;
    }
}
```

---

## 8. DTO Mẫu Cho JavaFX

### ProductDto.java

```java
package inventory.desktop.model;

public class ProductDto {
    private Integer id;
    private String code;
    private String name;
    private String description;
    private String imgUrl;
    private Integer categoryId;
    private String categoryName;
    private Integer activeFlag;

    // Getter/Setter
}
```

### ApiPage.java

```java
package inventory.desktop.model;

import java.util.List;

public class ApiPage<T> {
    private List<T> items;
    private int currentPage;
    private int recordPerPage;
    private int totalPages;
    private long totalRows;

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    // Getter/Setter cho các field còn lại
}
```

---

## 9. Nguyên Tắc Bắt Buộc Khi Code Thật

1. **Controller JavaFX không gọi HttpClient trực tiếp.**

   Sai:

   ```java
   HttpClient.newHttpClient().send(...);
   ```

   Đúng:

   ```java
   productApiService.getAllProducts();
   ```

2. **Không gọi API trên JavaFX Application Thread.**

   Sai:

   ```java
   refreshButton.setOnAction(e -> productApiService.getAllProducts());
   ```

   Đúng:

   ```java
   Task<List<ProductDto>> task = new Task<>() {
       protected List<ProductDto> call() throws Exception {
           return productApiService.getAllProducts();
       }
   };
   ```

3. **Không update UI từ background thread.**

   Sai:

   ```java
   protected List<ProductDto> call() {
       productTable.setItems(...);
   }
   ```

   Đúng:

   ```java
   Platform.runLater(() -> productTable.setItems(...));
   ```

4. **Backend luôn trả `ResponseEntity<?>`.**

5. **Backend trả DTO, không trả Entity Hibernate trực tiếp.**

6. **JWT token được lưu trong RAM phiên đăng nhập, không ghi ra file plaintext.**

7. **Lỗi API phải được parse và hiển thị thân thiện bằng tiếng Việt.**

---

## 10. Kết Luận

Kiến trúc đề xuất giúp dự án đạt các mục tiêu:

- JavaFX UI không bị treo khi mạng chậm.
- Dễ bảo trì vì UI, Service, HTTP Client và DTO được tách riêng.
- Backend có REST API rõ ràng, trả JSON thống nhất.
- JWT được đính kèm tự động trong mọi request.
- Có thể mở rộng thêm module như Category, Goods Receipt, Goods Issue, User bằng cùng một pattern.

Pattern chuẩn cho mọi module:

```text
FXML View
  -> JavaFX Controller
  -> ModuleApiService
  -> ApiClient
  -> Spring Boot REST Controller
  -> Backend Service
  -> Repository/DAO
  -> MySQL
```
