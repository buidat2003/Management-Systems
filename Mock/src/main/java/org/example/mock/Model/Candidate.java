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
@Table(name = "candidate")
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private LocalDate dob;

    @Column(nullable = false)
    private Boolean gender;

    @Column(nullable = false, length = 15)
    private String phone;

    @Column(nullable = false, length = 100)
    private String education;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(name = "tech_skill", nullable = false, length = 100)
    private String techSkill;

    @Column(name = "language_skill", nullable = false, length = 100)
    private String languageSkill;

    @Column(name = "main_tech", nullable = false, length = 100)
    private String mainTech;

    @Column(nullable = false, length = 50)
    private String exp;

    @Column(nullable = false, length = 50)
    private String level;

    @Column(name = "expected_salary", nullable = false, length = 50)
    private String expectedSalary;

    @Column(name = "submit_date", nullable = false)
    private LocalDate submitDate;

    @Column(name = "submit_time", nullable = false)
    private LocalTime submitTime;

    @Column(name = "is_employ", nullable = false)
    private Boolean isEmploy;

    @ManyToOne
    @JoinColumn(name = "employed_user_id")
    private User employedUser;

    @Column(name = "is_mail_sent", nullable = false)
    private Boolean isMailSent;

    @Column(name = "is_recall")
    private Boolean isRecall;
    @Column(name = "cv_path", length = 255)
    private String cvPath;
    @ManyToOne
    @JoinColumn(name = "vacancy_id", nullable = false)
    private Vacancy vacancy;

    @PrePersist
    public void prePersist() {
        if (isEmploy == null) {
            isEmploy = false; // Giá trị mặc định
        }
        if (isMailSent == null) {
            isMailSent = false; // Giá trị mặc định
        }
        if (isRecall == null) {
            isRecall = false; // Giá trị mặc định nếu không có yêu cầu
        }
        if (submitDate == null) {
            submitDate = LocalDate.now(); // Ngày hiện tại
        }
        if (submitTime == null) {
            submitTime = LocalTime.now(); // Thời gian hiện tại
        }
    }
}
