package inventory.api.request;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class InvoiceRequest {
    @NotBlank(message = "Mã phiếu không được để trống.")
    private String code;

    @NotNull(message = "Sản phẩm không được để trống.")
    private Integer productId;

    @NotNull(message = "Số lượng không được để trống.")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0.")
    private Integer qty;

    @NotNull(message = "Giá tiền không được để trống.")
    @DecimalMin(value = "0.01", message = "Giá tiền phải lớn hơn 0.")
    private BigDecimal price;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
