package com.choice.moduletest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.choice.auth.entity.Member;
import com.choice.auth.repository.MemberRepository;
import com.choice.shopping.dto.OrderDetailDTO;
import com.choice.shopping.dto.OrderItemDTO;
import com.choice.shopping.dto.OrderListResponseDTO;
import com.choice.shopping.dto.OrderRequsetDTO;
import com.choice.shopping.dto.OrderResponseDTO;
import com.choice.shopping.dto.PaymentsRequestDTO;
import com.choice.shopping.service.CartService;
import com.choice.shopping.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.ArrayList;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private CartService cartService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "testuser")
    public void testGetOrderList() throws Exception {
        Member mockMember = new Member();
        mockMember.setUserId(1L);
        when(memberRepository.findByUsername("testuser")).thenReturn(java.util.Optional.of(mockMember));

        List<OrderListResponseDTO> mockOrderList = new ArrayList<>();
        when(orderService.getOrderListForMember(1L)).thenReturn(mockOrderList);

        mockMvc.perform(get("/api/orders/my-orders"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockOrderList)));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testGetOrderDetail() throws Exception {
        OrderDetailDTO mockOrderDetail = new OrderDetailDTO();
        when(orderService.getOrderDetail(1L)).thenReturn(mockOrderDetail);

        mockMvc.perform(get("/api/orders/my-orders/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockOrderDetail)));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testCreateOrder() throws Exception {
        Member mockMember = new Member();
        mockMember.setUserId(1L);
        when(memberRepository.findByUsername("testuser")).thenReturn(java.util.Optional.of(mockMember));

        OrderRequsetDTO orderRequest = new OrderRequsetDTO();
        OrderResponseDTO orderResponse = new OrderResponseDTO();
        when(orderService.createOrder(any(), any(), any())).thenReturn(orderResponse);

        mockMvc.perform(post("/api/orders/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(orderResponse)));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testCompleteOrder() throws Exception {
        PaymentsRequestDTO paymentsRequest = new PaymentsRequestDTO();
        paymentsRequest.setUserId(1L);
        paymentsRequest.setOrderItems(List.of(new OrderItemDTO()));

        mockMvc.perform(post("/api/orders/done/imp_123456789")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentsRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("결제 완료"));
    }
}