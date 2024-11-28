package org.example.mock.Controller;


import org.example.mock.Model.Candidate;
import org.example.mock.Model.Offer;
import org.example.mock.Model.Reviews;
import org.example.mock.Repository.Admin.OfferRepository;
import org.example.mock.Repository.CandidateRepository;
import org.example.mock.Repository.ReviewsRepository;
import org.example.mock.Service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ListOfferController {

    private final OfferService offerService;

    @Autowired
    public ListOfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @Autowired
    private OfferRepository offerRepository;
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private ReviewsRepository reviewsRepository;

    @GetMapping("/offers")
    public String showOffers(Model model) {
        // Lấy tất cả các offer từ database
        List<Offer> offers = offerService.getAllOffers();

        // Thêm danh sách offer vào model để hiển thị trong view
        model.addAttribute("offers", offers);

        return "/Offer/ListOffer"; // Trả về tên view sẽ hiển thị danh sách offer
    }

    @GetMapping("/offers/{id}/detail")
    public String showOfferDetails(@PathVariable("id") Long id, Model model) {
        // Lấy ID của Candidate thông qua Offer
        Long idCandidate = offerRepository.findCandidateIdByOfferId(id);



        Candidate candidate = candidateRepository.findById(idCandidate).orElse(null);


        Offer offer = offerService.findOfferById(id);
        if (offer == null) {
            return "redirect:/offers"; // Redirect nếu không tìm thấy offer
        }

        // Truy vấn danh sách reviews
        List<Reviews> reviews = reviewsRepository.findByCandidateId(idCandidate);

        model.addAttribute("candidate", candidate);
        model.addAttribute("reviews", reviews);
        model.addAttribute("offer", offer);

        return "/Offer/EditOfferDetail"; // Tên view
    }


    @PostMapping("/offers/update")
    public String updateOffer(@RequestParam Long id,
                              @RequestParam LocalDate startDate,
                              @RequestParam String salary,
                              @RequestParam String statusBan,
                              RedirectAttributes redirectAttributes) {

        // Xác định ID của người dùng được tạo
        long createdUserId = 1; // Giá trị giả định, cần thay đổi theo ứng dụng thực tế

        // Cập nhật các trường của offer
        int updated = offerRepository.updateOfferFields(id, startDate, salary, statusBan, createdUserId);

        // Nếu cập nhật thất bại
        if (updated == 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update offer. Invalid offer ID.");
            return "redirect:/offers/" + id + "/detail";
        }

        // Truy xuất ID candidate liên kết với offer
        Long idCandidate = offerRepository.findCandidateIdByOfferId(id);
        Candidate candidate = candidateRepository.findById(idCandidate).orElse(null);

        if (candidate == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Candidate not found!");
            return "redirect:/offers/" + id + "/detail";
        }

        // Truy vấn danh sách reviews của candidate
        List<Reviews> reviews = reviewsRepository.findByCandidateId(idCandidate);

        // Lưu thông báo thành công
        redirectAttributes.addFlashAttribute("message", "Offer updated successfully.");
        redirectAttributes.addFlashAttribute("candidate", candidate);
        redirectAttributes.addFlashAttribute("reviews", reviews);

        // Chuyển hướng về trang chi tiết offer
        return "redirect:/offers/" + id + "/detail";
    }


    @PostMapping("/offers/{id}/deactivate")
    @ResponseBody
    public ResponseEntity<Void> deactivateOffer(@PathVariable Long id) {
        Offer offer = offerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid offer ID"));
        offer.setStatusBan("0");  // Set status to "Deactive"
        offerRepository.save(offer);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/offers/{id}/activate")
    @ResponseBody
    public ResponseEntity<Void> activateOffer(@PathVariable Long id) {
        Offer offer = offerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid offer ID"));
        offer.setStatusBan("1");  // Set status to "Active"
        offerRepository.save(offer);
        return ResponseEntity.ok().build();
    }

}
