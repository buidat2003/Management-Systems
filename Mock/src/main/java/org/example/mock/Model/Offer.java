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
@Table(name = "offer")
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "offer_date", nullable = false)
    private LocalDateTime offerDate;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "salary", nullable = false, length = 50)
    private String salary;

    @Column(name = "terms", columnDefinition = "TEXT")
    private String terms;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @ManyToOne
    @JoinColumn(name = "created_user_id", nullable = false)
    private User createdUser;

    @ManyToOne
    @JoinColumn(name = "updated_user_id")
    private User updatedUser;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
