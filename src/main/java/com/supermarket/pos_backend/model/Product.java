package com.supermarket.pos_backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonBackReference("category-products")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    @JsonBackReference("brand-products")
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    @JsonBackReference("admin-products")
    private AdminUser admin;

    @Column(unique = true, nullable = false)
    private String barcode;

    @PrePersist
    @PreUpdate
    public void ensureBarcode() {
        if (barcode == null || barcode.isEmpty()) {
            barcode = "BC" + System.currentTimeMillis() + (int)(Math.random() * 9000 + 1000);
        }
    }
    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private int stockQuantity;

    @Column(nullable = false)
    private int lowStockThreshold = 10;

    private String imageUrl;
    private boolean active = true;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "variant_id")
    @JsonManagedReference("product-variant")
    private ProductVariant variant;

    // Helper method to set both sides
    public void setVariant(ProductVariant variant) {
        this.variant = variant;
        if (variant != null) {
            variant.setProduct(this);
        }
    }
}
