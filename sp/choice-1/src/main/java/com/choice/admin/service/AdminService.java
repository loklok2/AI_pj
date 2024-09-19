package com.choice.admin.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.choice.admin.dto.DailySalesReportDTO;
import com.choice.admin.dto.InventoryDTO;
import com.choice.admin.dto.OrderSummaryDTO;
import com.choice.admin.dto.ProductDTO;
import com.choice.auth.entity.Member;
import com.choice.auth.entity.Role;
import com.choice.auth.repository.MemberRepository;
import com.choice.board.entity.Qboard;
import com.choice.board.repository.CommentRepository;
import com.choice.board.repository.QboardRepository;
import com.choice.product.entity.Product;
import com.choice.product.entity.ProductAttribute;
import com.choice.product.entity.ProductAttributeLink;
import com.choice.product.entity.ProductInventory;
import com.choice.product.repository.ProductAttributeRepository;
import com.choice.product.repository.ProductInventoryRepository;
import com.choice.product.repository.ProductRepository;
import com.choice.shopping.entity.Orders;
import com.choice.shopping.repository.OrderRepository;

@Service
@Transactional
public class AdminService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private QboardRepository qboardRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductAttributeRepository productAttributeRepository;

    @Autowired
    private ProductInventoryRepository productInventoryRepository;

    @Autowired
    private CommentRepository commentRepository;

    // 회원 수 조회
    public long getTotalMemberCount() {
        return memberRepository.count();
    }

    // 회원 정보 조회
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    // 게시글 수 조회
    public long getTotalQboardCount() {
        return qboardRepository.count();
    }

    // 게시글 정보 조회
    public List<Qboard> getAllQboards() {
        return qboardRepository.findAll();
    }

    // 관리자 댓글 수 조회
    public long getAdminCommentCount() {
        return commentRepository.countByMember_Role(Role.ADMIN);
    }

    // 상품 정보 조회
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 회원 정보 삭제
    public void deleteMember(Long memberId) {
        memberRepository.deleteById(memberId);
    }

    // 게시글 정보 삭제
    public void deleteQboard(Long qboardId) {
        qboardRepository.deleteById(qboardId);
    }

    // 상품 정보 삭제
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    // 주문 정보 조회
    public List<OrderSummaryDTO> getAllOrders() {
        List<Orders> orders = orderRepository.findAll();
        return orders.stream()
                .map(this::convertToOrderSummaryDTO)
                .collect(Collectors.toList());
    }

    public DailySalesReportDTO getDailySalesReport(LocalDate date) {
        List<Object[]> results = orderRepository.findDailySalesReport(date, date);
        if (results.isEmpty()) {
            return new DailySalesReportDTO(date, 0L, 0L);
        }
        Object[] result = results.get(0);
        return new DailySalesReportDTO(
                ((java.sql.Date) result[0]).toLocalDate(),
                ((Number) result[1]).longValue(),
                ((Number) result[2]).longValue());
    }

    public List<DailySalesReportDTO> getDailySalesReportRange(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = orderRepository.findDailySalesReport(startDate, endDate);
        return results.stream()
                .map(result -> new DailySalesReportDTO(
                        ((java.sql.Date) result[0]).toLocalDate(),
                        ((Number) result[1]).longValue(),
                        ((Number) result[2]).longValue()))
                .collect(Collectors.toList());
    }

    // 월별 매출 조회
    public List<DailySalesReportDTO> getMonthlySalesReport(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = orderRepository.findMonthlySalesReport(startDate, endDate);
        return results.stream()
                .map(result -> new DailySalesReportDTO(
                        ((java.sql.Date) result[0]).toLocalDate(),
                        ((Number) result[1]).longValue(),
                        ((Number) result[2]).longValue()))
                .collect(Collectors.toList());
    }

    // 상품 추가
    public Product addProduct(ProductDTO productDTO, InventoryDTO inventoryDTO) {
        Product product = new Product();
        updateProductFromDTO(product, productDTO);
        product.setCreateDate(LocalDateTime.now());
        product = productRepository.save(product);

        ProductInventory inventory = new ProductInventory();
        inventory.setProduct(product);
        updateInventoryFromDTO(inventory, inventoryDTO);
        productInventoryRepository.save(inventory);

        return product;
    }

    // 상품 업데이트
    public Product updateProduct(Long productId, ProductDTO productDTO, InventoryDTO inventoryDTO) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        updateProductFromDTO(product, productDTO);
        product = productRepository.save(product);

        ProductInventory inventory = productInventoryRepository.findByProduct(product)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
        updateInventoryFromDTO(inventory, inventoryDTO);
        productInventoryRepository.save(inventory);

        return product;
    }

    // 상품 정보 업데이트
    private void updateProductFromDTO(Product product, ProductDTO productDTO) {
        product.setName(productDTO.getName());
        product.setInfo(productDTO.getInfo());
        product.setSell(productDTO.getSell());
        product.setPrice(productDTO.getPrice());
        product.setLikeCount(productDTO.getLikeCount());
        product.setView(productDTO.getView());

        if (productDTO.getAttributeIds() != null) {
            Set<ProductAttributeLink> attributeLinks = productDTO.getAttributeIds().stream()
                    .map(id -> {
                        ProductAttribute attribute = productAttributeRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Attribute not found"));
                        ProductAttributeLink link = new ProductAttributeLink();
                        link.setProduct(product);
                        link.setAttribute(attribute);
                        return link;
                    })
                    .collect(Collectors.toSet());
            product.getAttributeLinks().clear();
            product.getAttributeLinks().addAll(attributeLinks);
        }
    }

    // 주문 정보 변환
    private OrderSummaryDTO convertToOrderSummaryDTO(Orders order) {
        Long totalAmount = order.getOrderItems().stream()
                .mapToLong(item -> item.getPrice() * item.getQuantity())
                .sum();

        return new OrderSummaryDTO(
                order.getOrderId(),
                order.getMember().getUsername(),
                order.getOrderDate(),
                totalAmount,
                order.getOrderStatus());
    }

    private void updateInventoryFromDTO(ProductInventory inventory, InventoryDTO inventoryDTO) {
        inventory.setStock(inventoryDTO.getStock());
        inventory.setSize(inventoryDTO.getSize());
        inventory.setLastUpdated(LocalDateTime.now());
    }

    @Transactional
    public Orders updateOrderStatus(Long orderId, Orders.OrderStatus newStatus) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setOrderStatus(newStatus);
        return orderRepository.save(order);
    }

    @Transactional
    public Orders cancelOrder(Long orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getOrderStatus() == Orders.OrderStatus.SHIPPED
                || order.getOrderStatus() == Orders.OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot cancel shipped or delivered orders");
        }
        order.setOrderStatus(Orders.OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    // 카테고리별 매출 조회
    public Map<String, Double> getCategorySalesPercentage() {
        List<Object[]> results = orderRepository.findCategorySalesPercentage();
        Map<String, Double> percentages = new HashMap<>();
        for (Object[] row : results) {
            String category = (String) row[0];
            Double percentage = ((Number) row[2]).doubleValue();
            percentages.put(category, Math.round(percentage * 100.0) / 100.0);
        }
        return percentages;
    }
}