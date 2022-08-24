package com.app.config;


import com.app.user.User;
import com.app.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class AppConfig {
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserRepository repository;

    @Bean
    public CommandLineRunner commandLineRunner(){
        return args -> {
            List<User> users = List.of(
                    new User("ann", encoder.encode("12345"),"Smith", "Ann" ),
                    new User("tom", encoder.encode("12345"),"John", "Tom" ),
                    new User("john", encoder.encode("12345"),"Doe", "John" )
            );

            repository.saveAll(users);
        };

    }
}
