package inventory.controller;

import inventory.api.ApiMapper;
import inventory.api.ApiResponse;
import inventory.api.PageResponse;
import inventory.api.ValidationErrorResponse;
import inventory.api.dto.RoleDto;
import inventory.dao.entity.Role;
import inventory.model.paging;
import inventory.service.RoleService;
import inventory.validate.RoleValiDator;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
    private static final Logger logger = Logger.getLogger(RoleController.class);
    private final RoleService roleService;
    private final RoleValiDator roleValidator;

    public RoleController(RoleService roleService, RoleValiDator roleValidator) {
        this.roleService = roleService;
        this.roleValidator = roleValidator;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        if (binder.getTarget() != null && binder.getTarget().getClass() == Role.class) {
            binder.addValidators(roleValidator);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<RoleDto>>> list(
            @ModelAttribute Role search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "4") int size) {
        paging paging = new paging(size);
        paging.setCurrentPage(page);
        List<Role> roles = roleService.findAll(search, paging);
        return ResponseEntity.ok(ApiResponse.ok(new PageResponse<>(ApiMapper.toRoleDtoList(roles), paging)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleDto>> getById(@PathVariable int id) {
        Role role = findRole(id);
        if (role == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Role not found"));
        }
        return ResponseEntity.ok(ApiResponse.ok(ApiMapper.toRoleDto(role)));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Role role, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validation(bindingResult);
        }
        try {
            roleService.SaveRole(role);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Insert success", ApiMapper.toRoleDto(role)));
        } catch (Exception ex) {
            logger.error("Error inserting role", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Error inserting role"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @Valid @RequestBody Role role, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validation(bindingResult);
        }
        if (findRole(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Role not found"));
        }
        role.setId(id);
        try {
            roleService.UpdateRole(role);
            return ResponseEntity.ok(ApiResponse.ok("Update success", ApiMapper.toRoleDto(role)));
        } catch (Exception ex) {
            logger.error("Error updating role", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Error updating role"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable int id) {
        Role role = findRole(id);
        if (role == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Role not found"));
        }
        try {
            roleService.DeleteRole(role);
            return ResponseEntity.ok(ApiResponse.ok("Delete success", null));
        } catch (Exception ex) {
            logger.error("Error deleting role", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Error deleting role"));
        }
    }

    @GetMapping("/check")
    public ResponseEntity<ApiResponse<RoleDto>> checkRole(@RequestParam String code) {
        List<Role> results = roleService.FindByProperty("roleName", code);
        if (results != null && !results.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.ok(ApiMapper.toRoleDto(results.get(0))));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Chua ton tai"));
    }

    private Role findRole(int id) {
        try {
            List<Role> roles = roleService.FindByProperty("id", id);
            return roles == null || roles.isEmpty() ? null : roles.get(0);
        } catch (Exception ex) {
            return null;
        }
    }

    private ResponseEntity<ValidationErrorResponse> validation(BindingResult bindingResult) {
        return ResponseEntity.badRequest().body(new ValidationErrorResponse("Validation failed", ApiMapper.validationErrors(bindingResult)));
    }
}
