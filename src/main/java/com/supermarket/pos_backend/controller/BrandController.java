package com.supermarket.pos_backend.controller;

import com.supermarket.pos_backend.model.Brand;
import com.supermarket.pos_backend.repository.BrandRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@Tag(name = "Brand", description = "Shop management APIs")
@RequiredArgsConstructor
public class BrandController {

    private final BrandRepository brandRepository;

    @PostMapping
    public ResponseEntity<Brand> create(@RequestBody Brand brand) {
        if (brandRepository.existsByName(brand.getName())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(brandRepository.save(brand));
    }

    @GetMapping
    public List<Brand> getAll() {
        return brandRepository.findAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        brandRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
