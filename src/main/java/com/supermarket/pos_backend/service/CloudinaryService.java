package com.supermarket.pos_backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }
    // Existing MultipartFile method (for API uploads)
    public String uploadFile(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("folder", "supermarket_uploads"));
        return uploadResult.get("secure_url").toString();
    }

    // New method for java.io.File (for Excel import)
    public String uploadFile(File file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file,
                ObjectUtils.asMap("folder", "supermarket_uploads"));
        return uploadResult.get("secure_url").toString();
    }

    // ✅ Upload directly from a remote URL
    public String uploadFromUrl(String imageUrl) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(imageUrl,
                ObjectUtils.asMap("folder", "supermarket_uploads"));
        return uploadResult.get("secure_url").toString();
    }
}
