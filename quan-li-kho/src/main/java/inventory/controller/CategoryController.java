package inventory.controller;

import inventory.api.ApiMapper;
import inventory.api.ApiResponse;
import inventory.api.PageResponse;
import inventory.api.ValidationErrorResponse;
import inventory.api.dto.CategoryDto;
import inventory.dao.entity.Category;
import inventory.model.paging;
import inventory.service.ProductService;
import inventory.validate.CategoryValidator;
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

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private static final Logger logger = Logger.getLogger(CategoryController.class);
    private final ProductService productService;
    private final CategoryValidator categoryValidator;

    public CategoryController(ProductService productService, CategoryValidator categoryValidator) {
        this.productService = productService;
        this.categoryValidator = categoryValidator;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        if (binder.getTarget() != null && binder.getTarget().getClass() == Category.class) {
            binder.addValidators(categoryValidator);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CategoryDto>>> list(
            @ModelAttribute Category search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "4") int size) {
        paging paging = new paging(size);
        paging.setCurrentPage(page);
        List<Category> categories = productService.FindAll(search, paging);
        return ResponseEntity.ok(ApiResponse.ok(new PageResponse<>(ApiMapper.toCategoryDtoList(categories), paging)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDto>> getById(@PathVariable int id) {
        Category category = findCategory(id);
        if (category == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Category not found"));
        }
        return ResponseEntity.ok(ApiResponse.ok(ApiMapper.toCategoryDto(category)));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Category category, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validation(bindingResult);
        }
        try {
            productService.save(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Insert success", ApiMapper.toCategoryDto(category)));
        } catch (Exception ex) {
            logger.error("Error inserting category", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Error inserting category"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @Valid @RequestBody Category category, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validation(bindingResult);
        }
        Category existing = findCategory(id);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Category not found"));
        }
        category.setId(id);
        try {
            productService.Update(category);
            return ResponseEntity.ok(ApiResponse.ok("Update success", ApiMapper.toCategoryDto(category)));
        } catch (Exception ex) {
            logger.error("Error updating category", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Error updating category"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable int id) {
        Category category = findCategory(id);
        if (category == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Category not found"));
        }
        try {
            productService.delete(category);
            return ResponseEntity.ok(ApiResponse.ok("Delete success", null));
        } catch (Exception ex) {
            logger.error("Error deleting category", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Error deleting category"));
        }
    }

    @GetMapping("/check")
    public ResponseEntity<ApiResponse<CategoryDto>> checkCategory(@RequestParam String code) {
        List<Category> results = productService.FindByCode(code);
        if (results != null && !results.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.ok(ApiMapper.toCategoryDto(results.get(0))));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Chua ton tai"));
    }

    private Category findCategory(int id) {
        try {
            return productService.FindById(id);
        } catch (Exception ex) {
            return null;
        }
    }

    private ResponseEntity<ValidationErrorResponse> validation(BindingResult bindingResult) {
        return ResponseEntity.badRequest().body(new ValidationErrorResponse("Validation failed", ApiMapper.validationErrors(bindingResult)));
    }
}
