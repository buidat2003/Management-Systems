package org.example.mock.Repository;

import org.example.mock.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    // Lọc theo vị trí
    List<User> findByrole(String role);

    // Lọc theo trạng thái
    List<User> findByStatus(Boolean status);

    // Tìm kiếm theo tên hoặc email
    List<User> findByNameContainingOrEmailContaining(String name, String email);

    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameAndEmail(String username, String email);
    Optional<User> findByEmail(String email);
}
