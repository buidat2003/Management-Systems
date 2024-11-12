package org.example.mock.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@RequestMapping("/recovery")
@SessionAttributes("recoveryCode")
public class RecoveryCodeController {

    // Handles GET requests to show the recovery form
    @GetMapping
    public String showRecoveryForm() {
        return "RecoveryCodeForgot";  // Show the recovery code page (HTML form)
    }

    // Handles POST requests to validate the recovery code
    @PostMapping
    public String processRecovery(@RequestParam("code") String code,
                                  @SessionAttribute("recoveryCode") String recoveryCode,
                                  @SessionAttribute("verifiedEmail") String verifiedEmail,
                                  Model model, SessionStatus sessionStatus) {
        // Check if the entered code matches the generated recovery code
        if (code == null || !code.equals(recoveryCode)) {
            model.addAttribute("error", "Invalid recovery code. Please try again.");
            return "redirect:/recovery";  // Reloads the recovery page with an error message
        }

        // Clear session attributes after successful verification
        sessionStatus.setComplete();
        return "redirect:/newpass";  // Redirect to the new password page if the code matches
    }
}
