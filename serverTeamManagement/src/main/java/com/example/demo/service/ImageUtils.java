package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Base64;

public class ImageUtils {
    private static final String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/images/";

    public static void uploadImage(MultipartFile file) throws IOException {
        // ליצור תיקייה אם לא קיימת
        Path uploadPath = Paths.get(UPLOAD_DIRECTORY);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // לשמור את הקובץ עצמו
        Path filePath = uploadPath.resolve(file.getOriginalFilename());
        Files.write(filePath, file.getBytes());
    }

    public static String getImage(String fileName) throws IOException {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }

        Path filePath = Paths.get(UPLOAD_DIRECTORY).resolve(fileName);

        if (!Files.exists(filePath)) {
            System.err.println("❌ File not found: " + filePath);
            return null;
        }

        byte[] imageBytes = Files.readAllBytes(filePath);
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}
