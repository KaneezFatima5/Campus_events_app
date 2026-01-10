package com.example.demo.service;

import com.example.demo.dto.AttendanceStatusResponse;
import com.example.demo.dto.AttendeeResponse;
import com.example.demo.model.Event;
import com.example.demo.model.EventAttendee;
import com.example.demo.model.User;
import com.example.demo.repository.EventAttendeeRepository;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventAttendeeService {
    private EventAttendeeRepository eventAttendeeRepository;
    private UserRepository userRepository;
    private EventRepository eventRepository;

    @Transactional
    public AttendanceStatusResponse markAttending(long eventId){
        User user=getCurrentUser();
        Event event =eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));
        //Check if already attending
        if(eventAttendeeRepository.existsByEventIdAndUserId(eventId, user.getId())){
            throw new RuntimeException("You are already registered for this event");
        }
        // Check capacity
        if (event.getCapacity() != null) {
            long currentAttendees = eventAttendeeRepository.countByEventId(eventId);
            if (currentAttendees >= event.getCapacity()) {
                throw new RuntimeException("Event is at full capacity");
            }
        }
        EventAttendee attendee =EventAttendee.builder().event(event).user(user).build();
        eventAttendeeRepository.save(attendee);
        long attendeeCount=eventAttendeeRepository.countByEventId(eventId);
        return AttendanceStatusResponse.builder().isAttending(true).attendeeCount(attendeeCount).message("Successfully registered for the event").build();
    }
    //un mark user as attendee
    @Transactional
    public AttendanceStatusResponse unmarkAttending(long eventId){
        User user = getCurrentUser();
        if(!eventRepository.existsById(eventId)){
            throw new RuntimeException("Event not found");
        }
        if(!eventAttendeeRepository.existsByEventIdAndUserId(eventId, user.getId())){
            throw new RuntimeException("You are not registered for this event");
        }
        eventAttendeeRepository.deleteByEventIdAndUserId(eventId, user.getId());
        long attendeeCount = eventAttendeeRepository.countByEventId(eventId);

        return AttendanceStatusResponse.builder().isAttending(false).message("Successfully unregistered from the event").build();
    }
    //Check if current user is attending
    public AttendanceStatusResponse checkAttendanceStatus(long eventId){
        User user= getCurrentUser();
        boolean isAttending =eventAttendeeRepository.existsByEventIdAndUserId(eventId, user.getId());
        long attendeeCount= eventAttendeeRepository.countByEventId(eventId);

        return AttendanceStatusResponse.builder()
                .isAttending(isAttending)
                .attendeeCount(attendeeCount)
                .message(isAttending ? "You are attending this event" : "You are not attending this event")
                .build();
    }
    public List<AttendeeResponse> getEventAttendees(long eventId){
        if(!eventRepository.existsById(eventId)){
            throw new RuntimeException("Event not Found");
        }
        List<EventAttendee> attendees= eventAttendeeRepository.findByEventId(eventId);
        return attendees.stream().map(this::mapToAttendeeResponse).collect(Collectors.toList());
    }
    public List<Long> getMyAttendingEvents(){
        User user =getCurrentUser();
        List<EventAttendee> attendees=eventAttendeeRepository.findByUserId(user.getId());
        return attendees.stream().map(attendee -> attendee.getEvent().getId()).collect(Collectors.toList());
    }
    private User getCurrentUser(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String email =authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }
    private AttendeeResponse mapToAttendeeResponse(EventAttendee attendee){
        User user= attendee.getUser();
        return AttendeeResponse.builder().id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .department(user.getDepartment())
                .registeredAt(attendee.getRegisteredAt())
                .build();
    }

}
