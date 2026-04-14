package com.heroku.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.heroku.model.Customer;
import com.heroku.repository.CustomerRepository;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository repository;

    public Customer createCustomer(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        Customer customer = new Customer();
        customer.setName(name);
        return repository.save(customer); // 這裡會回傳帶有 ID 的物件
    }
}