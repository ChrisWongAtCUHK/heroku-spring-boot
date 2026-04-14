package com.heroku.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.heroku.dto.CustomerResponse;
import com.heroku.model.Customer;
import com.heroku.repository.CustomerRepository;

@ExtendWith(MockitoExtension.class) // 使用 Mockito 擴展
class CustomerServiceTest {
    @Mock
    private CustomerRepository repository; // 模擬數據庫訪問

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
        when(repository.save(any(Customer.class))).thenReturn(savedCustomer);

        // 2. Act (執行受測方法)
        CustomerResponse result = customerService.createCustomer(inputName);

        // 3. Assert (斷言結果)
        assertNotNull(result.id(), "ID 不應為空");
        assertEquals("Allen", result.name());

        // 驗證 repository.save() 確曾被調用過一次
        verify(repository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("新增客戶：當姓名為空時，應拋出異常")
    void createCustomer_ShouldThrowException_WhenNameIsEmpty() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            customerService.createCustomer("");
        });

        // 驗證數據庫 save 方法「從未」被調用（保證邏輯正確截斷）
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("獲取客戶：當搜尋姓名為空時，應回傳所有客戶")
    void getCustomers() {
        // 1. Arrange (準備資料與模擬行為)
        // 假設有全參數建構子
        List<Customer> mockCustomers = List.of(
                new Customer(1L, "Allen"),
                new Customer(2L, "Bob"));
        when(repository.findAll()).thenReturn(mockCustomers);

        // 2. Act (執行受測方法)
        List<CustomerResponse> result = customerService.getCustomers(java.util.Optional.empty());

        // 3. Assert (斷言結果)
        assertEquals(2, result.size());
        assertEquals("Allen", result.get(0).name());
        assertEquals("Bob", result.get(1).name());

        // 驗證 repository.findAll() 確曾被調用過一次
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("獲取客戶：當提供搜尋姓名時，應調用搜尋方法")
    void getCustomers_WithSearchName() {
        // Arrange
        String searchName = "Allen";
        Customer customer = new Customer(1L, "Allen");
        // 假設你的 repository 有這個自定義方法
        when(repository.getContainingCustomer(searchName)).thenReturn(List.of(customer));

        // Act
        List<CustomerResponse> result = customerService.getCustomers(Optional.of(searchName));

        // Assert
        assertEquals(1, result.size());
        assertEquals("Allen", result.get(0).name());
        // 驗證是調用搜尋方法而不是 findAll
        verify(repository, times(1)).getContainingCustomer(searchName);
        verify(repository, never()).findAll();
    }

    @Test
    @DisplayName("讀取客戶：當 ID 存在時，應回傳對應客戶資料")
    void readCustomer_ShouldReturnCustomer_WhenIdExists() {
        // Arrange
        Long customerId = 1L;
        Customer customer = new Customer(customerId, "Allen");
        when(repository.findById(customerId)).thenReturn(Optional.of(customer));

        // Act
        CustomerResponse result = customerService.readCustomer(customerId);
        // Assert
        assertNotNull(result);
        assertEquals(customerId, result.id());
        assertEquals("Allen", result.name());
        verify(repository, times(1)).findById(customerId);
    }

    @Test
    @DisplayName("讀取客戶：當 ID 不存在時，應回傳 null (或拋出異常)")
    void readCustomer_ShouldReturnNull_WhenIdDoesNotExist() {
        // Arrange
        Long customerId = 99L;
        // 模擬資料庫找不到資料
        when(repository.findById(customerId)).thenReturn(Optional.empty());

        // Act
        CustomerResponse result = customerService.readCustomer(customerId);

        // Assert
        assertNull(result);
        verify(repository, times(1)).findById(customerId);
    }

    @Test
    @DisplayName("更新客戶：當 ID 存在時，應更新並回傳新的客戶資料")
    void updateCustomer_ShouldReturnUpdatedCustomer_WhenIdExists() {
        // Arrange
        Long customerId = 1L;
        String newName = "Allen Updated";
        Customer existingCustomer = new Customer(customerId, "Allen");
        Customer updatedCustomer = new Customer(customerId, newName);

        when(repository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(repository.save(any(Customer.class))).thenReturn(updatedCustomer);

        // Act
        CustomerResponse result = customerService.updateCustomer(customerId, newName);
        // Assert
        assertNotNull(result);
        assertEquals(customerId, result.id());
        assertEquals(newName, result.name());

        // 驗證 findById 被呼叫
        verify(repository, times(1)).findById(customerId);

        // 驗證 save 被呼叫，且傳入的 Customer 物件名字已經改好了
        verify(repository)
                .save(argThat(customer -> customer.getName().equals(newName) && customer.getId().equals(customerId)));
    }

    @Test
    @DisplayName("更新客戶：當 ID 不存在時，應拋出 IllegalArgumentException")
    void updateCustomer_ShouldThrowException_WhenIdDoesNotExist() {
        // Arrange
        Long customerId = 99L;
        when(repository.findById(customerId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            customerService.updateCustomer(customerId, "Some Name");
        });

        assertEquals("Customer not found", exception.getMessage());
        // 驗證既然找不到人，就絕對不應該呼叫 save
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("刪除客戶：應調用 repository 的 deleteById 方法")
    void deleteCustomer_ShouldCallRepositoryDelete() {
        // Arrange
        Long customerId = 1L;
        // Act
        customerService.deleteCustomer(customerId);
        // Assert
        verify(repository, times(1)).deleteById(customerId);
    }
}
