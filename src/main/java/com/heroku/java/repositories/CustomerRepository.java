package com.heroku.java.repositories;

import com.heroku.java.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c FROM Customer c WHERE c.name LIKE %?1%")
    List<Customer> getContainingCustomer(String word);     // dont need to define method bc in repository
}
