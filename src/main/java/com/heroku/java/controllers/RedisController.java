package com.heroku.java.controllers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisController {

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  @GetMapping("/redis/keys")
  public Set<String> getAllKeys() {
    // "*" is the wildcard pattern to match all keys
    return redisTemplate.keys("*");
  }

  @GetMapping("/redis/scanAllKeys")
  public Set<String> scanAllKeys() {
    Set<String> keys = new HashSet<>();
    ScanOptions scanOptions = ScanOptions.scanOptions().match("*").count(100).build();

    // RedisConnection must be closed manually if obtained from the factory
    try (RedisConnection connection = redisTemplate.getConnectionFactory().getConnection()) {
      // The method scan(ScanOptions) from the type DefaultedRedisConnection is
      // deprecated
      // Cursor<byte[]> cursor = connection.scan(scanOptions);
      Cursor<byte[]> cursor = connection.keyCommands().scan(scanOptions);
      while (cursor.hasNext()) {
        keys.add(new String(cursor.next()));
      }
    } catch (Exception e) {
      throw new RuntimeException("Error scanning Redis keys", e);
    }
    return keys;
  }

  @GetMapping("/redis/keyValues")
  public Map<String, String> keyValues() {
    Map<String, String> keyValues = new HashMap<>();
    ScanOptions scanOptions = ScanOptions.scanOptions().match("*").count(100).build();

    // RedisConnection must be closed manually if obtained from the factory
    try (RedisConnection connection = redisTemplate.getConnectionFactory().getConnection()) {
      // The method scan(ScanOptions) from the type DefaultedRedisConnection is
      // deprecated
      // Cursor<byte[]> cursor = connection.scan(scanOptions);
      Cursor<byte[]> cursor = connection.keyCommands().scan(scanOptions);
      while (cursor.hasNext()) {
        String key = new String(cursor.next());
        String value = redisTemplate.opsForValue().get(key).toString();
        keyValues.put(key, value);
      }
    } catch (Exception e) {
      throw new RuntimeException("Error scanning Redis keys", e);
    }
    return keyValues;
  }
}
