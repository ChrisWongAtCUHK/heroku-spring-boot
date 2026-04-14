package com.heroku.service;

import java.util.List;
import java.util.Optional;

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
                .orElse(repository.findAll())
                .stream()
                .map(c -> new CustomerResponse(c.getId(), c.getName()))
                .toList();

        return customers;
    }

    public CustomerResponse readCustomer(Long id) {
        Optional<Customer> customerOpt = repository.findById(id);
        return customerOpt.map(c -> new CustomerResponse(c.getId(), c.getName())).orElse(null);
    }

    public CustomerResponse updateCustomer(Long id, String name) {
        Optional<Customer> customerOpt = repository.findById(id);
        if (customerOpt.isEmpty()) {
            throw new IllegalArgumentException("Customer not found");
        }

        Customer customer = customerOpt.get();
        customer.setName(name);
        Customer updated = repository.save(customer);

        return new CustomerResponse(updated.getId(), updated.getName());
    }

    public void deleteCustomer(Long id) {
        repository.deleteById(id);
    }
}