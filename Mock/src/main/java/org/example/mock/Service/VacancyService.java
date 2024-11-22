package org.example.mock.Service;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import org.example.mock.Model.Department;
import org.example.mock.Model.PositionAll;
import org.example.mock.Model.Vacancy;
import org.example.mock.Model.VacancyStatus;
import org.example.mock.Repository.VacancyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class VacancyService {

    private final VacancyRepository vacancyRepository;

    @Autowired
    public VacancyService(VacancyRepository vacancyRepository) {
        this.vacancyRepository = vacancyRepository;
    }

    public List<Vacancy> getAllVacancies() {
        return vacancyRepository.findAll();
    }
    public List<Vacancy> getFilteredVacancies(Long positionId, String requiredSkills, Long departmentId, String status, String search) {
        VacancyStatus vacancyStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                vacancyStatus = VacancyStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Nếu status không hợp lệ, bạn có thể xử lý hoặc bỏ qua lỗi
                vacancyStatus = null;
            }
        }
        return vacancyRepository.findFilteredVacancie(positionId, requiredSkills, departmentId, vacancyStatus, search);
    }


    // Placeholder methods for fetching other required data
    public List<PositionAll> getAllPositions() {
        return vacancyRepository.findAllPositions();
    }
    public List<String> getAllDetails() {
        return vacancyRepository.findAllDetails();
    }


    public List<Department> getAllDepartments() {
        List<Department> departments = vacancyRepository.findAllDepartments();

        // Lọc để chỉ giữ lại các phòng ban duy nhất
        return departments.stream()
                .distinct()  // Đảm bảo không có phòng ban trùng lặp
                .collect(Collectors.toList());
    }


    public List<String> getAllStatuses() {
        return vacancyRepository.findAllStatuses();
    }

    public Optional<Vacancy> findById(Long id) {
        return vacancyRepository.findById(id);
    }

    // Create a new job (Vacancy)
    public Vacancy createVacancy(Vacancy vacancy) {
        vacancy.setCreatedAt(LocalDateTime.now().now());  // Set the created date
        return vacancyRepository.save(vacancy);  // Save the vacancy to the database
    }

    // Delete a job (Vacancy)
    public void deleteVacancy(Long id) {
        vacancyRepository.deleteById(id);  // Delete vacancy by ID
    }

    public void updateVacancy(Vacancy vacancy) {
        vacancyRepository.save(vacancy);  // Save updated Vacancy entity
    }
    public List<String> getAllVacancyStatuses() {
        return Arrays.stream(VacancyStatus.values())
                .map(Enum::name)  // Lấy tên của các enum
                .collect(Collectors.toList());
    }


    // Other methods for filter logic, if needed
}

