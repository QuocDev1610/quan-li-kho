package inventory.desktop.controller.module;

import com.google.gson.reflect.TypeToken;
import inventory.desktop.context.AppContext;
import inventory.desktop.model.CategoryDto;
import inventory.desktop.model.FormField;
import inventory.desktop.model.ProductDto;
import inventory.desktop.ui.ProductImageTableCell;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProductViewController extends BaseModuleController<ProductDto> {
    @FXML
    private void initialize() {
        configure(
                "Products",
                "Quản lý thông tin mặt hàng, mã sản phẩm và nhóm danh mục.",
                List.of("ID", "Code", "Name", "Category", "Image", "Status"),
                true
        );
    }

    @Override
    protected String endpoint() {
        return "/api/products";
    }

    @Override
    protected Type pageType() {
        return pageTypeFor(ProductDto.class);
    }

    @Override
    protected List<String> rowValues(ProductDto item) {
        return List.of(text(item.id), text(item.code), text(item.name), text(item.categoryName), text(item.imgUrl), status(item.activeFlag));
    }

    @Override
    protected void configureColumn(TableColumn<ObservableList<String>, String> column, String columnName, int columnIndex) {
        if ("Image".equalsIgnoreCase(columnName)) {
            column.setMinWidth(86);
            column.setCellFactory(value -> new ProductImageTableCell());
        }
    }

    @Override
    protected Integer idOf(ProductDto item) {
        return item.id;
    }

    @Override
    protected List<FormField> formFields(boolean editing) {
        return List.of(
                FormField.text("code", "Mã sản phẩm"),
                FormField.text("name", "Tên sản phẩm"),
                FormField.text("description", "Mô tả"),
                FormField.integer("categoryId", "ID danh mục"),
                FormField.optionalText("imgUrl", "Đường dẫn ảnh")
        );
    }

    @Override
    protected List<FormField> loadFormFields(boolean editing) throws Exception {
        Type optionsType = new TypeToken<Map<String, List<CategoryDto>>>() {
        }.getType();
        Map<String, List<CategoryDto>> options = AppContext.apiClient().get("/api/products/form-options", null, optionsType);
        List<FormField.Option> categories = new ArrayList<>();
        if (options != null && options.get("categories") != null) {
            for (CategoryDto category : options.get("categories")) {
                categories.add(FormField.option(category.id, text(category.code) + " - " + text(category.name)));
            }
        }
        return List.of(
                FormField.text("code", "Mã sản phẩm"),
                FormField.text("name", "Tên sản phẩm"),
                FormField.text("description", "Mô tả"),
                FormField.select("categoryId", "Danh mục", categories),
                FormField.optionalText("imgUrl", "Đường dẫn ảnh")
        );
    }

    @Override
    protected Map<String, String> formValues(ProductDto item) {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("code", text(item.code));
        values.put("name", text(item.name));
        values.put("description", text(item.description));
        values.put("categoryId", text(item.categoryId));
        values.put("imgUrl", text(item.imgUrl));
        return values;
    }

    @Override
    protected Object requestBody(Map<String, String> values, ProductDto editingItem) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", stringValue(values, "code"));
        body.put("name", stringValue(values, "name"));
        body.put("description", stringValue(values, "description"));
        body.put("imgUrl", stringValue(values, "imgUrl"));
        body.put("cate", Map.of("id", integerValue(values, "categoryId")));
        return body;
    }

    private String text(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private String status(Integer activeFlag) {
        return Integer.valueOf(1).equals(activeFlag) ? "Active" : "Inactive";
    }
}
