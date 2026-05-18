package com.example.webflow.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Cấu hình Spring MVC.
 * 
 * Đăng ký các view controller đơn giản (không cần logic xử lý)
 * để ánh xạ URL trực tiếp tới view template.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Trang chủ: URL "/" → view "home"
        registry.addViewController("/").setViewName("home");
    }
}
