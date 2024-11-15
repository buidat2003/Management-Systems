package org.example.mock.Controller;


import jakarta.annotation.PostConstruct;
import org.example.mock.Model.*;
import org.example.mock.Repository.*;
import org.example.mock.Service.PositionService;
import org.example.mock.Service.VacancyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.core.io.Resource;
import jakarta.servlet.http.HttpSession;

import org.springframework.core.io.UrlResource;
@Controller
public class VacancyController {
    private final VacancyService vacancyService;
    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private DepartmentRepository departmentRepository;


    @Autowired
    public VacancyController(VacancyService vacancyService) {
        this.vacancyService = vacancyService;

    }

    @Autowired
    private VacancyRepository vacancyRepository;
    @Autowired
    private CandidateRepository candidateRepository; // Thêm CandidateRepository
    @Autowired
    private CandidateStatusRepository candidateStatusRepository; // Thêm CandidateRepository

    @GetMapping("/vacancy/{id}")
    public String getVacancyDetails(@PathVariable Long id, Model model) {
        Optional<Vacancy> vacancyOptional = vacancyRepository.findById(id);
        if (vacancyOptional.isPresent()) {
            Vacancy vacancy = vacancyOptional.get();
            model.addAttribute("vacancy", vacancy); // Thêm đối tượng Vacancy vào model

            // Tìm kiếm candidate dựa trên vacancy ID (ví dụ: lấy candidate đầu tiên liên kết với vacancy)
            Optional<Candidate> candidateOptional = candidateRepository.findFirstByVacancyId(id);
            Candidate candidate = candidateOptional.orElse(new Candidate()); // Nếu không có, tạo candidate trống
            model.addAttribute("candidate", candidate); // Thêm candidate vào model

            return "Candidate/detailvacancy";
        } else {
            return "error";
        }
    }

