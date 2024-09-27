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
import com.choice.shopping.dto.*;
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

    public List<OrderResponseDTO> getOrdersByUserId(Long userId) {
        List<Orders> orders = orderRepository.findByMember_UserId(userId);
        return orders.stream().map(this::convertToOrderResponseDTO).collect(Collectors.toList());
    }

    private OrderResponseDTO convertToOrderResponseDTO(Orders order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(order.getOrderId());
        dto.setUsername(order.getMember().getUsername());
        dto.setOrderDate(order.getOrderDate());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setTotalAmount(calculateTotalAmount(order));
        dto.setOrderItems(order.getOrderItems().stream()
                .map(this::convertToOrderItemResponseDTO)
                .collect(Collectors.toList()));
        dto.setShippingAddress(convertToShippingAddressDTO(order.getShippingAddress()));
        return dto;
    }

    private OrderItemResponseDTO convertToOrderItemResponseDTO(OrderItem item) {
        OrderItemResponseDTO dto = new OrderItemResponseDTO();
        dto.setProductId(item.getProduct().getProductId());
        dto.setProductName(item.getProduct().getName());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        dto.setSize(item.getSize());

        if (!item.getProduct().getImages().isEmpty()) {
            ProductImg firstImage = item.getProduct().getImages().iterator().next();
            dto.setPimgPath("/images/" + firstImage.getPimgPath() + "/" + firstImage.getPimgName());
        }

        return dto;
    }

    private ShippingAddressDTO convertToShippingAddressDTO(ShippingAddress address) {
        ShippingAddressDTO dto = new ShippingAddressDTO();
        dto.setAddressId(address.getAddressId());
        dto.setRecipientName(address.getRecipientName());
        dto.setAddress(address.getAddress());
        dto.setPhone(address.getPhone());
        dto.setDeliveryInstructions(address.getDeliveryInstructions());
        return dto;
    }

    private Long calculateTotalAmount(Orders order) {
        return order.getOrderItems().stream()
                .mapToLong(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    @Transactional
    public OrderResponseDTO createOrderFromCart(Long userId, ProductOrderDTO productOrderDTO) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        CartSummaryDTO cartSummary = cartService.getCartItemsUser(userId);
        List<CartItemDTO> cartItems = cartSummary.getItems();
        if (cartItems.isEmpty()) {
            throw new RuntimeException("장바구니가 비어있습니다.");
        }

        ShippingAddress shippingAddress = createShippingAddress(member, productOrderDTO);
        Orders order = createOrder(member, shippingAddress);

        for (CartItemDTO cartItem : cartItems) {
            OrderItem item = createOrderItem(order, cartItem);
            order.getOrderItems().add(item);
        }

        order = orderRepository.save(order);
        cartService.clearCart(userId);

        return convertToOrderResponseDTO(order);
    }

    @Transactional
    public OrderResponseDTO createOrderFromProduct(Long userId, ProductOrderDTO productOrderDTO) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Product product = productRepository.findById(productOrderDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        ShippingAddress shippingAddress = createShippingAddress(member, productOrderDTO);
        Orders order = createOrder(member, shippingAddress);

        OrderItem item = createOrderItem(order, product, productOrderDTO.getQuantity());
        order.getOrderItems().add(item);

        order = orderRepository.save(order);

        return convertToOrderResponseDTO(order);
    }

    private ShippingAddress createShippingAddress(Member member, ProductOrderDTO productOrderDTO) {
        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setMember(member);
        shippingAddress.setRecipientName(productOrderDTO.getRecipientName());
        shippingAddress.setAddress(productOrderDTO.getAddress());
        shippingAddress.setPhone(productOrderDTO.getPhone());
        shippingAddress.setDeliveryInstructions(productOrderDTO.getDeliveryInstructions());
        shippingAddress.setCreatedAt(LocalDateTime.now());
        shippingAddress.setUpdatedAt(LocalDateTime.now());
        return shippingAddressRepository.save(shippingAddress);
    }

    private Orders createOrder(Member member, ShippingAddress shippingAddress) {
        Orders order = new Orders();
        order.setMember(member);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(Orders.OrderStatus.PENDING);
        order.setShippingAddress(shippingAddress);
        return order;
    }

    private OrderItem createOrderItem(Orders order, CartItemDTO cartItem) {
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(productRepository.findById(cartItem.getProductId()).orElseThrow());
        item.setQuantity(cartItem.getQuantity());
        item.setPrice(cartItem.getPrice());
        item.setSize(cartItem.getSize());
        return item;
    }

    private OrderItem createOrderItem(Orders order, Product product, int quantity) {
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setPrice(product.getPrice());
        return item;
    }
}