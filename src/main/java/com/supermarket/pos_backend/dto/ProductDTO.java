package com.supermarket.pos_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String name;
    private Long categoryId;
    private Long brandId;
    private String barcode;
    private BigDecimal price;
    private int stockQuantity;
    private int lowStockThreshold;
    private String imageUrl;
    private boolean active;
}
