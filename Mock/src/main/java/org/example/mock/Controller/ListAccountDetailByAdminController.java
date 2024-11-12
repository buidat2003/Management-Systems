package org.example.mock.Controller;

import org.example.mock.Model.User;
import org.example.mock.Repository.UserRepository;
import org.example.mock.Repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class ListAccountDetailByAdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    // Get users with filter options
    @GetMapping("/users")
    public String getUsers(
            @RequestParam(value = "position", required = false) String position,
            @RequestParam(value = "status", required = false) Boolean status,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "display", required = false) Integer display,
            Model model
    ) {
        List<User> users;

        if (position != null && !position.isEmpty()) {
            users = userRepository.findByrole(position);
        } else if (status != null) {
            users = userRepository.findByStatus(status);
        } else if (search != null && !search.isEmpty()) {
            users = userRepository.findByNameContainingOrEmailContaining(search, search);
        } else {
            users = userRepository.findAll();
        }

        // Limit the number of displayed users
        if (display != null) {
            users = users.stream().limit(display).toList();
        }

        model.addAttribute("users", users);
        return "/Admin/AccountList";
    }

    // Get user edit form
    @GetMapping("/users/{id}/edit")
    public String editUser(@PathVariable("id") Long id, Model model) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("departments", departmentRepository.findAll());  // Add departments for dropdown
        } else {
            model.addAttribute("errorMessage", "User not found");
            return "/Admin/EditAccount";  // Return to edit page with error message
        }
        return "/Admin/EditAccount";  // Ensure this matches your HTML page name
    }

    // Handle the update user request
    @PostMapping("/users/{id}/updateStatus")
    public String updateStatus(@PathVariable Long id, @RequestParam Boolean status) {
        // Lấy thông tin người dùng từ cơ sở dữ liệu
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Cập nhật trạng thái của người dùng
            user.setStatus(status);
            // Lưu lại thay đổi vào cơ sở dữ liệu
            userRepository.save(user);
        }

        // Chuyển hướng về danh sách người dùng hoặc trang chi tiết người dùng
        return "redirect:/users/{id}/edit"; // Hoặc trang khác nếu cần
    }

}
