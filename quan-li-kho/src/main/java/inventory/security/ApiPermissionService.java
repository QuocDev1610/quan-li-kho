package inventory.security;

import inventory.dao.entity.Auth;
import inventory.dao.entity.User;
import inventory.dao.entity.UserRole;
import inventory.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApiPermissionService {
    private static final Map<String, String> API_TO_MENU_URL = new LinkedHashMap<>();
    private final UserService userService;

    public ApiPermissionService(UserService userService) {
        this.userService = userService;
    }

    static {
        API_TO_MENU_URL.put("/api/categories", "/category/list");
        API_TO_MENU_URL.put("/api/products", "/product-info/list");
        API_TO_MENU_URL.put("/api/product-in-stocks", "/product-in-stock/list");
        API_TO_MENU_URL.put("/api/goods-receipts", "/goods-receipt/list");
        API_TO_MENU_URL.put("/api/goods-issues", "/goods-issue/list");
        API_TO_MENU_URL.put("/api/users", "/user/list");
        API_TO_MENU_URL.put("/api/roles", "/role/list");
        API_TO_MENU_URL.put("/api/menus", "/menu/list");
        API_TO_MENU_URL.put("/api/histories", "/history/list");
    }

    @Transactional(readOnly = true)
    public boolean hasPermission(String userName, String servletPath) {
        if (userName == null || servletPath == null) {
            return false;
        }
        if (servletPath.startsWith("/api/auth") || servletPath.startsWith("/api/dashboard")) {
            return true;
        }
        String menuUrl = resolveMenuUrl(servletPath);
        if (menuUrl == null) {
            return servletPath.startsWith("/api/");
        }
        List<User> users = userService.FindByProperty("userName", userName);
        if (users == null || users.isEmpty()) {
            return false;
        }
        User user = users.get(0);
        if (user.getUserRoles() == null || user.getUserRoles().isEmpty()) {
            return false;
        }
        for (UserRole userRole : user.getUserRoles()) {
            if (userRole.getRole() == null || userRole.getRole().getAuths() == null) {
                continue;
            }
            for (Auth auth : userRole.getRole().getAuths()) {
                boolean allowed = auth.getMenu() != null
                        && auth.getMenu().getUrl() != null
                        && menuUrl.startsWith(auth.getMenu().getUrl())
                        && Integer.valueOf(1).equals(auth.getPermission())
                        && Integer.valueOf(1).equals(auth.getActiveFlag())
                        && Integer.valueOf(1).equals(auth.getMenu().getActiveFlag());
                if (allowed) {
                    return true;
                }
            }
        }
        return false;
    }

    private String resolveMenuUrl(String servletPath) {
        for (Map.Entry<String, String> entry : API_TO_MENU_URL.entrySet()) {
            if (servletPath.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
}
