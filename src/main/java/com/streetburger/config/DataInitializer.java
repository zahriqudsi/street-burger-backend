package com.streetburger.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.streetburger.model.User;
import com.streetburger.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String adminPhone = "0000000000";

        Optional<User> existingAdmin = userRepository.findByPhoneNumber(adminPhone);
        if (existingAdmin.isPresent()) {
            User admin = existingAdmin.get();
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
            System.out.println("Default admin user password verified/reset: " + adminPhone + " / admin123");
        } else {
            User admin = new User();
            admin.setPhoneNumber(adminPhone);
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setName("System Admin");
            admin.setEmail("admin@streetburger.com");
            admin.setRole(User.Role.ADMIN);

            userRepository.save(admin);
            System.out.println("Default admin user created: " + adminPhone + " / admin123");
        }
    }
}
