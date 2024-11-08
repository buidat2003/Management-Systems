package org.example.mock.Controller;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.example.mock.Model.Candidate;
import org.example.mock.Model.Department;
import org.example.mock.Model.PositionAll;
import org.example.mock.Model.Vacancy;
import org.example.mock.Repository.CandidateRepository;
import org.example.mock.Repository.DepartmentRepository;
import org.example.mock.Repository.PositionRepository;
import org.example.mock.Repository.VacancyRepository;
import org.example.mock.Service.PositionService;
import org.example.mock.Service.VacancyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
                .orElseThrow(() -> new IllegalArgumentException("Invalid vacancy ID"));

        // Thiết lập trường vacancy cho candidate
        candidate.setVacancy(vacancy);

        // Kiểm tra và lưu tệp nếu có
        if (file != null && !file.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get("uploads/cv", fileName);
            Files.createDirectories(filePath.getParent());
            file.transferTo(filePath); // Lưu file vào thư mục

            // Lưu đường dẫn vào đối tượng Candidate
            candidate.setCvPath(filePath.toString());
        }

        candidateRepository.save(candidate);

        model.addAttribute("vacancy", vacancy);
        model.addAttribute("candidate", candidate);
        // Thêm thông báo thành công vào model
        model.addAttribute("successMessage", "Application submitted successfully!");

    } catch (Exception e) {
        e.printStackTrace();
        return "error";
    }

    return "Candidate/detailvacancy";
}
    @GetMapping("/downloadCV")
    public ResponseEntity<Resource> downloadCV(@RequestParam("candidateId") Long candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid candidate ID"));

        if (candidate.getCvPath() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path path = Paths.get(candidate.getCvPath());
            Resource resource = (Resource) new UrlResource(path.toUri());

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + resource.getClass() + "\"")
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
    public String getFilteredVacancies(
            @RequestParam(value = "position", required = false) Long positionId,
            @RequestParam(value = "skills", required = false) String requiredSkills,
            @RequestParam(value = "department", required = false) Long departmentId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "search", required = false) String search,
            Model model) {
        try {
            // Lấy dữ liệu từ các filter
            List<PositionAll> positions = vacancyService.getAllPositions();
            List<Department> departments = departmentRepository.findAll();
            List<String> statuses = vacancyService.getAllStatuses();

            // Lọc danh sách vacancies
            List<Vacancy> filteredVacancies = vacancyService.getFilteredVacancies(positionId, requiredSkills, departmentId, status, search);

            // Thêm các attribute vào model cho Thymeleaf
            model.addAttribute("positions", positions);
            model.addAttribute("departments", departments);
            model.addAttribute("statuses", statuses);
            model.addAttribute("vacancies", filteredVacancies);
            model.addAttribute("positionId", positionId);
            model.addAttribute("requiredSkills", requiredSkills);
            model.addAttribute("departmentId", departmentId);
            model.addAttribute("status", status);
            model.addAttribute("search", search);

            return "Manager/joblist";
        } catch (Exception e) {
            e.printStackTrace(); // In chi tiết lỗi ra console để kiểm tra
            model.addAttribute("errorMessage", "Đã xảy ra lỗi trong quá trình lọc. Vui lòng thử lại sau.");
            return "error"; // Trả về trang lỗi tùy chỉnh
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



}
