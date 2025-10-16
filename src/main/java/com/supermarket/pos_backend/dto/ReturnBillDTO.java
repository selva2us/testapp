package com.supermarket.pos_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnBillDTO {
    private Long billId;
    private List<ReturnItemDTO> items;
    private String staffName;
}
