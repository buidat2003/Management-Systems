package org.example.mock.Controller;


import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
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

    @PostMapping("/markAsInterviewed")
    public ResponseEntity<String> markAsInterviewed(@RequestParam("scheduleId") Long scheduleId) {
        InterviewSchedule schedule = interviewScheduleService.findById(scheduleId);
        if (schedule == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Schedule not found");
        }

        // Cập nhật trạng thái "Đã phỏng vấn" cho ứng viên
        CandidateStatus existingStatus = candidateStatusRepository.findByCandidateId(schedule.getCandidate().getId());
        if (existingStatus == null) {
            CandidateStatus newStatus = new CandidateStatus();
            newStatus.setCandidate(schedule.getCandidate());
            newStatus.setStatusName("Đã phỏng vấn");
            newStatus.setUpdatedAt(LocalDateTime.now());
            candidateStatusRepository.save(newStatus);
        } else {
            existingStatus.setStatusName("Đã phỏng vấn");
            existingStatus.setUpdatedAt(LocalDateTime.now());
            candidateStatusRepository.save(existingStatus);
        }

        return ResponseEntity.ok("Đã phỏng vấn");
    }


    @DeleteMapping("/deleteInterviewed")
    public ResponseEntity<?> deleteInterviewed(@RequestParam("scheduleId") Long scheduleId) {
        InterviewSchedule schedule = interviewScheduleService.findById(scheduleId);
        if (schedule == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Schedule not found");
        }

        // Xóa lịch phỏng vấn
        interviewScheduleService.delete(schedule);

        // Cập nhật trạng thái ứng viên về "Đã hủy"
        CandidateStatus existingStatus = candidateStatusRepository.findByCandidateId(schedule.getCandidate().getId());
        if (existingStatus != null) {
            existingStatus.setStatusName("Đã hủy");
            existingStatus.setUpdatedAt(LocalDateTime.now());
            candidateStatusRepository.save(existingStatus);
        }

        return ResponseEntity.ok("Schedule deleted and status updated to Đã hủy");
    }

    @GetMapping("/detail")
    public String getAllInterviewSchedules(Model model) {
        // Lấy danh sách tất cả lịch phỏng vấn từ service
        List<InterviewSchedule> schedules = interviewScheduleService.getAllSchedules();
        model.addAttribute("schedules", schedules);

        // Trả về trang hiển thị danh sách lịch phỏng vấn
        return "Interviewer/interviewschedule";
    }
    @PostMapping("/uploadExcel")
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        // Kiểm tra nếu tệp rỗng hoặc không phải tệp Excel
        if (file.isEmpty() || !file.getOriginalFilename().endsWith(".xlsx")) {
            return ResponseEntity.badRequest().body("Please upload a valid Excel file.");
        }

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
            int rowCount = sheet.getLastRowNum() + 1; // Tổng số hàng (bao gồm cả hàng trống)

            for (int i = 3; i < rowCount; i++) { // Bắt đầu từ dòng thứ 4 (index = 3)
                Row row = sheet.getRow(i);

                if (row == null || isRowEmpty(row)) { // Bỏ qua dòng trống
                    continue;
                }

                // Đọc dữ liệu từ các ô
                Long scheduleId = getNumericCellValueAsLong(row.getCell(0)); // Schedule ID
                String candidateName = getStringCellValue(row.getCell(1));  // Candidate Name
                String date = getStringCellValue(row.getCell(2));           // Date
                String time = getStringCellValue(row.getCell(3));           // Time
                String interviewerName = getStringCellValue(row.getCell(4)); // Interviewer Name
                String googleMeetLink = getStringCellValue(row.getCell(5)); // Google Meet Link
                String result = getStringCellValue(row.getCell(6));         // Result

                // Kiểm tra dữ liệu hợp lệ
                if (scheduleId == null || googleMeetLink == null || result == null) {
                    continue; // Bỏ qua nếu dữ liệu thiếu
                }

                // Cập nhật thông tin trong cơ sở dữ liệu
                InterviewSchedule schedule = interviewScheduleService.findById(scheduleId);
                if (schedule != null) {
                    schedule.setGoogleMeetLink(googleMeetLink);
                    schedule.setResult(result);
                    interviewScheduleService.update(schedule);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading Excel file.");
        }

        return ResponseEntity.ok("File uploaded and data updated successfully.");
    }

    // Kiểm tra nếu hàng trống
    private boolean isRowEmpty(Row row) {
        for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    // Tiện ích để lấy giá trị từ cell
    private Long getNumericCellValueAsLong(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case NUMERIC:
                return (long) cell.getNumericCellValue();
            case STRING:
                try {
                    return Long.parseLong(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return null;
                }
            default:
                return null;
        }
    }

    private String getStringCellValue(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            default:
                return null;
        }
    }




    @GetMapping("/exportExcel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=InterviewSchedules.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Interview Schedules");

        // Tạo tiêu đề chính
        Row titleRow = sheet.createRow(0);
        titleRow.createCell(0).setCellValue("Phỏng vấn Interview");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6)); // Gộp các ô
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleRow.getCell(0).setCellStyle(titleStyle);

        // Tạo khoảng cách giữa tiêu đề và bảng
        Row blankRow = sheet.createRow(1);

        // Tạo tiêu đề cột
        Row header = sheet.createRow(2);
        header.createCell(0).setCellValue("Schedule ID");
        header.createCell(1).setCellValue("Candidate Name");
        header.createCell(2).setCellValue("Date");
        header.createCell(3).setCellValue("Time");
        header.createCell(4).setCellValue("Interviewer Name");
        header.createCell(5).setCellValue("Google Meet Link");
        header.createCell(6).setCellValue("Kết quả"); // Cột mới để nhập kết quả

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        for (int i = 0; i <= 6; i++) {
            header.getCell(i).setCellStyle(headerStyle);
        }

        // Thêm dữ liệu
        List<InterviewSchedule> schedules = interviewScheduleService.getAllSchedules();
        int rowIdx = 3;

        for (InterviewSchedule schedule : schedules) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(schedule.getId());
            row.createCell(1).setCellValue(schedule.getCandidate().getName());
            row.createCell(2).setCellValue(schedule.getScheduleDate().toString());
            row.createCell(3).setCellValue(schedule.getScheduleTime().toString());
            row.createCell(4).setCellValue(schedule.getInterviewer().getName());
            row.createCell(5).setCellValue(schedule.getGoogleMeetLink());
            row.createCell(6).setCellValue(schedule.getResult());
        }

        // Tự động chỉnh kích thước cột
        for (int i = 0; i <= 6; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
