package com.cathalob.medtracker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableMethodSecurity
public class MedTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedTrackerApplication.class, args);
    }

    @Value("${website.origin}")
    private String websiteOrigin;
    @Value("${website.localOrigin}")
    private String websiteLocalOrigin;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins(
                                websiteOrigin,
                                websiteLocalOrigin);
            }
        };
    }
}
