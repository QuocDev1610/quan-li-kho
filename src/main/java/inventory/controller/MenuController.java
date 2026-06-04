package inventory.controller;

import inventory.dao.MenuDAO;
import inventory.dao.entity.Auth;
import inventory.dao.entity.Menu;
import inventory.dao.entity.Role;
import inventory.dao.entity.User;
import inventory.model.paging;
import inventory.service.MenuService;
import inventory.service.RoleService;
import inventory.utils.constant;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Controller
@Transactional
public class MenuController {
        @Autowired
        private MenuService MenuService;

        @Autowired
        private RoleService RoleService;
        private static final Logger logger = Logger.getLogger(inventory.controller.MenuController.class);
        @RequestMapping(value={"/menu/list","/menu/list/"})
        public String redirect(){
            return "redirect:/menu/list/1";

        }
        @RequestMapping(value="/menu/list/{page}")
        public String MenuList(Model model, HttpSession session, @ModelAttribute("searchForm") Menu Menu, @PathVariable("page") int page ) {
            logger.info("Getting Menu list");
            paging paging= new paging(12);
            paging.setCurrentPage(page);
            List<Menu> x=MenuService.getListMenu(paging,Menu);
            List<Role> roles=RoleService.findAll(null,null);
            Collections.sort(roles,(o1,o2) -> o1.getId()-o2.getId());
            for (Menu item : x) {
                TreeMap<Integer, Integer> mapAuth = new TreeMap<>();

                for (Role role : roles) {
                    mapAuth.put(role.getId(), 0);
                }
                for (Object obj : item.getAuths()) {
                    Auth auth = (Auth) obj;
                    mapAuth.put(auth.getRole().getId(), auth.getPermission());
                }
                item.setChildrenMap(mapAuth);
            }

            model.addAttribute("Menus", x);
            model.addAttribute("roles", roles);
            if (session.getAttribute(constant.MSG_SUCCESS) != null) {
                model.addAttribute(constant.MSG_SUCCESS,session.getAttribute(constant.MSG_SUCCESS));
                session.removeAttribute(constant.MSG_SUCCESS);
            } if (session.getAttribute(constant.MSG_ERROR) != null) {
                model.addAttribute(constant.MSG_ERROR,session.getAttribute(constant.MSG_ERROR));
                session.removeAttribute(constant.MSG_ERROR);
            }  model.addAttribute("paging", paging);
            return "Menu-list";
        }
    @GetMapping("/menu/change-status/{id}")
    public String change(Model model, @PathVariable int id,HttpSession session) {
        logger.info("Changing Menu status for ID: " + id);
            Menu menu = MenuService.findById( id);
            if (menu != null) {
                menu.setActiveFlag(menu.getActiveFlag() == 1 ? 0 : 1);
                MenuService.changeMenu(menu);
                session.setAttribute(constant.MSG_SUCCESS, "Menu status changed successfully!");
            } else {
                session.setAttribute(constant.MSG_ERROR, "Menu not found!");
            }
        return "redirect:/menu/list";

    }



}
