package com.heroku.controller;

import com.heroku.dto.CustomerResponse;
import com.heroku.model.Customer;
import com.heroku.repository.CustomerRepository;
import com.heroku.service.CustomerService;

import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;

    // constructor
    public CustomerController(CustomerService customerService, CustomerRepository customerRepository) {
        this.customerService = customerService;
        this.customerRepository = customerRepository;
    }


    @RequestMapping(value = "/api/customers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Customer> getCustomers(@RequestParam("name") Optional<String> searchName) {
        List<Customer> customers = searchName
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
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody String name) {
        return ResponseEntity.ok(customerService.createCustomer(name));
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
