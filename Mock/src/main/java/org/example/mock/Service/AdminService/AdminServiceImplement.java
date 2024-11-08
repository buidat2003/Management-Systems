package org.example.mock.Service.AdminService;

import org.example.mock.Common.FileUpload;
import org.example.mock.DTO.request.CreateUser;
import org.example.mock.Model.User;
import org.example.mock.Repository.Admin.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class AdminServiceImplement implements AdminService {



    @Autowired
    private AdminRepository adminRepository;

    public void addUser(CreateUser createUser, MultipartFile avatar) throws IOException {
        User user = new User();
        user.setName(createUser.getName());
        user.setEmail(createUser.getEmail());
        user.setUsername(createUser.getUsername());
        user.setPassword(createUser.getPassword());
        user.setDoB(createUser.getDoB());
        user.setPhone(createUser.getPhone());
        user.setAddress(createUser.getAddress());
        user.setGender(createUser.getGender()); // Giả sử bạn dùng enum cho Gender
        user.setRole(createUser.getRole());
        user.setStatus(createUser.getStatus());
        //user.setDepartment(new Department(createUser.getDepartmentId())); // Assuming you have a Department constructor with ID

        // Xử lý avatar nếu có
        if (avatar != null && !avatar.isEmpty()) {
            String avatarName = avatar.getOriginalFilename();
            FileUpload.saveFile(avatar);  // Lưu avatar vào thư mục
            user.setAvatar(avatarName); // Lưu tên tệp vào cơ sở dữ liệu
        }

        adminRepository.save(user); // Lưu người dùng vào cơ sở dữ liệu
    }



}
