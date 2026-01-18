package com.example.demo.service;

import com.example.demo.model.Event;
import com.example.demo.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class CalenderService {
    private final EventRepository eventRepository;
    private static final DateTimeFormatter ICS_DATE_FORMAT= DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
    public String generateICalender(Long eventId){
        Event event =eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));
        return generateICalenderContent(event);
    }
    private String generateICalenderContent(Event event){
        StringBuilder ics =new StringBuilder();
        // Header
        ics.append("BEGIN:VCALENDAR\r\n");
        ics.append("VERSION:2.0\r\n");
        ics.append("PRODID:-//Campus Events//Event Calendar//EN\r\n");
        ics.append("CALSCALE:GREGORIAN\r\n");
        ics.append("METHOD:PUBLISH\r\n");

        // Event details
        ics.append("BEGIN:VEVENT\r\n");
        // Unique ID
        ics.append("UID:").append(event.getId()).append("@campusevents.com\r\n");

        // Date and time
        String startDateTime = event.getStartDate()
                .atZone(ZoneId.systemDefault())
                .format(ICS_DATE_FORMAT);
        String endDateTime = event.getEndDate()
                .atZone(ZoneId.systemDefault())
                .format(ICS_DATE_FORMAT);

        ics.append("DTSTART:").append(startDateTime).append("\r\n");
        ics.append("DTEND:").append(endDateTime).append("\r\n");

        // Event summary (title)
        ics.append("SUMMARY:").append(escapeText(event.getTitle())).append("\r\n");

        // Description
        if (event.getDescription() != null) {
            ics.append("DESCRIPTION:").append(escapeText(event.getDescription())).append("\r\n");
        }

        // Location
        if (event.getLocation() != null) {
            ics.append("LOCATION:").append(escapeText(event.getLocation())).append("\r\n");
        }

        // Organizer
        if (event.getOrganizer() != null) {
            ics.append("ORGANIZER;CN=").append(escapeText(event.getOrganizer().getFullName()))
                    .append(":MAILTO:").append(event.getOrganizer().getEmail()).append("\r\n");
        }

        // Status
        ics.append("STATUS:CONFIRMED\r\n");

        // Categories (Event Type)
        ics.append("CATEGORIES:").append(event.getType().toString()).append("\r\n");

        // Timestamp
        String timestamp = java.time.LocalDateTime.now()
                .atZone(ZoneId.systemDefault())
                .format(ICS_DATE_FORMAT);
        ics.append("DTSTAMP:").append(timestamp).append("\r\n");

        // End event
        ics.append("END:VEVENT\r\n");

        // End calendar
        ics.append("END:VCALENDAR\r\n");

        return ics.toString();
    }
    private String escapeText(String text){
        if(text==null)return "";
        return text.replace("\\", "\\\\")
                .replace(",", "\\,")
                .replace(";", "\\;")
                .replace("\n", "\\n");
    }
}
