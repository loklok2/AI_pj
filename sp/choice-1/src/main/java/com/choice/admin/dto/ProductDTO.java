package com.choice.admin.dto;

import java.util.Set;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ProductDTO {
    private Long productId;
    private String name;
    private String info;
    private Long sell;
    private Long price;
    private Long likeCount;
    private LocalDateTime createDate;
    private Long viewCount;
    private Set<String> attributeNames;
}