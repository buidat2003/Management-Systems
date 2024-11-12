package org.example.mock.Repository;

import org.example.mock.Model.Department;
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
    @Query("SELECT v FROM Vacancy v " +
            "WHERE (:positionId IS NULL OR v.position.id = :positionId) " +
            "AND (:requiredSkills IS NULL OR v.details LIKE %:requiredSkills%) " +
            "AND (:departmentId IS NULL OR v.department.id = :departmentId) " +
            "AND (:status IS NULL OR v.status = :status) " +
            "AND (:search IS NULL OR (v.position.name LIKE %:search% OR v.department.name LIKE %:search%))")
    List<Vacancy> findFilteredVacancie(@Param("positionId") Long positionId,
                                        @Param("requiredSkills") String requiredSkills,
                                        @Param("departmentId") Long departmentId,
                                        @Param("status") String status,
                                        @Param("search") String search);



    @Query("SELECT DISTINCT v.position FROM Vacancy v")
    List<PositionAll> findAllPositions();

    @Query("SELECT DISTINCT v.details FROM Vacancy v")
    List<String> findAllDetails();

    @Query("SELECT DISTINCT v.department FROM Vacancy v")
    List<Department> findAllDepartments();


    @Query("SELECT DISTINCT v.status FROM Vacancy v")
    List<String> findAllStatuses();
}


