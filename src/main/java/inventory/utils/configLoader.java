package inventory.utils;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class configLoader {
    // 1. Tạo instance duy nhất
    private static configLoader instance = null;
    private Properties properties;

    // 2. Private constructor để ngăn các class khác gọi 'new ConfigLoader()'
    private configLoader() {
        properties = new Properties();
        // Load file config.properties từ thư mục resources
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Xin lỗi, không tìm thấy file config.properties");
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // 3. Phương thức static để lấy instance
    public static configLoader getInstance() {
        if (instance == null) {
            instance = new configLoader();
        }
        return instance;
    }

    // 4. Phương thức để lấy giá trị ra (Ví dụ hàm get mà bạn đang tìm)
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}