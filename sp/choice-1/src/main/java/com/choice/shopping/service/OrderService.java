package com.choice.shopping.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.choice.auth.entity.Member;
import com.choice.auth.repository.MemberRepository;
import com.choice.product.entity.Product;
import com.choice.product.entity.ProductImg;
import com.choice.product.repository.ProductRepository;
import com.choice.shopping.dto.OrderDetailDTO;
import com.choice.shopping.dto.OrderItemDTO;
import com.choice.shopping.dto.OrderListResponseDTO;
import com.choice.shopping.dto.OrderRequsetDTO;
import com.choice.shopping.dto.OrderResponseDTO;
import com.choice.shopping.dto.PaymentsRequestDTO;
import com.choice.shopping.entity.OrderItem;
import com.choice.shopping.entity.Orders;
import com.choice.shopping.entity.Payments;
import com.choice.shopping.entity.ShippingAddress;
import com.choice.shopping.repository.CartRepository;
import com.choice.shopping.repository.OrderRepository;
import com.choice.shopping.repository.PaymentsRepository;
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
    public final PaymentsRepository paymentsRepository;

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
        shippingAddress.setPostCode(payload.getPostCode());
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
        orderResponseDTO.setPostCode(payload.getPostCode());

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
    @Transactional
    public OrderResponseDTO completeOrder(PaymentsRequestDTO paymentsRequestDTO) {
        System.out.println("paymentsRequestDTO = " + paymentsRequestDTO.getMerchantUid());
        Orders order = orderRepository.findByMerchantUid(paymentsRequestDTO.getMerchantUid())
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        // 주문 아이템 검증
        validateOrderItems(order, paymentsRequestDTO.getOrderItems());

        // 주문 상태 업데이트
        order.setOrderStatus(Orders.OrderStatus.PROCESSING);

        // 결제 정보 저장
        Payments payment = new Payments();
        payment.setOrder(order);
        payment.setPaymentMethod(paymentsRequestDTO.getPaymentMethod());
        payment.setPayValue(paymentsRequestDTO.getPayValue());
        payment.setPaymentDate(LocalDateTime.now());
        paymentsRepository.save(payment);

        // 주문 정보 업데이트
        order = orderRepository.save(order);

        return new OrderResponseDTO(order, paymentsRequestDTO.getOrderItems());
    }

    private void validateOrderItems(Orders order, List<OrderItemDTO> requestOrderItems) {
        for (OrderItem orderItem : order.getOrderItems()) {
            OrderItemDTO requestItem = requestOrderItems.stream()
                    .filter(item -> item.getProductId().equals(orderItem.getProduct().getProductId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("주문 아이템을 찾을 수 없습니다."));

            if (!orderItem.getQuantity().equals(requestItem.getQuantity())) {
                throw new RuntimeException("주문 아이템 수량이 일치하지 않습니다.");
            }
        }
    }

    public List<OrderListResponseDTO> getOrderListForMember(Long userId) {
        List<Orders> orders = orderRepository.findByMemberUserIdOrderByOrderDateDesc(userId);
        return orders.stream().map(this::convertToOrderListResponseDTO).collect(Collectors.toList());
    }

    private OrderListResponseDTO convertToOrderListResponseDTO(Orders order) {
        OrderListResponseDTO dto = new OrderListResponseDTO();
        dto.setOrderId(order.getOrderId());
        dto.setMerchantUid(order.getMerchantUid());
        dto.setOrderDate(order.getOrderDate());
        dto.setOrderStatus(order.getOrderStatus().toString());
        dto.setOrderItems(
                order.getOrderItems().stream().map(this::convertToOrderItemListDTO).collect(Collectors.toList()));
        return dto;
    }

    private OrderItemDTO convertToOrderItemListDTO(OrderItem orderItem) {
        OrderItemDTO dto = new OrderItemDTO();
        Product product = orderItem.getProduct();
        String imagePath = null;
        if (!product.getImages().isEmpty()) {
            ProductImg firstImage = product.getImages().iterator().next();
            imagePath = "/images/" + firstImage.getPimgPath() + "/" + firstImage.getPimgName();
        }
        dto.setProductId(orderItem.getProduct().getProductId());
        dto.setProductName(orderItem.getProduct().getName());
        dto.setPrice(orderItem.getPrice());
        dto.setQuantity(orderItem.getQuantity());
        dto.setSize(orderItem.getSize());
        dto.setPimgPath(imagePath);
        return dto;
    }

    // 주문 상세 조회
    public OrderDetailDTO getOrderDetail(Long orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));
        List<OrderItemDTO> orderItems = order.getOrderItems().stream()
                .map(this::convertToOrderItemListDTO)
                .collect(Collectors.toList());
        return OrderDetailDTO.builder()
                .orderId(order.getOrderId())
                .recipientName(order.getShippingAddress().getRecipientName())
                .recipientPhone(order.getShippingAddress().getPhone())
                .recipientAddress(order.getShippingAddress().getAddress())
                .recipientMessage(order.getOrderComment())
                .orderDate(order.getOrderDate().toString())
                .orderItems(orderItems)
                .build();
    }
}
