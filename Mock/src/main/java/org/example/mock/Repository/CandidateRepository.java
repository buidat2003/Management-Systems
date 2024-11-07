package org.example.mock.Repository;

import org.example.mock.Model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    Optional<Candidate> findFirstByVacancyId(Long vacancyId); // Thêm phương thức tùy chỉnh này
}

