package com.supermarket.pos_backend.dto;

import com.supermarket.pos_backend.model.WeightUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantDTO {
    private Double weightValue;
    private WeightUnit weightUnit;
    private BigDecimal price;
    private int stockQuantity;
}
