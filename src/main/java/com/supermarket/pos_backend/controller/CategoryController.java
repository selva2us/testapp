package com.supermarket.pos_backend.controller;

import com.supermarket.pos_backend.annotations.CurrentAdmin;
import com.supermarket.pos_backend.model.AdminUser;
import com.supermarket.pos_backend.model.Category;
import com.supermarket.pos_backend.repository.CategoryRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Category", description = "Shop management APIs")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @PostMapping
    public ResponseEntity<?> create(@CurrentAdmin AdminUser admin,@RequestBody Category category) {
        try {
            category.setAdmin(admin);
            if (categoryRepository.existsByNameAndAdmin(category.getName(), admin)) {
                return ResponseEntity.badRequest().body("Brand already exists for this admin");
            }
            return ResponseEntity.ok(categoryRepository.save(category));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAll(@CurrentAdmin AdminUser admin) {
        try {
            return ResponseEntity.ok(categoryRepository.findByAdmin(admin));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Staff deleted successfully");
        return ResponseEntity.ok(response);
    }
}
