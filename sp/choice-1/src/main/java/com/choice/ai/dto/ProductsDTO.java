package com.choice.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductsDTO {
    private Long productId;
    private String name;
    private String info;
    private Long price;
    private String imagePath;
}
