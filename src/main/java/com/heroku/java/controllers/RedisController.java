package com.heroku.java.controllers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.heroku.java.models.User;

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

  /**
   * Save or update an user
   */
  @PostMapping("/redis/users/{key}")
  public User saveUser(@PathVariable("key") String key, @RequestBody User user) {
    // RedisConnection must be closed manually if obtained from the factory
    try (RedisConnection connection = redisTemplate.getConnectionFactory().getConnection()) {
      redisTemplate.opsForValue().set(key, user);

    } catch (Exception e) {
      throw new RuntimeException("Error scanning Redis keys", e);
    }

    return user;
  }

  @GetMapping("/redis/users/{key}")
  public User getUserByKey(@PathVariable("key") String key) {
    // RedisConnection must be closed manually if obtained from the factory
    try (RedisConnection connection = redisTemplate.getConnectionFactory().getConnection()) {
      Object obj = redisTemplate.opsForValue().get(key);
      User user = new User();
      if(obj == null) {
        // not found by key
        return user;
      }

      if(obj instanceof LinkedHashMap<?, ?>){
        LinkedHashMap<?, ?> linkedHashMap = (LinkedHashMap<?, ?>)obj;
        linkedHashMap.forEach((k, value) -> {
          switch((String)k) {
            case "name":
              user.setName((String)value);
              break;
            case "age":
              user.setAge((int)value);
              break;
            default:
              break;
          }
        });
      }

      return user;
    } catch (Exception e) {
      throw new RuntimeException("Error scanning Redis keys", e);
    }
  }

  @GetMapping("/redis/users")
  public Map<String, User> getUsers() {
    Map<String, User> users = new HashMap<>();
    ScanOptions scanOptions = ScanOptions.scanOptions().match("*").count(100).build();

    // RedisConnection must be closed manually if obtained from the factory
    try (RedisConnection connection = redisTemplate.getConnectionFactory().getConnection()) {
      // The method scan(ScanOptions) from the type DefaultedRedisConnection is
      // deprecated
      // Cursor<byte[]> cursor = connection.scan(scanOptions);
      Cursor<byte[]> cursor = connection.keyCommands().scan(scanOptions);
      while (cursor.hasNext()) {
        String key = new String(cursor.next());
        User user = (User) redisTemplate.opsForValue().get(key);

        users.put(key, user);
      }
    } catch (Exception e) {
      throw new RuntimeException("Error scanning Redis keys", e);
    }
    return users;
  }
}
