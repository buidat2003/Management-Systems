package org.example.mock.Model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.*;

@Entity
@Table(name = "candidate_status")
public class CandidateStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @Column(name = "status_name", nullable = false)
    private String statusName;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
