package com.choice.admin.dto;

import com.choice.product.entity.ProductInventory.Size;
import lombok.Data;

@Data
public class InventoryDTO {
    private Long productId;
    private Long stock;
    private Size size;
}