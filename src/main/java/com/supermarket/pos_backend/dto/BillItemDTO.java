package com.supermarket.pos_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillItemDTO {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double price;
    private Double totalPrice;

    public static BillItemDTO fromEntity(com.supermarket.pos_backend.model.BillItem item) {
        BillItemDTO dto = new BillItemDTO();
        dto.setProductId(item.getProductId());
        dto.setProductName(item.getProductName());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        dto.setTotalPrice(item.getTotalPrice());
        return dto;
    }
}
