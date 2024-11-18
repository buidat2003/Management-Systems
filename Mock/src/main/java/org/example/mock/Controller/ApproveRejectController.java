package org.example.mock.Controller;

import lombok.RequiredArgsConstructor;
import org.example.mock.Model.ApproveStatus;
import org.example.mock.Model.Offer;
import org.example.mock.Model.Vacancy;
import org.example.mock.Model.VacancyStatus;
import org.example.mock.Repository.VacancyRepository;
import org.example.mock.Service.MailHistoryService;
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

    private final OfferService offerService;

    private final MailHistoryService mailHistoryService;

    //Go to Offer List for Manager
    @GetMapping("/offers")
    public String showOffersList(Model model) {
        // Lấy tất cả các offer từ database
        List<Offer> offers = offerService.getAllOffers();

        // Thêm danh sách offer vào model để hiển thị trong view
        model.addAttribute("offers", offers);

        return "Manager/OfferListForManager";
    }


    @GetMapping("/viewOffer/{id}")
    public String showOfferDetails(@PathVariable("id") Long id, Model model) {
        Offer offer = offerService.findOfferById(id);
        if (offer != null) {
            model.addAttribute("offer", offer);
            return "Manager/OfferDetailsForManager";
        } else {
            return "Manager/OfferListForManager";
        }
    }



    @PostMapping("/approveOffer/{id}")
    public String approveOffer(@PathVariable Long id, Model model){
        Offer offer = offerService.findOfferById(id);
        if(offer!= null) {
            offer.setStatus(ApproveStatus.APPROVED);
            offerService.saveOffer(offer);
            try{
                String recipientEmail = offer.getCandidate().getEmail(); // Email của ứng viên
                mailHistoryService.sendApprovalEmail(offer, recipientEmail);
            }catch(Exception e){
                model.addAttribute("errorMessage", "Failed to send email: " + e.getMessage());
                return "error";
            }
            return "redirect:/ApproveReject/offers";
        }else{
            return "error";
        }
    }



    @PostMapping("/rejectOffer/{id}")
    public String rejectOffer(@PathVariable Long id, Model model){
        Offer offer = offerService.findOfferById(id);
        if(offer!= null) {
            offer.setStatus(ApproveStatus.REJECTED);
            offerService.saveOffer(offer);
            return "redirect:/ApproveReject/offers";
        }else{
            return "error";
        }
    }
//
//    private String generateApprovalEmailContent(Offer offer) {
//        return "<h1>Congratulations!</h1>" +
//                "<p>Your offer with ID: " + offer.getId() + " has been approved.</p>" +
//                "<p>Details:</p>" +
//                "<ul>" +
//                "<li>Position: " + offer.getStatus() + "</li>" +
//                "<li>Salary: " + offer.getSalary() + "</li>" +
//                "<li>Start Date: " + offer.getStartDate() + "</li>" +
//                "</ul>" +
//                "<p>Please contact us if you have any questions.</p>";
//    }

}
