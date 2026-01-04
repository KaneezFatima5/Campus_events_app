package com.example.demo.dto;

import com.example.demo.model.role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class auth_response {
    private String token;
    private String email;
    private String fullName;
    private role role;
    private String message;
}
