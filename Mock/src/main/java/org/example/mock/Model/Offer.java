package org.example.mock.Model;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApproveStatus  status = ApproveStatus.PENDING;
    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getOfferDate() {
        return offerDate;
    }

    public void setOfferDate(LocalDateTime offerDate) {
        this.offerDate = offerDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public User getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(User createdUser) {
        this.createdUser = createdUser;
    }

    public User getUpdatedUser() {
        return updatedUser;
    }

    public void setUpdatedUser(User updatedUser) {
        this.updatedUser = updatedUser;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ApproveStatus getStatus() {
        return status;
    }

    public void setStatus(ApproveStatus status) {
        this.status = status;
    }

    public Offer() {
    }

    public Offer(Long id, LocalDateTime offerDate, LocalDate startDate, String salary, String terms, Candidate candidate, User createdUser, User updatedUser, LocalDateTime updatedAt, ApproveStatus status) {
        this.id = id;
        this.offerDate = offerDate;
        this.startDate = startDate;
        this.salary = salary;
        this.terms = terms;
        this.candidate = candidate;
        this.createdUser = createdUser;
        this.updatedUser = updatedUser;
        this.updatedAt = updatedAt;
        this.status = status;
    }
}
