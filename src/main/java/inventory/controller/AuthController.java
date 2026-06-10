package inventory.controller;

import inventory.api.ApiMapper;
import inventory.api.ApiResponse;
import inventory.api.dto.MenuDto;
import inventory.dao.entity.Auth;
import inventory.dao.entity.Menu;
import inventory.dao.entity.Role;
import inventory.dao.entity.User;
import inventory.dao.entity.UserRole;
import inventory.security.JwtTokenProvider;
import inventory.security.LoginRequest;
import inventory.security.LoginResponse;
import inventory.service.UserService;
import inventory.utils.HashingPassword;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        if (request.getUserName() == null || request.getUserName().trim().isEmpty()
                || request.getPassword() == null || request.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Vui long nhap tai khoan va mat khau."));
        }

        List<User> users = userService.FindByProperty("userName", request.getUserName());
        if (users == null || users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Tai khoan khong ton tai."));
        }

        User user = users.get(0);
        if (!Integer.valueOf(1).equals(user.getActiveFlag())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Tai khoan da bi khoa."));
        }

        if (!passwordMatches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Mat khau khong chinh xac."));
        }

        UserRole userRole = firstUserRole(user);
        Role role = userRole == null ? null : userRole.getRole();
        String roleName = role == null ? "USER" : role.getRoleName();
        String token = jwtTokenProvider.generateToken(user, roleName);

        LoginResponse response = new LoginResponse();
        response.setAccessToken(token);
        response.setExpiresInSeconds(jwtTokenProvider.getExpirationSeconds());
        response.setUser(ApiMapper.toUserDto(user));
        response.setMenus(buildMenu(role));
        return ResponseEntity.ok(ApiResponse.ok("Login success", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok(ApiResponse.ok("Logout success. Client can remove the token.", null));
    }

    private boolean passwordMatches(String rawPassword, String storedPassword) {
        if (storedPassword == null) {
            return false;
        }
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
            return HashingPassword.checkPassword(rawPassword, storedPassword);
        }
        return rawPassword.equals(storedPassword);
    }

    private UserRole firstUserRole(User user) {
        if (user.getUserRoles() == null || user.getUserRoles().isEmpty()) {
            return null;
        }
        return user.getUserRoles().iterator().next();
    }

    private List<MenuDto> buildMenu(Role role) {
        List<Menu> parents = new ArrayList<>();
        List<Menu> children = new ArrayList<>();
        if (role == null || role.getAuths() == null) {
            return ApiMapper.toMenuDtoList(parents);
        }
        Set<Auth> auths = role.getAuths();
        for (Auth auth : auths) {
            Menu menu = auth.getMenu();
            boolean valid = menu != null
                    && menu.getOrderIndex() != null
                    && menu.getOrderIndex() != -1
                    && Integer.valueOf(1).equals(menu.getActiveFlag())
                    && Integer.valueOf(1).equals(auth.getPermission())
                    && Integer.valueOf(1).equals(auth.getActiveFlag());
            if (valid) {
                if (Integer.valueOf(0).equals(menu.getParentId())) {
                    parents.add(menu);
                } else {
                    children.add(menu);
                }
            }
        }
        parents.sort((m1, m2) -> m1.getOrderIndex().compareTo(m2.getOrderIndex()));
        children.sort((m1, m2) -> m1.getOrderIndex().compareTo(m2.getOrderIndex()));
        for (Menu parent : parents) {
            List<Menu> childList = new ArrayList<>();
            for (Menu child : children) {
                if (child.getParentId().equals(parent.getId())) {
                    childList.add(child);
                }
            }
            parent.setChildren(childList);
        }
        return ApiMapper.toMenuDtoList(parents);
    }
}
