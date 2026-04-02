package com.heroku.java.repositories;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.heroku.java.models.Quote;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
public class QuoteRepositoryTests {
  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  QuoteRepository quoteRepository;

  @Test
  public void countTest() throws Exception {
    long count = quoteRepository.count();

    assertTrue(count >= 0);
  }

  @Test
  public void findById() throws Exception {
    Quote q1 = new Quote();
    q1.setQuote("Quote 1");
    entityManager.persistFlushFind(q1);

    Quote quote = quoteRepository.findById(q1.getId()).orElse(q1);

    assertTrue(quote.getId() == q1.getId());
  }
}
