package org.example.mock.Repository;

import org.example.mock.Model.CandidateStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateStatusRepository extends JpaRepository<CandidateStatus, Long> {
}
