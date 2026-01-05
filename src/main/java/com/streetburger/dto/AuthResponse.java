package com.streetburger.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String phoneNumber;
    private String name;
    private String role;
    private String message;

    public AuthResponse(String token, String phoneNumber, String name, String role) {
        this.token = token;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.role = role;
        this.message = "Success";
    }
}
