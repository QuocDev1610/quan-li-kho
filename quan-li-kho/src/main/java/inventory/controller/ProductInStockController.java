package inventory.controller;

import inventory.api.ApiMapper;
import inventory.api.ApiResponse;
import inventory.api.PageResponse;
import inventory.api.dto.ProductInStockDto;
import inventory.dao.entity.ProductInStock;
import inventory.model.paging;
import inventory.service.ProductinStockService;
import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/product-in-stocks")
public class ProductInStockController {
    private static final Logger logger = Logger.getLogger(ProductInStockController.class);
    private final ProductinStockService productService;

    public ProductInStockController(ProductinStockService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductInStockDto>>> list(
            @ModelAttribute ProductInStock search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "4") int size) {
        logger.info("Getting ProductInStock list");
        paging paging = new paging(size);
        paging.setCurrentPage(page);
        List<ProductInStock> products = productService.findAllProduct(search, paging);
        return ResponseEntity.ok(ApiResponse.ok(new PageResponse<>(ApiMapper.toProductInStockDtoList(products), paging)));
    }

    @PostMapping("/reconcile")
    public ResponseEntity<ApiResponse<Integer>> reconcile() {
        int updatedRows = productService.reconcileFromActiveInvoices();
        return ResponseEntity.ok(ApiResponse.ok(
                "Đã đồng bộ tồn kho từ các phiếu nhập/xuất đang hoạt động.",
                updatedRows
        ));
    }
}
