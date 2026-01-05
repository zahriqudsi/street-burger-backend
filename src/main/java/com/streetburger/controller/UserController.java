package com.streetburger.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.streetburger.model.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.streetburger.dto.ApiResponse;
import com.streetburger.dto.UserDTO;
import com.streetburger.repository.UserRepository;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "User profile management")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser(@AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User not authenticated"));
        }
        return ResponseEntity.ok(ApiResponse.success(toDTO(currentUser)));
    }

    @GetMapping("/getUserFromPhone")
    @Operation(summary = "Get user by phone number")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByPhone(@RequestParam String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .map(user -> ResponseEntity.ok(ApiResponse.success(toDTO(user))))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("User not found")));
    }

    @GetMapping("/allUsers")
    @Operation(summary = "Get all users (Admin only)")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        List<UserDTO> users = userRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    private UserDTO toDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getPhoneNumber(),
                user.getName(),
                user.getEmail(),
                user.getEmailVerified(),
                user.getDateOfBirth(),
                user.getRole().name());
    }
}
