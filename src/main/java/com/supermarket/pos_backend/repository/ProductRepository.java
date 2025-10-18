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

    // Count products with low stock
    long countByStockQuantityLessThan(int threshold);

    // Sum total stock
    @Query("SELECT SUM(p.stockQuantity) FROM Product p")
    Integer getTotalProductsInStock();

    Optional<Product> findByBarcode(String barcode);

    @Query("SELECT p FROM Product p WHERE p.stockQuantity < p.lowStockThreshold")
    List<Product> findByStockQuantityLessThanThreshold();
}