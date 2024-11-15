package org.example.mock.Repository;

import org.example.mock.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // Câu lệnh SQL thuần túy để lấy tất cả thông tin của người dùng có id = 1
    @Query(value = "SELECT * FROM user WHERE id = :id", nativeQuery = true)
    User findUserById(@Param("id") Integer id);  // Lấy thông tin của người dùng theo id

    // Câu lệnh SQL thuần túy để cập nhật thông tin người dùng
    @Modifying
    @Query(value = "UPDATE user SET name = :name, email = :email, phone = :phone, address = :address, gender = :gender, status = :status WHERE id = :id", nativeQuery = true)
    void executeUpdate(@Param("name") String name,
                       @Param("email") String email,
                       @Param("phone") String phone,
                       @Param("address") String address,
                       @Param("gender") Boolean gender,
                       @Param("status") String status,
                       @Param("id") Integer id);




}
