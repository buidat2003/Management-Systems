package org.example.mock.Service;
import java.util.Optional;
import java.util.stream.Collectors;

import org.example.mock.Model.Department;
import org.example.mock.Model.PositionAll;
import org.example.mock.Model.Vacancy;
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
        return vacancyRepository.findFilteredVacancie(positionId, requiredSkills, departmentId, status, search);
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

    // Other methods for filter logic, if needed
}

