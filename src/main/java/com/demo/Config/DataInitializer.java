package com.demo.config;

import com.demo.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {
    
    @Autowired
    private UserService userService;
    
    @Bean
    public ApplicationRunner initDatabase() {
        return args -> {
            userService.initAdminUser();
        };
    }
}
