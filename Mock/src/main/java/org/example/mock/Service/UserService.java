package org.example.mock.Service;

import org.example.mock.Model.Department;
import org.example.mock.Model.User;
import org.example.mock.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
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

    public User verifyUser(String username, String email) {
        return userRepository.findByUsernameAndEmail(username, email)
                .orElse(null);
    }

    // Updates user's password
    public boolean updatePassword(String email, String newPassword) {
        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email này"));

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);

        logger.info("Password updated for user with email {}", email);
        return true;
    }

    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public Department getDepartmentById(Integer departmentId) {
        return null;
    }

    public boolean changePassword(String username, String currentPassword, String newPassword) {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (user.getPassword().equals(currentPassword)) {
                user.setPassword(newPassword);
                return true;
            }
        }

        return false;
    }

    public List<User> getInterviewers() {
        return userRepository.findByRole("INTERVIEWER");
    }
}
