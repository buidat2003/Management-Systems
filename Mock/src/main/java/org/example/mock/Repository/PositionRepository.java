package org.example.mock.Repository;

import org.example.mock.Model.PositionAll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepository extends JpaRepository<PositionAll, Long> {
}
