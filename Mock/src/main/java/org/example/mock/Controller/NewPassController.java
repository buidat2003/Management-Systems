package org.example.mock.Controller;

import org.example.mock.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@RequestMapping("/newpass")
public class NewPassController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String showNewPasswordForm() {
        return "NewPass";  // Show the new password form
    }

    @PostMapping
    public String processNewPassword(@SessionAttribute("verifiedEmail") String email,
                                     @RequestParam("password") String password,
                                     @RequestParam("repass") String confirmPassword,

                                     Model model, SessionStatus status) {
        // Check password and confirmPassword match
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "NewPass";
        }

        try {
            // Update the password using the email from session
            boolean isUpdated = userService.updatePassword(email, password);
            if (isUpdated) {
                status.setComplete();  // Clear session attribute after success
                return "redirect:/login";  // Redirect to login page
            } else {
                model.addAttribute("error", "An error occurred while updating the password.");
                return "NewPass";
            }
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "NewPass";
        } catch (Exception e) {
            model.addAttribute("error", "An unknown error occurred.");
            return "NewPass";
        }
    }
}
