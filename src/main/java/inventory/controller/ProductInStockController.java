package inventory.controller;

import inventory.dao.entity.ProductInStock;
import inventory.model.paging;
import inventory.service.ProductinStockService;
import inventory.validate.ProductInfoValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
@Controller
public class ProductInStockController {

    @Autowired
    private ProductinStockService productService;
    @Autowired
    private ProductInfoValidator productInfoValidator;
    private static final Logger logger = Logger.getLogger(ProductInStockController.class);

    @RequestMapping(value={"/product-in-stock/list","product-in-stock/list/"})
    public String redirect(){
        return "redirect:/product-in-stock/list/1";

    }
    @RequestMapping(value="/product-in-stock/list/{page}")
    public String ProductInfoList(Model model, HttpSession session, @ModelAttribute("searchForm") ProductInStock ProductInfo1, @PathVariable("page") int page ) {
        logger.info("Getting ProductInStock list");
        paging paging= new paging(4);
        paging.setCurrentPage(page);
        model.addAttribute("products", productService.findAllProduct(ProductInfo1,paging));
        model.addAttribute("paging", paging);
        return "product-in-stock";
    }



}


