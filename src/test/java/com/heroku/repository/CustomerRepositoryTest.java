package com.heroku.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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
}