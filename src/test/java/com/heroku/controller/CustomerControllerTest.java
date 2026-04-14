package com.heroku.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.heroku.advice.GlobalExceptionHandler;
import com.heroku.dto.CustomerResponse;
import com.heroku.repository.CustomerRepository;
import com.heroku.service.CustomerService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({ CustomerController.class, GlobalExceptionHandler.class }) // 加上這行
class CustomerControllerTest {
  @Autowired
  private MockMvc mockMvc; // 這是 Web 測試的核心，用來模擬 HTTP 請求

  @MockitoBean
  private CustomerService customerService; // 必須用 @MockitoBean，Spring 才會把它塞進 Controller

  @MockitoBean
  private CustomerRepository customerRepository; // 如果 Controller 沒用到它，甚至可以不寫這行

  @Test
  void createCustomer_ShouldReturnJSON() throws Exception {
    final long customerId = 1L;
    final String customerName = "Allen";

    // 1. Arrange: 準備 DTO 回傳值 (因為 Service 被 Mock 了，它現在回傳 DTO)
    CustomerResponse mockResponse = new CustomerResponse(customerId, customerName);

    // 注意：這裡 Mock 的是對象是 customerService，不是 repository
    when(customerService.createCustomer(customerName)).thenReturn(mockResponse);

    // 2. Act & Assert: 使用 mockMvc 發送請求並檢查 JSON
    mockMvc.perform(post("/api/customers")
        .contentType(MediaType.TEXT_PLAIN)
        .content(customerName))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(customerId))
        .andExpect(jsonPath("$.name").value(customerName));
  }

  @Test
  void createCustomer_ShouldReturn400_WhenNameIsEmpty() throws Exception {
    // Arrange
    when(customerService.createCustomer("")).thenThrow(new IllegalArgumentException("Name cannot be empty"));

    // Act & Assert
    mockMvc.perform(post("/api/customers")
        .contentType(MediaType.TEXT_PLAIN)
        .content("")) // 完全沒內容
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("請求主體不能為空")); // 完全沒有 Body
  }

  @Test
  void createCustomer_ShouldReturn400_WhenNameIsBlank() throws Exception {
    // Arrange
    when(customerService.createCustomer(" ")).thenThrow(new IllegalArgumentException("Name cannot be empty"));

    mockMvc.perform(post("/api/customers")
        .contentType(MediaType.TEXT_PLAIN)
        .content(" ")) // 現在 isBlank() 會抓到這個空格了
        .andExpect(status().isBadRequest()) // 現在會拿到 400 了
        .andExpect(jsonPath("$.message").value("Name cannot be empty"));
  }
}