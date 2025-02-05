package com.heroku.java.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class ApiController {
    @GetMapping("/api/time")
    public String time() {
        return "Hello, the time at the server is now " + new Date() + "\n";
    }
}