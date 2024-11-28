package org.example.mock.Controller;

import org.example.mock.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserCalendarLink {
    @Autowired
    private UserService userService;

    @PostMapping("/update-calendar-links-for-interviewers")
    public ResponseEntity<String> updateCalendarLinksForInterviewers() {
        try {
            userService.updateGoogleCalendarLinkForAllInterviewers();
            return ResponseEntity.ok("Calendar links for INTERVIEWER role updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating calendar links: " + e.getMessage());
        }
    }

}
