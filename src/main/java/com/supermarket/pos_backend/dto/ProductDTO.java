package com.supermarket.pos_backend.dto;

import com.supermarket.pos_backend.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

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
    private String brandName;
    private String categoryName;
    private ProductVariantDTO variant;

    public static ProductDTO fromEntity(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setBarcode(product.getBarcode());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setLowStockThreshold(product.getLowStockThreshold());
        dto.setActive(product.isActive());
        dto.setImageUrl(product.getImageUrl());

        if (product.getCategory() != null) {
            dto.setCategoryName(product.getCategory().getName());
        }
        if (product.getBrand() != null) {
            dto.setBrandName(product.getBrand().getName());
        }
        if (product.getVariant() != null) {
            dto.setVariant(ProductVariantDTO.fromEntity(product.getVariant()));
        }

        return dto;
    }
}
