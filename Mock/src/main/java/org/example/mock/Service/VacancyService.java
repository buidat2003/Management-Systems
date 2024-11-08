package org.example.mock.Service;

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
        // Use repository method to get filtered vacancies based on parameters
        return vacancyRepository.findFilteredVacancie(positionId, requiredSkills, departmentId, status, search);
    }

    // Placeholder methods for fetching other required data
    public List<PositionAll> getAllPositions() {
        return vacancyRepository.findAllPositions();
    }

    public List<String> getAllDepartments() {
        return vacancyRepository.findAllDepartments();
    }

    public List<String> getAllStatuses() {
        return vacancyRepository.findAllStatuses();
    }


    // Other methods for filter logic, if needed
}

