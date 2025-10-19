package com.supermarket.pos_backend.repository;

import com.supermarket.pos_backend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
    Optional<Category> findByNameIgnoreCase(String name);
}
