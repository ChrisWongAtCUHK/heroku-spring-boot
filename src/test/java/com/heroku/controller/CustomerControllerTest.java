package com.heroku.controller;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.heroku.model.Customer;
import com.heroku.repository.CustomerRepository;
import com.heroku.service.CustomerService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class) // 只啟動 Web 層，不啟動資料庫
class CustomerControllerTest {

  @Autowired
  private MockMvc mockMvc; // 模擬發送 HTTP 請求的工具

  @MockitoBean
  private CustomerService customerService; // 模擬 Controller 依賴的 Service

  // 如果 Controller 有用到 Repository，這裡也要 Mock，否則 Spring 啟動時會去尋找資料庫配置
  @MockitoBean
  private CustomerRepository customerRepository;

  @Test
  void shouldReturnCustomerWhenPost() throws Exception {
    // 1. Arrange: 準備模擬資料
    Customer mockCustomer = new Customer();
    mockCustomer.setId(1L);
    mockCustomer.setName("Allen");

    // 確保這裡的參數與 Controller 調用的參數一致
    when(customerService.createCustomer("Allen")).thenReturn(mockCustomer);

    // 2. Act & Assert: 發送 POST 請求並驗證結果
    mockMvc.perform(post("/api/customers")
        .contentType(MediaType.TEXT_PLAIN) // 這裡要對應 @RequestBody String
        .content("Allen")) // 直接傳送字串內容
        .andExpect(status().isOk()) // 驗證 HTTP 200
        .andExpect(jsonPath("$.id").value(1)) // 驗證 JSON 內容
        .andExpect(jsonPath("$.name").value("Allen"));
  }
}
