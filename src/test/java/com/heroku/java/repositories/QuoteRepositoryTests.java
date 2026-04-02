package com.heroku.java.repositories;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DataJpaTest
public class QuoteRepositoryTests {
  @Autowired
  QuoteRepository quoteRepository;

  @Test
  public void test() throws Exception {
    // long count = quoteRepository.count();
  }
}
