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
    public String showNewPasswordForm(@SessionAttribute(value = "verifiedEmail", required = false) String email, Model model) {
        if (email == null) {
            return "redirect:/forgot";
        }
        return "NewPass";
    }

    @PostMapping
    public String processNewPassword(@SessionAttribute("verifiedEmail") String email,
                                     @RequestParam("password") String password,
                                     @RequestParam("repass") String confirmPassword,
                                     Model model, SessionStatus status) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "NewPass";
        }

        try {
            boolean isUpdated = userService.updatePassword(email, password);
            if (isUpdated) {
                status.setComplete();  // Clear session attributes
                return "redirect:/login?success=true"; // Pass success message as a URL parameter
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
