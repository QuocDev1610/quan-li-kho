package inventory.validate;


import inventory.dao.entity.User;
import inventory.service.UserService;
import inventory.utils.HashingPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


import java.util.List;

@Component
public class loginValidator implements Validator {
    @Autowired
    private UserService userService;
    @Override// du lieu gui toi co phai trong bang user k
    public boolean supports(Class<?> clazz) {
        return User.class.isAssignableFrom(clazz);//kiem tra lop truyen vao

    }
    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;

        // 1. Kiểm tra rỗng (Có lỗi là return DỪNG LẠI luôn)
        if (user.getUserName() == null || user.getUserName().isEmpty()) {
            errors.rejectValue("userName", "userName.empty", "Vui lòng nhập tài khoản.");
            return;
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            errors.rejectValue("password", "password.empty", "Vui lòng nhập mật khẩu.");
            return;
        }


        List<User> users = userService.FindByProperty("userName", user.getUserName());

        if (users == null || users.isEmpty()) {
            errors.rejectValue("userName", "userName.notfound", "Tài khoản không tồn tại trong hệ thống.");
            return;}
User userinDB=users.get(0);

            if (!user.getPassword().equals(userinDB.getPassword())) {
                errors.rejectValue("password", "password.invalid", "Mật khẩu không chính xác.");
                return;
            }


            if (userinDB.getActiveFlag() == 0) {
                errors.rejectValue("userName", "userName.locked", "Tài khoản này đã bị khóa hoặc nhân viên đã nghỉ việc.");
            }
        }}



