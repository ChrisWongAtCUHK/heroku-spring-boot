package com.heroku;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@SpringBootApplication
@EntityScan(basePackages = {"com.heroku.model"}) 
@EnableJpaRepositories(basePackages = {"com.heroku.repository"})
@Controller
public class MainApplication {
    @GetMapping("/")
    public String index(Map<String, Object> model) {
        model.put("message", "This is a test");
        return "index";
    }

    @GetMapping("/hello")
    public String hello(Map<String, Object> model, String input) {
        // http://localhost:5000/hello?input=testspringboot
        try {
            if(input == null) {
                input = "N/A";
            }
            model.put("message", input);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return "index";
    }

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
