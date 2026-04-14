package com.heroku.controller;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.heroku.dto.CustomerResponse;
import com.heroku.repository.CustomerRepository;
import com.heroku.service.CustomerService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

  @Autowired
  private MockMvc mockMvc; // 這是 Web 測試的核心，用來模擬 HTTP 請求

  @MockitoBean
  private CustomerService customerService; // 必須用 @MockitoBean，Spring 才會把它塞進 Controller

  @MockitoBean
  private CustomerRepository customerRepository; // 如果 Controller 沒用到它，甚至可以不寫這行

  @Test
  void createCustomer_ShouldReturnJSON() throws Exception {
    // 1. Arrange: 準備 DTO 回傳值 (因為 Service 被 Mock 了，它現在回傳 DTO)
    CustomerResponse mockResponse = new CustomerResponse(1L, "Allen");

    // 注意：這裡 Mock 的是對象是 customerService，不是 repository
    when(customerService.createCustomer("Allen")).thenReturn(mockResponse);

    // 2. Act & Assert: 使用 mockMvc 發送請求並檢查 JSON
    mockMvc.perform(post("/api/customers")
        .contentType(MediaType.TEXT_PLAIN)
        .content("Allen"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Allen"));
  }
}