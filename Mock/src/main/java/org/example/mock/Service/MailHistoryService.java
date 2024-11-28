package org.example.mock.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.example.mock.Model.MailHistory;
import org.example.mock.Model.Offer;
import org.example.mock.Model.User;
import org.example.mock.Repository.MailHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class MailHistoryService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MailHistoryRepository mailHistoryRepository;

    @Autowired
    private ApprovalEmailContentGenerator emailContentGenerator;

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


    public void sendApprovalEmail(Offer offer, String recipientEmail) throws MessagingException {
        // Render nội dung email từ Thymeleaf
        String emailContent = emailContentGenerator.generateApprovalEmailContent(offer);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        helper.setTo(recipientEmail);
        helper.setSubject("Offer Approval Notification");
        helper.setText(emailContent, true); // true để cho phép gửi nội dung HTML

        mailSender.send(mimeMessage);
    }

//    //Send offer
//    public void sendEmail(String to, String subject, String body) throws MessagingException {
//        String emailContent = emailContentGenerator.generateApprovalEmailContent(offer);
//        MimeMessage mimeMessage = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
//
//        helper.setTo(to);
//        helper.setSubject(subject);
//        helper.setText(body, true); // Set true để hỗ trợ HTML
//
//        mailSender.send(mimeMessage);
//    }
}
