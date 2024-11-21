package org.example.mock.Controller;


import jakarta.annotation.PostConstruct;
//import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.example.mock.Model.*;
import org.example.mock.Repository.*;

import org.example.mock.Model.*;
import org.example.mock.Repository.*;
import org.example.mock.Service.PositionService;
import org.example.mock.Service.VacancyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import javax.swing.text.Position;
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
    private UserRepository userRepository;



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
                                       @RequestParam(value = "limit", required = false, defaultValue = "5") int limit,
                                       @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                       Model model) {
        List<Vacancy> filteredVacancies = vacancyService.getFilteredVacancies(
                positionId, requiredSkills, departmentId, status, search
        );

        model.addAttribute("vacancies", filteredVacancies);
        model.addAttribute("positions", vacancyService.getAllPositions());
        model.addAttribute("departments", vacancyService.getAllDepartments());
        model.addAttribute("statuses", VacancyStatus.values());  // Cung cấp enum VacancyStatus cho view
        model.addAttribute("requiredSkills", vacancyService.getAllDetails());

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

    // Show form to create a job
    @GetMapping("/manager/createJob")
    public String showCreateJobForm(Model model) {
        model.addAttribute("vacancy", new Vacancy());
        model.addAttribute("positions", vacancyService.getAllPositions());
        model.addAttribute("departments", vacancyService.getAllDepartments());
        model.addAttribute("requiredSkills", vacancyService.getAllDetails());
        return "Manager/createJob";  // Thymeleaf page for job creation
    }

    // Handle form submission for job creation
    @PostMapping("/manager/CreateJob")
    public String createJob(
            @RequestParam("positionId") Long positionId,
            @RequestParam("departmentId") Long departmentId,
            @RequestParam("requiredSkills") String requiredSkills,  // Updated field
            @ModelAttribute Vacancy vacancy) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Assuming the principal is a User object
        if (authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();
            String currentUsername = currentUser.getUsername();
            System.out.println("Current logged-in username: " + currentUsername);

            // Fetch the user from the repository
            currentUser = userRepository.findByUsername(currentUsername)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Retrieve the recruiter's department and set the vacancy's creator
            vacancy.setCreatedUser(currentUser);
            vacancy.setDepartment(currentUser.getDepartment()); // Use department from logged-in user

            // Set the position and other vacancy details as before
            PositionAll position = positionRepository.findById(positionId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid position ID"));

            vacancy.setPosition(position);
            vacancy.setDetails(requiredSkills);

            vacancyService.createVacancy(vacancy);  // Create the job (Vacancy)
            return "redirect:/joblist";  // Redirect to job list after successful creation
        } else {
            throw new IllegalStateException("Authentication principal is not a User object");
        }
    }



    // Handle job deletion
    @PostMapping("/Manager/deleteJob/{id}")
    public String deleteJob(@PathVariable Long id) {
        vacancyService.deleteVacancy(id);  // Delete job (Vacancy) by ID
        return "redirect:/joblist";  // Redirect back to job list after deletion
    }
    // Show form to edit a job
    @GetMapping("/manager/editJob/{id}")
    public String showEditJobForm(@PathVariable Long id, Model model) {
        Optional<Vacancy> vacancyOptional = vacancyService.findById(id);

        if (vacancyOptional.isPresent()) {
            Vacancy vacancy = vacancyOptional.get();
            model.addAttribute("vacancy", vacancy);
            model.addAttribute("positions", vacancyService.getAllPositions());
            model.addAttribute("departments", vacancyService.getAllDepartments());
            return "Manager/editJob";  // Thymeleaf page for job editing
        } else {
            return "error"; // Handle invalid job ID
        }
    }

    // Handle form submission for job update
    @PostMapping("/manager/updateJob")
    public String updateJob(
            @RequestParam("positionId") Long positionId,
            @RequestParam("departmentId") Long departmentId,
            @RequestParam("details") String details,
            @RequestParam("status") VacancyStatus status,
            @RequestParam("type") JobType type,
            @RequestParam("dueDate") String dueDate,
            @ModelAttribute Vacancy vacancy) {

        // Retrieve Position and Department from the database
        PositionAll position = positionRepository.findById(positionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid position ID"));
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid department ID"));

        // Get the current logged-in user's information using Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Assuming the principal is a User object
        User loggedInUser;
        if (authentication.getPrincipal() instanceof User) {
            loggedInUser = (User) authentication.getPrincipal();
        } else {
            // If it's not a User object, you can handle this situation (e.g., throw an exception or log an error)
            throw new IllegalArgumentException("Authentication principal is not an instance of User");
        }

        // Print the username for debugging purposes
        String currentUsername = loggedInUser.getUsername();
        System.out.println("Current logged-in username: " + currentUsername);

        // Fetch the user from the repository to ensure it's up-to-date
        loggedInUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Ensure created_at is set during creation, if null
        if (vacancy.getCreatedAt() == null) {
            vacancy.setCreatedAt(LocalDateTime.now()); // Set current time if not already set
            vacancy.setCreatedUser(loggedInUser); // Set the created user if it's a new vacancy
        }

        // Parse the string date into a LocalDate object
        LocalDate parsedDueDate = LocalDate.parse(dueDate);

        // Update vacancy details
        vacancy.setStatus(status);
        vacancy.setPosition(position);
        vacancy.setDepartment(department);
        vacancy.setDetails(details);
        vacancy.setDueDate(parsedDueDate);
        vacancy.setType(type);

        // Set updated_at to current time and updatedUser to the current user
        vacancy.setUpdatedAt(LocalDateTime.now());
        vacancy.setUpdatedUser(loggedInUser);

        // Update the vacancy in the database
        vacancyService.updateVacancy(vacancy);

        // Redirect to the view job page after successful update
        return "redirect:/Manager/viewJob/" + vacancy.getId();
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




