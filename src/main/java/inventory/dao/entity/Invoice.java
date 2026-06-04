package inventory.dao.entity;

import javax.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "invoice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "type", nullable = false)
    private Integer type;

    @Column(name = "qty", nullable = false)
    private Integer qty;

    @Column(name = "price", nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @ColumnDefault("1")
    @Column(name = "active_flag", nullable = false)
    private Integer activeFlag;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "CREATE_DATE", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Instant createDate;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "UPDATE_DATE", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Instant updateDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductInfo product;
@Transient
@DateTimeFormat(pattern = "yyyy-MM-dd")
private Date Fromdate;
@Transient
@DateTimeFormat(pattern = "yyyy-MM-dd")
private Date Todate;

    public Date getFromdate() {
        return Fromdate;
    }

    public void setFromdate(Date fromdate) {
        Fromdate = fromdate;
    }

    public Date getTodate() {
        return Todate;
    }

    public void setTodate(Date todate) {
        Todate = todate;
    }

    public ProductInfo getProduct() {
        return product;
    }

    public void setProduct(ProductInfo product) {
        this.product = product;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public Integer getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(Integer activeFlag) {
        this.activeFlag = activeFlag;
    }

    public Instant getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Instant createDate) {
        this.createDate = createDate;
    }

    public Instant getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Instant updateDate) {
        this.updateDate = updateDate;
    }

}