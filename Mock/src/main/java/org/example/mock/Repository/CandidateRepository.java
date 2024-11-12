package org.example.mock.Repository;

import org.example.mock.Model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    Optional<Candidate> findFirstByVacancyId(Long vacancyId); // Thêm phương thức tùy chỉnh này
    @Query("SELECT c FROM Candidate c " +
            "LEFT JOIN CandidateStatus cs ON c.id = cs.candidate.id " +
            "WHERE (:status IS NULL OR :status = '' OR cs.statusName = :status) " +
            "AND (:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "ORDER BY c.submitDate DESC")
    List<Candidate> findCandidatesByStatusAndSearch(@Param("status") String status,
                                                    @Param("search") String search);

}

