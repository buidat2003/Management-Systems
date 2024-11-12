package org.example.mock.Repository;

import org.example.mock.Model.CandidateStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CandidateStatusRepository extends JpaRepository<CandidateStatus, Long> {
    @Query("SELECT cs FROM CandidateStatus cs WHERE cs.candidate.id = :candidateId ORDER BY cs.updatedAt DESC")
    List<CandidateStatus> findStatusHistoryByCandidateId(@Param("candidateId") Long candidateId);

}
