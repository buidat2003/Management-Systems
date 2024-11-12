package org.example.mock.Controller;

import org.example.mock.Model.Department;
import org.example.mock.Model.JobType;
import org.example.mock.Model.PositionAll;
import org.example.mock.Model.Vacancy;
import org.example.mock.Repository.DepartmentRepository;
import org.example.mock.Repository.PositionRepository;
import org.example.mock.Repository.VacancyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller // Thêm annotation này để Spring nhận diện đây là một controller
public class JobHomeController {
    @Autowired
    private VacancyRepository vacancyRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @GetMapping("/jobcandidate")
    public String jobHome(@RequestParam(required = false) Long positionId,
                          @RequestParam(required = false) String type,
                          @RequestParam(required = false) Long departmentId,
                          @RequestParam(required = false) Boolean all,
                          Model model) {

        List<PositionAll> positions = positionRepository.findAll();
        List<Department> departments = departmentRepository.findAll();
        List<String> types = vacancyRepository.findAll().stream()
                .map(vacancy -> vacancy.getType().toString())
                .distinct()
                .collect(Collectors.toList());

        List<Vacancy> filteredVacancies;

        if (Boolean.TRUE.equals(all)) {
            filteredVacancies = vacancyRepository.findAll();
        } else {
            JobType typeEnum = null;
            if (type != null && !type.isEmpty()) {
                try {
                    typeEnum = JobType.valueOf(type);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }

            filteredVacancies = vacancyRepository.findFilteredVacancies(positionId, typeEnum, departmentId);
        }

        model.addAttribute("types", types);
        model.addAttribute("positions", positions);
        model.addAttribute("departments", departments);
        model.addAttribute("vacancies", filteredVacancies);

        return "Candidate/jobhome";
    }


}



//    @GetMapping("/jobcandidate")
//    public String jobHome(@RequestParam(required = false) Long positionId,
//                          @RequestParam(required = false) String type,
//                          @RequestParam(required = false) Long departmentId,
//                          Model model) {
//
//        List<PositionAll> positions = positionRepository.findAll();
//        List<Department> departments = departmentRepository.findAll();
//        List<String> types = vacancyRepository.findAll().stream()
//                .map(vacancy -> vacancy.getType().toString())
//                .distinct()
//                .collect(Collectors.toList());
//
//        // Chuyển đổi type từ String sang Enum JobType
//        JobType typeEnum = null;
//        if (type != null) {
//            try {
//                typeEnum = JobType.valueOf(type);
//            } catch (IllegalArgumentException e) {
//                // Nếu type không hợp lệ, xử lý hoặc để trống typeEnum
//                e.printStackTrace();
//            }
//        }
//
//        // Lọc vacancies dựa trên params
//        List<Vacancy> filteredVacancies = vacancyRepository.findFilteredVacancies(positionId, typeEnum, departmentId);
//
//        model.addAttribute("types", types);
//        model.addAttribute("positions", positions);
//        model.addAttribute("departments", departments);
//        model.addAttribute("vacancies", filteredVacancies);
//
//        return "Candidate/jobhome"; // Trả về trang jobhome.html
//    }
