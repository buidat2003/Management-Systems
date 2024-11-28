package org.example.mock.Service;

import org.example.mock.Model.*;
import org.example.mock.Repository.CandidateStatusRepository;
import org.example.mock.Repository.InterviewScheduleRepository;
import org.example.mock.Repository.ReviewsRepository;
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
    private CandidateStatusRepository candidateStatusRepository;

    @Autowired
    private GoogleMeetService googleMeetService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailScheduleService emailService;

    // Autowiring the ReviewsRepository
    @Autowired
    private ReviewsRepository reviewsRepository;  // This was missing

    public InterviewSchedule createInterviewSchedule(Candidate candidate, LocalDate date, LocalTime time, Long interviewerId) {
        User interviewer = userService.findById(interviewerId);

        // Create Google Meet link with start and end time
        LocalDateTime startDateTime = LocalDateTime.of(date, time);
        LocalDateTime endDateTime = startDateTime.plusHours(1); // Example interview duration is 1 hour

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

        // Send email with interview schedule information
        emailService.sendInterviewScheduleEmail(schedule);
        emailService.sendInterviewScheduleEmailToInterviewer(schedule);

        // Create or update the candidate's status to "Scheduled"
//        updateCandidateStatus(candidate, "Đang chờ");

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

        // Get all interview schedules for the interviewer on that date
        List<InterviewSchedule> schedules = interviewScheduleRepository.findByInterviewerAndScheduleDate(interviewerId, date);

        for (InterviewSchedule schedule : schedules) {
            LocalDateTime existingInterviewDateTime = LocalDateTime.of(schedule.getScheduleDate(), schedule.getScheduleTime());
            Duration duration = Duration.between(existingInterviewDateTime, newInterviewDateTime);

            // Check if the time gap is at least 20 minutes
            if (Math.abs(duration.toMinutes()) < 20) {
                return false;  // Not available if the gap is less than 20 minutes
            }
        }
        return true; // Available if no schedules conflict within 20 minutes
    }

    // Method to update candidate status
    public void updateCandidateStatus(Candidate candidate, String status) {
        List<CandidateStatus> existingStatus = candidateStatusRepository.findStatusByCandidateIdAndStatusName(candidate.getId(), status);
        if (existingStatus.isEmpty()) {
            CandidateStatus candidateStatus = new CandidateStatus();
            candidateStatus.setCandidate(candidate);
            candidateStatus.setStatusName(status);
            candidateStatus.setUpdatedAt(LocalDateTime.now());
            candidateStatusRepository.save(candidateStatus);
        }
    }

    // Mark as Interviewed
    public void markAsInterviewed(Long scheduleId) {
        InterviewSchedule schedule = interviewScheduleRepository.findById(scheduleId).orElse(null);
        if (schedule != null) {
            Candidate candidate = schedule.getCandidate();
            updateCandidateStatus(candidate, "Đã phỏng vấn");
        }
    }

    // Mark as Canceled if the time has passed
    public void markAsCanceled(Long scheduleId) {
        InterviewSchedule schedule = interviewScheduleRepository.findById(scheduleId).orElse(null);
        if (schedule != null) {
            Candidate candidate = schedule.getCandidate();
            updateCandidateStatus(candidate, "Đã hủy");
        }
    }

    public void update(InterviewSchedule schedule) {
        interviewScheduleRepository.save(schedule);
    }

    public List<Reviews> getReviewsBySchedule(Long scheduleId) {
        return reviewsRepository.findByCandidateId(scheduleId); // Now it can fetch reviews properly
    }

    public void saveReview(Reviews review) {
        reviewsRepository.save(review);
    }
}
