package com.choice.shopping.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.choice.auth.entity.Member;
import com.choice.auth.repository.MemberRepository;
import com.choice.product.repository.ProductRepository;
import com.choice.shopping.dto.OrderDTO;
import com.choice.shopping.dto.OrderItemDTO;
import com.choice.shopping.dto.CartItemDTO;
import com.choice.shopping.entity.OrderItem;
import com.choice.shopping.entity.Orders;
import com.choice.shopping.repository.OrderRepository;

@Service
public class OrderService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductRepository productRepository;

    public List<OrderDTO> getOrdersByUserId(Long userId) {
        List<Orders> orders = orderRepository.findByMember_UserId(userId);
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private OrderDTO convertToDTO(Orders order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setOrderDate(order.getOrderDate());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setTotalAmount(calculateTotalAmount(order));
        dto.setOrderItems(order.getOrderItems().stream()
                .map(this::convertToOrderItemDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private OrderItemDTO convertToOrderItemDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(item.getProduct().getProductId());
        dto.setProductName(item.getProduct().getName());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        return dto;
    }

    private Long calculateTotalAmount(Orders order) {
        return order.getOrderItems().stream()
                .mapToLong(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    @Transactional
    public Orders createOrderFromCart(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        List<CartItemDTO> cartItems = cartService.getCartItems(userId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("장바구니가 비어있습니다.");
        }

        Orders order = new Orders();
        order.setMember(member);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(Orders.OrderStatus.PENDING);

        for (CartItemDTO cartItem : cartItems) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(productRepository.findById(cartItem.getProductId()).orElseThrow());
            item.setQuantity(cartItem.getQuantity());
            item.setPrice(cartItem.getPrice());
            order.getOrderItems().add(item);
        }

        order = orderRepository.save(order);

        cartService.clearCart(userId);

        return order;
    }
}
