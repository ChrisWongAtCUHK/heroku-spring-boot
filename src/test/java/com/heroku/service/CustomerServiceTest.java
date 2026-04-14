package com.heroku.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.heroku.dto.CustomerResponse;
import com.heroku.exception.CustomerNotFoundException;
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
    @DisplayName("獲取客戶：當搜尋姓名為空時，應回傳分頁後的第一頁客戶")
    void getCustomers() {
        // 預期每頁 2 筆
        Pageable pageable = PageRequest.of(0, 2, Sort.by("name").ascending());

        // 1. Arrange
        // 💡 關鍵：這裡只放該分頁「實際上」會拿到的 2 筆資料
        List<Customer> customerList = List.of(
                new Customer(1L, "Apple"),
                new Customer(2L, "Apricot"));

        // PageImpl(當前頁資料, 分頁請求, 總筆數)
        Page<Customer> mockPage = new PageImpl<>(customerList, pageable, 4);

        when(repository.findAll(pageable)).thenReturn(mockPage);

        // 2. Act
        Page<CustomerResponse> result = customerService.getCustomers(Optional.empty(), pageable);

        // 3. Assert
        assertEquals(2, result.getContent().size(), "當前頁面應只有 2 筆");
        assertEquals(4, result.getTotalElements(), "總筆數應為 4 筆");
        assertEquals(2, result.getTotalPages(), "總頁數應為 2 頁 (4/2)");
    }

    @Test
    @DisplayName("獲取客戶：當提供搜尋姓名時，應調用搜尋方法")
    void getCustomers_WithSearchName() {
        // Arrange
        String searchName = "Allen";
        Customer customer = new Customer(1L, "Allen");
        // 假設你的 repository 有這個自定義方法
        when(repository.getContainingCustomer(searchName)).thenReturn(List.of(customer));

        Pageable pageable = PageRequest.of(0, 2, Sort.by("name").ascending());

        // Act
        Page<CustomerResponse> result = customerService.getCustomers(Optional.of(searchName), pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("Allen", result.getContent().get(0).name());
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
    @DisplayName("更新客戶：當 ID 不存在時，應拋出 CustomerNotFoundException")
    void updateCustomer_ShouldThrowException_WhenIdDoesNotExist() {
        // Arrange
        Long customerId = 99L;
        when(repository.findById(customerId)).thenReturn(Optional.empty());

        // Act & Assert
        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class, () -> {
            customerService.updateCustomer(customerId, "Some Name");
        });

        assertEquals("找不到 ID 為 " + customerId + " 的客戶", exception.getMessage());
        // 驗證既然找不到人，就絕對不應該呼叫 save
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("刪除客戶：應調用 repository 的 deleteById 方法")
    void deleteCustomer_ShouldCallRepositoryDelete() {
        Long customerId = 1L;
        Customer customer = new Customer(customerId, "Allen");

        // 這次 Service 呼叫的是 findById，所以這個 Mock 會成功匹配！
        when(repository.findById(customerId)).thenReturn(Optional.of(customer));

        customerService.deleteCustomer(customerId);

        verify(repository).delete(customer);
    }

    @Test
    @DisplayName("讀取客戶：當 ID 不存在時，應拋出 CustomerNotFoundException")
    void readCustomer_ShouldThrowNotFound_WhenIdDoesNotExist() {
        // Arrange
        Long customerId = 99L;
        when(repository.findById(customerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.readCustomer(customerId);
        });
    }

    @Test
    @DisplayName("更新客戶：當 ID 不存在時，應拋出 CustomerNotFoundException")
    void updateCustomer_ShouldThrowNotFound_WhenIdDoesNotExist() {
        // Arrange
        Long customerId = 99L;
        when(repository.findById(customerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.updateCustomer(customerId, "New Name");
        });

        verify(repository, never()).save(any());
    }
}
