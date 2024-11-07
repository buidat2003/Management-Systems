package org.example.mock.Controller;

import lombok.RequiredArgsConstructor;
import org.example.mock.DTO.request.CreateUser;
import org.example.mock.Repository.Admin.AdminCustomRepositoryImplement;
import org.example.mock.Repository.Admin.AdminRepository;
import org.example.mock.Service.AdminService.AdminServiceImplement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminServiceImplement adminService;
    private AdminRepository adminRepository;

    @GetMapping("/getForm")
    public String getForm(Model model) {
        CreateUser createUser = new CreateUser();
        model.addAttribute("user", createUser);
        return "Admin/createAccount";
    }

    @PostMapping("/addAccount")
    public String addAccount(@Validated @ModelAttribute("user") CreateUser createUser,
                             BindingResult result,
                             @RequestParam("avatar") MultipartFile avatar,
                             Model model) {

        if(adminRepository.existsByEmail(createUser.getEmail()) ) {
            result.
                    addError(new FieldError("user",
                            "email",
                            "Email is already in use!"));
        }
        if(adminRepository.existsByUsername(createUser.getUsername()) ) {
            result.
                    addError(new FieldError("user",
                            "username",
                            "Username is already in use!"));

        }
        if(adminRepository.existsByPhoneNumber(createUser.getPhone()) ) {
            result.
                    addError(new FieldError("user",
                            "phone",
                            "Phone number is already in use!"));

        }
        if (result.hasErrors()) {
            // Return back to form if there are validation errors
            return "Admin/createAccount";
        }

        try {
            adminService.addUser(createUser, avatar);
        } catch (IOException e) {
            model.addAttribute("errorMessage", "Error while uploading file: " + e.getMessage());
            return "Admin/Error";
        }

        return "Admin/AccountList";  // Redirect after successful form submission
    }

    @GetMapping("update/{id}")
    public String getUpdateForm(){
        CreateUser createUser = new CreateUser();

        return "Admin/updateAccount";
    }

}
