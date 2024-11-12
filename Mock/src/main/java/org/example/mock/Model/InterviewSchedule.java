package org.example.mock.Model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.*;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "interviewer_schedule")
public class InterviewSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "schedule_date", nullable = false)
    private LocalDate scheduleDate;

    @Column(name = "schedule_time", nullable = false)
    private LocalTime scheduleTime;

    @ManyToOne
    @JoinColumn(name = "interviewer_id", nullable = false)
    private User interviewer;

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private Candidate candidate; // Thiết lập khóa ngoại đến bảng Candidate

    @Column(name = "google_meet_link", length = 500)
    private String googleMeetLink; // Trường mới để lưu liên kết Google Meet

}
