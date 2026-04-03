package com.heroku.java.models;

import org.springframework.data.redis.core.index.Indexed;

import com.redis.om.spring.annotations.Document;

@Document(value = "sample_restaurant", indexName = "idx:smpl_restaurant")
public class Restaurant {
  @Indexed
  private String name;
  private String cuisine;
  private String location;

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getCuisine() {
    return cuisine;
  }
  public void setCuisine(String cuisine) {
    this.cuisine = cuisine;
  }
  public String getLocation() {
    return location;
  }
  public void setLocation(String location) {
    this.location = location;
  }
  
}
