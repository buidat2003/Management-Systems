package org.example.mock.Repository;

import org.example.mock.Model.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {
}
