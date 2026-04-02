package com.heroku.java.repositories;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@DataJpaTest
public class QuoteRepositoryTests {
  @Autowired
  QuoteRepository quoteRepository;

  @Test
  public void test() throws Exception {
    long count = quoteRepository.count();

    assertTrue(count >= 0);
  }
}
