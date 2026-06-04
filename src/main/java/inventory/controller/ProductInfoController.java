package inventory.controller;

import inventory.dao.entity.Category;
import inventory.dao.entity.ProductInfo;
import inventory.model.paging;
import inventory.service.ProductService;
import inventory.utils.constant;
import inventory.validate.ProductInfoValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.*;

@Controller
public class ProductInfoController {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductInfoValidator productInfoValidator;
    private static final Logger logger = Logger.getLogger(ProductInfoController.class);
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        if (binder.getTarget() != null &&
                binder.getTarget().getClass() == inventory.dao.entity.ProductInfo.class) {
            binder.addValidators(productInfoValidator);
        }
    }
    @RequestMapping(value={"/product-info/list","product-info/list/"})
    public String redirect(){
        return "redirect:/product-info/list/1";

    }
    @RequestMapping(value="/product-info/list/{page}")
    public String ProductInfoList(Model model, HttpSession session, @ModelAttribute("searchForm") ProductInfo ProductInfo1, @PathVariable("page") int page ) {
        logger.info("Getting ProductInfo list");
        paging paging= new paging(4);
        paging.setCurrentPage(page);
        model.addAttribute("products", productService.FindAllProduct(ProductInfo1,paging));
        if (session.getAttribute(constant.MSG_SUCCESS) != null) {
            model.addAttribute(constant.MSG_SUCCESS,session.getAttribute(constant.MSG_SUCCESS));
            session.removeAttribute(constant.MSG_SUCCESS);
        } if (session.getAttribute(constant.MSG_ERROR) != null) {
            model.addAttribute(constant.MSG_ERROR,session.getAttribute(constant.MSG_ERROR));
            session.removeAttribute(constant.MSG_ERROR);
        }  model.addAttribute("paging", paging);
        return "ProductInfo-List";
    }
    @GetMapping("/product-info/add")
    public String ProductInfoAdd(Model model) {
        logger.info("Getting ProductInfo add");
        model.addAttribute("model", new ProductInfo());
        List<Category> categoryList = productService.FindAll(null,null);
        Map<String, String> map = new HashMap<>();
        Set<String> seenNames = new HashSet<>();

        for (Category category : categoryList) {
            String categoryName = category.getName();

            if (!seenNames.contains(categoryName)) {
                seenNames.add(categoryName);
                map.put(String.valueOf(category.getId()), categoryName);
            }
        }

        model.addAttribute("categoryList", map);
        model.addAttribute("titleForm", "Add ProductInfo");
        model.addAttribute("viewOnly",false);
        return "product-action";
    }
    @GetMapping("product-info/edit/{id}")
    public String ProductInfoEdit(Model model, @PathVariable int id) {
        logger.info("Getting ProductInfo edit form for ID: " + id);


        ProductInfo ProductInfo = productService.FindByIdProduct(id);


        if (ProductInfo == null) {
            return "redirect:/product-info/list";
        }
        List<Category> categoryList=productService.FindAll(null,null);
        Map<String,String> map=new HashMap<>();
        for(Category category:categoryList){
            map.put(String.valueOf(category.getId()),category.getName());
        }
        model.addAttribute("categoryList",map);
        model.addAttribute("titleForm", "Edit ProductInfo");
        model.addAttribute("viewOnly", false);

        model.addAttribute("model", ProductInfo);
        model.addAttribute("Viewonly", false);

        return "product-action";
    }
    @GetMapping("product-info/view/{id}")
    public String ProductInfoView(Model model, @PathVariable int id) {
        logger.info("Getting ProductInfo view");
        ProductInfo ProductInfo = productService.FindByIdProduct(id);
        if (ProductInfo == null) {
            return "redirect:/product-info/list";
        }
        model.addAttribute("titleForm", "view ProductInfo");
        model.addAttribute("model", ProductInfo);
        model.addAttribute("Viewonly", true);
        return "ProductInfo-List";
    }
    @PostMapping("product-info/save")
    public String ProductInfoSave(Model springModel, @Valid @ModelAttribute("model") ProductInfo ProductInfo, BindingResult bindingResult, HttpSession session) {
        if(bindingResult.hasErrors()) {
            if(ProductInfo.getId()!=null) {
                springModel.addAttribute("titleForm", "edit ProductInfo");
            }
            else springModel.addAttribute("titleForm", "add ProductInfo");
            List<Category> categoryList = productService.FindAll(null,null);
            Map<String, String> map = new HashMap<>();
            Set<String> seenNames = new HashSet<>();

            for (Category category : categoryList) {
                String categoryName = category.getName();

                if (!seenNames.contains(categoryName)) {
                    seenNames.add(categoryName);
                    map.put(String.valueOf(category.getId()), categoryName);
                }
            }

            springModel.addAttribute("categoryList", map);
            springModel.addAttribute("model", ProductInfo);
            springModel.addAttribute("Viewonly", false);
            return "product-action";
        }
        if(ProductInfo.getId() != null && ProductInfo.getId() != 0) {
            try {
                productService.Update(ProductInfo);
                session.setAttribute(constant.MSG_SUCCESS, "Update success!!!");
            } catch (Exception e) {
                logger.info("Error updating ProductInfo: " + e.getMessage());
                session.setAttribute(constant.MSG_ERROR, "Error updating ProductInfo !!!");
            }
        } else {
            // Trường hợp INSERT (Thêm mới)
            try {
                productService.saveProductInfo(ProductInfo);
                session.setAttribute(constant.MSG_SUCCESS, "Insert success!!!");
            } catch (Exception e) {
                logger.info("Error updating ProductInfo: " + e.getMessage());
                session.setAttribute(constant.MSG_ERROR, "Error Inserting ProductInfo !!!");
            }}
        return "redirect:/product-info/list";


    }
    @GetMapping("product-info/delete/{id}")
    public String ProductInfoDelete(HttpSession session, @PathVariable int id) {
        logger.info("Getting ProductInfo delete");
        ProductInfo ProductInfo = productService.FindByIdProduct(id);
        if (ProductInfo == null) {
            return "redirect:/product-info/list";
        }
        try {
            productService.delete(ProductInfo);
            session.setAttribute(constant.MSG_SUCCESS, "Delete success!!!");
        } catch (Exception e) {
            logger.info("Error deleting ProductInfo: " + e.getMessage());
            session.setAttribute(constant.MSG_ERROR, "Error deleting ProductInfo !!!");
        }

        return "redirect:/product-info/list";
    }
    @GetMapping("/api/check-code")
    @ResponseBody
    public ResponseEntity<?> checkCategory(@RequestParam String code) {
        java.util.List<ProductInfo> results = productService.FindByCodeProduct(code);
        if (results != null && !results.isEmpty()) {
            return ResponseEntity.ok(results.get(0));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chưa tồn tại");
    }

}
