package inventory.desktop.model;

import java.math.BigDecimal;

public class InvoiceDto {
    public Integer id;
    public String code;
    public Integer type;
    public Integer qty;
    public BigDecimal price;
    public Integer productId;
    public String productCode;
    public String productName;
    public Integer activeFlag;
    public String updateDate;
}
