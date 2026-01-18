package com.example.demo.dto;

import com.example.demo.model.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
    private Long id;
    private String title;
    private String description;
    private String department;
    private EventType type;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private Integer capacity;
    private String imageUrl;
    private OrganizerInfo organizer;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long attendeeCount; // Add this field


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrganizerInfo {
        private Long id;
        private String fullName;
        private String email;
        private String department;
    }
}