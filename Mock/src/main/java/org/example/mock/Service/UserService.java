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

    // Verifies user by username and email
    public User verifyUser(String username, String email) {
        return userRepository.findByUsernameAndEmail(username, email)
                .orElse(null);
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
        return true;
    }

    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }

    public void saveUser(User user) {
        userRepository.save(user);
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
            User user = optionalUser.get();

            // Check if the current password matches
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


    public String getGoogleCalendarLinkById(Long interviewerId) {
        return userRepository.findById(interviewerId)
                .map(User::getGoogleCalendarLink)
                .orElse(null);
    }
    @Autowired
    private GoogleCalendarService googleCalendarService;

    public void updateGoogleCalendarLinkForAllInterviewers() {
        // Lấy danh sách người dùng có vai trò INTERVIEWER
        List<User> interviewers = userRepository.findByRole("INTERVIEWER");

        for (User interviewer : interviewers) {
            try {
                // Lấy danh sách lịch của từng INTERVIEWER dựa trên email
                List<String> calendars = googleCalendarService.getCalendarsByEmail(interviewer.getEmail());

                if (!calendars.isEmpty()) {
                    // Gắn link Google Calendar vào đối tượng user
                    interviewer.setGoogleCalendarLink(calendars.get(0));
                    userRepository.save(interviewer); // Lưu vào DB
                    System.out.println("Updated calendar link for INTERVIEWER: " + interviewer.getName());
                } else {
                    System.out.println("No calendar link found for INTERVIEWER: " + interviewer.getName());
                }
            } catch (Exception e) {
                System.err.println("Error updating calendar for INTERVIEWER " + interviewer.getEmail() + ": " + e.getMessage());
            }
        }
    }


}
