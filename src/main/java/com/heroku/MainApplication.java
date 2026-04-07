package com.heroku;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@SpringBootApplication
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
            if (input == null) {
                input = "N/A";
            }
            model.put("message", input);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "index";
    }

    @GetMapping("/view-pdf")
    public ResponseEntity<Resource> viewPdf() {
        Resource pdf = new ClassPathResource("static/sample.pdf");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"sample.pdf\"")
                .body(pdf);
    }

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
