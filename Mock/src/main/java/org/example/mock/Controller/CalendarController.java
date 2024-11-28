package org.example.mock.Controller;

import org.example.mock.Model.User;
import org.example.mock.Service.GoogleCalendarService;
import org.example.mock.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    @Autowired
    private GoogleCalendarService googleCalendarService;
    @Autowired
    private UserService userService;

    @GetMapping("/interviewer-link")
    public ResponseEntity<String> getInterviewerCalendarLink(@RequestParam("interviewerId") Long interviewerId) {
        try {
            // Lấy thông tin interviewer từ database
            User interviewer = userService.findById(interviewerId);

            if (interviewer == null) {
                return ResponseEntity.badRequest().body("Interviewer not found.");
            }

            // Tạo link Google Calendar từ email
            String calendarLink = "https://calendar.google.com/calendar/embed?src=" + interviewer.getEmail();
            return ResponseEntity.ok(calendarLink);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching calendar link: " + e.getMessage());
        }
    }
    @PostMapping("/create-event")
    public ResponseEntity<String> createCalendarEvent(
            @RequestParam String calendarId,
            @RequestParam String summary,
            @RequestParam String description,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime
    ) {
        try {
            String eventLink = googleCalendarService.createEvent(calendarId, summary, description, startDateTime, endDateTime);
            return ResponseEntity.ok("Event created successfully. View it here: " + eventLink);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating event: " + e.getMessage());
        }
    }
}

