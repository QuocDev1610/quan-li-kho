package inventory.controller;

import inventory.api.ApiMapper;
import inventory.api.ApiResponse;
import inventory.api.dto.UserDto;
import inventory.dao.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class HomeController {
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> dashboard(Authentication authentication) {
        Map<String, Object> data = new HashMap<>();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            UserDto user = ApiMapper.toUserDto((User) authentication.getPrincipal());
            data.put("user", user);
        }
        data.put("status", "Inventory API is running");
        return ResponseEntity.ok(ApiResponse.ok(data));
    }
}
