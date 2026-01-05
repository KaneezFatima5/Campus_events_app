package com.example.demo.dto;

import com.example.demo.model.EventType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventFilterRequest {
    private String department;
    private EventType type;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String search; // For searching by title or description
}