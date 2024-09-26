package com.choice.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.choice.auth.entity.Member;
import com.choice.auth.entity.MemberLike;
import com.choice.product.entity.Product;

public interface MemberLikeRepository extends JpaRepository<MemberLike, Long> {
    Optional<MemberLike> findByMemberAndProduct(Member member, Product product);
}
