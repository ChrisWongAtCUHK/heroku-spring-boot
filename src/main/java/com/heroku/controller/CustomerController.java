package com.heroku.controller;

import com.heroku.dto.CustomerResponse;
import com.heroku.service.CustomerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
public class CustomerController {
    @Autowired
    private final CustomerService service;

    // constructor
    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @RequestMapping(value = "/api/customers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CustomerResponse>> getCustomers(@RequestParam("name") Optional<String> searchName) {
        List<CustomerResponse> response = service.getCustomers(searchName);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/customers/{id}")
    public ResponseEntity<CustomerResponse> readCustomer(@PathVariable("id") Long id) {
        CustomerResponse response = service.readCustomer(id);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/customers")
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody String name) {
        return ResponseEntity.ok(service.createCustomer(name));
    }

    @PutMapping("/api/customers/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable("id") Long id, @RequestBody String name) {
        CustomerResponse response = service.updateCustomer(id, name);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/api/customers/{id}", method = RequestMethod.DELETE)
    public void deleteCustomer(@PathVariable(value = "id") Long id) {
        service.deleteCustomer(id);
    }
}
