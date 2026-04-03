package com.heroku.java.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmojiController {
    @GetMapping("/emoji")
    public String emoji() {
        return "😀";
    }
}