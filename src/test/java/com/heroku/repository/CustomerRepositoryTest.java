package com.heroku.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.heroku.model.Customer;

@DataJpaTest // 專門用於 JPA 測試，預設會尋找 H2
class CustomerRepositoryTest {

  @Autowired
  private CustomerRepository customerRepository;

  @Test
  void shouldSaveAndFindCustomer() {
    // 1. Arrange
    Customer customer = new Customer();
    customer.setName("H2 User");

    // 2. Act (這次是真的寫入 H2 記憶體中)
    Customer saved = customerRepository.save(customer);

    // 3. Assert
    Optional<Customer> found = customerRepository.findById(saved.getId());

    assertTrue(found.isPresent());
    assertEquals("H2 User", found.get().getName());
  }

  @Test
  @DisplayName("模糊搜尋：應回傳名稱包含關鍵字的客戶（不分大小寫）")
  void findByNameContaining_ShouldReturnMatchingCustomers() {
    // Arrange
    customerRepository.save(new Customer(null, "Allen Wang"));
    customerRepository.save(new Customer(null, "Bob Alexander"));
    customerRepository.save(new Customer(null, "Charlie"));

    // Act
    List<Customer> result = customerRepository.findByNameContainingIgnoreCase("AL");

    // Assert
    // 預期會找到 "Allen" 和 "Alexander"
    assertEquals(2, result.size());
  }

  @Test
  @DisplayName("複合查詢：應根據 ID 範圍與姓名關鍵字過濾資料")
  void testComplexQuery() {
    // Arrange
    customerRepository.save(new Customer(null, "Allen"));
    customerRepository.save(new Customer(null, "Alex"));
    customerRepository.save(new Customer(null, "Bob"));

    // Act: 找 ID > 0 且名字包含 "Al" 的人
    List<Customer> result = customerRepository.findByIdGreaterThanAndNameContaining(0L, "Al");

    // Assert
    assertEquals(2, result.size()); // 應該只有 Allen 和 Alex
  }

  @Test
  @DisplayName("分頁測試：應回傳指定頁面的資料並按名稱排序")
  void findAll_WithPagination_ShouldReturnSortedPage() {
    // Arrange: 存入多筆資料
    customerRepository.save(new Customer(null, "C"));
    customerRepository.save(new Customer(null, "A"));
    customerRepository.save(new Customer(null, "B"));

    // Act: 請求第 0 頁，每頁 2 筆，按名稱升序排列
    PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("name").ascending());
    Page<Customer> page = customerRepository.findAll(pageRequest);

    // Assert
    assertEquals(2, page.getContent().size());
    assertEquals("A", page.getContent().get(0).getName());
    assertEquals("B", page.getContent().get(1).getName());
  }
}