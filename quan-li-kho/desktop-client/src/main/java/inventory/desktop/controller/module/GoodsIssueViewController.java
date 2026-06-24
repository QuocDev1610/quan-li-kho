package inventory.desktop.controller.module;

import com.google.gson.reflect.TypeToken;
import inventory.desktop.context.AppContext;
import inventory.desktop.model.FormField;
import inventory.desktop.model.InvoiceDto;
import inventory.desktop.model.ProductDto;
import javafx.fxml.FXML;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GoodsIssueViewController extends BaseModuleController<InvoiceDto> {
    @FXML
    private void initialize() {
        configure(
                "Goods Issues",
                "Lập phiếu xuất và kiểm soát lượng hàng rời kho.",
                List.of("ID", "Issue Code", "Product", "Qty", "Price", "Updated At"),
                true
        );
    }

    @Override
    protected String endpoint() {
        return "/api/goods-issues";
    }

    @Override
    protected Type pageType() {
        return pageTypeFor(InvoiceDto.class);
    }

    @Override
    protected List<String> rowValues(InvoiceDto item) {
        return List.of(text(item.id), text(item.code), text(item.productName), text(item.qty), text(item.price), text(item.updateDate));
    }

    @Override
    protected Integer idOf(InvoiceDto item) {
        return item.id;
    }

    @Override
    protected List<FormField> formFields(boolean editing) {
        return List.of(
                FormField.text("code", "Mã phiếu xuất"),
                FormField.integer("productId", "ID sản phẩm"),
                FormField.integer("qty", "Số lượng"),
                FormField.decimal("price", "Giá xuất")
        );
    }

    @Override
    protected List<FormField> loadFormFields(boolean editing) throws Exception {
        Type optionsType = new TypeToken<Map<String, List<ProductDto>>>() {
        }.getType();
        Map<String, List<ProductDto>> options = AppContext.apiClient().get("/api/goods-issues/form-options", null, optionsType);
        List<FormField.Option> products = new ArrayList<>();
        if (options != null && options.get("products") != null) {
            for (ProductDto product : options.get("products")) {
                products.add(FormField.option(product.id, text(product.code) + " - " + text(product.name)));
            }
        }
        return List.of(
                FormField.text("code", "Mã phiếu xuất"),
                FormField.select("productId", "Sản phẩm", products),
                FormField.integer("qty", "Số lượng"),
                FormField.decimal("price", "Giá xuất")
        );
    }

    @Override
    protected Map<String, String> formValues(InvoiceDto item) {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("code", text(item.code));
        values.put("productId", text(item.productId));
        values.put("qty", text(item.qty));
        values.put("price", text(item.price));
        return values;
    }

    @Override
    protected Object requestBody(Map<String, String> values, InvoiceDto editingItem) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", stringValue(values, "code"));
        body.put("productId", integerValue(values, "productId"));
        body.put("qty", integerValue(values, "qty"));
        body.put("price", new BigDecimal(stringValue(values, "price")));
        return body;
    }

    private String text(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
