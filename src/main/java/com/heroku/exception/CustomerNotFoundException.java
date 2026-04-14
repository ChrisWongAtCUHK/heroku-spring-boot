package com.heroku.exception;

// 繼承 RuntimeException，這樣就不需要在方法上強制寫 throws
public class CustomerNotFoundException extends RuntimeException {
  public CustomerNotFoundException(Long id) {
    super("找不到 ID 為 " + id + " 的客戶");
  }
}