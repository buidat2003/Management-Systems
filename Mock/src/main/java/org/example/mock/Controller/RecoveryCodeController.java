package org.example.mock.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;


@Controller
@RequestMapping("/recovery")
@SessionAttributes({"recoveryCode", "verifiedEmail"})
public class RecoveryCodeController {

    @GetMapping
    public String showRecoveryForm() {
        return "RecoveryCodeForgot";
    }

    @PostMapping
    public String processRecovery(@RequestParam("code") String code,
                                  @SessionAttribute("recoveryCode") String recoveryCode,
                                  @SessionAttribute("verifiedEmail") String verifiedEmail,
                                  Model model, SessionStatus sessionStatus) {
        if (code == null || !code.equals(recoveryCode)) {
            model.addAttribute("error", "Invalid recovery code. Please try again.");
            return "RecoveryCodeForgot";
        }

        return "redirect:/newpass";
    }
}
