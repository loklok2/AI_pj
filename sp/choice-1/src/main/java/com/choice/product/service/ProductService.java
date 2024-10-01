package com.choice.product.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.choice.auth.entity.Member;
import com.choice.auth.entity.MemberLike;
import com.choice.auth.repository.MemberLikeRepository;
import com.choice.auth.repository.MemberRepository;
import com.choice.product.dto.AttributeDTO;
import com.choice.product.dto.ProductAllDTO;
import com.choice.product.dto.ProductDetailDTO;
import com.choice.product.dto.ProductRecommendationDTO;
import com.choice.product.entity.Product;
import com.choice.product.entity.ProductImg;
import com.choice.product.entity.SimilarProduct;
import com.choice.product.repository.ProductRepository;
import com.choice.product.repository.SimilarProductRepository;

@Transactional
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberLikeRepository memberLikeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SimilarProductRepository similarProductRepository;

    // 상품 전체 조회
    public Page<ProductAllDTO> getAllProducts(String category, Pageable pageable) {
        Page<Product> products = productRepository.findAllWithImagesAndCategory(category, pageable);
        return products.map(this::convertToDTO);
    }

    private ProductAllDTO convertToDTO(Product product) {
        ProductAllDTO dto = new ProductAllDTO();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setInfo(product.getInfo());
        dto.setPrice(product.getPrice());
        dto.setLikeCount(product.getLikeCount());
        dto.setViewCount(product.getViewCount());
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
        dto.setProductName(product.getName());
        dto.setInfo(product.getInfo());
        dto.setPrice(product.getPrice());
        dto.setLikeCount(product.getLikeCount());
        dto.setViewCount(product.getViewCount());
        dto.setCategory(product.getCategory());
        if (!product.getImages().isEmpty()) {
            ProductImg firstImage = product.getImages().iterator().next();
            dto.setPimgName(firstImage.getPimgName());
            dto.setPimgPath("/images/" + firstImage.getPimgPath() + "/" + firstImage.getPimgName());

        } else {
            dto.setPimgName(null);
            dto.setPimgPath(null);
        }

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

    // 랜덤 추천 상품 조회
    public List<ProductRecommendationDTO> getSimilarProducts(Long productId) {
        List<SimilarProduct> similarProducts = similarProductRepository.findByProduct_ProductId(productId);
        return similarProducts.stream()
                .map(sp -> {
                    Product similarProduct = sp.getSimilarProduct();
                    return new ProductRecommendationDTO(
                            similarProduct.getProductId(),
                            similarProduct.getName(),
                            similarProduct.getPrice(),
                            getFirstImagePath(similarProduct));
                })
                .collect(Collectors.toList());
    }

    private String getFirstImagePath(Product product) {
        return product.getImages().stream()
                .findFirst()
                .map(img -> "/images/" + img.getPimgPath() + "/" + img.getPimgName())
                .orElse(null);
    }

    @Transactional
    public boolean toggleLike(Long userId, Long productId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<MemberLike> existingLike = memberLikeRepository.findByMemberAndProduct(member, product);
        if (existingLike.isPresent()) {
            memberLikeRepository.delete(existingLike.get());
            product.setLikeCount(product.getLikeCount() - 1);
            return false; // 좋아요 취소
        } else {
            MemberLike newLike = new MemberLike();
            newLike.setMember(member);
            newLike.setProduct(product);
            memberLikeRepository.save(newLike);
            product.setLikeCount(product.getLikeCount() + 1);
            return true; // 좋아요 추가
        }
    }

}
