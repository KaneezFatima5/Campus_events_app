package com.example.demo.controller;

import com.example.demo.service.CalenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalenderService calendarService;

    @GetMapping("/events/{eventId}")
    public ResponseEntity<String> downloadEventCalendar(@PathVariable Long eventId) {
        String icsContent = calendarService.generateICalender(eventId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/calendar"));
        headers.setContentDispositionFormData("attachment", "event-" + eventId + ".ics");

        return ResponseEntity.ok()
                .headers(headers)
                .body(icsContent);
    }
}