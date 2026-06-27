package inventory.service;

import inventory.dao.InvoiceDAO;
import inventory.dao.ProductinStockDAO;
import inventory.dao.entity.Invoice;
import inventory.dao.entity.ProductInStock;
import inventory.dao.entity.ProductInfo;
import inventory.model.paging;
import inventory.utils.constant;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ProductinStockService {
    private static final Logger logger = Logger.getLogger(ProductinStockService.class);

    private final ProductinStockDAO productInStockDAO;
    private final InvoiceDAO invoiceDAO;

    public ProductinStockService(ProductinStockDAO productInStockDAO, InvoiceDAO invoiceDAO) {
        this.productInStockDAO = productInStockDAO;
        this.invoiceDAO = invoiceDAO;
    }

    public List<ProductInStock> findAllProduct(ProductInStock searchForm, paging paging) {
        StringBuilder query = new StringBuilder(
                " and model.activeFlag = :activeFlag and model.product.activeFlag = :productActiveFlag"
        );
        Map<String, Object> params = new HashMap<>();
        params.put("activeFlag", 1);
        params.put("productActiveFlag", 1);

        if (searchForm != null && searchForm.getProduct() != null) {
            ProductInfo product = searchForm.getProduct();
            if (product.getCate() != null
                    && product.getCate().getName() != null
                    && !product.getCate().getName().isBlank()) {
                query.append(" and model.product.cate.name like :categoryName");
                params.put("categoryName", "%" + product.getCate().getName().trim() + "%");
            }
            if (product.getCode() != null && !product.getCode().isBlank()) {
                query.append(" and model.product.code = :code");
                params.put("code", product.getCode().trim());
            }
            if (product.getName() != null && !product.getName().isBlank()) {
                query.append(" and model.product.name like :productName");
                params.put("productName", "%" + product.getName().trim() + "%");
            }
        }

        return productInStockDAO.findAll(query.toString(), params, paging);
    }

    public void applyInvoice(Invoice invoice) {
        validateInvoice(invoice);
        int delta = movementSign(invoice.getType()) * invoice.getQty();
        BigDecimal receiptPrice = isReceipt(invoice.getType()) ? invoice.getPrice() : null;
        adjust(invoice.getProduct(), delta, receiptPrice);
    }

    public void updateInvoice(Invoice existing, Invoice replacement) {
        validateInvoice(existing);
        validateInvoice(replacement);

        Integer oldProductId = existing.getProduct().getId();
        Integer newProductId = replacement.getProduct().getId();
        int oldEffect = movementSign(existing.getType()) * existing.getQty();
        int newEffect = movementSign(replacement.getType()) * replacement.getQty();

        if (oldProductId.equals(newProductId)) {
            BigDecimal receiptPrice = isReceipt(replacement.getType()) ? replacement.getPrice() : null;
            adjust(replacement.getProduct(), newEffect - oldEffect, receiptPrice);
            return;
        }

        adjust(existing.getProduct(), -oldEffect, null);
        BigDecimal receiptPrice = isReceipt(replacement.getType()) ? replacement.getPrice() : null;
        adjust(replacement.getProduct(), newEffect, receiptPrice);
    }

    public void reverseInvoice(Invoice invoice) {
        validateInvoice(invoice);
        int reverseDelta = -(movementSign(invoice.getType()) * invoice.getQty());
        adjust(invoice.getProduct(), reverseDelta, null);
    }

    /**
     * Rebuilds derived stock quantities from active warehouse receipts/issues.
     * This is intended for repairing legacy data created by the previous delta logic.
     */
    public int reconcileFromActiveInvoices() {
        Map<String, Object> activeParam = Map.of("activeFlag", 1);
        List<Invoice> invoices = invoiceDAO.findAll(
                " and model.activeFlag = :activeFlag",
                activeParam,
                null
        );
        List<ProductInStock> stocks = productInStockDAO.findAll(
                " and model.activeFlag = :activeFlag",
                activeParam,
                null
        );

        Map<Integer, Integer> expectedQty = new HashMap<>();
        Map<Integer, ProductInfo> products = new HashMap<>();
        Map<Integer, Invoice> latestReceipts = new HashMap<>();

        for (Invoice invoice : invoices) {
            validateInvoice(invoice);
            Integer productId = invoice.getProduct().getId();
            products.put(productId, invoice.getProduct());
            expectedQty.merge(productId, movementSign(invoice.getType()) * invoice.getQty(), Integer::sum);

            if (isReceipt(invoice.getType())) {
                Invoice currentLatest = latestReceipts.get(productId);
                if (currentLatest == null || isAfter(invoice.getUpdateDate(), currentLatest.getUpdateDate())) {
                    latestReceipts.put(productId, invoice);
                }
            }
        }

        for (Map.Entry<Integer, Integer> entry : expectedQty.entrySet()) {
            if (entry.getValue() < 0) {
                ProductInfo product = products.get(entry.getKey());
                throw insufficientStock(product, -entry.getValue(), 0);
            }
        }

        Map<Integer, ProductInStock> canonicalStocks = new HashMap<>();
        int updatedRows = 0;
        for (ProductInStock stock : stocks) {
            Integer productId = stock.getProduct().getId();
            ProductInStock canonical = canonicalStocks.putIfAbsent(productId, stock);
            if (canonical != null) {
                stock.setActiveFlag(0);
                stock.setUpdateDate(Instant.now());
                productInStockDAO.update(stock);
                updatedRows++;
                continue;
            }

            int expected = expectedQty.getOrDefault(productId, 0);
            stock.setQty(expected);
            Invoice latestReceipt = latestReceipts.get(productId);
            if (latestReceipt != null) {
                stock.setPrice(latestReceipt.getPrice());
            }
            stock.setUpdateDate(Instant.now());
            productInStockDAO.update(stock);
            updatedRows++;
            expectedQty.remove(productId);
        }

        for (Map.Entry<Integer, Integer> entry : expectedQty.entrySet()) {
            ProductInfo product = products.get(entry.getKey());
            Invoice latestReceipt = latestReceipts.get(entry.getKey());
            ProductInStock stock = newStock(
                    product,
                    entry.getValue(),
                    latestReceipt == null ? BigDecimal.ZERO : latestReceipt.getPrice()
            );
            productInStockDAO.save(stock);
            updatedRows++;
        }
        return updatedRows;
    }

    // Retained for compatibility with older callers.
    public void SaveorUpdate(Invoice invoice) {
        applyInvoice(invoice);
    }

    private void adjust(ProductInfo product, int delta, BigDecimal receiptPrice) {
        ProductInStock stock = findActiveStock(product.getId());
        if (stock == null) {
            if (delta < 0) {
                throw insufficientStock(product, -delta, 0);
            }
            stock = newStock(product, 0, receiptPrice == null ? BigDecimal.ZERO : receiptPrice);
        }

        int currentQty = stock.getQty() == null ? 0 : stock.getQty();
        int newQty = currentQty + delta;
        if (newQty < 0) {
            throw insufficientStock(product, -delta, currentQty);
        }

        stock.setQty(newQty);
        if (receiptPrice != null) {
            stock.setPrice(receiptPrice);
        }
        stock.setUpdateDate(Instant.now());

        if (stock.getId() == null) {
            productInStockDAO.save(stock);
        } else {
            productInStockDAO.update(stock);
        }
        logger.info("Adjusted stock for product " + product.getCode() + ": " + currentQty + " -> " + newQty);
    }

    private ProductInStock findActiveStock(Integer productId) {
        List<ProductInStock> rows = productInStockDAO.findAll(
                " and model.product.id = :productId and model.activeFlag = :activeFlag",
                Map.of("productId", productId, "activeFlag", 1),
                null
        );
        return rows == null || rows.isEmpty() ? null : rows.get(0);
    }

    private ProductInStock newStock(ProductInfo product, int qty, BigDecimal price) {
        ProductInStock stock = new ProductInStock();
        stock.setProduct(product);
        stock.setQty(qty);
        stock.setPrice(price == null ? BigDecimal.ZERO : price);
        stock.setActiveFlag(1);
        stock.setCreateDate(new Date().toInstant());
        stock.setUpdateDate(new Date().toInstant());
        return stock;
    }

    private void validateInvoice(Invoice invoice) {
        if (invoice == null || invoice.getProduct() == null || invoice.getProduct().getId() == null) {
            throw new IllegalArgumentException("Phiếu kho thiếu thông tin sản phẩm.");
        }
        if (invoice.getType() == null
                || (!isReceipt(invoice.getType()) && !Integer.valueOf(constant.MSG_GOODS_ISSUES).equals(invoice.getType()))) {
            throw new IllegalArgumentException("Loại phiếu kho không hợp lệ.");
        }
        if (invoice.getQty() == null || invoice.getQty() <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0.");
        }
    }

    private int movementSign(Integer type) {
        return isReceipt(type) ? 1 : -1;
    }

    private boolean isReceipt(Integer type) {
        return Integer.valueOf(constant.MSG_GOODS_RECIEPT).equals(type);
    }

    private boolean isAfter(Instant candidate, Instant current) {
        if (candidate == null) {
            return false;
        }
        return current == null || candidate.isAfter(current);
    }

    private IllegalStateException insufficientStock(ProductInfo product, int requested, int available) {
        String productName = product == null ? "sản phẩm" : product.getCode() + " - " + product.getName();
        return new IllegalStateException(
                "Không đủ tồn kho cho " + productName
                        + ". Yêu cầu: " + requested
                        + ", hiện có: " + available + "."
        );
    }
}
