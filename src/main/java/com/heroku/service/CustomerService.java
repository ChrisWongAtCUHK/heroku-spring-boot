package com.heroku.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.heroku.dto.CustomerResponse;
import com.heroku.exception.CustomerNotFoundException;
import com.heroku.model.Customer;
import com.heroku.repository.CustomerRepository;

@Service
public class CustomerService {
    private CustomerRepository repository;

    // 刪除 @Autowired，改用建構子
    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public CustomerResponse createCustomer(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        Customer customer = new Customer();
        customer.setName(name);
        Customer saved = repository.save(customer);

        // 轉換為 DTO 回傳
        return new CustomerResponse(saved.getId(), saved.getName());
    }

    public List<CustomerResponse> getCustomers(Optional<String> searchName) {
        List<CustomerResponse> customers = searchName
                .map(repository::getContainingCustomer)
                .orElseGet(() -> repository.findAll())
                .stream()
                .map(c -> new CustomerResponse(c.getId(), c.getName()))
                .toList();

        return customers;
    }

    public CustomerResponse readCustomer(Long id) {
        Optional<Customer> customer = repository.findById(id);
        return customer.map(c -> new CustomerResponse(c.getId(), c.getName()))
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    public CustomerResponse updateCustomer(Long id, String name) {
        // 之前可能是 if (customer.isEmpty()) throw new IllegalArgumentException(...)
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id)); // 改成拋出自定義異常

        customer.setName(name);
        Customer updated = repository.save(customer);
        return new CustomerResponse(updated.getId(), updated.getName());
    }

    public void deleteCustomer(Long id) {
        // 1. 嘗試找出該客戶，找不到直接噴 404 (CustomerNotFoundException)
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        // 2. 執行刪除
        repository.delete(customer); // 或者 repository.deleteById(id);
    }
}