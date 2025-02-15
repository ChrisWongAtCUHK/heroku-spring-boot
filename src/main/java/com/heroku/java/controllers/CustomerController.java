package com.heroku.java.controllers;

import com.heroku.java.models.Customer;
import com.heroku.java.repositories.CustomerRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin
@RestController
public class CustomerController {
    // constructor
    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    private final CustomerRepository customerRepository;

    @RequestMapping(value = "/api/customers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Customer> getCustomers(@RequestParam("search") Optional<String> searchParam) {
        List<Customer> customers = searchParam
                .map(customerRepository::getContainingCustomer)
                .orElse(customerRepository.findAll());

        return customers;
    }

    @GetMapping("/api/customers/{id}")
    public ResponseEntity<Object> readCustomer(@PathVariable("id") Long id) {
        Customer customer = customerRepository.getReferenceById(id);
        Map<String, String> data = new HashMap<>();
        data.put("id", id.toString());
        data.put("name", customer.getName());

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @PostMapping("/api/customers")
    public Customer addCustomer(@RequestBody String name) {
        Customer customer = new Customer();
        customer.setName(name);
        return customerRepository.save(customer);
    }

    @PutMapping("/api/customers/{id}")
    public Customer updateCustomer(@PathVariable("id") Long id, @RequestBody String name) {
        Customer customer = customerRepository.getReferenceById(id);
        customer.setName(name);
        return customerRepository.save(customer);
    }

    @RequestMapping(value = "/api/customers/{id}", method = RequestMethod.DELETE)
    public void deleteCustomer(@PathVariable(value = "id") Long id) {
        customerRepository.deleteById(id);
    }
}
