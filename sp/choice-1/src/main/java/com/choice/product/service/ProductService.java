package com.choice.product.service;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.choice.product.dto.AttributeDTO;
import com.choice.product.dto.ProductAllDTO;
import com.choice.product.dto.ProductDetailDTO;
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
        dto.setCategory(product.getCategory());
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

    public ProductDetailDTO getProductDetail(Long id) {
        Product product = productRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return convertToDetailDTO(product);
    }

    private ProductDetailDTO convertToDetailDTO(Product product) {
        ProductDetailDTO dto = new ProductDetailDTO();
        // 기본 정보 설정
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setInfo(product.getInfo());
        dto.setPrice(product.getPrice());
        dto.setLikeCount(product.getLikeCount());
        dto.setView(product.getView());
        dto.setCategory(product.getCategory());

        // 이미지 정보 설정
        dto.setImages(product.getImages().stream()
                .map(img -> "/images/" + img.getPimgPath() + "/" + img.getPimgName())
                .collect(Collectors.toList()));

        // 속성 정보 설정
        dto.setAttributes(product.getAttributeLinks().stream()
                .map(link -> {
                    AttributeDTO attributeDTO = new AttributeDTO();
                    attributeDTO.setName(link.getAttribute().getNameKo());
                    return attributeDTO;
                })
                .collect(Collectors.toList()));

        return dto;
    }

}
