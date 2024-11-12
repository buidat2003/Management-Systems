package org.example.mock.Service;

import org.example.mock.Model.User;
import org.example.mock.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}
