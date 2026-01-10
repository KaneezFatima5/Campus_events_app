package com.example.demo.controller;

import com.example.demo.dto.AttendanceStatusResponse;
import com.example.demo.dto.AttendeeResponse;
import com.example.demo.service.EventAttendeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventAttendeeController {

    private final EventAttendeeService attendeeService;

    // Mark as attending
    @PostMapping("/{eventId}/attend")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AttendanceStatusResponse> markAttending(@PathVariable Long eventId) {
        return ResponseEntity.ok(attendeeService.markAttending(eventId));
    }

    // Unmark as attending
    @DeleteMapping("/{eventId}/attend")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AttendanceStatusResponse> unmarkAttending(@PathVariable Long eventId) {
        return ResponseEntity.ok(attendeeService.unmarkAttending(eventId));
    }

    // Check attendance status
    @GetMapping("/{eventId}/attendance-status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AttendanceStatusResponse> checkAttendanceStatus(@PathVariable Long eventId) {
        return ResponseEntity.ok(attendeeService.checkAttendanceStatus(eventId));
    }

    // Get all attendees for an event
    @GetMapping("/{eventId}/attendees")
    public ResponseEntity<List<AttendeeResponse>> getEventAttendees(@PathVariable Long eventId) {
        return ResponseEntity.ok(attendeeService.getEventAttendees(eventId));
    }

    // Get events current user is attending
    @GetMapping("/my-attending")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Long>> getMyAttendingEvents() {
        return ResponseEntity.ok(attendeeService.getMyAttendingEvents());
    }
}