package org.example.mock.Controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/home")
    public String home() {

        return "candidate.html"; // Đây là tên của file template (home.html) trong thư mục templates
    }
}
