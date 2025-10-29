package com.supermarket.pos_backend.dto;


import lombok.Data;

@Data
public class CreateCreditRequest {
    private String customerName;
    private String customerPhone;
}