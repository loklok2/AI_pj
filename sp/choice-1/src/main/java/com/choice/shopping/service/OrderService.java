package com.choice.shopping.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.choice.auth.entity.Member;
import com.choice.auth.repository.MemberRepository;
import com.choice.product.repository.ProductRepository;
import com.choice.shopping.dto.OrderItemDTO;
import com.choice.shopping.dto.OrderRequsetDTO;
import com.choice.shopping.dto.OrderResponseDTO;
import com.choice.shopping.entity.Orders;
import com.choice.shopping.entity.ShippingAddress;
import com.choice.shopping.repository.CartRepository;
import com.choice.shopping.repository.OrderRepository;
import com.choice.shopping.repository.ShippingAddressRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    public final CartRepository cartRepository;
    public final OrderRepository orderRepository;
    public final ProductRepository productRepository;
    public final MemberRepository memberRepository;
    public final ShippingAddressRepository shippingAddressRepository;

    // 주문 생성 메서드
    public OrderResponseDTO createOrder(OrderRequsetDTO payload, Member member, List<OrderItemDTO> carts) {
        Orders orders = new Orders();
        orders.setMember(member);
        orders.setOrderComment(payload.getRecipientMessage());
        orders.setOrderDate(LocalDateTime.now());
        orders.setOrderStatus(Orders.OrderStatus.PENDING);
        orders.setMerchantUid(generateMerchantUid());

        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setMember(member);
        shippingAddress.setRecipientName(payload.getRecipientName());
        shippingAddress.setAddress(payload.getRecipientAddress());
        shippingAddress.setPhone(payload.getRecipientPhone());
        shippingAddress.setDeliveryInstructions(payload.getRecipientMessage());
        shippingAddress.setCreatedAt(LocalDateTime.now());
        shippingAddress.setUpdatedAt(LocalDateTime.now());
        shippingAddress = shippingAddressRepository.save(shippingAddress);
        orders.setShippingAddress(shippingAddress);
        orderRepository.save(orders);

        OrderResponseDTO orderResponseDTO = new OrderResponseDTO(orders, carts);
        orderResponseDTO.setRecipientMessage(payload.getRecipientMessage());
        orderResponseDTO.setRecipientName(payload.getRecipientName());
        orderResponseDTO.setRecipientPhone(payload.getRecipientPhone());
        orderResponseDTO.setRecipientAddress(payload.getRecipientAddress());

        return orderResponseDTO;
    }

    // 주문번호 MerchantUid 생성
    private String generateMerchantUid() {
        // 현재 날짜와 시간을 포함한 고유한 문자열 생성
        String uniqueString = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime today = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDay = today.format(formatter).replace("-", "");

        // 무작위 문자열과 현재 날짜/시간을 조합하여 주문번호 생성
        return formattedDay + '-' + uniqueString;
    }

    // 주문 완료 메서드
    public OrderResponseDTO completeOrder(OrderRequsetDTO request) {
        return null;
    }
}
