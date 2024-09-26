package com.choice.auth.entity;

import com.choice.product.entity.Product;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "member_likes")
@Data
public class MemberLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}