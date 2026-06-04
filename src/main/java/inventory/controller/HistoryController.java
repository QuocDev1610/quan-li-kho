package inventory.controller;

import inventory.dao.entity.Category;
import inventory.dao.entity.History;
import inventory.dao.entity.ProductInStock;
import inventory.dao.entity.ProductInfo;
import inventory.model.paging;
import inventory.service.HistoryService;
import inventory.service.ProductService;
import inventory.service.ProductinStockService;
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
public class HistoryController {
        @Autowired
        private HistoryService HistoryService;
        @Autowired
        private ProductInfoValidator productInfoValidator;
        private static final Logger logger = Logger.getLogger(inventory.controller.HistoryController.class);

        @RequestMapping(value={"/history/list","history/list/"})
        public String redirect(){
            return "redirect:/history/list/1";

        }
        @RequestMapping(value="/history/list/{page}")
        public String ProductInfoList(Model model, HttpSession session, @ModelAttribute("searchForm") History history, @PathVariable("page") int page ) {
            logger.info("Getting ProductInStock list");
            paging paging= new paging(4);
            paging.setCurrentPage(page);
            Map<String,Object> params = new HashMap<>();
            params.put(String.valueOf(constant.MSG_GOODS_RECIEPT), "Goods Reciept");
            params.put(String.valueOf(constant.MSG_GOODS_ISSUES), "Goods Issues");
            params.put(String.valueOf(constant.MSG_GET_ALL), "All");
            model.addAttribute("params", params);
            model.addAttribute("products", HistoryService.findAll(history,paging));
            model.addAttribute("paging", paging);
            return "history";
        }



    }




