package org.example.mock.Service;

import org.example.mock.Model.Vacancy;
import org.example.mock.Repository.VacancyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VacancyService {

    @Autowired
    private VacancyRepository vacancyRepository;

    public List<Vacancy> getAllVacancies() {
        return vacancyRepository.findAll();
    }

}
