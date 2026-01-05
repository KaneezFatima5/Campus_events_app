package com.example.demo.controller;

import com.example.demo.dto.EventFilterRequest;
import com.example.demo.dto.EventRequest;
import com.example.demo.dto.EventResponse;
import com.example.demo.model.EventType;
import com.example.demo.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventRequest request){
        EventResponse response = eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) EventType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String search
    ) {
        EventFilterRequest filters=new EventFilterRequest();
        filters.setDepartment(department);
        filters.setType(type);
        filters.setStartDate(startDate);
        filters.setEndDate(endDate);
        filters.setSearch(search);
        List<EventResponse> events=eventService.getAllEvents(filters);
        return ResponseEntity.ok(events);
    }
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long id){
        EventResponse event= eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable Long id, @Valid @RequestBody EventRequest request){
        EventResponse response= eventService.updateEvent(id, request);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id){
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/my-events")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<List<EventResponse>> getMyEvents(){
        List<EventResponse> events = eventService.getMyEvents();
        return ResponseEntity.ok(events);
    }
    @GetMapping("/department/{department}")
    public ResponseEntity<List<EventResponse>> getEventsByDepartment(@PathVariable String department){
        EventFilterRequest filters= new EventFilterRequest();
        filters.setDepartment(department);
        List<EventResponse> events = eventService.getAllEvents(filters);
        return ResponseEntity.ok(events);
    }
    @GetMapping("/type/{type}")
    public ResponseEntity<List<EventResponse>> getEventsByType(@PathVariable EventType type){
        EventFilterRequest filters=new EventFilterRequest();
        filters.setType(type);
        List<EventResponse> events = eventService.getAllEvents(filters);
        return ResponseEntity.ok(events);
    }
}
