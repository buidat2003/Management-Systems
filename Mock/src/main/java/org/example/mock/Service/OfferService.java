package org.example.mock.Service;

import org.example.mock.Model.ApproveStatus;
import org.example.mock.Model.Offer;
import org.example.mock.Repository.Admin.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Service
public class OfferService {

    private final OfferRepository offerRepository;

    @Autowired
    public OfferService(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    public List<Offer> getAllOffers() {
        return offerRepository.findAll();  // Trả về tất cả các offer
    }

    public Offer findOfferById(Long id) {
        Optional<Offer> offerOptional = offerRepository.findById(id); // Tìm kiếm offer theo id
        return offerOptional.orElse(null); // Nếu tìm thấy, trả về offer, nếu không trả về null
    }


    public Offer saveOffer(Offer offer) {
        return offerRepository.save(offer);
    }

    public Page<Offer> getOffersWithPagination(Pageable pageable) {
        return offerRepository.findAll(pageable);
    }

    public Page<Offer> searchOffersByName(String name, Pageable pageable) {
        return offerRepository.findByCandidateNameContainingIgnoreCase(name, pageable);
    }

    public Page<Offer> searchAndFilterOffers(String search, ApproveStatus status, Pageable pageable) {
        if (search != null && !search.isEmpty() && status != null ) {
            return offerRepository.findByCandidateNameContainingIgnoreCaseAndStatus(search, status, pageable);
        } else if (search != null && !search.isEmpty()) {
            return offerRepository.findByCandidateNameContainingIgnoreCase(search, pageable);
        } else if (status != null) {
            return offerRepository.findByStatus(status, pageable);
        } else {
            return offerRepository.findAll(pageable);
        }
    }
}
