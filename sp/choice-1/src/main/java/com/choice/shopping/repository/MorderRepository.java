package com.choice.shopping.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.choice.shopping.entity.Order;

public interface MorderRepository extends JpaRepository<Order, Long> {

}
