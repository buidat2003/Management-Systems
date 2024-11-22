package org.example.mock.Service;

import org.example.mock.Model.Candidate;
import org.example.mock.Model.InterviewSchedule;
import org.example.mock.Model.User;
import org.example.mock.Repository.InterviewScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class InterviewScheduleService {

    @Autowired
    private InterviewScheduleRepository interviewScheduleRepository;
    @Autowired
    private GoogleMeetService googleMeetService;
    @Autowired
    private UserService userService;
    @Autowired
    private EmailScheduleService emailService;

    public InterviewSchedule createInterviewSchedule(Candidate candidate, LocalDate date, LocalTime time, Long interviewerId) {
        User interviewer = userService.findById(interviewerId);

        // Tạo link Google Meet với thời gian bắt đầu và kết thúc
        LocalDateTime startDateTime = LocalDateTime.of(date, time);
        LocalDateTime endDateTime = startDateTime.plusHours(1); // Ví dụ thời gian phỏng vấn là 1 giờ

        String googleMeetLink;
        try {
            googleMeetLink = googleMeetService.createGoogleMeetEvent("Phỏng vấn tại ACE", startDateTime, endDateTime);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Google Meet link", e);
        }

        InterviewSchedule schedule = new InterviewSchedule();
        schedule.setCandidate(candidate);
        schedule.setScheduleDate(date);
        schedule.setScheduleTime(time);
        schedule.setInterviewer(interviewer);
        schedule.setGoogleMeetLink(googleMeetLink);

        interviewScheduleRepository.save(schedule);

        // Gửi email với thông tin lịch phỏng vấn
//        emailService.sendInterviewScheduleEmail(schedule);
        System.out.println("Sending email...");
        emailService.sendInterviewScheduleEmail(schedule);
        System.out.println("Email sent.");
        emailService.sendInterviewScheduleEmailToInterviewer(schedule);
        return schedule;
    }
    public InterviewSchedule findByCandidate(Long candidateId) {
        return interviewScheduleRepository.findByCandidateId(candidateId);
    }
    public List<InterviewSchedule> getAllSchedules() {
        return interviewScheduleRepository.findAll();
    }

    public InterviewSchedule findById(Long id) {
        return interviewScheduleRepository.findById(id).orElse(null);
    }

    public void delete(InterviewSchedule schedule) {
        interviewScheduleRepository.delete(schedule);
    }

    public List<InterviewSchedule> findSchedulesByInterviewerAndTime(Long interviewerId, LocalDate date, LocalTime time) {
        return interviewScheduleRepository.findByInterviewerAndScheduleDateAndTime(interviewerId, date, time);
    }
    public boolean isInterviewerAvailable(Long interviewerId, LocalDate date, LocalTime time) {
        LocalDateTime newInterviewDateTime = LocalDateTime.of(date, time);

        // Lấy tất cả các lịch phỏng vấn của người phỏng vấn trong ngày đó
        List<InterviewSchedule> schedules = interviewScheduleRepository.findByInterviewerAndScheduleDate(interviewerId, date);

        for (InterviewSchedule schedule : schedules) {
            LocalDateTime existingInterviewDateTime = LocalDateTime.of(schedule.getScheduleDate(), schedule.getScheduleTime());
            Duration duration = Duration.between(existingInterviewDateTime, newInterviewDateTime);

            // Kiểm tra khoảng cách thời gian là ít nhất 20 phút
            if (Math.abs(duration.toMinutes()) < 20) {
                return false;  // Không khả dụng nếu khoảng cách dưới 20 phút
            }
        }
        return true; // Khả dụng nếu không có lịch nào trùng trong 20 phút
    }





//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private EmailScheduleService emailService;
//
//    public InterviewSchedule createInterviewSchedule(Candidate candidate, LocalDate date, LocalTime time, Long interviewerId) {
//        User interviewer = userService.findById(interviewerId);
//        if (interviewer == null) {
//            throw new IllegalArgumentException("Interviewer not found with ID: " + interviewerId);
//        }
//
//        String googleMeetLink = "https://meet.google.com/fake-link";
//
//        InterviewSchedule schedule = new InterviewSchedule();
//        schedule.setCandidate(candidate);
//        schedule.setScheduleDate(date);
//        schedule.setScheduleTime(time);
//        schedule.setInterviewer(interviewer);
//        schedule.setGoogleMeetLink(googleMeetLink);
//        interviewScheduleRepository.save(schedule);
//
//        emailService.sendInterviewScheduleEmail(schedule);
//
//        return schedule;
//    }
}
