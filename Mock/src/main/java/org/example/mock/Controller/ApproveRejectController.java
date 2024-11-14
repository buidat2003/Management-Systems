package org.example.mock.Controller;

import lombok.RequiredArgsConstructor;
import org.example.mock.Model.ApproveStatus;
import org.example.mock.Model.Offer;
import org.example.mock.Model.Vacancy;
import org.example.mock.Model.VacancyStatus;
import org.example.mock.Repository.VacancyRepository;
import org.example.mock.Service.OfferService;
import org.example.mock.Service.VacancyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ApproveReject")
public class ApproveRejectController {
    private final VacancyService vacancyService;
    @Autowired
    private VacancyRepository vacancyRepository;
    private final OfferService offerService;

    //Go to Joblist for Manager
    @GetMapping("/jobList")
    public String gẹtJobList(@RequestParam(value = "position", required = false) Long positionId,
                             @RequestParam(value = "skills", required = false) String requiredSkills,
                             @RequestParam(value = "department", required = false) Long departmentId,
                             @RequestParam(value = "status", required = false) String status,
                             @RequestParam(value = "search", required = false) String search,
                             Model model){
        List<Vacancy> filteredVacancies = vacancyService.getFilteredVacancies(
                positionId != null ? positionId : null,
                requiredSkills != null && !requiredSkills.isEmpty() ? requiredSkills : null,
                departmentId != null ? departmentId : null,
                status != null && !status.isEmpty() ? status : null,
                search != null && !search.isEmpty() ? search : null
        );

        // Add the filtered list and form fields to the model
        model.addAttribute("vacancies", filteredVacancies);
        model.addAttribute("positions", vacancyService.getAllPositions());
        model.addAttribute("departments", vacancyRepository.findAllDepartments());
        model.addAttribute("statuses", vacancyService.getAllStatuses());
        model.addAttribute("details", vacancyService.getAllDetails());

        return "Manager/JobListForManager";
    }

    //View job details for manager
    @GetMapping("/viewJob/{id}")
    public String viewJobDetails(@PathVariable Long id, Model model) {
        Optional<Vacancy> vacancyOptional = vacancyService.findById(id);
        if (vacancyOptional.isPresent()) {
            Vacancy vacancy = vacancyOptional.get();
            model.addAttribute("vacancy", vacancy);
            return "Manager/JobDetailsForManager"; // The new View Job page
        } else {
            return "error"; // Handle invalid job ID
        }
    }

    @PostMapping("/approveJob/{id}")
    public String approveJob(@PathVariable Long id, Model model){
        Optional<Vacancy> vacancyOptional = vacancyService.findById(id);
        if(vacancyOptional.isPresent()) {
            Vacancy vacancy = vacancyOptional.get();
            vacancy.setApproveStatus(ApproveStatus.REJECTED);
            vacancyRepository.save(vacancy);
        }
        return "Manager/JobListForManager";
    }

    @PostMapping("/rejectJob/{id}")
    public String rejectJob(@PathVariable Long id, Model model){
        Optional<Vacancy> vacancyOptional = vacancyService.findById(id);
        if(vacancyOptional.isPresent()) {
            Vacancy vacancy = vacancyOptional.get();
            vacancy.setApproveStatus(ApproveStatus.REJECTED);
            vacancyRepository.save(vacancy);
        }
        return "Manager/JobListForManager";
    }

    //Go to Offer List for Manager
    @GetMapping("/offers")
    public String showOffers(Model model) {
        // Lấy tất cả các offer từ database
        List<Offer> offers = offerService.getAllOffers();

        // Thêm danh sách offer vào model để hiển thị trong view
        model.addAttribute("offers", offers);

        return "Manager/OfferListForManager";
    }

}
