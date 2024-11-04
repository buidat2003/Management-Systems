package org.example.mock.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.*;


@Entity
@NoArgsConstructor
@AllArgsConstructor
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

    @Lob
    private byte[] file;

    @Column(nullable = false, length = 50)
    private String filetype;

    @Column(name = "is_employ", nullable = false)
    private Boolean isEmploy;

    @ManyToOne
    @JoinColumn(name = "employed_user_id")
    private User employedUser;

    @Column(name = "is_mail_sent", nullable = false)
    private Boolean isMailSent;

    @Column(name = "is_recall")
    private Boolean isRecall;

    @ManyToOne
    @JoinColumn(name = "vacancy_id", nullable = false)
    private Vacancy vacancy;
}
