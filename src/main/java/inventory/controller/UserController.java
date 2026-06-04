package inventory.controller;


import inventory.dao.UserDAO;
import inventory.dao.entity.Role;
import inventory.dao.entity.User;
import inventory.model.paging;
import inventory.service.UserService;
import inventory.utils.constant;
import inventory.validate.UserValidator;
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
public class UserController {
        @Autowired
        private UserService userService;
        @Autowired
        private UserValidator UserValidator;
        @Autowired
        private UserDAO UserDAO;
        private static final Logger logger = Logger.getLogger(inventory.controller.UserController.class);
        @InitBinder
        protected void initBinder(WebDataBinder binder) {
            if (binder.getTarget() != null &&
                    binder.getTarget().getClass() == User.class) {
                binder.addValidators(UserValidator);
            }
        }
        @RequestMapping(value={"/user/list","/user/list/"})
        public String redirect(){
            return "redirect:/user/list/1";

        }
        @RequestMapping(value="/user/list/{page}")
        public String UserList(Model model, HttpSession session, @ModelAttribute("searchForm") User user, @PathVariable("page") int page ) {
            logger.info("Getting User list");
            paging paging= new paging(4);
            paging.setCurrentPage(page);
           List<User> x=userService.findAll(user,paging);
            for (User u : x) {

                if (u.getUserRoles() != null && !u.getUserRoles().isEmpty()) {

                    String roleName = u.getUserRoles().iterator().next().getRole().getRoleName();
                    String desc = u.getUserRoles().iterator().next().getRole().getDescription();
                    u.setNameRole(roleName);
                    u.setDescriptionRole(desc);

                } else {
                    u.setNameRole("Chưa phân quyền");
                    u.setDescriptionRole("");
                }
            }
            model.addAttribute("users", x);
            if (session.getAttribute(constant.MSG_SUCCESS) != null) {
                model.addAttribute(constant.MSG_SUCCESS,session.getAttribute(constant.MSG_SUCCESS));
                session.removeAttribute(constant.MSG_SUCCESS);
            } if (session.getAttribute(constant.MSG_ERROR) != null) {
                model.addAttribute(constant.MSG_ERROR,session.getAttribute(constant.MSG_ERROR));
                session.removeAttribute(constant.MSG_ERROR);
            }  model.addAttribute("paging", paging);
            return "User-list";
        }
        @GetMapping("/user/add")
        public String UserAdd(Model model) {
            logger.info("Getting user add");
            model.addAttribute("model", new User());
            model.addAttribute("mapRole", mapProduct());
            model.addAttribute("titleForm", "Add user");
            model.addAttribute("Viewonly",false);
            model.addAttribute("editMode",false);
            return "User-action";
        }
        @GetMapping("/user/edit/{id}")
        public String UserEdit(Model model, @PathVariable int id) {
            logger.info("Getting User edit form for ID: " + id);


            User User =userService.FindByProperty("id", id).get(0);


            if (User == null) {
                return "redirect:/user/list";
            }

            model.addAttribute("titleForm", "Edit user!");
            model.addAttribute("mapRole", mapProduct());
            model.addAttribute("model", User);
            model.addAttribute("Viewonly", false);
            model.addAttribute("editMode",true);

            return "User-action";
        }
        @GetMapping("/user/view/{id}")
        public String UserView(Model model, @PathVariable int id) {
            logger.info("Getting User view");
            User User = userService.FindByProperty("id",id).get(0);
            if (User == null) {
                return "redirect:/user/list";
            }
            model.addAttribute("titleForm", "view user!");
            model.addAttribute("model", User);
            model.addAttribute("Viewonly", true);
            model.addAttribute("editMode",false);
            return "User-action";
        }
        @PostMapping("/user/save")
        public String UserSave(Model springModel, @Valid @ModelAttribute("model") User user, BindingResult bindingResult, HttpSession session) {
            if(bindingResult.hasErrors()) {
                if(user.getId()!=null) {
                    springModel.addAttribute("titleForm", "edit Users!");
                }
                else springModel.addAttribute("titleForm", "add Users!");
                springModel.addAttribute("mapRole", mapProduct());
                springModel.addAttribute("model", user);
                springModel.addAttribute("Viewonly", false);
                return "User-action";
            }
            if(user.getId() != null && user.getId() != 0) {
                try {
                    userService.UpdateUser(user);
                    session.setAttribute(constant.MSG_SUCCESS, "Update success!!!");
                } catch (Exception e) {
                    logger.info("Error updating User: " + e.getMessage());
                    session.setAttribute(constant.MSG_ERROR, "Error updating User !!!");
                }
            } else {
                // Trường hợp INSERT (Thêm mới)
                try {
                    userService.SaveUser(user);
                    session.setAttribute(constant.MSG_SUCCESS, "Insert success!!!");
                } catch (Exception e) {
                    logger.info("Error updating User: " + e.getMessage());
                    session.setAttribute(constant.MSG_ERROR, "Error Inserting User !!!");
                }}
            return "redirect:/user/list";


        }
        @GetMapping("/user/delete/{id}")
        public String UserDelete(HttpSession session, @PathVariable int id) {
            logger.info("Getting User delete");
            User User = userService.FindByProperty("id",id).get(0);
            if (User == null) {
                return "redirect:/user/list";
            }
            try {
                userService.DeleteUser(User);
                session.setAttribute(constant.MSG_SUCCESS, "Delete success!!!");
            } catch (Exception e) {
                logger.info("Error deleting User: " + e.getMessage());
                session.setAttribute(constant.MSG_ERROR, "Error deleting User !!!");
            }

            return "redirect:/user/list";
        }
        @GetMapping("/api/user/check")
        @ResponseBody
        public ResponseEntity<?> checkUser(@RequestParam String code) {
            java.util.List<User> results = userService.FindByProperty("username",code);
            if (results != null && !results.isEmpty()) {
                return ResponseEntity.ok(results.get(0));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chưa tồn tại");
        }

        private Map<String,String> mapProduct() {
            List<User> users = userService.findAll(null, null);
            Map<String, String> map = new HashMap<>();
            Set<String> role = new HashSet<>();

            for (User user : users) {
                String roleName = user.getUserRoles().iterator().next().getRole().getRoleName();

                if (!role.contains(roleName)) {
                    role.add(roleName);
                    map.put(String.valueOf(user.getUserRoles().iterator().next().getId()), roleName);
                }
            } return map;
        }
    }


