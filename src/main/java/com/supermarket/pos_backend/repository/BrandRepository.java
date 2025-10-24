package com.supermarket.pos_backend.repository;

import com.supermarket.pos_backend.model.AdminUser;
import com.supermarket.pos_backend.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    boolean existsByName(String name);
    Optional<Brand> findByNameIgnoreCase(String name);
    Optional<Brand> findByNameIgnoreCaseAndAdmin(String name, AdminUser admin);
    boolean existsByNameAndAdmin(String name, AdminUser admin);
    List<Brand> findByAdmin(AdminUser admin);
}
