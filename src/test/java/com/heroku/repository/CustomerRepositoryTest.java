package com.heroku.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

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
    System.out.println("成功在 H2 中找到 ID: " + found.get().getId());
  }
}