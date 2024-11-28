package org.example.mock.Controller;

import org.example.mock.Model.User;
import org.example.mock.Service.MailHistoryService;
import org.example.mock.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;


@Controller
@RequestMapping("/forgot")
@SessionAttributes({"verifiedEmail", "recoveryCode"})  // Store email and recovery code in session
public class ResetPasswordController {

    @Autowired
    private UserService userService;

    @Autowired
    private MailHistoryService mailHistoryService;

    @GetMapping
    public String showRequestForm() {
        return "ForgotPassword";
    }

    @PostMapping
    public String processRequest(@RequestParam("username") String username,
                                 @RequestParam("email") String email, Model model) {
        User user = userService.verifyUser(username, email);
        if (user == null) {
            model.addAttribute("error", "Invalid username or email.");
            return "ForgotPassword";
        }

        try {
            String recoveryCode = mailHistoryService.sendRecoveryCode(user);
            if (recoveryCode == null) {
                model.addAttribute("error", "Error generating recovery code.");
                return "ForgotPassword";
            }

            model.addAttribute("verifiedEmail", email);  // Store email in session
            model.addAttribute("recoveryCode", recoveryCode);  // Store recovery code in session
            return "redirect:/recovery";
        } catch (Exception e) {
            model.addAttribute("error", "Error sending recovery code.");
            e.printStackTrace();
            return "ForgotPassword";
        }
    }
}
