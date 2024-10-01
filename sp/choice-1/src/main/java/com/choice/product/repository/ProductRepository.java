package com.choice.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import com.choice.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

        @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE p.productId IN :ids")
        List<Product> findAllWithImagesById(@Param("ids") List<Long> ids);

        // @Query(value = "SELECT * FROM product_details_view WHERE product_id IN
        // (:productIds)", nativeQuery = true)
        // List<Object[]> findProductDetailsById(@Param("productIds") List<Long>
        // productIds);

        // @Query(value = "SELECT p FROM Product p LEFT JOIN FETCH p.images", countQuery
        // = "SELECT COUNT(p) FROM Product p")
        // Page<Product> findAllWithImages(Pageable pageable);

        @Query(value = "SELECT p.product_id, p.name, p.info, p.price, p.like_count, p.view_count, p.category, " +
                        "pi.pimg_name, CONCAT('/images/', pi.pimg_path, '/', pi.pimg_name) AS pimg_path, " +
                        "GROUP_CONCAT(DISTINCT pa.name_ko ORDER BY pa.name_ko SEPARATOR ', ') AS attributes, " +
                        "GROUP_CONCAT(DISTINCT piv.size ORDER BY piv.size SEPARATOR ', ') AS sizes " +
                        "FROM product p " +
                        "LEFT JOIN product_img pi ON p.product_id = pi.product_id " +
                        "LEFT JOIN product_attribute_link pal ON p.product_id = pal.product_id " +
                        "LEFT JOIN product_attributes pa ON pal.attribute_id = pa.attribute_id " +
                        "LEFT JOIN product_inventory piv ON p.product_id = piv.product_id " +
                        "WHERE p.product_id IN :ids " +
                        "GROUP BY p.product_id, p.name, p.info, p.price, p.like_count, p.view_count, p.category, pi.pimg_name, pi.pimg_path", nativeQuery = true)
        List<Object[]> findProductDetailsById(@Param("ids") List<Long> ids);

        @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images " +
                        "WHERE (:category IS NULL OR p.category = :category)")
        Page<Product> findAllWithImagesAndCategory(@Param("category") String category, Pageable pageable);

        @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images LEFT JOIN FETCH p.attributeLinks WHERE p.productId = :id")
        Optional<Product> findByIdWithDetails(@Param("id") Long id);

        @Procedure(name = "getrandomproductsbycategory")
        List<Product> getrandomproductsbycategory(
                        @Param("p_category") String category,
                        @Param("p_exclude_product_id") Long excludeProductId,
                        @Param("p_limit") Integer limit);

        @Query("SELECT ml.product FROM MemberLike ml WHERE ml.member.userId = :userId")
        List<Product> findLikedProductsByUserId(@Param("userId") Long userId);

}