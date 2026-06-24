package inventory.validate;

import inventory.dao.entity.User;
import inventory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.List;

@Component
public class UserValidator implements Validator {
    @Autowired
    private UserService userService;
    @Override// du lieu gui toi co phai trong bang user k
    public boolean supports(Class<?> clazz) {
        return User.class.isAssignableFrom(clazz);//kiem tra lop truyen vao

    }

    @Override
    public void validate(Object o, Errors errors) {
        User user = (User) o;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "msg.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName", "msg.required"); // Sửa thành u thường
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "msg.required");

        if(user.getId() == null){
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "msg.required");
        if (user.getUserName() != null && !user.getUserName().trim().isEmpty()) {
            List<User> users = userService.FindByProperty("userName", user.getUserName());
            if (users != null && !users.isEmpty()) {

                    errors.rejectValue("userName", "msg.code.exist");
                }
            }
        }
    }
}
