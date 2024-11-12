package org.example.mock.Repository;

import org.example.mock.Model.InterviewSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InterviewScheduleRepository extends JpaRepository<InterviewSchedule, Long> {

    @Query("SELECT s FROM InterviewSchedule s WHERE s.candidate.id = :candidateId")
    InterviewSchedule findByCandidateId(@Param("candidateId") Long candidateId);

}
