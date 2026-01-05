package com.streetburger.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.streetburger.model.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.streetburger.dto.ApiResponse;
import com.streetburger.dto.SignupRequest;
import com.streetburger.repository.UserRepository;

@RestController
@RequestMapping("/admins")
@Tag(name = "Admin", description = "Admin management (Admin only)")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/registerAdmin")
    @Operation(summary = "Register a new admin user")
    public ResponseEntity<ApiResponse<User>> registerAdmin(@RequestBody SignupRequest request) {
        // Check if phone number already exists
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Phone number already registered"));
        }

        User admin = new User();
        admin.setPhoneNumber(request.getPhoneNumber());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setName(request.getName());
        admin.setEmail(request.getEmail());
        admin.setDateOfBirth(request.getDateOfBirth());
        admin.setRole(User.Role.ADMIN);
        admin.setCreatedAt(LocalDateTime.now());

        User saved = userRepository.save(admin);

        // Don't return password
        saved.setPassword(null);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Admin registered successfully", saved));
    }
}
