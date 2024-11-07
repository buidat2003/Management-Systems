package org.example.mock.Repository;

import org.example.mock.Model.InterviewSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewScheduleRepository extends JpaRepository<InterviewSchedule, Long> {
}
