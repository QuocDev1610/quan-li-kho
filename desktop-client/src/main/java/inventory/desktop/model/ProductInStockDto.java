package inventory.desktop.model;

import java.math.BigDecimal;

public class ProductInStockDto {
    public Integer id;
    public Integer productId;
    public String productCode;
    public String productName;
    public String categoryName;
    public Integer qty;
    public BigDecimal price;
    public Integer activeFlag;
}
