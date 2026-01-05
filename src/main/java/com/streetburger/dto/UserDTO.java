package com.streetburger.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String phoneNumber;
    private String name;
    private String email;
    private Boolean emailVerified;
    private LocalDate dateOfBirth;
    private String role;
}
