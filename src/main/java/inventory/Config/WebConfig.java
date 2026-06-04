package inventory.Config;

import inventory.security.FilterSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration // Báo cho Spring đây là file cấu hình
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private FilterSystem filterSystem; // Gọi " bảo vệ" vào đây

    // 1. CẤU HÌNH BỘ LỌC (INTERCEPTOR)
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(filterSystem)
                .addPathPatterns("/**")  // Bắt buộc kiểm tra TẤT CẢ các đường dẫn
                .excludePathPatterns(    // NGOẠI TRỪ các đường dẫn dưới đây (cho phép đi qua tự do)
                        "/login",        // Trang đăng nhập
                        "/logout",       // Trang đăng xuất
                        "/access-denied",// Trang báo lỗi không có quyền
                        "/assets/**",    // Cho phép tải CSS, JS
                        "/category/add",
                        "/upload/**"     // <--- THÊM DÒNG NÀY ĐỂ KHÔNG CHẶN ẢNH NỮA
                );
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Tự động generate đường dẫn chuẩn đến ổ D
        String uploadPath = Paths.get("D:/upload").toUri().toString();

        registry.addResourceHandler("/upload/**")
                .addResourceLocations(uploadPath);
    }
}