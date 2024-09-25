package com.choice.admin.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.choice.ai.service.FlaskClientService;
import com.choice.product.entity.Product;
import com.choice.product.entity.ProductImg;
import com.choice.product.repository.ProductRepository;
import com.choice.store.repository.StoresSalesRepository;

@Transactional
@Service
public class AdminRecommendationService {

    @Autowired
    private StoresSalesRepository storesSalesRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FlaskClientService flaskClientService;

    public Map<String, Object> getRecommendedProducts(Integer year, Integer month, Integer day, Long storeId) {
        LocalDate now = LocalDate.now();
        year = (year != null) ? year : now.getYear();
        month = (month != null) ? month : now.getMonthValue();
        day = (day != null) ? day : now.getDayOfMonth();

        List<Object[]> topSellingProducts = storesSalesRepository.getTopSellingProducts(year, month, day, storeId);

        List<Map<String, Object>> productDataList = topSellingProducts.stream()
                .map(obj -> {
                    Long productId = ((Number) obj[0]).longValue();
                    String productName = (String) obj[1];
                    Long totalQuantity = ((Number) obj[2]).longValue();

                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

                    String imagePath = null;
                    if (!product.getImages().isEmpty()) {
                        ProductImg firstImage = product.getImages().iterator().next();
                        imagePath = "/images/" + firstImage.getPimgPath() + "/" + firstImage.getPimgName();
                    }

                    Map<String, Object> productData = new HashMap<>();
                    productData.put("productId", productId);
                    productData.put("productName", productName);
                    productData.put("totalQuantity", totalQuantity);
                    productData.put("category", product.getCategory());
                    productData.put("pimgPath", imagePath);
                    return productData;
                })
                .collect(Collectors.toList());

        return flaskClientService.getAdminRecommendations(productDataList);
    }
}