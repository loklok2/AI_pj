package com.choice.shopping.dto;

import lombok.Data;
import java.util.List;

@Data
public class CartSummaryDTO {
    private List<CartItemDTO> items;
    private Long total;
}