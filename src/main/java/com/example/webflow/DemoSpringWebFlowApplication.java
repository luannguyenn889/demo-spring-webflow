package com.example.webflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Lớp khởi động chính của ứng dụng Spring Boot.
 * 
 * @SpringBootApplication bao gồm:
 * - @Configuration: đánh dấu là lớp cấu hình
 * - @EnableAutoConfiguration: tự động cấu hình Spring Boot
 * - @ComponentScan: quét các component trong package hiện tại và con
 */
@SpringBootApplication
public class DemoSpringWebFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoSpringWebFlowApplication.class, args);
    }
}
