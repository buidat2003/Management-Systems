package org.example.mock.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer  id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 40)
    private String email;

    @Column(nullable = false, length = 255)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, name = "date_of_birth")
    private LocalDate doB;

    @Column(nullable = false, length = 255)
    private String phone;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false, length = 30)
    private String role;

    @Column(nullable = false)
    private Boolean status;

    private String avatar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

}