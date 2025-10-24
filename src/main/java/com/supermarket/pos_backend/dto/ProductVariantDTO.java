package com.supermarket.pos_backend.dto;

import com.supermarket.pos_backend.model.ProductVariant;
import com.supermarket.pos_backend.model.WeightUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantDTO {
    private Long id;
    private Double weightValue;
    private WeightUnit weightUnit;
    private BigDecimal price;
    private int stockQuantity;

    public static ProductVariantDTO fromEntity(ProductVariant variant) {
        if (variant == null) return null;

        ProductVariantDTO dto = new ProductVariantDTO();
        dto.setId(variant.getId());
        dto.setWeightValue(variant.getWeightValue());
        dto.setWeightUnit(variant.getWeightUnit());
        dto.setPrice(variant.getPrice());
        dto.setStockQuantity(variant.getStockQuantity());
        return dto;
    }
}
