package com.supermarket.pos_backend.repository;

import com.supermarket.pos_backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= p.lowStockThreshold AND p.active = true")
    List<Product> findLowStockProducts();

    List<Product> findByStockQuantityLessThan(int threshold);

    List<Product> findByActiveTrue();

    Optional<Product> findByBarcode(String barcode);

    @Query("SELECT p FROM Product p WHERE p.stockQuantity < p.lowStockThreshold")
    List<Product> findByStockQuantityLessThanThreshold();
}