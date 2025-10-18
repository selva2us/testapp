package com.supermarket.pos_backend.dto;

import com.supermarket.pos_backend.model.WeightUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProductVariantDTO {
    private Long id;
    private Double weightValue;
    private WeightUnit weightUnit;
    private BigDecimal price;
    private int stockQuantity;
}
