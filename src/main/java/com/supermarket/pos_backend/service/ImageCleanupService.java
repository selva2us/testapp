package com.supermarket.pos_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class ImageCleanupService {

    @Value("${app.upload.dir}")
    private String uploadDir; // e.g. "uploads" or "/var/www/uploads"

    public String deleteAllFiles() {
        File folder = new File(uploadDir);
        if (!folder.exists()) {
            return "Upload directory does not exist: " + uploadDir;
        }

        int deletedCount = deleteRecursively(folder);

        return "✅ Cleanup completed. Total deleted files/folders: " + deletedCount;
    }

    private int deleteRecursively(File folder) {
        int count = 0;
        File[] files = folder.listFiles();
        if (files == null) return 0;

        for (File file : files) {
            if (file.isDirectory()) {
                count += deleteRecursively(file);
            }
            if (file.delete()) {
                count++;
            }
        }
        return count;
    }
}
