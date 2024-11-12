package org.example.mock.Controller;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.example.mock.Model.Candidate;
import org.example.mock.Model.CandidateStatus;
import org.example.mock.Model.Vacancy;
import org.example.mock.Repository.CandidateRepository;
import org.example.mock.Repository.CandidateStatusRepository;
import org.example.mock.Repository.VacancyRepository;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Controller
public class VacancyController {

    @Autowired
    private VacancyRepository vacancyRepository;
    @Autowired
    private CandidateRepository candidateRepository; // Thêm CandidateRepository
    @Autowired
    private CandidateStatusRepository candidateStatusRepository;
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

            candidate.setVacancy(vacancy);

            if (file != null && !file.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get("uploads/cv", fileName);
                Files.createDirectories(filePath.getParent());
                file.transferTo(filePath);

                candidate.setCvPath(filePath.toString());
            }

            candidateRepository.save(candidate);

            // Tạo một bản ghi CandidateStatus với trạng thái "đang chờ"
            CandidateStatus candidateStatus = new CandidateStatus();
            candidateStatus.setCandidate(candidate);
            candidateStatus.setStatusName("Đang chờ"); // Trạng thái mặc định
            candidateStatus.setUpdatedAt(LocalDateTime.now()); // Ngày giờ hiện tại

            candidateStatusRepository.save(candidateStatus); // Lưu vào bảng candidate_status

            model.addAttribute("vacancy", vacancy);
            model.addAttribute("candidate", candidate);
            model.addAttribute("successMessage", "Application submitted successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }

        return "Candidate/detailvacancy";
    }
//@PostMapping("/submitApplication")
//public String submitApplication(
//        @ModelAttribute Candidate candidate,
//        @RequestParam("vacancyId") Long vacancyId,
//        @RequestParam("file") MultipartFile file,
//        Model model) {
//    try {
//        Vacancy vacancy = vacancyRepository.findById(vacancyId)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid vacancy ID"));
//
//        // Thiết lập trường vacancy cho candidate
//        candidate.setVacancy(vacancy);
//
//        // Kiểm tra và lưu tệp nếu có
//        if (file != null && !file.isEmpty()) {
//            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
//            Path filePath = Paths.get("uploads/cv", fileName);
//            Files.createDirectories(filePath.getParent());
//            file.transferTo(filePath); // Lưu file vào thư mục
//
//            // Lưu đường dẫn vào đối tượng Candidate
//            candidate.setCvPath(filePath.toString());
//        }
//
//        candidateRepository.save(candidate);
//
//        model.addAttribute("vacancy", vacancy);
//        model.addAttribute("candidate", candidate);
//        // Thêm thông báo thành công vào model
//        model.addAttribute("successMessage", "Application submitted successfully!");
//
//    } catch (Exception e) {
//        e.printStackTrace();
//        return "error";
//    }
//
//    return "Candidate/detailvacancy";
//}
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
