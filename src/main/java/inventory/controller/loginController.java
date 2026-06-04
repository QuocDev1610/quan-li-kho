package inventory.controller;


import inventory.dao.entity.*;
import inventory.utils.constant;
import inventory.validate.loginValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import inventory.service.UserService;
import org.springframework.web.servlet.FlashMapManager;


import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Collections.sort;


@Controller
public class loginController {
    @Autowired
    private UserService userService;
    @Autowired
    private loginValidator loginValidator;
    @Autowired
    private FlashMapManager flashMapManager;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        if (binder.getTarget() == null) {
            return;
        }
        if (binder.getTarget().getClass() == User.class) {
            binder.setValidator(loginValidator);
        }
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        User userObject = new User();
        model.addAttribute("user", userObject);
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@Valid @ModelAttribute("user") User user,
                               BindingResult bindingResult,
                               Model model, HttpSession session) {

        // 1. Kiểm tra xem loginValidator (được gắn ở @InitBinder) có bắt được lỗi nào không?
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));
            return "login";
        }
        User user1 = userService.FindByProperty("userName", user.getUserName()).get(0);
        UserRole userRole = null;


        Set<UserRole> roles = user1.getUserRoles();


        if (roles != null && !roles.isEmpty()) {

            userRole = roles.iterator().next();
        }
        List<Menu> menuList = new ArrayList<>(); // Giỏ chứa Menu Cha
        List<Menu> menuChildList = new ArrayList<>(); // Giỏ chứa Menu Con
        Role role = userRole.getRole();

        if (role != null && role.getAuths() != null) {

            // BƯỚC 1: Sàng lọc và chia giỏ (1 vòng lặp duy nhất)
            for (Auth auth : role.getAuths()) {
                Menu menu = auth.getMenu();

                // Gộp toàn bộ điều kiện bảo mật vào 1 biến boolean cho dễ đọc
                boolean isMenuValid = menu.getOrderIndex() != -1
                        && menu.getActiveFlag() == 1
                        && auth.getPermission() == 1
                        && auth.getActiveFlag() == 1;

                if ( isMenuValid) {
                    // Tạo ID động cho hiệu ứng Collapse trên Sneat
                    menu.setIdMenu(menu.getUrl().replace("/", "") + "Id");
                    if (menu.getParentId() == 0) {
                        menuList.add(menu);
                    } else {
                        menuChildList.add(menu);
                    }
                }
            }

            // BƯỚC 2: Sắp xếp theo thứ tự Order Index (Tránh việc Menu lộn xộn)
            menuList.sort((m1, m2) -> m1.getOrderIndex().compareTo(m2.getOrderIndex()));
            menuChildList.sort((m1, m2) -> m1.getOrderIndex().compareTo(m2.getOrderIndex()));


            for (Menu parent : menuList) {
                List<Menu> childList = new ArrayList<>();
                for (Menu child : menuChildList) {
                    // Ép kiểu hoặc dùng equals để so sánh an toàn
                    if (child.getParentId().equals(parent.getId())) {
                        childList.add(child);
                    }
                }
                parent.setChildren(childList);
            }
        }

session.setAttribute(constant.roleName, role);
        session.setAttribute(constant.MENU_SEASION, menuList);
        session.setAttribute(constant.userInf, user1);
        return "redirect:/dashboard";
    }
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // 1. Xóa thông tin user đã lưu trong session (nếu có)
        session.removeAttribute(constant.userInf);
        session.removeAttribute(constant.roleName);
        session.removeAttribute(constant.MENU_SEASION);
        session.invalidate();

        // 2. Chuyển hướng về trang login
        return "redirect:/login";
    }
}