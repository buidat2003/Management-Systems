package org.example.mock.Service.AdminService;

import org.example.mock.DTO.request.CreateUser;
import org.example.mock.Model.User;
import org.example.mock.Repository.Admin.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface AdminService {

    // Phương thức thêm người dùng mới từ DTO CreateUser
    void addUser(CreateUser createUser, MultipartFile avatar) throws IOException;
}
