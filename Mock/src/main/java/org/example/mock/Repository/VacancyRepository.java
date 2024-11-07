package org.example.mock.Repository;

import org.example.mock.Model.JobType;
import org.example.mock.Model.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, Long> {

    @Query("SELECT v FROM Vacancy v " +
            "WHERE (:positionId IS NULL OR v.position.id = :positionId) " +
            "AND (:type IS NULL OR v.type = :type) " +
            "AND (:departmentId IS NULL OR v.department.id = :departmentId)")
    List<Vacancy> findFilteredVacancies(@Param("positionId") Long positionId,
                                        @Param("type") JobType type,
                                        @Param("departmentId") Long departmentId);

}


