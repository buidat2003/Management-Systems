package org.example.mock.Repository;

import org.example.mock.Model.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewsRepository extends JpaRepository<Reviews, Long> {
}
