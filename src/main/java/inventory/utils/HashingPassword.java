package inventory.utils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
public class HashingPassword {
    private static final PasswordEncoder encoder = new BCryptPasswordEncoder();//thuat toan bam

    // 1. Dùng khi đăng ký tài khoản mới (Lưu chuỗi này vào Database)
    // Nó sẽ tự động sinh một chuỗi Salt ngẫu nhiên gắn thẳng vào kết quả
    public static String hashPassword(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    // 2. Dùng khi người dùng Đăng nhập
    public static boolean checkPassword(String rawPassword, String hashedPasswordFromDB) {
        return encoder.matches(rawPassword, hashedPasswordFromDB);
    }

}
