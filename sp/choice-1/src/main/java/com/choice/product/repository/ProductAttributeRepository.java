package com.choice.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.choice.product.entity.ProductAttribute;

public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {

    Optional<ProductAttribute> findByNameKo(String nameKo);
}