package com.streetburger.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.streetburger.dto.*;
import com.streetburger.model.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.streetburger.dto.ApiResponse;
import com.streetburger.dto.AuthResponse;
import com.streetburger.dto.LoginRequest;
import com.streetburger.dto.SignupRequest;
import com.streetburger.dto.UpdateUserRequest;
import com.streetburger.dto.UserDTO;
import com.streetburger.repository.UserRepository;
import com.streetburger.security.JwtUtil;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "User authentication and account management")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/signup")
    @Operation(summary = "Register a new user")
    public ResponseEntity<ApiResponse<AuthResponse>> signup(@RequestBody SignupRequest request) {
        // Check if phone number already exists
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Phone number already registered"));
        }

        // Create new user
        User user = new User();
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setRole(User.Role.USER);
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        // Generate token
        String token = jwtUtil.generateToken(user.getPhoneNumber(), user.getRole().name());

        AuthResponse response = new AuthResponse(token, user.getPhoneNumber(), user.getName(), user.getRole().name());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Login with phone number and password")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        return userRepository.findByPhoneNumber(request.getPhoneNumber())
                .map(user -> {
                    System.out.println("Login attempt for: " + request.getPhoneNumber());
                    boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());
                    System.out.println("Password match result: " + matches);

                    if (matches) {
                        String token = jwtUtil.generateToken(user.getPhoneNumber(), user.getRole().name());
                        AuthResponse response = new AuthResponse(token, user.getPhoneNumber(), user.getName(),
                                user.getRole().name());
                        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
                    } else {
                        System.out.println("Invalid password for: " + request.getPhoneNumber());
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(ApiResponse.<AuthResponse>error("Invalid password"));
                    }
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("User not found")));
    }

    @GetMapping("/generatetoken/{phoneNumber}")
    @Operation(summary = "Generate JWT token for a phone number")
    public ResponseEntity<ApiResponse<String>> generateToken(@PathVariable String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .map(user -> {
                    String token = jwtUtil.generateToken(user.getPhoneNumber(), user.getRole().name());
                    return ResponseEntity.ok(ApiResponse.success("Token generated", token));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("User not found")));
    }

    @PutMapping("/update/account")
    @Operation(summary = "Update user account")
    public ResponseEntity<ApiResponse<UserDTO>> updateAccount(
            @AuthenticationPrincipal User currentUser,
            @RequestBody UpdateUserRequest request) {

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User not authenticated"));
        }

        if (request.getName() != null) {
            currentUser.setName(request.getName());
        }
        if (request.getEmail() != null) {
            currentUser.setEmail(request.getEmail());
        }
        if (request.getDateOfBirth() != null) {
            currentUser.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            currentUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        userRepository.save(currentUser);

        UserDTO userDTO = new UserDTO(
                currentUser.getId(),
                currentUser.getPhoneNumber(),
                currentUser.getName(),
                currentUser.getEmail(),
                currentUser.getEmailVerified(),
                currentUser.getDateOfBirth(),
                currentUser.getRole().name());

        return ResponseEntity.ok(ApiResponse.success("Account updated", userDTO));
    }

    @DeleteMapping("/delete/account")
    @Operation(summary = "Delete user account")
    public ResponseEntity<ApiResponse<String>> deleteAccount(@AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User not authenticated"));
        }

        userRepository.delete(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Account deleted successfully", null));
    }
}
