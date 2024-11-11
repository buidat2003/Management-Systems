package org.example.mock.Controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.mock.DTO.request.CreateUser;
import org.example.mock.Model.User;
import org.example.mock.Repository.Admin.AdminRepository;
import org.example.mock.Repository.UserRepository;
import org.example.mock.Service.AdminService.AdminServiceImplement;
import org.springframework.beans.BeanUtils;
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
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private UserRepository userRepository;

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

        if (adminRepository.existsByEmail(createUser.getEmail())) {
            result.
                    addError(new FieldError("user",
                            "email",
                            "Email is already in use!"));
        }
        if (adminRepository.existsByUsername(createUser.getUsername())) {
            result.
                    addError(new FieldError("user",
                            "username",
                            "Username is already in use!"));

        }
        if (adminRepository.existsByPhone(createUser.getPhone())) {
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

    @GetMapping("/getUpdateForm/{id}")
    public String getUpdateForm(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            throw new EntityNotFoundException("Not found entity with id: " + id);
        });
        CreateUser createUser = new CreateUser();
        BeanUtils.copyProperties(user, createUser);
        model.addAttribute("user", createUser);
        createUser.setId(id);
        return "Admin/EditAccount";
    }

    @PostMapping("/update")
    public String updateAccount(@Validated @ModelAttribute("user") CreateUser createUser,
                                BindingResult result,
//                                @RequestParam("avatar") MultipartFile avatar,
                                Model model) {
        User updateUser = userRepository.findById(createUser.getId()).orElseThrow(() -> {
            throw new EntityNotFoundException("Not found entity with id: " + createUser.getId());
        });
        if(adminRepository.existsByEmail(createUser.getEmail()) &&
        !createUser.getEmail().equals(updateUser.getEmail()) ) {
            result.
                    addError(new FieldError("user",
                            "email",
                            "Email is already in use!"));
        }
        if(adminRepository.existsByUsername(createUser.getUsername()) &&
        !createUser.getUsername().equals(updateUser.getUsername()) ) {
            result.
                    addError(new FieldError("user",
                            "username",
                            "Username is already in use!"));

        }
        if(adminRepository.existsByPhone(createUser.getPhone()) &&
        !createUser.getPhone().equals(updateUser.getPhone()) ) {
            result.
                    addError(new FieldError("user",
                            "phone",
                            "Phone number is already in use!"));

        }
        if (result.hasErrors()) {
            // Return back to form if there are validation errors
            return "Admin/update";
        }


        BeanUtils.copyProperties(createUser, updateUser, "id");
        userRepository.save(updateUser);

        return "Admin/AccountList";
    }

}
