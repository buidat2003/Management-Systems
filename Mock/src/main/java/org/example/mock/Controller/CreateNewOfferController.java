package org.example.mock.Controller;

import org.example.mock.Model.Candidate;
import org.example.mock.Model.Offer;
import org.example.mock.Model.User;
import org.example.mock.Service.OfferService;
import org.example.mock.Repository.CandidateRepository;
import org.example.mock.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class CreateNewOfferController {

    private final OfferService offerService;
    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;

    @Autowired
    public CreateNewOfferController(OfferService offerService, CandidateRepository candidateRepository, UserRepository userRepository) {
        this.offerService = offerService;
        this.candidateRepository = candidateRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/offers/create")
    public String showOfferForm(Model model) {
        return "Offer/CreateNewOffer";  // Form view to create an offer
    }

    @PostMapping("/offers/create")
    public String createOffer(
            @RequestParam("offerDate") String offerDate,
            @RequestParam("startDate") String startDate,
            @RequestParam("salary") String salary,
            @RequestParam("candidateId") String candidateId,
            @RequestParam("terms") String terms,
            @RequestParam("createdUserId") String createdUserId,
            @RequestParam(value = "updatedUserId", required = false) String updatedUserId,
            @RequestParam(value = "status", required = false) String status,
            Model model
    ) {
        try {
            // Convert candidateId và createdUserId to Long
            Long candidateIdLong = Long.parseLong(candidateId);
            Long createdUserIdLong = Long.parseLong(createdUserId);
            Long updatedUserIdLong = (updatedUserId != null && !updatedUserId.isEmpty()) ? Long.parseLong(updatedUserId) : null;

            // Parse offerDate và startDate
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate offerDateParsed = LocalDate.parse(offerDate, formatter);
            LocalDate startDateParsed = LocalDate.parse(startDate, formatter);

            // Chuyển đổi offerDateParsed (LocalDate) thành LocalDateTime với thời gian 00:00:00
            LocalDateTime offerDateTime = offerDateParsed.atStartOfDay();

            // Retrieve Candidate và User
            Candidate candidate = candidateRepository.findById(candidateIdLong)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid candidate ID"));
            User createdUser = userRepository.findById(createdUserIdLong)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

            // Create Offer object
            Offer offer = new Offer();
            offer.setOfferDate(offerDateTime); // Gán giá trị LocalDateTime cho offerDate
            offer.setStartDate(startDateParsed);
            offer.setSalary(salary);
            offer.setTerms(terms);
            offer.setCandidate(candidate);
            offer.setCreatedUser(createdUser);
            offer.setStatus(status != null ? status : "1");

            if (updatedUserIdLong != null) {
                User updatedUser = userRepository.findById(updatedUserIdLong)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid updated user ID"));
                offer.setUpdatedUser(updatedUser);
            }

            // Set thời gian hiện tại cho updatedAt
            offer.setUpdatedAt(LocalDateTime.now());

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
