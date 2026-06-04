package inventory.controller;


import inventory.dao.entity.Category;
import inventory.dao.entity.Invoice;
import inventory.dao.entity.ProductInfo;
import inventory.model.paging;
import inventory.service.GoodsReceiptReport;
import inventory.service.InvoiceService;
import inventory.service.ProductService;
import inventory.utils.DateUtils;
import inventory.utils.constant;
import inventory.validate.CategoryValidator;
import inventory.validate.InvoiceValidator;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.yaml.snakeyaml.scanner.Constant;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import java.io.IOException;
import java.util.*;



@Controller
public class GoodsReceiptController {
    @Autowired
    private GoodsReceiptReport goodsReceiptReport;
    @Autowired
    private InvoiceService InvoiceService;
    @Autowired
    private ProductService ProductService;
    @Autowired
    private InvoiceValidator InvoiceValidator;
    private static final Logger logger = Logger.getLogger(GoodsReceiptController.class);
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        if (binder.getTarget() != null &&
                binder.getTarget().getClass() == Invoice.class) {
            binder.addValidators(InvoiceValidator);
        }
    }
    @RequestMapping(value={"/goods-receipt/list","/goods-receipt/list/"})
    public String redirect(){
        return "redirect:/goods-receipt/list/1";

    }
    @RequestMapping(value="/goods-receipt/list/{page}")
    public String GoodsReceiptList(Model model, HttpSession session, @ModelAttribute("searchForm") Invoice invoice, @PathVariable("page") int page ) {
        logger.info("Getting GoodsReceipt list");
        paging paging= new paging(4);
        paging.setCurrentPage(page);
if(invoice==null){invoice=new Invoice(); }
invoice.setType(constant.MSG_GOODS_RECIEPT);
        model.addAttribute("invoices", InvoiceService.FindAllProduct(invoice,paging));
        if (session.getAttribute(constant.MSG_SUCCESS) != null) {
            model.addAttribute(constant.MSG_SUCCESS,session.getAttribute(constant.MSG_SUCCESS));
            session.removeAttribute(constant.MSG_SUCCESS);
        } if (session.getAttribute(constant.MSG_ERROR) != null) {
            model.addAttribute(constant.MSG_ERROR,session.getAttribute(constant.MSG_ERROR));
            session.removeAttribute(constant.MSG_ERROR);
        }  model.addAttribute("paging", paging);
        return "GoodsReceipt-list";
    }
    @GetMapping("/goods-receipt/add")
    public String GoodsReceiptAdd(Model model) {
        logger.info("Getting Invoice add");
        model.addAttribute("model", new Invoice());
        model.addAttribute("mapProduct", mapProduct());
        model.addAttribute("titleForm", "Add Invoice");
        model.addAttribute("Viewonly",false);
        return "GoodsReceipt-action";
    }
    @GetMapping("/goods-receipt/edit/{id}")
    public String GoodsReceiptEdit(Model model, @PathVariable int id) {
        logger.info("Getting Invoice edit form for ID: " + id);


        Invoice GoodsReceipt =InvoiceService.FindByIdProduct(id);


        if (GoodsReceipt == null) {
            return "redirect:/goods-receipt/list";
        }

        model.addAttribute("titleForm", "Edit Invoice!");
model.addAttribute("mapProduct", mapProduct());
        model.addAttribute("model", GoodsReceipt);
        model.addAttribute("Viewonly", false);

        return "GoodsReceipt-action";
    }
    @GetMapping("/goods-receipt/view/{id}")
    public String GoodsReceiptView(Model model, @PathVariable int id) {
        logger.info("Getting GoodsReceipt view");
        Invoice GoodsReceipt = InvoiceService.FindByIdProduct(id);
        if (GoodsReceipt == null) {
            return "redirect:/goods-receipt/list";
        }
        model.addAttribute("titleForm", "view Invoice!");
        model.addAttribute("model", GoodsReceipt);
        model.addAttribute("Viewonly", true);
        return "GoodsReceipt-action";
    }
    @PostMapping("/goods-receipt/save")
    public String GoodsReceiptSave(Model springModel, @Valid @ModelAttribute("model") Invoice Invoice, BindingResult bindingResult, HttpSession session) {
        if(bindingResult.hasErrors()) {
            if(Invoice.getId()!=null) {
                springModel.addAttribute("titleForm", "edit Invoice!");
            }
            else springModel.addAttribute("titleForm", "add Invoice!");
            springModel.addAttribute("mapProduct", mapProduct());
            springModel.addAttribute("model", Invoice);
            springModel.addAttribute("Viewonly", false);
            return "GoodsReceipt-action";
        }     Invoice.setType(constant.MSG_GOODS_RECIEPT);
        if(Invoice.getId() != null && Invoice.getId() != 0) {
            try {
                InvoiceService.Update(Invoice);
                session.setAttribute(constant.MSG_SUCCESS, "Update success!!!");
            } catch (Exception e) {
                logger.info("Error updating GoodsReceipt: " + e.getMessage());
                session.setAttribute(constant.MSG_ERROR, "Error updating GoodsReceipt !!!");
            }
        } else {
            // Trường hợp INSERT (Thêm mới)
            try {
                InvoiceService.saveInvoice(Invoice);
                session.setAttribute(constant.MSG_SUCCESS, "Insert success!!!");
            } catch (Exception e) {
                logger.info("Error updating GoodsReceipt: " + e.getMessage());
                session.setAttribute(constant.MSG_ERROR, "Error Inserting GoodsReceipt !!!");
            }}
        return "redirect:/goods-receipt/list";


    }
    @GetMapping("/goods-receipt/delete/{id}")
    public String GoodsReceiptDelete(HttpSession session, @PathVariable int id) {
        logger.info("Getting GoodsReceipt delete");
        Invoice GoodsReceipt = InvoiceService.FindByIdProduct(id);
        if (GoodsReceipt == null) {
            return "redirect:/goods-receipt/list";
        }
        try {
            InvoiceService.delete(GoodsReceipt);
            session.setAttribute(constant.MSG_SUCCESS, "Delete success!!!");
        } catch (Exception e) {
            logger.info("Error deleting GoodsReceipt: " + e.getMessage());
            session.setAttribute(constant.MSG_ERROR, "Error deleting GoodsReceipt !!!");
        }

        return "redirect:/goods-receipt/list";
    }
    @GetMapping("/api/goods-receipt/check")
    @ResponseBody
    public ResponseEntity<?> checkGoodsReceipt(@RequestParam String code) {
        java.util.List<Invoice> results = InvoiceService.FindByProperties(code);
        if (results != null && !results.isEmpty()) {
            return ResponseEntity.ok(results.get(0));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chưa tồn tại");
    }
    @GetMapping("/goods-receipt/export")
    public void exportReport(HttpServletResponse response) {
        Invoice invoice= new Invoice();
        invoice.setType(constant.MSG_GOODS_RECIEPT);
        List<Invoice> invoiceList = InvoiceService.FindAllProduct(invoice, null);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");//khai báo định dạng file excell .xlsx
    // Tạo tên file đính kèm giờ giấc hiện tại

        String currentDateTime = DateUtils.DateToString(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=PhieuNhapKho_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        try {
            goodsReceiptReport.exportInvoicesToExcel(invoiceList, response);

        } catch (IOException e) {

            e.printStackTrace();

            try {
                response.reset();
                response.sendRedirect("/goods-receipt/list?error=export_failed");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
    }}
private Map<String,String> mapProduct() {
    List<ProductInfo> categoryList = ProductService.FindAllProduct(null, null);
    Map<String, String> map = new HashMap<>();
    Set<String> seenNames = new HashSet<>();

    for (ProductInfo ProductInfo : categoryList) {
        String ProductName = ProductInfo.getName();

        if (!seenNames.contains(ProductName)) {
            seenNames.add(ProductName);
            map.put(String.valueOf(ProductInfo.getId()), ProductName);
        }
    } return map;
}
}
