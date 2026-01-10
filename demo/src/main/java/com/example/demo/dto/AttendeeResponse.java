package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendeeResponse {
    private Long id;
    private String fullName;
    private String email;
    private String department;
    private LocalDateTime registeredAt;
}
