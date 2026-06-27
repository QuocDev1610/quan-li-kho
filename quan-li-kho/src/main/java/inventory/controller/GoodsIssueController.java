package inventory.controller;

import inventory.api.ApiMapper;
import inventory.api.ApiResponse;
import inventory.api.PageResponse;
import inventory.api.ValidationErrorResponse;
import inventory.api.dto.InvoiceDto;
import inventory.api.dto.ProductDto;
import inventory.api.request.InvoiceRequest;
import inventory.dao.entity.Invoice;
import inventory.dao.entity.ProductInfo;
import inventory.model.paging;
import inventory.service.GoodsReceiptReport;
import inventory.service.InvoiceService;
import inventory.service.ProductService;
import inventory.utils.DateUtils;
import inventory.utils.constant;
import inventory.validate.InvoiceValidator;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goods-issues")
public class GoodsIssueController {
    private static final Logger logger = Logger.getLogger(GoodsIssueController.class);
    private final GoodsReceiptReport goodsReceiptReport;
    private final InvoiceService invoiceService;
    private final ProductService productService;
    private final InvoiceValidator invoiceValidator;

    public GoodsIssueController(GoodsReceiptReport goodsReceiptReport, InvoiceService invoiceService,
                                ProductService productService, InvoiceValidator invoiceValidator) {
        this.goodsReceiptReport = goodsReceiptReport;
        this.invoiceService = invoiceService;
        this.productService = productService;
        this.invoiceValidator = invoiceValidator;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        if (binder.getTarget() != null && binder.getTarget().getClass() == Invoice.class) {
            binder.addValidators(invoiceValidator);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<InvoiceDto>>> list(
            @ModelAttribute Invoice search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "4") int size) {
        paging paging = new paging(size);
        paging.setCurrentPage(page);
        if (search == null) {
            search = new Invoice();
        }
        search.setType(constant.MSG_GOODS_ISSUES);
        List<Invoice> invoices = invoiceService.FindAllProduct(search, paging);
        return ResponseEntity.ok(ApiResponse.ok(new PageResponse<>(ApiMapper.toInvoiceDtoList(invoices), paging)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InvoiceDto>> getById(@PathVariable int id) {
        Invoice invoice = findInvoice(id);
        if (invoice == null || !Integer.valueOf(constant.MSG_GOODS_ISSUES).equals(invoice.getType())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Goods issue not found"));
        }
        return ResponseEntity.ok(ApiResponse.ok(ApiMapper.toInvoiceDto(invoice)));
    }

    @GetMapping("/form-options")
    public ResponseEntity<ApiResponse<Map<String, List<ProductDto>>>> formOptions() {
        Map<String, List<ProductDto>> data = new HashMap<>();
        data.put("products", ApiMapper.toProductDtoList(productService.FindAllProduct(null, null)));
        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody InvoiceRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validation(bindingResult);
        }
        Invoice invoice = toInvoice(request, constant.MSG_GOODS_ISSUES);
        if (invoice.getProduct() == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Sản phẩm không tồn tại."));
        }
        try {
            invoiceService.saveInvoice(invoice);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Insert success", ApiMapper.toInvoiceDto(invoice)));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(ex.getMessage()));
        } catch (Exception ex) {
            logger.error("Error inserting goods issue", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Error inserting goods issue"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @Valid @RequestBody InvoiceRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validation(bindingResult);
        }
        if (findInvoice(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Goods issue not found"));
        }
        Invoice invoice = toInvoice(request, constant.MSG_GOODS_ISSUES);
        if (invoice.getProduct() == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Sản phẩm không tồn tại."));
        }
        invoice.setId(id);
        try {
            invoiceService.Update(invoice);
            return ResponseEntity.ok(ApiResponse.ok("Update success", ApiMapper.toInvoiceDto(invoice)));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(ex.getMessage()));
        } catch (Exception ex) {
            logger.error("Error updating goods issue", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Error updating goods issue"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable int id) {
        Invoice invoice = findInvoice(id);
        if (invoice == null || !Integer.valueOf(constant.MSG_GOODS_ISSUES).equals(invoice.getType())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Goods issue not found"));
        }
        try {
            invoiceService.delete(invoice);
            return ResponseEntity.ok(ApiResponse.ok("Delete success", null));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(ex.getMessage()));
        } catch (Exception ex) {
            logger.error("Error deleting goods issue", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Error deleting goods issue"));
        }
    }

    @GetMapping("/check")
    public ResponseEntity<ApiResponse<InvoiceDto>> checkGoodsIssue(@RequestParam String code) {
        List<Invoice> results = invoiceService.FindByProperties(code);
        if (results != null && !results.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.ok(ApiMapper.toInvoiceDto(results.get(0))));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Chua ton tai"));
    }

    @GetMapping("/export")
    public void exportReport(HttpServletResponse response) throws IOException {
        Invoice invoice = new Invoice();
        invoice.setType(constant.MSG_GOODS_ISSUES);
        List<Invoice> invoiceList = invoiceService.FindAllProduct(invoice, null);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=PhieuXuatKho_" + DateUtils.DateToString(new Date()) + ".xlsx");
        goodsReceiptReport.exportInvoicesToExcel(invoiceList, response);
    }

    private Invoice findInvoice(int id) {
        try {
            return invoiceService.FindByIdProduct(id);
        } catch (Exception ex) {
            return null;
        }
    }

    private Invoice toInvoice(InvoiceRequest request, int type) {
        Invoice invoice = new Invoice();
        invoice.setCode(request.getCode());
        invoice.setQty(request.getQty());
        invoice.setPrice(request.getPrice());
        invoice.setType(type);
        ProductInfo product = findProduct(request.getProductId());
        invoice.setProduct(product);
        return invoice;
    }

    private ProductInfo findProduct(Integer productId) {
        if (productId == null) {
            return null;
        }
        try {
            return productService.FindByIdProduct(productId);
        } catch (Exception ex) {
            return null;
        }
    }

    private ResponseEntity<ValidationErrorResponse> validation(BindingResult bindingResult) {
        return ResponseEntity.badRequest().body(new ValidationErrorResponse("Validation failed", ApiMapper.validationErrors(bindingResult)));
    }
}
