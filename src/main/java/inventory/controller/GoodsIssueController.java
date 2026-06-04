package inventory.controller;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@Controller
public class GoodsIssueController {
         @Autowired
        private GoodsReceiptReport GoodsReceiptReport;
        @Autowired
        private InvoiceService InvoiceService;
        @Autowired
        private ProductService ProductService;
        @Autowired
        private InvoiceValidator InvoiceValidator;
        private static final Logger logger = Logger.getLogger(inventory.controller.GoodsIssueController.class);
        @InitBinder
        protected void initBinder(WebDataBinder binder) {
            if (binder.getTarget() != null &&
                    binder.getTarget().getClass() == Invoice.class) {
                binder.addValidators(InvoiceValidator);
            }
        }
        @RequestMapping(value={"/goods-issue/list","/goods-issue/list/"})
        public String redirect(){
            return "redirect:/goods-issue/list/1";

        }
        @RequestMapping(value="/goods-issue/list/{page}")
        public String GoodsIssueList(Model model, HttpSession session, @ModelAttribute("searchForm") Invoice invoice, @PathVariable("page") int page ) {
            logger.info("Getting GoodsIssue list");
            paging paging= new paging(4);
            paging.setCurrentPage(page);
            if(invoice==null){invoice=new Invoice(); }
            invoice.setType(constant.MSG_GOODS_ISSUES);
            model.addAttribute("invoices", InvoiceService.FindAllProduct(invoice,paging));
            if (session.getAttribute(constant.MSG_SUCCESS) != null) {
                model.addAttribute(constant.MSG_SUCCESS,session.getAttribute(constant.MSG_SUCCESS));
                session.removeAttribute(constant.MSG_SUCCESS);
            } if (session.getAttribute(constant.MSG_ERROR) != null) {
                model.addAttribute(constant.MSG_ERROR,session.getAttribute(constant.MSG_ERROR));
                session.removeAttribute(constant.MSG_ERROR);
            }  model.addAttribute("paging", paging);
            return "GoodsIssue-list";
        }
        @GetMapping("/goods-issue/add")
        public String GoodsIssueAdd(Model model) {
            logger.info("Getting Invoice add");
            model.addAttribute("model", new Invoice());
            model.addAttribute("mapProduct", mapProduct());
            model.addAttribute("titleForm", "Add Invoice");
            model.addAttribute("Viewonly",false);
            return "GoodsIssue-action";
        }
        @GetMapping("/goods-issue/edit/{id}")
        public String GoodsIssueEdit(Model model, @PathVariable int id) {
            logger.info("Getting Issue edit form for ID: " + id);


            Invoice GoodsIssue =InvoiceService.FindByIdProduct(id);


            if (GoodsIssue == null) {
                return "redirect:/goods-issue/list";
            }

            model.addAttribute("titleForm", "Edit Invoice!");
            model.addAttribute("mapProduct", mapProduct());
            model.addAttribute("model", GoodsIssue);
            model.addAttribute("Viewonly", false);

            return "GoodsIssue-action";
        }
        @GetMapping("/goods-issue/view/{id}")
        public String GoodsIssueView(Model model, @PathVariable int id) {
            logger.info("Getting GoodsIssue view");
            Invoice GoodsIssue = InvoiceService.FindByIdProduct(id);
            if (GoodsIssue == null) {
                return "redirect:/goods-issue/list";
            }
            model.addAttribute("titleForm", "view Invoice!");
            model.addAttribute("model", GoodsIssue);
            model.addAttribute("Viewonly", true);
            return "GoodsIssue-action";
        }
        @PostMapping("/goods-issue/save")
        public String GoodsIssueSave(Model springModel, @Valid @ModelAttribute("model") Invoice Invoice, BindingResult bindingResult, HttpSession session) {
            if(bindingResult.hasErrors()) {
                if(Invoice.getId()!=null) {
                    springModel.addAttribute("titleForm", "edit Issues!");
                }
                else springModel.addAttribute("titleForm", "add Issues!");
                springModel.addAttribute("mapProduct", mapProduct());
                springModel.addAttribute("model", Invoice);
                springModel.addAttribute("Viewonly", false);
                return "GoodsIssue-action";
            }     Invoice.setType(constant.MSG_GOODS_ISSUES);
            if(Invoice.getId() != null && Invoice.getId() != 0) {
                try {
                    InvoiceService.Update(Invoice);
                    session.setAttribute(constant.MSG_SUCCESS, "Update success!!!");
                } catch (Exception e) {
                    logger.info("Error updating GoodsIssue: " + e.getMessage());
                    session.setAttribute(constant.MSG_ERROR, "Error updating GoodsIssue !!!");
                }
            } else {
                // Trường hợp INSERT (Thêm mới)
                try {
                    InvoiceService.saveInvoice(Invoice);
                    session.setAttribute(constant.MSG_SUCCESS, "Insert success!!!");
                } catch (Exception e) {
                    logger.info("Error updating GoodsIssue: " + e.getMessage());
                    session.setAttribute(constant.MSG_ERROR, "Error Inserting GoodsIssue !!!");
                }}
            return "redirect:/goods-issue/list";


        }
        @GetMapping("/goods-issue/delete/{id}")
        public String GoodsIssueDelete(HttpSession session, @PathVariable int id) {
            logger.info("Getting GoodsIssue delete");
            Invoice GoodsIssue = InvoiceService.FindByIdProduct(id);
            if (GoodsIssue == null) {
                return "redirect:/goods-issue/list";
            }
            try {
                InvoiceService.delete(GoodsIssue);
                session.setAttribute(constant.MSG_SUCCESS, "Delete success!!!");
            } catch (Exception e) {
                logger.info("Error deleting GoodsIssue: " + e.getMessage());
                session.setAttribute(constant.MSG_ERROR, "Error deleting GoodsIssue !!!");
            }

            return "redirect:/goods-issue/list";
        }
        @GetMapping("/api/goods-issue/check")
        @ResponseBody
        public ResponseEntity<?> checkGoodsIssue(@RequestParam String code) {
            java.util.List<Invoice> results = InvoiceService.FindByProperties(code);
            if (results != null && !results.isEmpty()) {
                return ResponseEntity.ok(results.get(0));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chưa tồn tại");
        }
        @GetMapping("/goods-issue/export")
        public void exportReport(HttpServletResponse response) {
            Invoice invoice= new Invoice();
            invoice.setType(constant.MSG_GOODS_ISSUES);
            List<Invoice> invoiceList = InvoiceService.FindAllProduct(invoice, null);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");//khai báo định dạng file excell .xlsx
            // Tạo tên file đính kèm giờ giấc hiện tại

            String currentDateTime = DateUtils.DateToString(new Date());
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=PhieuXuatKho_" + currentDateTime + ".xlsx";
            response.setHeader(headerKey, headerValue);

            try {
               GoodsReceiptReport.exportInvoicesToExcel(invoiceList, response);

            } catch (IOException e) {

                e.printStackTrace();

                try {
                    response.reset();
                    response.sendRedirect("/goods-issue/list?error=export_failed");
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

