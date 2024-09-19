package com.choice.product.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.choice.product.dto.ProductAllDTO;
import com.choice.product.entity.Product;
import com.choice.product.entity.ProductImg;
import com.choice.product.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Page<ProductAllDTO> getAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAllWithImages(pageable);
        return products.map(this::convertToDTO);
    }

    private ProductAllDTO convertToDTO(Product product) {
        ProductAllDTO dto = new ProductAllDTO();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setInfo(product.getInfo());
        dto.setPrice(product.getPrice());
        dto.setLikeCount(product.getLikeCount());
        dto.setView(product.getView());
        if (!product.getImages().isEmpty()) {
            ProductImg firstImage = product.getImages().iterator().next();
            dto.setPimgName(firstImage.getPimgName());
            dto.setPimgPath("/images/" + firstImage.getPimgPath() + "/" + firstImage.getPimgName());

        } else {
            dto.setPimgName(null);
            dto.setPimgPath(null);
        }
        return dto;
    }
}
