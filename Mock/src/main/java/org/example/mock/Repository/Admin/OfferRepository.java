package org.example.mock.Repository.Admin;

import org.example.mock.Model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Offer o SET o.startDate = :startDate, o.salary = :salary, o.terms = :terms, "
            + "o.statusBan = :statusBan, o.updatedUser.id = :updatedUserId, o.updatedAt = CURRENT_TIMESTAMP "
            + "WHERE o.id = :id")
    int updateOfferFields(Long id, LocalDate startDate, String salary, String terms, String statusBan, Long updatedUserId);
}
