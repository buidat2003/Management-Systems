package org.example.mock.Controller;

import org.example.mock.Model.User;
import org.example.mock.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
public class ViewEditProfileController {

    @Autowired
    private UserService userService;

    // Display user profile based on user ID
    @GetMapping("/profile")
    public String showUserProfile(Model model) {
        User user = userService.getUserById(1L);  // Get user info by ID (1L is just an example)
        model.addAttribute("user", user);  // Add user data to model
        return "Admin/profile";  // Return the profile page
    }

    // Save updated user profile information
    @PostMapping("/profile/editprofile")
    public String saveUserProfile(@RequestParam("id") Long id,
                                  @RequestParam("name") String name,
                                  @RequestParam("email") String email,
                                  @RequestParam("phone") String phone,
                                  @RequestParam("address") String address,
                                  @RequestParam("status") Integer status,
                                  @RequestParam("dob") String dob,
                                  @RequestParam("gender") String gender,
                                  Model model) {

        // Convert status to Boolean (1 for Active, 0 for Inactive)
        Boolean statusBoolean = (status == 1);

        // Get user information from the service
        User user = userService.getUserById(id);

        // Update user information
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setAddress(address);
        user.setStatus(statusBoolean);  // Update status as Boolean
        user.setGender(gender);

        // If date of birth is provided, update it
        if (dob != null && !dob.isEmpty()) {
            LocalDate dateOfBirth = LocalDate.parse(dob);  // Convert string to LocalDate
            user.setDoB(dateOfBirth);
        }

        // Save the updated user information
        userService.saveUser(user);

        // Add the updated user to the model for displaying on the profile page
        model.addAttribute("user", user);

        // Return the updated profile page
        return "Admin/profile";
    }

    @GetMapping("/changepassword/{username}")
    public String showChangePasswordPage(@PathVariable("username") String username, Model model) {
        // Kiểm tra nếu có thông báo lỗi hoặc thành công từ Model
        if (model.containsAttribute("error")) {
            model.addAttribute("error", model.getAttribute("error"));
        }
        if (model.containsAttribute("message")) {
            model.addAttribute("message", model.getAttribute("message"));
        }

        // Trả về trang thay đổi mật khẩu với username trong URL
        model.addAttribute("username", username);  // Thêm username vào model để hiển thị trong form
        return "Admin/changepassword";  // Trả về view thay đổi mật khẩu
    }



    // Xử lý thay đổi mật khẩu
    @PostMapping("/changepassword/submit")
    public String changePassword(@RequestParam("current-password") String currentPassword,
                                 @RequestParam("new-password") String newPassword,
                                 @RequestParam("confirm-password") String confirmPassword,
                                 @RequestParam("username") String username,
                                 RedirectAttributes redirectAttributes) {

        // Kiểm tra mật khẩu xác nhận với mật khẩu mới
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New password and confirm password do not match.");
            return "redirect:/changepassword/" + username;  // Redirect với flash attribute
        }

        // Kiểm tra độ dài mật khẩu mới (ít nhất 6 ký tự)
        if (newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "New password must be at least 6 characters long.");
            return "redirect:/changepassword/" + username;
        }

        // Kiểm tra mật khẩu mới có ít nhất một chữ số
        if (!newPassword.matches(".*[0-9].*")) {
            redirectAttributes.addFlashAttribute("error", "New password must contain at least one number.");
            return "redirect:/changepassword/" + username;
        }

        // Gọi service để xử lý thay đổi mật khẩu
        boolean isPasswordChanged = userService.changePassword(username, currentPassword, newPassword);

        if (isPasswordChanged) {
            redirectAttributes.addFlashAttribute("message", "Password changed successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to change password. Please try again.");
        }

        return "redirect:/changepassword/" + username;  // Redirect về trang thay đổi mật khẩu
    }



}
