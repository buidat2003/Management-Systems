package org.example.mock.Repository.Admin;

import org.example.mock.Model.ApproveStatus;
import org.example.mock.Model.Offer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Offer o SET o.startDate = :startDate, o.salary = :salary, o.terms = :terms, "
            + "o.statusBan = :statusBan, o.updatedUser.id = :updatedUserId, o.updatedAt = CURRENT_TIMESTAMP "
            + "WHERE o.id = :id")
    int updateOfferFields(Long id, LocalDate startDate, String salary, String terms, String statusBan, Long updatedUserId);

    Page<Offer> findByCandidateNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Offer> findByStatus(ApproveStatus status, Pageable pageable);
    Page<Offer> findByCandidateNameContainingIgnoreCaseAndStatus(String name, ApproveStatus status, Pageable pageable);
}
