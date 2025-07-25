package com.techzen.techsale.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailDTO {

    private int id;
    private String productName;
    private int quantity;
    private String unit;
    private BigDecimal amount;
}
