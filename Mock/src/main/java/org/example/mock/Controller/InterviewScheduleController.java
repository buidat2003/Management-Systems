package org.example.mock.Controller;


import org.example.mock.Model.Candidate;
import org.example.mock.Model.CandidateStatus;
import org.example.mock.Model.InterviewSchedule;
import org.example.mock.Model.User;
import org.example.mock.Repository.CandidateStatusRepository;
import org.example.mock.Service.CandidateService;
import org.example.mock.Service.InterviewScheduleService;
import org.example.mock.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

//@RestController//chỉ dành cho api trả về json hoặc chỗi
@Controller

@RequestMapping("/interviewschedules")
public class InterviewScheduleController {

    @Autowired
    private InterviewScheduleService interviewScheduleService;

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private CandidateStatusRepository candidateStatusRepository;
    @Autowired
    private UserService userService;
//    @PostMapping("/create")
//    public String createInterviewSchedule(
//            @RequestParam("candidateId") Long candidateId,
//            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
//            @RequestParam("time") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
//            @RequestParam("interviewerId") Long interviewerId,
//            Model model
//    ) {
//        // Lấy thông tin ứng viên
//        Candidate candidate = candidateService.findById(candidateId);
//        if (candidate == null) {
//            throw new IllegalArgumentException("Candidate not found with ID: " + candidateId);
//        }
//
//        // Tạo lịch phỏng vấn và lưu vào database
//        interviewScheduleService.createInterviewSchedule(candidate, date, time, interviewerId);
//
//        // Lấy danh sách ứng viên sau khi tạo lịch phỏng vấn
//        List<Candidate> candidates = candidateService.getAllCandidates();
//        model.addAttribute("candidates", candidates);
//
//        // Trả về trang chứa danh sách các ứng viên đã tạo lịch phỏng vấn
//        return "Interviewer/interviewschedule";
//    }

//@PostMapping("/create")
//public ResponseEntity<String> createInterviewSchedule(
//        @RequestParam("candidateId") Long candidateId,
//        @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
//        @RequestParam("time") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
//        @RequestParam("interviewerId") Long interviewerId
//) {
//    // Lấy thông tin ứng viên
//    Candidate candidate = candidateService.findById(candidateId);
//    if (candidate == null) {
//        return ResponseEntity.badRequest().body("Candidate not found with ID: " + candidateId);
//    }
//
//    // Kiểm tra xem người phỏng vấn đã có lịch phỏng vấn tại thời gian này chưa
//    List<InterviewSchedule> existingSchedules = interviewScheduleService.findSchedulesByInterviewerAndTime(interviewerId, date, time);
//
//    if (!existingSchedules.isEmpty()) {
//        // Nếu đã có lịch phỏng vấn, trả về lỗi
//        return ResponseEntity.badRequest().body("Interviewer is already scheduled at this time.");
//    }
//
//    // Tạo lịch phỏng vấn và lưu vào database
//    interviewScheduleService.createInterviewSchedule(candidate, date, time, interviewerId);
//
//    return ResponseEntity.ok("Schedule created successfully.");
//}

    //@GetMapping("/interviewers")
//public List<User> getInterviewers() {
//    return userService.getInterviewers(); // Gọi phương thức trong service để lấy interviewers
//}
    @PostMapping("/create")
    public ResponseEntity<String> createInterviewSchedule(
            @RequestParam("candidateId") Long candidateId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("time") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
            @RequestParam("interviewerId") Long interviewerId
    ) {
        // Lấy thông tin ứng viên
        Candidate candidate = candidateService.findById(candidateId);
        if (candidate == null) {
            return ResponseEntity.badRequest().body("Candidate not found with ID: " + candidateId);
        }

        // Kiểm tra xem người phỏng vấn có khả dụng tại thời gian này không
        if (!interviewScheduleService.isInterviewerAvailable(interviewerId, date, time)) {
            return ResponseEntity.badRequest().body("Interviewer is not available within 20 minutes of this time.");
        }

        // Tạo lịch phỏng vấn và lưu vào database
        interviewScheduleService.createInterviewSchedule(candidate, date, time, interviewerId);

        return ResponseEntity.ok("Schedule created successfully.");
    }



    // Lấy danh sách tất cả các lịch phỏng vấn
    @GetMapping("/detail")
    public String getAllInterviewSchedules(Model model) {
        // Lấy danh sách tất cả lịch phỏng vấn từ service
        List<InterviewSchedule> schedules = interviewScheduleService.getAllSchedules();
        model.addAttribute("schedules", schedules);

        // Trả về trang hiển thị danh sách lịch phỏng vấn
        return "Interviewer/interviewschedule";
    }

    @PostMapping("/markAsInterviewed")
    public ResponseEntity<?> markAsInterviewed(@RequestParam("scheduleId") Long scheduleId) {
        InterviewSchedule schedule = interviewScheduleService.findById(scheduleId);
        if (schedule == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Schedule not found");
        }

        CandidateStatus candidateStatus = new CandidateStatus();
        candidateStatus.setCandidate(schedule.getCandidate());
        candidateStatus.setStatusName("Đã phỏng vấn");
        candidateStatus.setUpdatedAt(LocalDateTime.now());
        candidateStatusRepository.save(candidateStatus);

        return ResponseEntity.ok("Status updated to Đã phỏng vấn");
    }

    @DeleteMapping("/deleteInterviewed")
    public ResponseEntity<?> deleteInterviewed(@RequestParam("scheduleId") Long scheduleId) {
        InterviewSchedule schedule = interviewScheduleService.findById(scheduleId);
        if (schedule == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Schedule not found");
        }

        interviewScheduleService.delete(schedule);
        return ResponseEntity.ok("Schedule deleted successfully");
    }

    //api chuẩn


//    @PostMapping("/create")
//    public String createInterviewSchedule(
//            @RequestParam("candidateId") Long candidateId,
//            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
//            @RequestParam("time") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
//            @RequestParam("interviewerId") Long interviewerId,
//            Model model
//    ) {
//        Candidate candidate = candidateService.findById(candidateId);
//        if (candidate == null) {
//            throw new IllegalArgumentException("Candidate not found with ID: " + candidateId);
//        }
//
//        interviewScheduleService.createInterviewSchedule(candidate, date, time, interviewerId);
//
//        // Lấy danh sách ứng viên đã được tạo lịch phỏng vấn
//        List<Candidate> candidates = candidateService.getAllCandidates();
//        model.addAttribute("candidates", candidates);
//
//        return "Interviewer/interviewSchedule"; // Trả về view HTML để hiển thị danh sách lịch phỏng vấn
//    }

    // API để tạo lịch phỏng vấn và gửi email
//    @PostMapping("/create")
//    public InterviewSchedule createInterviewSchedule(
//            @RequestParam("candidateId") Long candidateId,
//            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
//            @RequestParam("time") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
//            @RequestParam("interviewerId") Long interviewerId
//    ) {
//        // Additional Logging (Optional)
//        System.out.println("Received date: " + date);
//
//        Candidate candidate = candidateService.findById(candidateId);
//        if (candidate == null) {
//            throw new IllegalArgumentException("Candidate not found with ID: " + candidateId);
//        }
//
//        return interviewScheduleService.createInterviewSchedule(candidate, date, time, interviewerId);
//    }
}
