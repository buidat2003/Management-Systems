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
@Table(name = "reviews")
public class Reviews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @ManyToOne
    @JoinColumn(name = "interviewer_id", nullable = false)
    private User interviewer;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false)
    private Integer rating;

    @Column(name = "review_date", nullable = false)
    private LocalDateTime reviewDate;
}
