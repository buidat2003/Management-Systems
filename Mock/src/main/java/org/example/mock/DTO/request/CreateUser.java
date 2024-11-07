package org.example.mock.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
@Getter
@Data
public class CreateUser {
    private Long id;
    @NotBlank(message = "Name is required!")
    private String name;

    @Pattern(regexp = "[a-zA-Z0-9@.]{5,30}", message = "Email has an invalid format!")
    private String email;

    @NotBlank(message = "Username is required!")
    private String username;

    @Length(min = 8, max = 12, message = "Password length must be between {min} and {max}")
    private String password;

    private LocalDate doB;

    @Pattern(regexp = "\\d{10}", message = "Phone number is invalid!")
    private String phone;

    private String address;

    private String gender;  // Use String here for gender field

    private String role;

    private Boolean status;

    private Long departmentId;  // Department ID instead of the Department object

    private MultipartFile avatar;
}