    @PostMapping("/submitApplication")
    public String submitApplication(
            @ModelAttribute Candidate candidate,
            @RequestParam("vacancyId") Long vacancyId,
            @RequestParam("file") MultipartFile file,
            Model model) {
        try {
            Vacancy vacancy = vacancyRepository.findById(vacancyId)
                    .orElseThrow(() -> new IllegalArgumentException("Mã công việc không hợp lệ"));

            candidate.setVacancy(vacancy);

            if (file != null && !file.isEmpty()) {
                // Kiểm tra định dạng tệp
                String fileName = file.getOriginalFilename();
                if (fileName != null && (fileName.endsWith(".pdf") || fileName.endsWith(".docx"))) {
                    String newFileName = System.currentTimeMillis() + "_" + fileName;
                    Path filePath = Paths.get("uploads/cv", newFileName);
                    Files.createDirectories(filePath.getParent());
                    file.transferTo(filePath);

                    candidate.setCvPath(filePath.toString());
                } else {
                    model.addAttribute("errorMessage", "Chỉ chấp nhận tệp PDF hoặc Word.");
                    return "Candidate/detailvacancy";
                }
            }

            // Lưu ứng viên vào cơ sở dữ liệu
            candidateRepository.save(candidate);

            // Tạo và lưu trạng thái cho ứng viên với trạng thái mặc định là "đang chờ"
            CandidateStatus candidateStatus = new CandidateStatus();
            candidateStatus.setCandidate(candidate);
            candidateStatus.setStatusName("Đang chờ");
            candidateStatus.setUpdatedAt(LocalDateTime.now());

            // Lưu vào bảng candidate_status
            candidateStatusRepository.save(candidateStatus);

            model.addAttribute("vacancy", vacancy);
            model.addAttribute("candidate", candidate);
            model.addAttribute("successMessage", "Đã nộp hồ sơ thành công!");

        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }

        return "Candidate/detailvacancy";
    }
    @GetMapping("/downloadCV")
    public ResponseEntity<Resource> downloadCV(@RequestParam("candidateId") Long candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Mã ứng viên không hợp lệ"));

        if (candidate.getCvPath() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path path = Paths.get(candidate.getCvPath());
            if (!Files.exists(path)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new FileNotFoundException("Không thể đọc tệp: " + candidate.getCvPath());
            }

            // Làm sạch tên tệp để loại bỏ các ký tự không hợp lệ
            String fileName = path.getFileName().toString().replaceAll("[^\\x00-\\x7F]", "_");

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    @PostMapping("/uploadTemporaryFile")
    public ResponseEntity<String> uploadTemporaryFile(
            @RequestParam("file") MultipartFile file, HttpSession session) {
        try {
            if (file != null && !file.isEmpty()) {
                // Kiểm tra định dạng tệp
                String fileName = file.getOriginalFilename();
                if (fileName != null && (fileName.endsWith(".pdf") || fileName.endsWith(".docx"))) {
                    String newFileName = "temp_" + System.currentTimeMillis() + "_" + fileName;
                    Path filePath = Paths.get("uploads/temp", newFileName);
                    Files.createDirectories(filePath.getParent());
                    file.transferTo(filePath);

                    // Lưu đường dẫn tệp tạm thời trong session
                    session.setAttribute("tempFilePath", filePath.toString());

                    return ResponseEntity.ok("Tệp đã tải tạm thời thành công.");
                } else {
                    return ResponseEntity.badRequest().body("Chỉ chấp nhận tệp PDF hoặc Word.");
                }
            } else {
                return ResponseEntity.badRequest().body("Vui lòng chọn tệp để tải lên.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra khi tải lên tệp.");
        }
    }
    @GetMapping("/downloadTemporaryFile")
    public ResponseEntity<Resource> downloadTemporaryFile(HttpSession session) {
        String tempFilePath = (String) session.getAttribute("tempFilePath");

        if (tempFilePath == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path path = Paths.get(tempFilePath);
            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new FileNotFoundException("Không thể đọc tệp tạm thời: " + tempFilePath);
            }

            String fileName = path.getFileName().toString().replaceAll("[^\\x00-\\x7F]", "_");

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get("uploads/cv"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @GetMapping("/joblist")
    public String getFilteredVacancies(@RequestParam(value = "position", required = false) Long positionId,
                                       @RequestParam(value = "skills", required = false) String requiredSkills,
                                       @RequestParam(value = "department", required = false) Long departmentId,
                                       @RequestParam(value = "status", required = false) String status,
                                       @RequestParam(value = "search", required = false) String search,
                                       Model model) {
        // Provide fallback defaults or handle nulls as necessary
        List<Vacancy> filteredVacancies = vacancyService.getFilteredVacancies(
                positionId != null ? positionId : null,
                requiredSkills != null && !requiredSkills.isEmpty() ? requiredSkills : null,
                departmentId != null ? departmentId : null,
                status != null && !status.isEmpty() ? status : null,
                search != null && !search.isEmpty() ? search : null
        );

        // Add the filtered list and form fields to the model
        model.addAttribute("vacancies", filteredVacancies);
        model.addAttribute("positions", vacancyService.getAllPositions());
        model.addAttribute("departments", vacancyRepository.findAllDepartments());
        model.addAttribute("statuses", vacancyService.getAllStatuses());
        model.addAttribute("details", vacancyService.getAllDetails());

        return "Manager/joblist";

    }
    @GetMapping("/Manager/viewJob/{id}")
    public String viewJobDetails(@PathVariable Long id, Model model) {
        Optional<Vacancy> vacancyOptional = vacancyService.findById(id);
        if (vacancyOptional.isPresent()) {
            Vacancy vacancy = vacancyOptional.get();
            model.addAttribute("vacancy", vacancy);
            return "Manager/viewJob"; // The new View Job page
        } else {
            return "error"; // Handle invalid job ID
        }
    }



}







//    @GetMapping("/vacancy/{id}")
//    public String getVacancyDetails(@PathVariable Long id, Model model) {
//        Optional<Vacancy> vacancyOptional = vacancyRepository.findById(id);
//        if (vacancyOptional.isPresent()) {
//            model.addAttribute("vacancy", vacancyOptional.get()); // Thêm đối tượng Vacancy vào model
//            return "Candidate/detailvacancy";
//        } else {
//            return "error";
//        }
//    }
//    @PostMapping("/submitApplication")
//    public String submitApplication(
//            @ModelAttribute Candidate candidate,
//            @RequestParam("vacancyId") Long vacancyId,
//            Model model) {
//        try {
//            Vacancy vacancy = vacancyRepository.findById(vacancyId)
//                    .orElseThrow(() -> new IllegalArgumentException("Invalid vacancy ID"));
//
//            // Thiết lập trường vacancy cho candidate
//            candidate.setVacancy(vacancy);
//
//            // Lưu candidate vào cơ sở dữ liệu
//            candidateRepository.save(candidate);
//
//            // Thêm vacancy và candidate vào model để hiển thị trong view
//            model.addAttribute("vacancy", vacancy);
//            model.addAttribute("candidate", candidate);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "error";  // Trả về trang lỗi trong trường hợp gặp ngoại lệ
//        }
//
//        return "Candidate/detailvacancy";  // Chuyển hướng đến trang chi tiết vacancy sau khi lưu thành công
//    }




