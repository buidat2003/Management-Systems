package org.example.mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderExample {
    public static void main(String[] args) {
        // Khởi tạo BCryptPasswordEncoder
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Mã hóa mật khẩu "password123"
        String rawPassword = "password123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // In ra mật khẩu đã mã hóa
        System.out.println("Mật khẩu đã mã hóa: " + encodedPassword);

        // So sánh mật khẩu nhập vào và mật khẩu đã mã hóa
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
        System.out.println("Mật khẩu khớp: " + matches);
    }
}
