package org.example.mock.Service;

import org.example.mock.Model.InterviewSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailScheduleService {
    @Autowired
    private JavaMailSender mailSender;
    public void sendInterviewScheduleEmail(InterviewSchedule schedule) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(schedule.getCandidate().getEmail());
        message.setSubject("Lịch Phỏng Vấn - " + schedule.getCandidate().getName());

        String emailContent = "Chào bạn, chúng tôi đến từ công ty ACE\n\n" +
                "Bạn đã được lên lịch phỏng vấn với thông tin sau:\n" +
                "Ngày: " + schedule.getScheduleDate() + "\n" +
                "Thời gian: " + schedule.getScheduleTime() + "\n" +
                "Người phỏng vấn: " + schedule.getInterviewer().getName() + "\n" +
                "Link Google Meet: " + schedule.getGoogleMeetLink() + "\n\n" +
                "Trân trọng,\nĐội ngũ tuyển dụng ACE";

        message.setText(emailContent);
        mailSender.send(message);
    }

//    public void sendInterviewScheduleEmail(InterviewSchedule schedule) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(schedule.getCandidate().getEmail()); // Địa chỉ email của ứng viên
//        message.setSubject("Lịch Phỏng Vấn - " + schedule.getCandidate().getName());
//
//        String emailContent = "Chào bạn, chúng tôi đến từ công ty ACE.\n\n" +
//                "Bạn đã được lên lịch phỏng vấn với thông tin sau:\n\n" +
//                "Ngày: " + schedule.getScheduleDate() + "\n" +
//                "Thời gian: " + schedule.getScheduleTime() + "\n" +
//                "Người phỏng vấn: " + schedule.getInterviewer().getName() + "\n" +
//                "Link Google Meet: " + schedule.getGoogleMeetLink() + "\n\n" +
//                "Trân trọng,\nĐội ngũ tuyển dụng của ACE Group";
//
//        message.setText(emailContent);
//
//        mailSender.send(message);
//    }
}
