package com.choice.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.choice.product.dto.ProductAllDTO;
import com.choice.product.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // @GetMapping
    // public ResponseEntity<Page<ProductAllDTO>> getAllProducts(
    // @PageableDefault(page = 0, size = 25, sort = "productId") Pageable pageable)
    // {
    // Page<ProductAllDTO> products = productService.getAllProducts(pageable);
    // return ResponseEntity.ok(products);
    // }

    @GetMapping
    // public ResponseEntity<Page<ProductAllDTO>> getAllProducts(
    public ResponseEntity<?> getAllProducts(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "25") Integer size,
            @RequestParam(name = "sort", defaultValue = "productId") String sort) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(sort));
        Page<ProductAllDTO> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

}
