package inventory.controller;

import inventory.dao.RoleDAO;
import inventory.dao.entity.Role;
import inventory.model.paging;
import inventory.service.RoleService;
import inventory.utils.constant;
import inventory.validate.RoleValiDator;
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
public class RoleController {

        @Autowired
        private RoleService RoleService;
        @Autowired
        private RoleValiDator RoleValidator;
        @Autowired
        private RoleDAO RoleDAO;
        private static final Logger logger = Logger.getLogger(inventory.controller.RoleController.class);
        @InitBinder
        protected void initBinder(WebDataBinder binder) {
            if (binder.getTarget() != null &&
                    binder.getTarget().getClass() == Role.class) {
                binder.addValidators(RoleValidator);
            }
        }
        @RequestMapping(value={"/role/list","/role/list/"})
        public String redirect(){
            return "redirect:/role/list/1";

        }
        @RequestMapping(value="/role/list/{page}")
        public String RoleList(Model model, HttpSession session, @ModelAttribute("searchForm") Role Role, @PathVariable("page") int page ) {
            logger.info("Getting Role list");
            paging paging= new paging(4);
            paging.setCurrentPage(page);
            List<Role> x=RoleService.findAll(Role,paging);
            model.addAttribute("Roles", x);
            if (session.getAttribute(constant.MSG_SUCCESS) != null) {
                model.addAttribute(constant.MSG_SUCCESS,session.getAttribute(constant.MSG_SUCCESS));
                session.removeAttribute(constant.MSG_SUCCESS);
            } if (session.getAttribute(constant.MSG_ERROR) != null) {
                model.addAttribute(constant.MSG_ERROR,session.getAttribute(constant.MSG_ERROR));
                session.removeAttribute(constant.MSG_ERROR);
            }  model.addAttribute("paging", paging);
            return "Role-list";
        }
        @GetMapping("/role/add")
        public String RoleAdd(Model model) {
            logger.info("Getting Role add");
            model.addAttribute("model", new Role());
            model.addAttribute("titleForm", "Add Role");
            model.addAttribute("Viewonly",false);
            return "Role-action";
        }
        @GetMapping("/role/edit/{id}")
        public String RoleEdit(Model model, @PathVariable int id) {
            logger.info("Getting Role edit form for ID: " + id);


            Role Role =RoleService.FindByProperty("id", id).get(0);


            if (Role == null) {
                return "redirect:/role/list";
            }
            model.addAttribute("titleForm", "Edit Role!");
            model.addAttribute("model", Role);
            model.addAttribute("Viewonly", false);

            return "Role-action";
        }
        @GetMapping("/role/view/{id}")
        public String RoleView(Model model, @PathVariable int id) {
            logger.info("Getting Role view");
            Role Role = RoleService.FindByProperty("id",id).get(0);
            if (Role == null) {
                return "redirect:/role/list";
            }
            model.addAttribute("titleForm", "view Role!");
            model.addAttribute("model", Role);
            model.addAttribute("Viewonly", true);
            return "Role-action";
        }
        @PostMapping("/role/save")
        public Object RoleSave(Model springModel, @Valid @ModelAttribute("model") Role Role, BindingResult bindingResult, HttpSession session) {
            if(bindingResult.hasErrors()) {
                if(Role.getId()!=null) {
                    springModel.addAttribute("titleForm", "edit Roles!");
                }
                else springModel.addAttribute("titleForm", "add Roles!");
                springModel.addAttribute("model", Role);
                springModel.addAttribute("Viewonly", false);
                return "Role-action";
            }
            if(Role.getId() != null && Role.getId() != 0) {
                try {
                    RoleService.UpdateRole(Role);
                    session.setAttribute(constant.MSG_SUCCESS, "Update success!!!");
                } catch (Exception e) {
                    logger.info("Error updating Role: " + e.getMessage());
                    session.setAttribute(constant.MSG_ERROR, "Error updating Role !!!");
                }
            } else {
                // Trường hợp INSERT (Thêm mới)
                try {
                    RoleService.SaveRole(Role);
                    session.setAttribute(constant.MSG_SUCCESS, "Insert success!!!");
                } catch (Exception e) {
                    logger.info("Error updating Role: " + e.getMessage());
                    session.setAttribute(constant.MSG_ERROR, "Error Inserting Role !!!");
                }}
            return new org.springframework.http.ResponseEntity<>("redirect:/role/list", org.springframework.http.HttpStatus.OK);


        }
        @GetMapping("/role/delete/{id}")
        public String RoleDelete(HttpSession session, @PathVariable int id) {
            logger.info("Getting Role delete");
            Role Role = RoleService.FindByProperty("id",id).get(0);
            if (Role == null) {
                return "redirect:/role/list";
            }
            try {
                RoleService.DeleteRole(Role);
                session.setAttribute(constant.MSG_SUCCESS, "Delete success!!!");
            } catch (Exception e) {
                logger.info("Error deleting Role: " + e.getMessage());
                session.setAttribute(constant.MSG_ERROR, "Error deleting Role !!!");
            }

            return "redirect:/role/list";
        }
        @GetMapping("/api/role/check")
        @ResponseBody
        public ResponseEntity<?> checkRole(@RequestParam String code) {
            java.util.List<Role> results = RoleService.FindByProperty("Rolename",code);
            if (results != null && !results.isEmpty()) {
                return ResponseEntity.ok(results.get(0));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chưa tồn tại");
        }


}
