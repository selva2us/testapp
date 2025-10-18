package com.supermarket.pos_backend.repository;

import com.supermarket.pos_backend.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {}