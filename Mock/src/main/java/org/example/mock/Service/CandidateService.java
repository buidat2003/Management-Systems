package org.example.mock.Service;

import org.example.mock.Model.Candidate;
import org.example.mock.Model.CandidateStatus;
import org.example.mock.Repository.CandidateRepository;
import org.example.mock.Repository.CandidateStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private CandidateStatusRepository candidateStatusRepository;

    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    public Map<Long, String> getLatestStatusByCandidateId(List<Candidate> candidates) {
        Map<Long, String> candidateStatusMap = new HashMap<>();

        for (Candidate candidate : candidates) {
            List<CandidateStatus> statuses = candidateStatusRepository.findStatusHistoryByCandidateId(candidate.getId());
            String latestStatus = statuses.isEmpty() ? "Unknown" : statuses.get(0).getStatusName();
            candidateStatusMap.put(candidate.getId(), latestStatus);
        }

        return candidateStatusMap;
    }
    //    public List<Candidate> filterCandidates(String status, Integer experience, String search) {
//        List<Candidate> candidates = candidateRepository.findCandidatesByStatusAndSearch(status, search);
//
//        if (experience != null) {
//            candidates = candidates.stream()
//                    .filter(candidate -> {
//                        int candidateExpInYears = Integer.parseInt(candidate.getExp()); // Trực tiếp chuyển từ chuỗi thành số nguyên
//
//                        // Kiểm tra ứng viên có đáp ứng kinh nghiệm theo các tùy chọn lọc
//                        switch (experience) {
//                            case 0:
//                                return candidateExpInYears < 1; // Dưới 1 năm
//                            case 1:
//                                return candidateExpInYears == 1; // 1 năm
//                            case 2:
//                                return candidateExpInYears == 2; // 2 năm
//                            case 3:
//                                return candidateExpInYears >= 3; // 3 năm trở lên
//                            default:
//                                return true; // Không lọc nếu không có giá trị hợp lệ
//                        }
//                    })
//                    .collect(Collectors.toList());
//        }
//
//        return candidates;
//    }
    public List<Candidate> filterCandidates(String status, Integer experience, String search) {
        List<Candidate> candidates = candidateRepository.findCandidatesByStatusAndSearch(status, search);

        if (experience != null) {
            candidates = candidates.stream()
                    .filter(candidate -> {
                        int candidateExpInYears;
                        try {
                            candidateExpInYears = Integer.parseInt(candidate.getExp()); // Chuyển đổi chuỗi thành số nguyên
                        } catch (NumberFormatException e) {
                            return false; // Loại bỏ nếu không phải số
                        }

                        // Kiểm tra ứng viên có đáp ứng kinh nghiệm theo các tùy chọn lọc
                        switch (experience) {
                            case 0:
                                return candidateExpInYears < 1; // Dưới 1 năm
                            case 1:
                                return candidateExpInYears == 1; // 1 năm
                            case 2:
                                return candidateExpInYears == 2; // 2 năm
                            case 3:
                                return candidateExpInYears >= 3; // 3 năm trở lên
                            default:
                                return true; // Không lọc nếu không có giá trị hợp lệ
                        }
                    })
                    .collect(Collectors.toList());
        }

        return candidates;
    }


    public Candidate findById(Long id) {
        return candidateRepository.findById(id).orElse(null);
    }

}
