package com.heroku.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.heroku.model.Customer;
import com.heroku.repository.CustomerRepository;

@ExtendWith(MockitoExtension.class) // 使用 Mockito 擴展
class CustomerServiceTest {
    @Mock
    private CustomerRepository customerRepository; // 模擬數據庫訪問

    @InjectMocks
    private CustomerService customerService; // 將 Mock 注入 Service

    @Test
    @DisplayName("新增客戶：當輸入正確姓名時，應成功儲存並回傳物件")
    void createCustomer_ShouldReturnSavedCustomer() {
        // 1. Arrange (準備資料與模擬行為)
        String inputName = "Allen";
        Customer savedCustomer = new Customer();
        savedCustomer.setId(1L); // 模擬數據庫產生的 ID
        savedCustomer.setName(inputName);

        // 當調用 save 時，不論傳入什麼 Customer 物件，都回傳我們準備好的 savedCustomer
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        // 2. Act (執行受測方法)
        Customer result = customerService.createCustomer(inputName);

        // 3. Assert (斷言結果)
        assertNotNull(result.getId(), "ID 不應為空");
        assertEquals("Allen", result.getName());
        
        // 驗證 repository.save() 確曾被調用過一次
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("新增客戶：當姓名為空時，應拋出異常")
    void createCustomer_ShouldThrowException_WhenNameIsEmpty() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            customerService.createCustomer("");
        });
        
        // 驗證數據庫 save 方法「從未」被調用（保證邏輯正確截斷）
        verify(customerRepository, never()).save(any());
    }
}

