package inventory.validate;

import inventory.dao.entity.Category;
import inventory.dao.entity.Invoice;
import inventory.service.InvoiceService;
import inventory.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
import java.util.List;
@Component
public class InvoiceValidator implements Validator {
    @Autowired
    private InvoiceService InvoiceService;
    @Override
    public boolean supports(Class<?> aClass) {
        return Invoice.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Invoice invoice = (Invoice) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "code", "msg.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qty", "msg.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "price", "msg.required");

        if(invoice.getCode()!=null && !invoice.getCode().isEmpty()){
            List<Invoice> results= InvoiceService.FindByProperties(invoice.getCode());
            if(results!=null && !results.isEmpty()) {
                Invoice existingInvoice = results.get(0);
                if (invoice.getId() == null || !invoice.getId().equals(existingInvoice.getId())) {
                    errors.rejectValue("code", "msg.code.exist");
                }
            }}if (invoice.getQty() <= 0) {
                errors.rejectValue("qty", "msg.wrong.format");
            }
            if (invoice.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                errors.rejectValue("price", "msg.wrong.format");
            }
            if (invoice.getFromdate() != null && invoice.getTodate() != null) {
                if (invoice.getFromdate().after(invoice.getTodate())) {
                    errors.rejectValue("fromDate", "msg.wrong.date");
                }
            }
        }
}
