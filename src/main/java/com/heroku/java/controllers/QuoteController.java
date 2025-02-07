package com.heroku.java.controllers;

import com.heroku.java.models.Quote;
import com.heroku.java.repositories.QuoteRepository;

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
    public List<Quote> getQuotes(@RequestParam("search") Optional<String> searchParam) {
        return searchParam
                .map(quoteRepository::getContainingQuote)
                .orElse(quoteRepository.findAll());
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
        Quote q = quoteRepository.getById(id);
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
