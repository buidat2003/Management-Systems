package org.example.mock.Service;

import org.example.mock.Model.MailHistory;
import org.example.mock.Model.User;
import org.example.mock.Repository.MailHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class MailHistoryService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MailHistoryRepository mailHistoryRepository;


    public String sendRecoveryCode(User user) {
        String code = generateRandomCode();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Password Recovery Code");
        message.setText("Your password recovery code is: " + code);


        mailSender.send(message);
        MailHistory mailHistory = new MailHistory();mailHistory.setUser(user);
        mailHistory.setSubject("Password Recovery Code");
        mailHistory.setContent("Your password recovery code is: " + code);
        mailHistory.setSentAt(LocalDateTime.now());mailHistoryRepository.save(mailHistory);

        return code;}
    String generateRandomCode() {
        Random random = new Random();int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
