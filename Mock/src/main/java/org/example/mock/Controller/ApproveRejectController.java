package org.example.mock.Controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.mock.Model.*;
import org.example.mock.Repository.VacancyRepository;
import org.example.mock.Service.MailHistoryService;
import org.example.mock.Service.OfferService;
import org.example.mock.Service.VacancyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ApproveReject")
public class ApproveRejectController {

    private final OfferService offerService;

    private final MailHistoryService mailHistoryService;

//    //Go to Offer List for Manager
//    @GetMapping("/offers")
//    public String showOffersList(HttpSession session, Model model) {
//        // Lấy tất cả các offer từ database
//        List<Offer> offers = offerService.getAllOffers();
//
//        model.addAttribute("ApproveStatus", ApproveStatus.class);
//        // Thêm danh sách offer vào model để hiển thị trong view
//        model.addAttribute("offers", offers);
//
//        User user = (User) session.getAttribute("USER");
//        if (user != null) {
//            model.addAttribute("username", user.getUsername());
//            model.addAttribute("role", user.getRole());
//        }
//
//        return "Manager/OfferListForManager";
//    }

    @GetMapping("/offers")
    public String showOffersList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) ApproveStatus status,
            HttpSession session,
            Model model) {

        // Lấy danh sách phân trang từ service
        Pageable pageable = PageRequest.of(page, size);

        Page<Offer> offerPage;

        // Kiểm tra nếu có từ khóa tìm kiếm hoặc lọc theo trạng thái
        if ((search != null && !search.isEmpty()) || (status != null)) {
            offerPage = offerService.searchAndFilterOffers(search, status, pageable);
        } else {
            offerPage = offerService.getOffersWithPagination(pageable);
        }

        if (page >= offerPage.getTotalPages()) {
            page = offerPage.getTotalPages() - 1; // Đưa về trang cuối
        }
        if (page < 0) {
            page = 0; // Đưa về trang đầu
        }

        // Truyền dữ liệu vào model
        model.addAttribute("offers", offerPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", offerPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("ApproveStatus", ApproveStatus.class);
        // Lấy thông tin người dùng từ session
        User user = (User) session.getAttribute("USER");
        if (user != null) {
            model.addAttribute("username", user.getUsername());
            model.addAttribute("role", user.getRole());
        }

        return "Manager/OfferListForManager";
    }

    @GetMapping("/viewOffer/{id}")
    public String showOfferDetails(@PathVariable("id") Long id,HttpSession session, Model model) {
        Offer offer = offerService.findOfferById(id);
        User user = (User) session.getAttribute("USER");
        if (user != null) {
            model.addAttribute("username", user.getUsername());
            model.addAttribute("role", user.getRole());
        }
        if (offer != null) {
            model.addAttribute("ApproveStatus", ApproveStatus.class);
            model.addAttribute("offer", offer);
            return "Manager/OfferDetailsForManager";
        } else {
            return "Manager/OfferListForManager";
        }
    }

    @GetMapping("/goReason/{id}")
    public String GetInforForReason(@PathVariable("id") Long id,HttpSession session, Model model) {
        Offer offer = offerService.findOfferById(id);
        User user = (User) session.getAttribute("USER");
        if (user != null) {
            model.addAttribute("username", user.getUsername());
            model.addAttribute("role", user.getRole());
        }
        if (offer != null) {
            model.addAttribute("ApproveStatus", ApproveStatus.class);
            model.addAttribute("offer", offer);
            return "Manager/RejectReason";
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
    public String rejectOffer(@PathVariable Long id,  @RequestParam("terms") String terms, Model model){
        Offer offer = offerService.findOfferById(id);
        if(offer!= null) {
            offer.setTerms(terms);
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
