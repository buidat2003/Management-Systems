package org.example.mock.Service.AdminService;

import jakarta.persistence.EntityNotFoundException;
import org.example.mock.Common.FileUpload;
import org.example.mock.DTO.request.CreateUser;
import org.example.mock.Model.Department;
import org.example.mock.Model.User;
import org.example.mock.Repository.Admin.AdminCustomRepositoryImplement;
import org.example.mock.Repository.Admin.AdminRepository;
import org.example.mock.Repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class AdminServiceImplement implements AdminService {



    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void addUser(CreateUser createUser, MultipartFile avatar) throws IOException {
        User user;
        // Kiểm tra xem người dùng có tồn tại không
        if (createUser.getId() != null) {
            // Nếu có ID, tìm người dùng cũ và cập nhật
            user = adminRepository.findById(createUser.getId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
        } else {
            // Nếu không có ID, tạo người dùng mới
            user = new User();
        }

        user.setName(createUser.getName());
        user.setEmail(createUser.getEmail());
        user.setUsername(createUser.getUsername());
        String encodedPassword = passwordEncoder.encode(createUser.getPassword());
        user.setPassword(encodedPassword);
        user.setDoB(createUser.getDoB());
        user.setPhone(createUser.getPhone());
        user.setAddress(createUser.getAddress());
        user.setGender(createUser.getGender()); // Giả sử bạn dùng enum cho Gender
        user.setRole(createUser.getRole());
        user.setStatus(createUser.getStatus());

        Department department = departmentRepository.findById(createUser.getDepartmentId())
                .orElseThrow(() -> new EntityNotFoundException("Department not found"));
        user.setDepartment(department);

        //user.setDepartment(new Department(createUser.getDepartmentId())); // Assuming you have a Department constructor with ID

        // Xử lý avatar
        if (avatar != null && !avatar.isEmpty()) {
            // Kiểm tra định dạng MIME type của tệp ảnh
//            String contentType = avatar.getContentType();
//            if (contentType == null || !contentType.startsWith("image/")) {
//                throw new IOException("Only image files are allowed.");
//            }
            // Đảm bảo đường dẫn upload ảnh hợp lệ và tạo thư mục nếu cần
            //String uploadDir = System.getProperty("user.dir") + "src/main/webapp/resources/static/images/Avatar/"; // Đường dẫn lưu ảnh
            Path uploadDirPath = Paths.get("src/main/resources/static/image/Avatar/_").toAbsolutePath();
            String uploadDir = uploadDirPath.toString();
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs();  // Tạo thư mục nếu chưa tồn tại
            }

            String avatarName = System.currentTimeMillis() + "_" + avatar.getOriginalFilename(); // Tạo tên ảnh duy nhất
            File destFile = new File(uploadDir + avatarName);

            // Lưu ảnh vào thư mục
            avatar.transferTo(destFile);

            // Gắn tên file vào đối tượng người dùng
            user.setAvatar( "static/image/Avatar/_" + avatarName);
        }

        adminRepository.save(user); // Lưu người dùng vào cơ sở dữ liệu
    }



}
