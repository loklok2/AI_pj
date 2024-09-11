package com.choice.admin.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import com.choice.board.entity.Qboard;
import com.choice.product.entity.Product;
import com.choice.shopping.entity.Orders;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/members")
    public ResponseEntity<List<Member>> getAllMembers() {
        return ResponseEntity.ok(adminService.getAllMembers());
    }

    @GetMapping("/qboards")
    public ResponseEntity<List<Qboard>> getAllQboards() {
        return ResponseEntity.ok(adminService.getAllQboards());
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(adminService.getAllProducts());
    }

    @DeleteMapping("/members/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable Long id) {
        adminService.deleteMember(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/qboards/{id}")
    public ResponseEntity<?> deleteQboard(@PathVariable Long id) {
        adminService.deleteQboard(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        adminService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderSummaryDTO>> getAllOrders() {
        return ResponseEntity.ok(adminService.getAllOrders());
    }

    @GetMapping("/sales-report")
    public ResponseEntity<DailySalesReportDTO> getDailySalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(adminService.getDailySalesReport(date));
    }

    @GetMapping("/daily-sales-range")
    public ResponseEntity<List<DailySalesReportDTO>> getDailySalesReportRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(adminService.getDailySalesReportRange(startDate, endDate));
    }

    @GetMapping("/monthly-sales")
    public ResponseEntity<List<DailySalesReportDTO>> getMonthlySalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(adminService.getMonthlySalesReport(startDate, endDate));
    }

    @PostMapping("/products")
    public ResponseEntity<Product> addProduct(@RequestBody ProductDTO productDTO) {
        Product newProduct = adminService.addProduct(productDTO, new InventoryDTO());
        return ResponseEntity.ok(newProduct);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        Product updatedProduct = adminService.updateProduct(id, productDTO, new InventoryDTO());
        return ResponseEntity.ok(updatedProduct);
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<Orders> updateOrderStatus(@PathVariable Long id, @RequestBody Orders.OrderStatus newStatus) {
        Orders updatedOrder = adminService.updateOrderStatus(id, newStatus);
        return ResponseEntity.ok(updatedOrder);
    }

    @PutMapping("/orders/{id}/cancel")
    public ResponseEntity<Orders> cancelOrder(@PathVariable Long id) {
        Orders cancelledOrder = adminService.cancelOrder(id);
        return ResponseEntity.ok(cancelledOrder);
    }
}