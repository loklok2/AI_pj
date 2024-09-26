package com.choice.shopping.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.choice.auth.entity.Member;
import com.choice.auth.repository.MemberRepository;
import com.choice.product.entity.Product;
import com.choice.product.entity.ProductImg;
import com.choice.product.repository.ProductRepository;
import com.choice.shopping.dto.CartItemDTO;
import com.choice.shopping.dto.CartSummaryDTO;
import com.choice.shopping.dto.OrderDTO;
import com.choice.shopping.dto.OrderItemDTO;
import com.choice.shopping.dto.ProductOrderDTO;
import com.choice.shopping.entity.OrderItem;
import com.choice.shopping.entity.Orders;
import com.choice.shopping.entity.ShippingAddress;
import com.choice.shopping.repository.OrderRepository;
import com.choice.shopping.repository.ShippingAddressRepository;

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

    @Autowired
    private ShippingAddressRepository shippingAddressRepository;

    // 주문 내역 조회
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
        dto.setSize(item.getSize());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());

        if (!item.getProduct().getImages().isEmpty()) {
            ProductImg firstImage = item.getProduct().getImages().iterator().next();
            dto.setPimgName(firstImage.getPimgName());
            dto.setPimgPath("/images/" + firstImage.getPimgPath() + "/" + firstImage.getPimgName());
        } else {
            dto.setPimgName(null);
            dto.setPimgPath(null);
        }

        return dto;
    }

    // 총 주문 금액 계산
    private Long calculateTotalAmount(Orders order) {
        return order.getOrderItems().stream()
                .mapToLong(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    // 주문 생성
    @Transactional
    public Orders createOrderFromCart(Long userId, ProductOrderDTO productOrderDTO) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        CartSummaryDTO cartSummary = cartService.getCartItemsUser(userId);
        List<CartItemDTO> cartItems = cartSummary.getItems();
        if (cartItems.isEmpty()) {
            throw new RuntimeException("장바구니가 비어있습니다.");
        }

        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setMember(member);
        shippingAddress.setRecipientName(productOrderDTO.getRecipientName());
        shippingAddress.setAddress(productOrderDTO.getAddress());
        shippingAddress.setPhone(productOrderDTO.getPhone());
        shippingAddress.setDeliveryInstructions(productOrderDTO.getDeliveryInstructions());
        shippingAddress.setCreatedAt(LocalDateTime.now());
        shippingAddress.setUpdatedAt(LocalDateTime.now());
        shippingAddress = shippingAddressRepository.save(shippingAddress);

        Orders order = new Orders();
        order.setMember(member);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(Orders.OrderStatus.PENDING);
        order.setShippingAddress(shippingAddress);

        for (CartItemDTO cartItem : cartItems) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(productRepository.findById(cartItem.getProductId()).orElseThrow());
            item.setQuantity(cartItem.getQuantity());
            item.setPrice(cartItem.getPrice());
            item.setSize(cartItem.getSize());
            order.getOrderItems().add(item);
        }

        order = orderRepository.save(order);

        cartService.clearCart(userId);

        return order;
    }

    @Transactional
    public Orders createOrderFromProduct(Long userId, ProductOrderDTO productOrderDTO) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Product product = productRepository.findById(productOrderDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        // 새로운 배송지 생성
        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setMember(member);
        shippingAddress.setRecipientName(productOrderDTO.getRecipientName());
        shippingAddress.setAddress(productOrderDTO.getAddress());
        shippingAddress.setPhone(productOrderDTO.getPhone());
        shippingAddress.setDeliveryInstructions(productOrderDTO.getDeliveryInstructions());
        shippingAddress.setCreatedAt(LocalDateTime.now());
        shippingAddress.setUpdatedAt(LocalDateTime.now());
        shippingAddress = shippingAddressRepository.save(shippingAddress);

        Orders order = new Orders();
        order.setMember(member);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(Orders.OrderStatus.PENDING);
        order.setShippingAddress(shippingAddress);

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(productOrderDTO.getQuantity());
        item.setPrice(product.getPrice());
        order.getOrderItems().add(item);

        return orderRepository.save(order);
    }

}
