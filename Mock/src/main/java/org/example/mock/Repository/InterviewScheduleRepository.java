package org.example.mock.Repository;

import org.example.mock.Model.InterviewSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface InterviewScheduleRepository extends JpaRepository<InterviewSchedule, Long> {

    @Query("SELECT s FROM InterviewSchedule s WHERE s.candidate.id = :candidateId")
    InterviewSchedule findByCandidateId(@Param("candidateId") Long candidateId);
    @Query("SELECT i FROM InterviewSchedule i WHERE i.interviewer.id = :interviewerId AND " +
            "i.scheduleDate = :scheduleDate AND i.scheduleTime = :scheduleTime")
    List<InterviewSchedule> findByInterviewerAndScheduleDateAndTime(@Param("interviewerId") Long interviewerId,
                                                                    @Param("scheduleDate") LocalDate scheduleDate,
                                                                    @Param("scheduleTime") LocalTime scheduleTime);
    @Query("SELECT i FROM InterviewSchedule i WHERE i.interviewer.id = :interviewerId AND i.scheduleDate = :scheduleDate")
    List<InterviewSchedule> findByInterviewerAndScheduleDate(@Param("interviewerId") Long interviewerId,
                                                             @Param("scheduleDate") LocalDate scheduleDate);


}
