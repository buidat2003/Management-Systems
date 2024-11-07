package org.example.mock.Controller;
import org.example.mock.Model.Vacancy;
import org.example.mock.Service.VacancyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private VacancyService vacancyService;

    @GetMapping("/home")
    public String home(Model model) {
        // Fetch vacancies and add them to the model
        List<Vacancy> vacancies = vacancyService.getAllVacancies();
        model.addAttribute("vacancies", vacancies);
            return "Candidate/homecandidate"; // Path to your HTML template
    }
}
