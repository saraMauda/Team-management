package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class ImageUtils {
    private static String UPLOAD_DIRECTORY = System.getProperty("user.dir")+"\\images\\";
    public static void uploadImage(MultipartFile file) throws IOException {
        String path = UPLOAD_DIRECTORY + file.getOriginalFilename();
        Path fileName = Paths.get(path);
        Files.write(fileName, file.getBytes());
    }

    public static String getImage(String path) throws IOException {
        Path fileName = Paths.get(UPLOAD_DIRECTORY+path);
        byte[] byteImage = Files.readAllBytes(fileName);
        return Base64.getEncoder().encodeToString(byteImage);
    }

}
