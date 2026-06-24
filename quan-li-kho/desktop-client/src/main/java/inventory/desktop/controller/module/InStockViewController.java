package inventory.desktop.controller.module;

import inventory.desktop.model.FormField;
import inventory.desktop.model.ProductInStockDto;
import javafx.fxml.FXML;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class InStockViewController extends BaseModuleController<ProductInStockDto> {
    @FXML
    private void initialize() {
        configure(
                "In Stock",
                "Theo dõi tồn kho hiện tại và cảnh báo định mức.",
                List.of("ID", "Product Code", "Product Name", "Category", "Qty", "Price", "Stock Status"),
                false
        );
    }

    @Override
    protected String endpoint() {
        return "/api/product-in-stocks";
    }

    @Override
    protected Type pageType() {
        return pageTypeFor(ProductInStockDto.class);
    }

    @Override
    protected List<String> rowValues(ProductInStockDto item) {
        return List.of(text(item.id), text(item.productCode), text(item.productName), text(item.categoryName), text(item.qty), text(item.price), stockStatus(item.qty));
    }

    @Override
    protected Integer idOf(ProductInStockDto item) {
        return item.id;
    }

    @Override
    protected List<FormField> formFields(boolean editing) {
        return Collections.emptyList();
    }

    @Override
    protected Map<String, String> formValues(ProductInStockDto item) {
        return Collections.emptyMap();
    }

    @Override
    protected Object requestBody(Map<String, String> values, ProductInStockDto editingItem) {
        return Collections.emptyMap();
    }

    private String text(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private String stockStatus(Integer qty) {
        if (qty == null) {
            return "Unknown";
        }
        return qty <= 10 ? "Low Stock" : "Healthy";
    }
}
