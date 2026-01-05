package com.example.demo.dto;

import com.example.demo.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuthRequest {
    private String token;
    private String email;
    private String fullName;
    private Role role;
    private String message;
}
