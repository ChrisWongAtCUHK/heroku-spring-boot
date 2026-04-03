package com.heroku.java.controllers.redis;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.heroku.java.models.Restaurant;
import com.heroku.java.repositories.RestaurantRespository;

@RestController
public class RestaurantController {

  @Autowired
  private RestaurantRespository restaurantRespository;

  @GetMapping("/redis/restaurants")
  public List<Restaurant> getAllRestaurants() {
    List<Restaurant> restaurants = new ArrayList<>();
    try{
      restaurants = restaurantRespository.findAll();
    } catch(Exception e) {
      Restaurant restaurant = new Restaurant();
      restaurant.setName(e.getMessage());
    }
    return restaurants;
  }
}
