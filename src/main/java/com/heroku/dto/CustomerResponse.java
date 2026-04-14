package com.heroku.dto;

// record 會自動產生 constructor, getter, equals, hashCode, toString
public record CustomerResponse(Long id, String name) {
}