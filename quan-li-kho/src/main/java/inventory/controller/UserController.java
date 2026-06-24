package inventory.controller;

import inventory.api.ApiMapper;
import inventory.api.ApiResponse;
import inventory.api.PageResponse;
import inventory.api.ValidationErrorResponse;
import inventory.api.dto.RoleDto;
import inventory.api.dto.UserDto;
import inventory.dao.entity.Role;
import inventory.dao.entity.User;
import inventory.model.paging;
import inventory.service.RoleService;
import inventory.service.UserService;
import inventory.validate.UserValidator;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = Logger.getLogger(UserController.class);
    private final UserService userService;
    private final RoleService roleService;
    private final UserValidator userValidator;

    public UserController(UserService userService, RoleService roleService, UserValidator userValidator) {
        this.userService = userService;
        this.roleService = roleService;
        this.userValidator = userValidator;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        if (binder.getTarget() != null && binder.getTarget().getClass() == User.class) {
            binder.addValidators(userValidator);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserDto>>> list(
            @ModelAttribute User search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "4") int size) {
        paging paging = new paging(size);
        paging.setCurrentPage(page);
        List<User> users = userService.findAll(search, paging);
        return ResponseEntity.ok(ApiResponse.ok(new PageResponse<>(ApiMapper.toUserDtoList(users), paging)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getById(@PathVariable int id) {
        User user = findUser(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("User not found"));
        }
        return ResponseEntity.ok(ApiResponse.ok(ApiMapper.toUserDto(user)));
    }

    @GetMapping("/form-options")
    public ResponseEntity<ApiResponse<Map<String, List<RoleDto>>>> formOptions() {
        Map<String, List<RoleDto>> data = new HashMap<>();
        data.put("roles", ApiMapper.toRoleDtoList(roleService.findAll(null, null)));
        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validation(bindingResult);
        }
        try {
            userService.SaveUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Insert success", ApiMapper.toUserDto(user)));
        } catch (Exception ex) {
            logger.error("Error inserting user", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Error inserting user"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @Valid @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validation(bindingResult);
        }
        if (findUser(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("User not found"));
        }
        user.setId(id);
        try {
            userService.UpdateUser(user);
            return ResponseEntity.ok(ApiResponse.ok("Update success", ApiMapper.toUserDto(findUser(id))));
        } catch (Exception ex) {
            logger.error("Error updating user", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Error updating user"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable int id) {
        User user = findUser(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("User not found"));
        }
        try {
            userService.DeleteUser(user);
            return ResponseEntity.ok(ApiResponse.ok("Delete success", null));
        } catch (Exception ex) {
            logger.error("Error deleting user", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Error deleting user"));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<UserDto>> changeStatus(@PathVariable int id) {
        User user = findUser(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("User not found"));
        }
        try {
            User updated = userService.ChangeStatus(user);
            return ResponseEntity.ok(ApiResponse.ok("User status changed successfully", ApiMapper.toUserDto(updated)));
        } catch (Exception ex) {
            logger.error("Error changing user status", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Error changing user status"));
        }
    }

    @GetMapping("/check")
    public ResponseEntity<ApiResponse<UserDto>> checkUser(@RequestParam String code) {
        List<User> results = userService.FindByProperty("userName", code);
        if (results != null && !results.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.ok(ApiMapper.toUserDto(results.get(0))));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Chua ton tai"));
    }

    private User findUser(int id) {
        try {
            List<User> users = userService.FindByProperty("id", id);
            return users == null || users.isEmpty() ? null : users.get(0);
        } catch (Exception ex) {
            return null;
        }
    }

    private ResponseEntity<ValidationErrorResponse> validation(BindingResult bindingResult) {
        return ResponseEntity.badRequest().body(new ValidationErrorResponse("Validation failed", ApiMapper.validationErrors(bindingResult)));
    }
}
