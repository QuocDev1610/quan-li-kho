package inventory.validate;

import inventory.dao.entity.Role;
import inventory.dao.entity.Role;
import inventory.service.RoleService;
import inventory.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.List;
@Component
public class RoleValiDator implements Validator {
    @Autowired
    private RoleService RoleService;
    @Override// du lieu gui toi co phai trong bang Role k
    public boolean supports(Class<?> clazz) {
        return Role.class.isAssignableFrom(clazz);//kiem tra lop truyen vao

    }

    @Override
    public void validate(Object o, Errors errors) {
        Role Role = (Role) o;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "roleName", "msg.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "msg.required");

        if (Role.getRoleName() != null && !Role.getRoleName().trim().isEmpty()) {
            List<Role> Roles = RoleService.FindByProperty("roleName", Role.getRoleName());
            if (Roles != null && !Roles.isEmpty()) {
                if (Role.getId() == null || !Roles.get(0).getId().equals(Role.getId())) {
                    errors.rejectValue("roleName", "msg.code.exist");
                }
            }
        }
    }
}
