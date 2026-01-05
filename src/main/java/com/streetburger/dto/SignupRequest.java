package com.streetburger.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    private String phoneNumber;
    private String password;
    private String name;
    private String email;
    private LocalDate dateOfBirth;
}
