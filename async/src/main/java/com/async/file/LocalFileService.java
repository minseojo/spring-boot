package com.async.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalFileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String saveImage(byte[] imageData) throws IOException {
        String fileName = UUID.randomUUID().toString() + ".png";
        Path filePath = Paths.get(uploadDir, fileName);

        Files.createDirectories(filePath.getParent());

        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
            fos.write(imageData);
        }

        return filePath.toString();
    }
}
