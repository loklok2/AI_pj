package com.choice.shopping.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.choice.shopping.entity.Orders;

public interface OrderRepository extends JpaRepository<Orders, Long> {

    List<Orders> findByMember_UserId(Long userId);

}