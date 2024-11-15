package org.example.mock.Service;

import org.example.mock.Model.Department;
import org.example.mock.Model.User;
import org.example.mock.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);



    @Autowired
    private PasswordEncoder passwordEncoder;

    // Registers a new user
    public void registerUser(User user) {
        // Validate password strength
        if (user.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }

        // Encode password and save user
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);

        logger.info("User registered: {}", user.getUsername());
    }

    // Verifies user by username and email
    public User verifyUser(String username, String email) {
        return userRepository.findByUsernameAndEmail(username, email)
                .orElse(null); // Returns null if no user found
    }

    // Updates user's password
    public boolean updatePassword(String email, String newPassword) {
        // Validate password strength
        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email này"));

        // Encode and set the new password
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);

        logger.info("Password updated for user with email {}", email);
        return true; // Indicating successful update
    }

    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);  // Trả về null nếu không tìm thấy người dùng
    }

    // Cập nhật thông tin người dùng
    public void saveUser(User user) {
        userRepository.save(user);  // Lưu người dùng đã thay đổi vào cơ sở dữ liệu
    }

    // Giả sử bạn cũng có một phương thức để lấy thông tin department
    public Department getDepartmentById(Integer departmentId) {
        // Nếu cần lấy department từ cơ sở dữ liệu, bạn có thể tạo một repository khác cho Department
        return null;
    }

    public boolean changePassword(String username, String currentPassword, String newPassword) {
        // Find the user by username, which returns an Optional<User>
        Optional<User> optionalUser = userRepository.findByUsername(username);

        // Check if the user exists
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();  // Get the User object

            // Check if the current password matches
            if (user.getPassword().equals(currentPassword)) {
                user.setPassword(newPassword);  // Set the new password
                userRepository.save(user);  // Save the updated user
                return true;  // Password successfully changed
            }
        }

        return false;  // Either user not found or current password incorrect
    }

}
