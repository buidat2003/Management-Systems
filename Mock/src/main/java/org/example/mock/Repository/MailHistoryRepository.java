package org.example.mock.Repository;

import org.example.mock.Model.MailHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailHistoryRepository extends JpaRepository<MailHistory, Long> {
}
