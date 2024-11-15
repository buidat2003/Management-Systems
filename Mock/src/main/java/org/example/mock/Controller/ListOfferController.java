package org.example.mock.Controller;


import org.example.mock.Model.Offer;
import org.example.mock.Repository.Admin.OfferRepository;
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
        Offer offer = offerService.findOfferById(id);
        if (offer != null) {
            model.addAttribute("offer", offer);
            return "/Offer/EditOfferDetail";  // Tên view hiển thị chi tiết offer
        } else {
            // Nếu không tìm thấy offer với id đó, bạn có thể redirect về trang danh sách
            return "/Offer/EditOfferDetail";
        }
    }

    @PostMapping("/offers/update")
    public String updateOffer(@RequestParam Long id,
                              @RequestParam LocalDate startDate,
                              @RequestParam String salary,
                              @RequestParam String terms,
                              @RequestParam String statusBan,
                              RedirectAttributes redirectAttributes) {

        long createdUserId = 1;
        int updated = offerRepository.updateOfferFields(id, startDate, salary, terms, statusBan, createdUserId);

        if (updated == 0) {
            // Add an error message if the update fails
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update offer. Invalid offer ID.");
            return "redirect:/offers/" + id + "/detail";
        }

        // Add a success message for a successful update
        redirectAttributes.addFlashAttribute("message", "Offer updated successfully.");

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
