package com.choice.shopping.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.choice.auth.entity.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "orders")
@Data
// 주문 엔티티
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId; // 주문 ID

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Member member; // 회원 엔티티와의 연관 관계

    @Column(name = "order_comment")
    private String orderComment; // 주문 코멘트

    @Column(name = "order_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime orderDate; // 주문 날짜

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus; // 주문 상태

    @ManyToOne
    @JoinColumn(name = "shipping_address_id")
    private ShippingAddress shippingAddress; // 배송 주소 엔티티와의 연관 관계

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public enum OrderStatus { // 주문 상태
        PENDING, // 처리 대기
        PROCESSING, // 처리 중
        SHIPPED, // 배송 중
        DELIVERED, // 배송 완료
        CANCELLED // 주문 취소
    }
}