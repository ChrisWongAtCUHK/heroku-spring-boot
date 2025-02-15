package com.heroku.java.controllers;

import com.heroku.java.models.Customer;
import com.heroku.java.repositories.CustomerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
public class CustomerController {
    // constructor
    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    private final CustomerRepository customerRepository;

    @GetMapping("/api/customers")
    public List<Customer> getCustomers(@RequestParam("search") Optional<String> searchParam) {
        return searchParam
                .map(customerRepository::getContainingCustomer)
                .orElse(customerRepository.findAll());
    }

    @GetMapping("/api/customers/{id}")
    public ResponseEntity<String> readCustomer(@PathVariable("id") Long id) {
        return ResponseEntity.of(customerRepository.findById(id).map(Customer::getCustomer));
    }

    @PostMapping("/api/customers")
    public Customer addCustomer(@RequestBody String name) {
        Customer customer = new Customer();
        customer.setCustomer(name);
        return customerRepository.save(customer);
    }

    @PutMapping("/api/customers/{id}")
    public Customer updateCustomer(@PathVariable("id") Long id, @RequestBody String name) {
        Customer customer = customerRepository.getReferenceById(id);
        customer.setCustomer(name);
        return customerRepository.save(customer);
    }

    @RequestMapping(value = "/api/customers/{id}", method = RequestMethod.DELETE)
    public void deleteCustomer(@PathVariable(value = "id") Long id) {
        customerRepository.deleteById(id);
    }
}
