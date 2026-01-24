package com.example.demo.service;

import com.example.demo.dto.EventFilterRequest;
import com.example.demo.dto.EventRequest;
import com.example.demo.dto.EventResponse;
import com.example.demo.model.Event;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.EventAttendeeRepository;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final EventAttendeeRepository attendeeRepository; // Add this

    public EventResponse createEvent(EventRequest request){
        User organizer = getCurrentUser();
        if(!organizer.getRole().equals(Role.ORGANIZER) && !organizer.getRole().equals(Role.ADMIN)){
            throw new RuntimeException("Only organizers can create events");
        }

        if(request.getEndDate().isBefore(request.getStartDate())){
            throw new RuntimeException("End date must be after start date");
        }
        Event event = Event.builder().title(request.getTitle())
                .description(request.getDescription())
                .department(request.getDepartment())
                .type(request.getType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .location(request.getLocation())
                .capacity(request.getCapacity())
                .imageUrl(request.getImageUrl())
                .organizer(organizer)
                .build();

        Event saveEvent = eventRepository.save(event);
        return mapToEventResponse(saveEvent);
    }
    public List<EventResponse> getAllEvents(EventFilterRequest filters){
        List<Event> events;
        if(filters!=null && hasFilters(filters)){
            events = eventRepository.findByFilter(
                    filters.getDepartment(),
                    filters.getType(),
                    filters.getStartDate(),
                    filters.getEndDate(),
                    filters.getSearch()
            );
        }else{
            events= eventRepository.findByStartDateAfterOrderByStartDateAsc(LocalDateTime.now());
        }
        return events.stream().map(this::mapToEventResponse).collect(Collectors.toList());
    }
    public EventResponse getEventById(Long id){
        Event event = eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found with id: "+id));
        return mapToEventResponse(event);
    }

    @Transactional
    public EventResponse updateEvent(Long id, EventRequest request){
        Event event = eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found with id: "+id));
        User currentUser =getCurrentUser();
        if(!event.getOrganizer().getId().equals(currentUser.getId()) && !currentUser.getRole().equals(Role.ADMIN)){
            throw new RuntimeException("You don't have permission to update this event");
        }
        //Validate dates
        if(request.getEndDate().isBefore(request.getStartDate())){
            throw new RuntimeException("End date must be after start date");
        }
        //delete old image if new is uploaded
        if(request.getImageUrl()!=null && !request.getImageUrl().equals(event.getImageUrl()) && event.getImageUrl()!=null){
            try{
                fileStorageService.deleteFile(event.getImageUrl());
            }catch (Exception ex){
                System.err.println("Failed to delete old image "+ ex.getMessage());
            }
        }
        // Update fields
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setDepartment(request.getDepartment());
        event.setType(request.getType());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setLocation(request.getLocation());
        event.setCapacity(request.getCapacity());
        event.setImageUrl(request.getImageUrl());

        Event updateEvent = eventRepository.save(event);
        return mapToEventResponse(updateEvent);
    }
    @Transactional
    public void deleteEvent(Long id){
        Event event=eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found with id: "+id));
        User currentUser = getCurrentUser();

        if(!event.getOrganizer().getId().equals(currentUser.getId()) && !currentUser.getRole().equals(Role.ADMIN)){
            throw new RuntimeException("You don't have permission to delete this event");
        }
        //Delete associated image
        if(event.getImageUrl()!=null){
            try{
                fileStorageService.deleteFile(event.getImageUrl());
            }catch (Exception e){
                System.err.println("Failed to delete image: "+e.getMessage());
            }
        }
        eventRepository.delete(event);
    }

    public List<EventResponse> getMyEvents(){
        User organizer =getCurrentUser();
        List<Event> events=eventRepository.findByOrganizerId(organizer.getId());
        return events.stream().map(this::mapToEventResponse).collect(Collectors.toList());
    }

    private boolean hasFilters(EventFilterRequest filters){
        return filters.getDepartment() != null ||
                filters.getType() != null ||
                filters.getStartDate() != null ||
                filters.getEndDate() != null ||
                filters.getSearch() != null;
    }

    private User getCurrentUser(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User Not Found"));
    }
    private EventResponse mapToEventResponse(Event event){
        long attendeeCount=attendeeRepository.countByEventId(event.getId());
        return EventResponse.builder().id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .department(event.getDepartment())
                .type(event.getType())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .location(event.getLocation())
                .capacity(event.getCapacity())
                .imageUrl(event.getImageUrl())
                .organizer(EventResponse.OrganizerInfo.builder()
                        .id(event.getOrganizer().getId())
                        .fullName(event.getOrganizer().getFullName())
                        .email(event.getOrganizer().getEmail())
                        .department(event.getOrganizer().getDepartment())
                        .build())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .attendeeCount(attendeeCount) // Add this
                .build();
    }

}
