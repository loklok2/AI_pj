package com.choice.shopping.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.choice.shopping.entity.ShippingAddress;

public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {

}
