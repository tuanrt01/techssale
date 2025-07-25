package com.techzen.techsale.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("OPTIONS", "PUT", "DELETE", "POST", "GET")
                .allowedHeaders("*")  // Thêm dòng này để cho phép tất cả headers
                .allowCredentials(true)
                .allowedOriginPatterns(
                        "http://*.techzen.vn",
                        "https://*.techzen.vn",
                        "http://localhost:3000",
                        "http://localhost:3001",
                        "http://localhost:3002",
                        "http://localhost:3003",
                        "http://localhost:3004",
                        "http://localhost:3006",
                        "http://192.168.2.69:3006",  
                        "http://192.168.2.69:3002"
                );
    }
}

