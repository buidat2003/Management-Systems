package org.example.mock.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/candidate")
    public String candidatePage() {
        return "candidate"; // Candidate page view
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "admin"; // Admin page view
    }
}
