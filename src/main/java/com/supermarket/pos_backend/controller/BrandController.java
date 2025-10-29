package com.supermarket.pos_backend.controller;

import com.supermarket.pos_backend.annotations.CurrentAdmin;
import com.supermarket.pos_backend.model.AdminUser;
import com.supermarket.pos_backend.model.Brand;
import com.supermarket.pos_backend.repository.BrandRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/brands")
@Tag(name = "Brand", description = "Shop management APIs")
@RequiredArgsConstructor
public class BrandController {

    private final BrandRepository brandRepository;

    @PostMapping
    public ResponseEntity<?> create(@CurrentAdmin AdminUser admin, @RequestBody Brand brand) {
        try {
             brand.setAdmin(admin);
            if (brandRepository.existsByNameAndAdmin(brand.getName(), admin)) {
                return ResponseEntity.badRequest().body("Brand already exists for this admin");
            }
            return ResponseEntity.ok(brandRepository.save(brand));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAll(@CurrentAdmin AdminUser admin) {
        try {
            return ResponseEntity.ok(brandRepository.findByAdmin(admin));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        brandRepository.deleteById(id);
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Brand deleted successfully");
        return ResponseEntity.ok(response);
    }
}
