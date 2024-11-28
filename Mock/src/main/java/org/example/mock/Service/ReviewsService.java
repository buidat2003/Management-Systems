package org.example.mock.Service;

import org.example.mock.Repository.ReviewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReviewsService {

    @Autowired
    private ReviewsRepository reviewRepository;

    public Map<Long, List<Integer>> getRatingsByCandidateId(List<Long> candidateIds) {
        List<Object[]> results = reviewRepository.findRatingsByCandidateIds(candidateIds);

        // Chuyển đổi kết quả từ List<Object[]> thành Map<Long, List<Integer>>
        return results.stream()
                .collect(Collectors.groupingBy(
                        result -> (Long) result[0], // candidateId
                        Collectors.mapping(result -> (Integer) result[1], Collectors.toList()) // List of ratings
                ));
    }


}
