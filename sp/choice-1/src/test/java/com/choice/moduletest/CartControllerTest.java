package com.choice.moduletest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

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
import com.choice.shopping.dto.CartItemDTO;
import com.choice.shopping.dto.CartSummaryDTO;
import com.choice.shopping.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
// 카트 컨트롤러 테스트
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "testuser")
    // 카트 아이템 조회 테스트
    public void testGetCartItems() throws Exception {
        // 테스트 멤버 생성
        Member mockMember = new Member();
        // 테스트 멤버의 아이디 설정
        mockMember.setUserId(1L);
        // 테스트 멤버를 모의로 설정
        when(memberRepository.findByUsername("testuser")).thenReturn(java.util.Optional.of(mockMember));

        // 테스트 카트 요약 생성
        CartSummaryDTO mockCartSummary = new CartSummaryDTO(new ArrayList<>(), 0L);
        // 카트 요약 조회 요청을 모의로 설정
        when(cartService.getCartItemsUser(1L)).thenReturn(mockCartSummary);

        // 카트 요약 조회 요청을 테스트
        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockCartSummary)));
    }

    @Test
    @WithMockUser(username = "testuser")
    // 카트에 상품 추가 테스트
    public void testAddToCart() throws Exception {
        // 테스트 멤버 생성
        Member mockMember = new Member();
        // 테스트 멤버의 아이디 설정
        mockMember.setUserId(1L);
        // 테스트 멤버를 모의로 설정
        when(memberRepository.findByUsername("testuser")).thenReturn(java.util.Optional.of(mockMember));

        // 테스트 카트 아이템 생성
        CartItemDTO mockCartItem = new CartItemDTO();
        // 테스트 카트 아이템의 상품 아이디 설정
        mockCartItem.setProductId(1L);
        // 테스트 카트 아이템의 수량 설정
        mockCartItem.setQuantity(1);
        // 테스트 카트 아이템의 사이즈 설정
        mockCartItem.setSize("M");

        // 카트에 상품 추가 요청을 모의로 설정
        when(cartService.addToCart(any(), any(), any(), any())).thenReturn(mockCartItem);

        // 카트에 상품 추가 요청을 테스트
        mockMvc.perform(post("/api/cart/add")
                // 요청 본문에 테스트 카트 아이템 추가
                .contentType(MediaType.APPLICATION_JSON)
                // 요청 본문에 테스트 카트 아이템 추가
                .content(objectMapper.writeValueAsString(List.of(mockCartItem))))
                // 응답 상태가 200 OK인지 확인
                .andExpect(status().isOk())
                // 응답 본문에 테스트 카트 아이템 추가
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(mockCartItem))));
    }

    @Test
    @WithMockUser(username = "testuser")
    // 카트에서 상품 삭제 테스트
    public void testRemoveFromCart() throws Exception {
        // 테스트 멤버 생성
        Member mockMember = new Member();
        // 테스트 멤버의 아이디 설정
        mockMember.setUserId(1L);
        // 테스트 멤버를 모의로 설정
        when(memberRepository.findByUsername("testuser")).thenReturn(java.util.Optional.of(mockMember));

        // 카트에서 상품 삭제 요청을 테스트
        mockMvc.perform(delete("/api/cart")
                // 요청 본문에 테스트 카트 아이템 추가
                .param("productId", "1"))
                // 응답 상태가 200 OK인지 확인
                .andExpect(status().isOk())
                // 응답 본문에 테스트 카트 아이템 추가
                .andExpect(content().string("상품이 장바구니에서 삭제되었습니다."));
    }

    @Test
    @WithMockUser(username = "testuser")
    // 카트 아이템 업데이트 테스트
    public void testUpdateCartItem() throws Exception {
        // 테스트 멤버 생성
        Member mockMember = new Member();
        // 테스트 멤버의 아이디 설정
        mockMember.setUserId(1L);
        // 테스트 멤버를 모의로 설정
        when(memberRepository.findByUsername("testuser")).thenReturn(java.util.Optional.of(mockMember));

        // 테스트 카트 아이템 생성
        CartItemDTO mockCartItem = new CartItemDTO();
        // 테스트 카트 아이템의 상품 아이디 설정
        mockCartItem.setProductId(1L);
        // 테스트 카트 아이템의 수량 설정
        mockCartItem.setQuantity(2);
        // 테스트 카트 아이템의 사이즈 설정
        mockCartItem.setSize("L");

        // 카트 아이템 업데이트 요청을 모의로 설정
        when(cartService.updateCartItem(any(), any())).thenReturn(mockCartItem);

        // 카트 아이템 업데이트 요청을 테스트
        mockMvc.perform(put("/api/cart/update")
                // 요청 본문에 테스트 카트 아이템 추가
                .contentType(MediaType.APPLICATION_JSON)
                // 요청 본문에 테스트 카트 아이템 추가
                .content(objectMapper.writeValueAsString(List.of(mockCartItem))))
                // 응답 상태가 200 OK인지 확인
                .andExpect(status().isOk())
                // 응답 본문에 테스트 카트 아이템 추가
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(mockCartItem))));
    }
}