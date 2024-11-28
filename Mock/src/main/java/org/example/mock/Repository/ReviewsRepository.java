package org.example.mock.Repository;

import org.example.mock.Model.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface ReviewsRepository extends JpaRepository<Reviews, Long> {

    @Query("SELECT r.candidate.id, r.rating " +
            "FROM Reviews r " +
            "WHERE r.candidate.id IN :candidateIds")
    List<Object[]> findRatingsByCandidateIds(@Param("candidateIds") List<Long> candidateIds);
    List<Reviews> findByCandidateId(Long candidateId);

}
