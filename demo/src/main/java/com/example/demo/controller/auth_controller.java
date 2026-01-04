package com.example.demo.controller;

import com.example.demo.dto.auth_response;
import com.example.demo.dto.login_request;
import com.example.demo.dto.register_request;
import com.example.demo.service.auth_service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class auth_controller {

    private final auth_service authService;

    @PostMapping("/register/attendee")
    public ResponseEntity<auth_response> registerAttendee(@Valid @RequestBody register_request request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/register/organizer")
    public ResponseEntity<auth_response> registerOrganizer(@Valid @RequestBody register_request request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<auth_response> login(@Valid @RequestBody login_request request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
