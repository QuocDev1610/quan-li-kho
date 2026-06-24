package inventory.controller;

import inventory.api.ApiMapper;
import inventory.api.ApiResponse;
import inventory.api.PageResponse;
import inventory.api.dto.HistoryDto;
import inventory.dao.entity.History;
import inventory.model.paging;
import inventory.service.HistoryService;
import inventory.utils.constant;
import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/histories")
public class HistoryController {
    private static final Logger logger = Logger.getLogger(HistoryController.class);
    private final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<HistoryDto>>> list(
            @ModelAttribute History search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "4") int size) {
        logger.info("Getting history list");
        paging paging = new paging(size);
        paging.setCurrentPage(page);
        List<History> histories = historyService.findAll(search, paging);
        return ResponseEntity.ok(ApiResponse.ok(new PageResponse<>(ApiMapper.toHistoryDtoList(histories), paging)));
    }

    @GetMapping("/types")
    public ResponseEntity<ApiResponse<Map<Integer, String>>> types() {
        Map<Integer, String> params = new HashMap<>();
        params.put(constant.MSG_GOODS_RECIEPT, "Goods Receipt");
        params.put(constant.MSG_GOODS_ISSUES, "Goods Issue");
        params.put(constant.MSG_GET_ALL, "All");
        return ResponseEntity.ok(ApiResponse.ok(params));
    }
}
