package inventory.controller;

import inventory.dao.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

@Controller
public class HomeController {

    @GetMapping("/dashboard")
    public String viewSneatDashboard() {
        return "sneat-index";
    }
}

