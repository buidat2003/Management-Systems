package org.example.mock.Common;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUpload {
    private static final String UPLOAD_DIR = "/static/image/Avatar";

    public static void saveFile(MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            Path path = Paths.get(UPLOAD_DIR + "/" + file.getOriginalFilename());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}

