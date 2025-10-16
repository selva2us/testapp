package com.supermarket.pos_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnItemDTO {
    private Long productId;
    private Integer quantity;
    private String reason;
}
