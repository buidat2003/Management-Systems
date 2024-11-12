package org.example.mock.Repository;

import org.example.mock.Model.JobType;
import org.example.mock.Model.PositionAll;
import org.example.mock.Model.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
    List<Vacancy> findAll();

    @Query("SELECT v FROM Vacancy v " +
            "WHERE (:positionId IS NULL OR v.position.id = :positionId) " +
            "AND (:type IS NULL OR v.type = :type) " +
            "AND (:departmentId IS NULL OR v.department.id = :departmentId)")
    List<Vacancy> findFilteredVacancies(@Param("positionId") Long positionId,
                                        @Param("type") JobType type,
                                        @Param("departmentId") Long departmentId);

    ////////////////////////////////////////////////////////////////////
    @Query(value = "SELECT * FROM vacancy v WHERE " +
            "(:positionId IS NULL OR v.position_id = :positionId) " +
            "AND (:requiredSkills IS NULL OR JSON_CONTAINS(JSON_EXTRACT(v.details, '$.required_skills'), :requiredSkills)) " +
            "AND (:departmentId IS NULL OR v.department_id = :departmentId) " +
            "AND (:status IS NULL OR v.status = :status) " +
            "AND (:search IS NULL OR JSON_EXTRACT(v.details, '$.description') LIKE CONCAT('%', :search, '%'))",
            nativeQuery = true)
    List<Vacancy> findFilteredVacancie(@Param("positionId") Long positionId,
                                       @Param("requiredSkills") String requiredSkills,
                                       @Param("departmentId") Long departmentId,
                                       @Param("status") String status,
                                       @Param("search") String search);


    @Query("SELECT DISTINCT v.position FROM Vacancy v")
    List<PositionAll> findAllPositions();

    @Query("SELECT DISTINCT v.department.name FROM Vacancy v")
    List<String> findAllDepartments();

    @Query("SELECT DISTINCT v.status FROM Vacancy v")
    List<String> findAllStatuses();
}


