package org.example.mock.Repository;

import org.example.mock.Model.CandidateStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CandidateStatusRepository extends JpaRepository<CandidateStatus, Long> {

    @Query("SELECT cs FROM CandidateStatus cs WHERE cs.candidate.id = :candidateId ORDER BY cs.updatedAt DESC")
    List<CandidateStatus> findStatusHistoryByCandidateId(@Param("candidateId") Long candidateId);

    @Query("SELECT cs FROM CandidateStatus cs WHERE cs.candidate.id = :candidateId AND cs.statusName = :statusName ORDER BY cs.updatedAt DESC")
    List<CandidateStatus> findStatusByCandidateIdAndStatusName(@Param("candidateId") Long candidateId, @Param("statusName") String statusName);

    // New method: Find the most recent status by candidate ID
    @Query("SELECT cs FROM CandidateStatus cs WHERE cs.candidate.id = :candidateId ORDER BY cs.updatedAt DESC")
    CandidateStatus findByCandidateId(@Param("candidateId") Long candidateId);
}
