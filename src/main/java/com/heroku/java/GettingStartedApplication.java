package com.heroku.java;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.heroku.java.services.storage.StorageProperties;
import com.heroku.java.services.storage.StorageService;

import java.util.Map;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"com.heroku.java.repositories"})
@Controller
@EnableConfigurationProperties(StorageProperties.class)
public class GettingStartedApplication {
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
        SpringApplication.run(GettingStartedApplication.class, args);
    }

    @Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.init();
		};
	}
}
