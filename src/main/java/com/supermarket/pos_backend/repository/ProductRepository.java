package com.supermarket.pos_backend.repository;

import com.supermarket.pos_backend.model.AdminUser;
import com.supermarket.pos_backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= p.lowStockThreshold AND p.active = true")
    List<Product> findLowStockProducts();

    List<Product> findByStockQuantityLessThan(int threshold);

    List<Product> findByActiveTrue();

    List<Product> findByAdminId(Long adminId);
    Optional<Product> findByIdAndAdminId(Long id, Long adminId);

    List<Product> findByAdmin(AdminUser admin);

    Optional<Product> findByIdAndAdmin(Long id, AdminUser admin);

    @Query("SELECT COALESCE(SUM(p.stockQuantity), 0) FROM Product p WHERE p.admin = :admin")
    Integer getTotalProductsInStockByAdmin(@Param("admin") AdminUser admin);

    // 2️⃣ Low stock products count for a specific admin
    long countByAdminAndStockQuantityLessThan(AdminUser admin, int threshold);

    List<Product> findByAdminAndStockQuantityLessThanEqual(AdminUser admin, int threshold);

    // Count products with low stock
    long countByStockQuantityLessThan(int threshold);

    // Sum total stock
    @Query("SELECT SUM(p.stockQuantity) FROM Product p")
    Integer getTotalProductsInStock();

    Optional<Product> findByBarcode(String barcode);

    @Query("SELECT p FROM Product p WHERE p.admin = :admin AND p.stockQuantity < p.lowStockThreshold")
    List<Product> findByAdminAndStockQuantityLessThanThreshold(@Param("admin") AdminUser admin);

}