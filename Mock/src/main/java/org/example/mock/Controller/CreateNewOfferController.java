package org.example.mock.Controller;

import org.example.mock.Model.ApproveStatus;
import org.example.mock.Model.Candidate;
import org.example.mock.Model.Offer;
import org.example.mock.Model.Reviews;
import org.example.mock.Model.User;
import org.example.mock.Repository.ReviewsRepository;
import org.example.mock.Service.OfferService;
import org.example.mock.Repository.CandidateRepository;
import org.example.mock.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class CreateNewOfferController {

    private final OfferService offerService;
    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;
    private ReviewsRepository reviewsRepository;

    @Autowired
    public CreateNewOfferController(OfferService offerService, CandidateRepository candidateRepository, UserRepository userRepository, ReviewsRepository reviewsRepository) {
        this.offerService = offerService;
        this.candidateRepository = candidateRepository;
        this.userRepository = userRepository;
        this.reviewsRepository = reviewsRepository;
    }

    @GetMapping("/offers/infoCreate/{id}")
    public String getCandidateDetails(@PathVariable("id") Long id, Model model) {
        System.out.println("Received candidateId: " + id);

        // Truy vấn Candidate từ cơ sở dữ liệu
        Candidate candidate = candidateRepository.findById(id).orElse(null);

        if (candidate == null) {
            model.addAttribute("errorMessage", "Candidate not found!");
            return "Offer/CreateNewOffer";  // Tên file Thymeleaf template
        }

        // Truy vấn danh sách reviews của candidate
        List<Reviews> reviews = reviewsRepository.findByCandidateId(id);

        // Thêm dữ liệu vào model để hiển thị trên view
        model.addAttribute("candidate", candidate);
        model.addAttribute("reviews", reviews);
        model.addAttribute("offerDate", LocalDate.now());

        return "Offer/CreateNewOffer";  // Tên file Thymeleaf template
    }




//    @GetMapping("/offers/create")
//    public String showOfferForm(Model model) {
//        return "Offer/CreateNewOffer";  // Form view to create an offer
//    }

    @PostMapping("/offers/create")
    public String createOffer(
            @RequestParam("offerDate") String offerDate,
            @RequestParam("startDate") String startDate,
            @RequestParam("salary") String salary,
            @RequestParam("candidateId") String candidateId,

            Model model
    ) {
        try {
            long createdUserId = 1;
            // Convert candidateId và createdUserId to Long
            Long candidateIdLong = Long.parseLong(candidateId);

            // Parse offerDate và startDate
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate offerDateParsed = LocalDate.parse(offerDate, formatter);
            LocalDate startDateParsed = LocalDate.parse(startDate, formatter);

            // Chuyển đổi offerDateParsed (LocalDate) thành LocalDateTime với thời gian 00:00:00
            LocalDateTime offerDateTime = offerDateParsed.atStartOfDay();

            // Retrieve Candidate và User
            Candidate candidates = candidateRepository.findById(candidateIdLong)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid candidate ID"));
            User createdUser = userRepository.findById(createdUserId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

            // Create Offer object
            Offer offer = new Offer();
            offer.setOfferDate(offerDateTime); // Gán giá trị LocalDateTime cho offerDate
            offer.setStartDate(startDateParsed);
            offer.setSalary(salary);
            offer.setCandidate(candidates);
            offer.setCreatedUser(createdUser);

            Candidate candidate = candidateRepository.findById(candidateIdLong).orElse(null);

            if (candidate == null) {
                model.addAttribute("errorMessage", "Candidate not found!");
                return "Offer/CreateNewOffer";  // Tên file Thymeleaf template
            }

            // Truy vấn danh sách reviews của candidate
            List<Reviews> reviews = reviewsRepository.findByCandidateId(candidateIdLong);

            // Thêm dữ liệu vào model để hiển thị trên view
            model.addAttribute("candidate", candidate);
            model.addAttribute("reviews", reviews);
            model.addAttribute("offerDate", LocalDate.now());



            // Save offer
            offerService.saveOffer(offer);

            // Add success message
            model.addAttribute("message", "Offer created successfully!");

            return "Offer/CreateNewOffer";

        } catch (Exception e) {
            // Add error message
            model.addAttribute("errorMessage", "An error occurred while creating the offer: " + e.getMessage());
            return "Offer/CreateNewOffer";
        }
    }

}
