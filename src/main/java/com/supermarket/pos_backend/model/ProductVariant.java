package com.supermarket.pos_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "product_variants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🧮 Numeric part of the weight or volume
    @Column(nullable = false)
    private Double weightValue; // e.g., 200, 500, 1, 2

    // ⚖️ Unit of measurement (ml, L, g, kg, packet, piece, etc.)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WeightUnit weightUnit;

    // 💰 Variant-specific price
    @Column(nullable = false)
    private BigDecimal price;

    // 📦 Stock for this variant
    @Column(nullable = false)
    private int stockQuantity;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // 🔤 Computed label (optional helper)
    public String getDisplayLabel() {
        return weightValue + " " + weightUnit.getLabel();
    }
}
