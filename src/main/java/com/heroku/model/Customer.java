package com.heroku.model;

import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;

    // 必須保留一個無參數建構子 (JPA 規範)
    public Customer() {
    }

    // 加入這個全參數建構子供測試使用
    public Customer(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // not necessary but can keep this func and try breaking the program
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
