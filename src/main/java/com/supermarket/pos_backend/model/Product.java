package com.supermarket.pos_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Column(unique = true, nullable = false)
    private String barcode;
    @PreUpdate
    public void generateBarcode() {
        if (barcode == null || barcode.isEmpty()) {
            barcode = "BC" + System.currentTimeMillis();
        }
    }
    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private int stockQuantity;

    @Column(nullable = false)
    private int lowStockThreshold = 10; // configurable per product

    private String imageUrl;

    private boolean active = true;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;

    // Helper method to set both sides
    public void setVariant(ProductVariant variant) {
        this.variant = variant;
        if (variant != null) {
            variant.setProduct(this);
        }
    }
}

