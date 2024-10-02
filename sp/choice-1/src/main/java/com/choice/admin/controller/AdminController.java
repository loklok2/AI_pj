package com.choice.admin.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.choice.admin.dto.DailySalesReportDTO;
import com.choice.admin.dto.InventoryDTO;
import com.choice.admin.dto.OrderSummaryDTO;
import com.choice.admin.dto.ProductDTO;
import com.choice.admin.service.AdminService;
import com.choice.auth.entity.Member;
import com.choice.board.dto.QboardDTO;
import com.choice.product.entity.Product;
import com.choice.shopping.entity.Orders;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // 회원 수 조회
    @GetMapping("/members/count")
    public ResponseEntity<?> getTotalMemberCount() {
        try {
            long count = adminService.getTotalMemberCount(); // 회원수 조회 메서드 호출
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("회원 수 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 회원 목록 조회
    @GetMapping("/members")
    public ResponseEntity<?> getAllMembers() {
        try {
            List<Member> members = adminService.getAllMembers(); // 회원 목록 조회 메서드 호출
            return new ResponseEntity<>(members, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("회원 목록을 가져오는 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 게시글 수 조회
    @GetMapping("/qboards/count")
    public ResponseEntity<?> getTotalQboardCount() {
        try {
            long count = adminService.getTotalQboardCount(); // 게시글 수 조회 메서드 호출
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Q&A 게시글 수 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 게시글 목록 조회
    @GetMapping("/qboards")
    public ResponseEntity<?> getAllQboards() {
        try {
            List<QboardDTO> qboards = adminService.getAllAdminQboards(); // 게시글 목록 조회 메서드 호출
            return new ResponseEntity<>(qboards, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("게시판 목록을 가져오는 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 상품 목록 조회
    @GetMapping("/products")
    public ResponseEntity<?> getAllProducts() {
        try {
            log.info("상품 목록 조회 메서드 호출");
            List<ProductDTO> products = adminService.getAllProductDTOs(); // 상품 목록 조회 메서드 호출
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("상품 목록을 가져오는 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 관리자 댓글 수 조회
    @GetMapping("/comments/admin/count")
    public ResponseEntity<?> getAdminCommentCount() {
        try {
            long count = adminService.getAdminCommentCount(); // 관리자 댓글 수 조회 메서드 호출
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("관리자 댓글 수 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 회원 삭제
    @DeleteMapping("/members/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable Long id) {
        try {
            adminService.deleteMember(id); // 회원 삭제 메서드 호출
            return new ResponseEntity<>("회원이 성공적으로 삭제되었습니다.", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("해당 회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("회원 삭제 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 게시글 삭제
    @DeleteMapping("/qboards/{id}")
    public ResponseEntity<?> deleteQboard(@PathVariable Long id) {
        try {
            adminService.deleteQboard(id); // 게시글 삭제 메서드 호출
            return new ResponseEntity<>("게시글이 성공적으로 삭제되었습니다.", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("해당 게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("게시글 삭제 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 상품 삭제
    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") Long id) {
        try {
            adminService.deleteProduct(id); // 상품 삭제 메서드 호출
            return new ResponseEntity<>("상품이 성공적으로 삭제되었습니다.", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("해당 상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("상품 삭제 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 상품 추가
    @PostMapping("/products")
    public ResponseEntity<?> addProduct(@RequestBody ProductDTO productDTO) {
        try {
            ProductDTO newProduct = adminService.addProduct(productDTO); // 상품 추가 메서드 호출
            return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("상품 추가 중 오류가 발생했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 상품 업데이트
    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        try {
            Product updatedProduct = adminService.updateProduct(id, productDTO, new InventoryDTO()); // 상품 업데이트 메서드 호출
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("해당 상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("상품 업데이트 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 전체 주문 목록 조회
    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders() {
        try {
            List<OrderSummaryDTO> orders = adminService.getAllOrdersWithDetails();
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (Exception e) {
            log.error("주문 목록을 가져오는 중 오류 발생", e);
            return new ResponseEntity<>("주문 목록을 가져오는 중 오류가 발생했습니다: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 주문 상태 업데이트
    @PutMapping("/orders/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable("id") Long id,
            @RequestBody Map<String, String> statusMap) {
        try {
            String newStatusString = statusMap.get("status");
            Orders.OrderStatus newStatus = Orders.OrderStatus.valueOf(newStatusString.toUpperCase());
            Orders updatedOrder = adminService.updateOrderStatus(id, newStatus); // 주문 상태 업데이트 메서드 호출
            return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("잘못된 주문 상태입니다.", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("주문 상태 업데이트 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 주문 취소
    @PutMapping("/orders/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        try {
            Orders cancelledOrder = adminService.cancelOrder(id); // 주문 취소 메서드 호출
            return new ResponseEntity<>(cancelledOrder, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("해당 주문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("주문 취소 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 카테고리별 매출 조회
    @GetMapping("/sales/category-percentage")
    public ResponseEntity<?> getCategorySalesPercentage() {
        try {
            Map<String, Double> percentages = adminService.getCategorySalesPercentage(); // 카테고리별 매출 조회 메서드 호출
            return new ResponseEntity<>(percentages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("카테고리별 판매율 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/sales/daily")
    public ResponseEntity<?> getDailySales(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<DailySalesReportDTO> salesData = adminService.getDailySalesReport(startDate, endDate); // 일별 매출 조회 메서드
                                                                                                        // 호출
            return new ResponseEntity<>(salesData, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("일별 매출 조회 중 오류가 발생했습니다: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/sales/monthly")
    public ResponseEntity<?> getMonthlySales(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<DailySalesReportDTO> salesData = adminService.getMonthlySalesReport(startDate, endDate); // 월별 매출 조회
                                                                                                          // 메서드 호출
            return new ResponseEntity<>(salesData, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("월별 매출 조회 중 오류가 발생했습니다: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
