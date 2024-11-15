package org.example.mock.Controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.example.mock.Model.Candidate;
import org.example.mock.Model.CandidateStatus;
import org.example.mock.Repository.CandidateRepository;
import org.example.mock.Repository.CandidateStatusRepository;
import org.example.mock.Service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller

public class InterviewCandidateController {
    @Autowired
    private CandidateService candidateService;


    @GetMapping("/filterCandidates")
    public String filterCandidates(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer experience,
            @RequestParam(required = false) String search,
            Model model) {

        List<Candidate> candidates = candidateService.filterCandidates(status, experience, search);
        Map<Long, String> candidateStatuses = candidateService.getLatestStatusByCandidateId(candidates);

        model.addAttribute("candidates", candidates);
        model.addAttribute("candidateStatuses", candidateStatuses);

        // Thêm các giá trị đã chọn vào model để hiển thị lại trong form
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedExperience", experience);

        return "Interviewer/interviewcandidate";
    }
    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private CandidateStatusRepository candidateStatusRepository;
    @PostMapping("/cancelCandidate")
    public String cancelCandidate(@RequestParam("candidateId") Long candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid candidate ID"));

        CandidateStatus candidateStatus = new CandidateStatus();
        candidateStatus.setCandidate(candidate);
        candidateStatus.setStatusName("Đã hủy");
        candidateStatus.setUpdatedAt(LocalDateTime.now());

        candidateStatusRepository.save(candidateStatus);

        return "redirect:/filterCandidates";
    }

    @GetMapping("/download/cv/{candidateId}")
    public ResponseEntity<Resource> downloadCV(@PathVariable Long candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Mã ứng viên không hợp lệ"));

        if (candidate.getCvPath() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path path = Paths.get(candidate.getCvPath());
            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new FileNotFoundException("Không thể đọc tệp: " + candidate.getCvPath());
            }

            String fileName = path.getFileName().toString().replaceAll("[^\\x00-\\x7F]", "_");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    //    @GetMapping("/candidatelist")
//    public String getCandidates(Model model) {
//        List<Candidate> candidates = candidateService.getAllCandidates();
//        Map<Long, String> candidateStatuses = candidateService.getLatestStatusByCandidateId(candidates);
//        model.addAttribute("candidates", candidates);
//        model.addAttribute("candidateStatuses", candidateStatuses);
//        return "Interviewer/interviewcandidate"; // Tên của template HTML chứa bảng ứng viên
//    }
}
