package com.choice.moduletest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.choice.admin.dto.ProductDTO;
import com.choice.admin.service.AdminService;
import com.choice.auth.entity.Member;
import com.choice.board.dto.QboardDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AdminService adminService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetTotalMemberCount() throws Exception {
        when(adminService.getTotalMemberCount()).thenReturn(100L);

        mockMvc.perform(get("/api/admin/members/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("100"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetAllMembers() throws Exception {
        List<Member> mockMembers = new ArrayList<>();
        when(adminService.getAllMembers()).thenReturn(mockMembers);

        mockMvc.perform(get("/api/admin/members"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockMembers)));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetTotalQboardCount() throws Exception {
        when(adminService.getTotalQboardCount()).thenReturn(50L);

        mockMvc.perform(get("/api/admin/qboards/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("50"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetAllQboards() throws Exception {
        List<QboardDTO> mockQboards = new ArrayList<>();
        when(adminService.getAllAdminQboards()).thenReturn(mockQboards);

        mockMvc.perform(get("/api/admin/qboards"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockQboards)));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetAllProducts() throws Exception {
        List<ProductDTO> mockProducts = new ArrayList<>();
        when(adminService.getAllProductDTOs()).thenReturn(mockProducts);

        mockMvc.perform(get("/api/admin/products"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockProducts)));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetAdminCommentCount() throws Exception {
        when(adminService.getAdminCommentCount()).thenReturn(30L);

        mockMvc.perform(get("/api/admin/comments/admin/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("30"));
    }
}