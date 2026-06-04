package inventory.controller;

import inventory.dao.entity.Category;
import inventory.model.paging;
import inventory.service.ProductService;
import inventory.utils.constant;
import inventory.validate.CategoryValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class CategoryController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryValidator categoryValidator;
    private static final Logger logger = Logger.getLogger(CategoryController.class);
@InitBinder
    protected void initBinder(WebDataBinder binder) {
        if (binder.getTarget() != null && 
            binder.getTarget().getClass() == inventory.dao.entity.Category.class) {
            binder.addValidators(categoryValidator);
        }
    }
    @RequestMapping(value={"/category/list","category/list/"})
    public String redirect(){
    return "redirect:/category/list/1";

    }
   @RequestMapping(value="/category/list/{page}")
    public String categoryList(Model model,HttpSession session,@ModelAttribute("searchForm") Category category1,@PathVariable("page") int page ) {
        logger.info("Getting category list");
        paging paging= new paging(4);
        paging.setCurrentPage(page);

        model.addAttribute("categories", productService.FindAll(category1,paging));
        if (session.getAttribute(constant.MSG_SUCCESS) != null) {
            model.addAttribute(constant.MSG_SUCCESS,session.getAttribute(constant.MSG_SUCCESS));
            session.removeAttribute(constant.MSG_SUCCESS);
        } if (session.getAttribute(constant.MSG_ERROR) != null) {
            model.addAttribute(constant.MSG_ERROR,session.getAttribute(constant.MSG_ERROR));
            session.removeAttribute(constant.MSG_ERROR);
        }  model.addAttribute("paging", paging);
        return "category-list";
    }
@GetMapping("/category/add")
public String categoryAdd(Model model) {
    logger.info("Getting category add");
    model.addAttribute("model", new Category());
    model.addAttribute("titleForm", "Add Category");
    model.addAttribute("Viewonly",false);
    return "category-action";
}
    @GetMapping("category/edit/{id}")
    public String categoryEdit(Model model, @PathVariable int id) {
        logger.info("Getting category edit form for ID: " + id);


        Category category = productService.FindById(id);


        if (category == null) {
            return "redirect:/category/list";
        }

        model.addAttribute("titleForm", "Edit Category");


        model.addAttribute("model", category);
        model.addAttribute("Viewonly", false);

        return "category-action";
    }
@GetMapping("category/view/{id}")
    public String categoryView(Model model, @PathVariable int id) {
    logger.info("Getting category view");
    Category category = productService.FindById(id);
    if (category == null) {
        return "redirect:/category/list";
    }
    model.addAttribute("titleForm", "view Category");
    model.addAttribute("model", category);
    model.addAttribute("Viewonly", true);
    return "category-action";
}
@PostMapping("category/save")
    public String categorySave(Model springModel, @Valid @ModelAttribute("model") Category category, BindingResult bindingResult, HttpSession session) {
  if(bindingResult.hasErrors()) {
      if(category.getId()!=null) {
          springModel.addAttribute("titleForm", "edit Category");
      }
      else springModel.addAttribute("titleForm", "add Category");
      springModel.addAttribute("model", category);
      springModel.addAttribute("Viewonly", false);
      return "category-action";
}
    if(category.getId() != null && category.getId() != 0) {
        try {
            productService.Update(category);
            session.setAttribute(constant.MSG_SUCCESS, "Update success!!!");
        } catch (Exception e) {
     logger.info("Error updating category: " + e.getMessage());
     session.setAttribute(constant.MSG_ERROR, "Error updating category !!!");
        }
    } else {
        // Trường hợp INSERT (Thêm mới)
        try {
            productService.save(category);
            session.setAttribute(constant.MSG_SUCCESS, "Insert success!!!");
        } catch (Exception e) {
            logger.info("Error updating category: " + e.getMessage());
            session.setAttribute(constant.MSG_ERROR, "Error Inserting category !!!");
    }}
    return "redirect:/category/list";


}
@GetMapping("category/delete/{id}")
    public String categoryDelete(HttpSession session, @PathVariable int id) {
    logger.info("Getting category delete");
    Category category = productService.FindById(id);
    if (category == null) {
        return "redirect:/category/list";
    }
    try {
        productService.delete(category);
        session.setAttribute(constant.MSG_SUCCESS, "Delete success!!!");
    } catch (Exception e) {
        logger.info("Error deleting category: " + e.getMessage());
        session.setAttribute(constant.MSG_ERROR, "Error deleting category !!!");
    }

    return "redirect:/category/list";
}
    @GetMapping("/api/category/check")
    @ResponseBody
    public ResponseEntity<?> checkCategory(@RequestParam String code) {
        java.util.List<Category> results = productService.FindByCode(code);
       if (results != null && !results.isEmpty()) {
          return ResponseEntity.ok(results.get(0));
      }
       return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chưa tồn tại");
  }

}
