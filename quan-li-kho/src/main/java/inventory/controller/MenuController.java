package inventory.controller;

import inventory.api.ApiMapper;
import inventory.api.ApiResponse;
import inventory.api.PageResponse;
import inventory.api.dto.MenuDto;
import inventory.api.dto.RoleDto;
import inventory.dao.entity.Auth;
import inventory.dao.entity.Menu;
import inventory.dao.entity.Role;
import inventory.model.paging;
import inventory.service.MenuService;
import inventory.service.RoleService;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
@Transactional
@RequestMapping("/api/menus")
public class MenuController {
    private static final Logger logger = Logger.getLogger(MenuController.class);
    private final MenuService menuService;
    private final RoleService roleService;

    public MenuController(MenuService menuService, RoleService roleService) {
        this.menuService = menuService;
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> list(
            @ModelAttribute Menu search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size) {
        logger.info("Getting menu list");
        paging paging = new paging(size);
        paging.setCurrentPage(page);
        List<Menu> menus = menuService.getListMenu(paging, search);
        List<Role> roles = roleService.findAll(null, null);
        Collections.sort(roles, (o1, o2) -> o1.getId() - o2.getId());
        for (Menu item : menus) {
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
        Map<String, Object> data = new HashMap<>();
        data.put("menus", new PageResponse<>(ApiMapper.toMenuDtoList(menus), paging));
        data.put("roles", ApiMapper.toRoleDtoList(roles));
        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<MenuDto>> changeStatus(@PathVariable int id) {
        Menu menu = menuService.findById(id);
        if (menu == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Menu not found"));
        }
        menu.setActiveFlag(Integer.valueOf(1).equals(menu.getActiveFlag()) ? 0 : 1);
        menuService.changeMenu(menu);
        return ResponseEntity.ok(ApiResponse.ok("Menu status changed successfully", ApiMapper.toMenuDto(menu)));
    }
}
