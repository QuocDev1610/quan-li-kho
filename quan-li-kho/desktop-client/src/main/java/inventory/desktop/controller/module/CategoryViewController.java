package inventory.desktop.controller.module;

import inventory.desktop.model.CategoryDto;
import inventory.desktop.model.FormField;
import javafx.fxml.FXML;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CategoryViewController extends BaseModuleController<CategoryDto> {
    @FXML
    private void initialize() {
        configure(
                "Categories",
                "Quản lý danh mục sản phẩm trong kho.",
                List.of("ID", "Code", "Name", "Description", "Status"),
                true
        );
    }

    @Override
    protected String endpoint() {
        return "/api/categories";
    }

    @Override
    protected Type pageType() {
        return pageTypeFor(CategoryDto.class);
    }

    @Override
    protected List<String> rowValues(CategoryDto item) {
        return List.of(text(item.id), text(item.code), text(item.name), text(item.description), status(item.activeFlag));
    }

    @Override
    protected Integer idOf(CategoryDto item) {
        return item.id;
    }

    @Override
    protected List<FormField> formFields(boolean editing) {
        return List.of(FormField.text("code", "Mã danh mục"), FormField.text("name", "Tên danh mục"), FormField.text("description", "Mô tả"));
    }

    @Override
    protected Map<String, String> formValues(CategoryDto item) {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("code", text(item.code));
        values.put("name", text(item.name));
        values.put("description", text(item.description));
        return values;
    }

    @Override
    protected Object requestBody(Map<String, String> values, CategoryDto editingItem) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", stringValue(values, "code"));
        body.put("name", stringValue(values, "name"));
        body.put("description", stringValue(values, "description"));
        return body;
    }

    private String text(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private String status(Integer activeFlag) {
        return Integer.valueOf(1).equals(activeFlag) ? "Active" : "Inactive";
    }
}
