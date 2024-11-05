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
@Table(name = "meeting")
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "meeting_time", nullable = false)
    private LocalDateTime meetingTime;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String agenda;

    @ManyToOne
    @JoinColumn(name = "vacancy_id")
    private Vacancy vacancy;
}
