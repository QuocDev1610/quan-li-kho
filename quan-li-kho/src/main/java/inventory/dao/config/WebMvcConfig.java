package inventory.dao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

//    @Bean
//    public InternalResourceViewResolver viewResolver() {
//        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
//        resolver.setViewClass(JstlView.class);
//        // Lưu ý: ViewResolver của JSP bắt buộc phải bắt đầu bằng dấu "/", KHÔNG được dùng "classpath:"
//        resolver.setPrefix("/WEB-INF/views/");
//        resolver.setSuffix(".jsp");
//        resolver.setOrder(0);
//        return resolver;
//    }
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // Giao diện cũ
    registry.addResourceHandler("/resources/**", "/static/**", "/build/**", "/css/**", "/images/**", "/js/**", "/vendors/**")
            .addResourceLocations("classpath:/static/");

    // Giao diện Sneat mới
    registry.addResourceHandler("/assets/**")
            .addResourceLocations("classpath:/static/assets/");
}}