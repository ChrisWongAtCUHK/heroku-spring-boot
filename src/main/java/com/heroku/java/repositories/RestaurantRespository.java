package com.heroku.java.repositories;

import org.springframework.stereotype.Repository;

import com.heroku.java.models.Restaurant;
import com.redis.om.spring.repository.RedisDocumentRepository;

@Repository
public interface RestaurantRespository extends RedisDocumentRepository<Restaurant, String> {
}
