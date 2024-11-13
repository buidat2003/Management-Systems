package org.example.mock.Controller;


import org.example.mock.Model.Offer;
import org.example.mock.Service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ListOfferController {

    private final OfferService offerService;

    @Autowired
    public ListOfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @GetMapping("/offers")
    public String showOffers(Model model) {
        // Lấy tất cả các offer từ database
        List<Offer> offers = offerService.getAllOffers();

        // Thêm danh sách offer vào model để hiển thị trong view
        model.addAttribute("offers", offers);

        return "/Offer/ListOffer"; // Trả về tên view sẽ hiển thị danh sách offer
    }

    @GetMapping("/offers/{id}/edit")
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
}
