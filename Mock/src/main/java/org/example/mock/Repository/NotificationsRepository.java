package org.example.mock.Repository;

import org.example.mock.Model.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationsRepository extends JpaRepository<Notifications, Long> {
}
