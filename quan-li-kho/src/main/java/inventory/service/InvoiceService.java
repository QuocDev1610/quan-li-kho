package inventory.service;

import inventory.dao.InvoiceDAO;
import inventory.dao.ProductinStockDAO;
import inventory.dao.entity.Invoice;

import inventory.model.paging;
import inventory.utils.constant;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Transactional
@Service
public class InvoiceService {
    @Autowired
    private InvoiceDAO invoiceDAO;
    @Autowired
  private HistoryService HistoryService;
    @Autowired
  private ProductinStockService ProductinStockService;
    Logger  logger = Logger.getLogger(InvoiceService.class);
    public void saveInvoice(Invoice invoice) throws Exception {
        logger.info("Insert Invoice: " + invoice);
        invoice.setCreateDate(new Date().toInstant());
        invoice.setActiveFlag(1);
        invoice.setUpdateDate(new Date().toInstant());
        invoiceDAO.save(invoice);
        HistoryService.Save(invoice, constant.ACTION_ADD);
        ProductinStockService.applyInvoice(invoice);

    }
    public void Update(Invoice invoice) throws Exception {
        logger.info("Update invoice: " + invoice);
        Invoice existing = invoiceDAO.findById(Invoice.class, invoice.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Invoice not found");
        }

        ProductinStockService.updateInvoice(existing, invoice);
        existing.setCode(invoice.getCode());
        existing.setType(invoice.getType());
        existing.setProduct(invoice.getProduct());
        existing.setQty(invoice.getQty());
        existing.setPrice(invoice.getPrice());
        existing.setUpdateDate(new Date().toInstant());

        HistoryService.Save(existing, constant.ACTION_EDIT);
        invoiceDAO.update(existing);
    }
    public void delete(Invoice invoice) throws Exception {
        logger.info("Delete invoice: " + invoice);
        if (!Integer.valueOf(1).equals(invoice.getActiveFlag())) {
            throw new IllegalStateException("Phiếu kho đã được xóa trước đó.");
        }
        ProductinStockService.reverseInvoice(invoice);
        invoice.setActiveFlag(0);
        invoice.setUpdateDate(new Date().toInstant());
        HistoryService.Save(invoice, constant.ACTION_DELETE);
        invoiceDAO.update(invoice);
    }
    public List<Invoice> FindByProperties(String code) {
        logger.info("Finding invoice: " + code);
        return invoiceDAO.findByProperty("code", code);

    }
    public List<Invoice> FindAllProduct(Invoice invoice, paging paging) {
        logger.info("Finding all invoices");
        StringBuffer query = new StringBuffer();
        Map<String, Object> params = new java.util.HashMap<>();
        query.append(" and model.activeFlag = :activeFlag");
        params.put("activeFlag", 1);
        if(invoice != null){
            if (invoice.getType()!=null && invoice.getType()!=0){
                query.append(" and model.type=:type");
                params.put("type",invoice.getType());
            }
            if (invoice.getCode() != null && !invoice.getCode().isEmpty()) {
                query.append(" and model.code = :code");
                params.put("code", invoice.getCode());
            }

            if (invoice.getFromdate() != null) {
                query.append(" and model.updateDate >= :fromdate ");
                params.put("fromdate", invoice.getFromdate());
            }
            if (invoice.getTodate() != null) {
                query.append(" and model.updateDate <= :todate ");
                params.put("todate", invoice.getTodate());
            }
        }
        return invoiceDAO.findAll(query.toString(),params,paging);
    }
    public Invoice FindByIdProduct(int id) {
        logger.info("Finding invoice: " + id);
        return invoiceDAO.findByProperty("id", id).get(0);
    }
}
