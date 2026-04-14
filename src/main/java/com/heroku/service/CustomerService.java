package com.heroku.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.heroku.dto.CustomerResponse;
import com.heroku.model.Customer;
import com.heroku.repository.CustomerRepository;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository repository;

    public CustomerResponse createCustomer(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        Customer customer = new Customer();
        customer.setName(name);
        Customer saved = repository.save(customer);

        // 轉換為 DTO 回傳
        return new CustomerResponse(saved.getId(), saved.getName());
    }
}