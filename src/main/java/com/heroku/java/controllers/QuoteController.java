package com.heroku.java.controllers;

import com.heroku.java.models.Quote;
import com.heroku.java.repositories.QuoteRepository;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
public class QuoteController {
    // constructor
    public QuoteController(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
    }

    private final QuoteRepository quoteRepository;

    @GetMapping("/quotes")
    public List<Quote> getQuotes(@RequestParam("search") Optional<String> searchParam, Optional<String> sortBy, Optional<String> sortDirection) {
        // Pass the entity property name (not the DB column name)
        Sort sort = Sort.by(sortBy.orElse("id"));
        // by default, ascending order
        sort = sortDirection.orElse("desc").equals("desc") ? sort.descending() : sort.ascending() ;
        List<Quote> quotes = quoteRepository.getContainingQuote(searchParam.orElse("%"), sort);
        
        return quotes;
    }

    @GetMapping("/quotes/{quoteId}")
    public ResponseEntity<String> readQuote(@PathVariable("quoteId") Long id) {
        return ResponseEntity.of(quoteRepository.findById(id).map(Quote::getQuote));
    }

    @PostMapping("/quotes")
    public Quote addQuote(@RequestBody String quote) {
        Quote q = new Quote();
        q.setQuote(quote);
        return quoteRepository.save(q);
    }

    @PostMapping("/quotes/{quoteId}")
    public Quote updateQuote(@PathVariable("quoteId") Long id, @RequestBody String quote) {
        Quote q = quoteRepository.getReferenceById(id);
        q.setQuote(quote);
        return quoteRepository.save(q);
    }

    @RequestMapping(value = "/quotes/{quoteId}", method = RequestMethod.DELETE)
    public void deleteQuote(@PathVariable(value = "quoteId") Long id) {
        quoteRepository.deleteById(id);
    }

    @GetMapping(value = "/sms")
    @ResponseBody
    public String sendSMS() {
        long min = 6;
        long max = 12;
        long random = (long) (Math.random() * (max - min + 1) + min);

        Quote quote = quoteRepository.findById(random).get();
        String returnString = quote.getQuote();

        return returnString;
    }
}
