package inventory.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration // Báo cho Spring đây là file cấu hình
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Tự động generate đường dẫn chuẩn đến ổ D
        String uploadPath = Paths.get("D:/upload").toUri().toString();

        registry.addResourceHandler("/upload/**")
                .addResourceLocations(uploadPath);
    }
}
