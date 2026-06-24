package inventory.controller;

import inventory.api.ApiMapper;
import inventory.api.ApiResponse;
import inventory.api.PageResponse;
import inventory.api.ValidationErrorResponse;
import inventory.api.dto.CategoryDto;
import inventory.api.dto.ProductDto;
import inventory.dao.entity.Category;
import inventory.dao.entity.ProductInfo;
import inventory.model.paging;
import inventory.service.ProductService;
import inventory.validate.ProductInfoValidator;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductInfoController {
    private static final Logger logger = Logger.getLogger(ProductInfoController.class);
    private final ProductService productService;
    private final ProductInfoValidator productInfoValidator;

    public ProductInfoController(ProductService productService, ProductInfoValidator productInfoValidator) {
        this.productService = productService;
        this.productInfoValidator = productInfoValidator;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        if (binder.getTarget() != null && binder.getTarget().getClass() == ProductInfo.class) {
            binder.addValidators(productInfoValidator);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductDto>>> list(
            @ModelAttribute ProductInfo search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "4") int size) {
        paging paging = new paging(size);
        paging.setCurrentPage(page);
        List<ProductInfo> products = productService.FindAllProduct(search, paging);
        return ResponseEntity.ok(ApiResponse.ok(new PageResponse<>(ApiMapper.toProductDtoList(products), paging)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> getById(@PathVariable int id) {
        ProductInfo product = findProduct(id);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Product not found"));
        }
        return ResponseEntity.ok(ApiResponse.ok(ApiMapper.toProductDto(product)));
    }

    @GetMapping("/form-options")
    public ResponseEntity<ApiResponse<Map<String, List<CategoryDto>>>> formOptions() {
        Map<String, List<CategoryDto>> data = new HashMap<>();
        data.put("categories", ApiMapper.toCategoryDtoList(productService.FindAll(null, null)));
        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ProductInfo product, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validation(bindingResult);
        }
        try {
            productService.saveProductInfo(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Insert success", ApiMapper.toProductDto(product)));
        } catch (Exception ex) {
            logger.error("Error inserting product", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Error inserting product"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @Valid @RequestBody ProductInfo product, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validation(bindingResult);
        }
        ProductInfo existing = findProduct(id);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Product not found"));
        }
        product.setId(id);
        if (product.getImgUrl() == null) {
            product.setImgUrl(existing.getImgUrl());
        }
        try {
            productService.Update(product);
            return ResponseEntity.ok(ApiResponse.ok("Update success", ApiMapper.toProductDto(product)));
        } catch (Exception ex) {
            logger.error("Error updating product", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Error updating product"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable int id) {
        ProductInfo product = findProduct(id);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Product not found"));
        }
        try {
            productService.delete(product);
            return ResponseEntity.ok(ApiResponse.ok("Delete success", null));
        } catch (Exception ex) {
            logger.error("Error deleting product", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Error deleting product"));
        }
    }

    @GetMapping("/check")
    public ResponseEntity<ApiResponse<ProductDto>> checkProduct(@RequestParam String code) {
        List<ProductInfo> results = productService.FindByCodeProduct(code);
        if (results != null && !results.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.ok(ApiMapper.toProductDto(results.get(0))));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Chua ton tai"));
    }

    private ProductInfo findProduct(int id) {
        try {
            return productService.FindByIdProduct(id);
        } catch (Exception ex) {
            return null;
        }
    }

    private ResponseEntity<ValidationErrorResponse> validation(BindingResult bindingResult) {
        return ResponseEntity.badRequest().body(new ValidationErrorResponse("Validation failed", ApiMapper.validationErrors(bindingResult)));
    }
}
