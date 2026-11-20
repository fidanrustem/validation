package com.example.validation;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
public class BookController {
    private static final Logger logger = LoggerFactory.getLogger(BookController.class);
    @Autowired
    private BookRepository bookRepository;

    private final BookValidator bookValidator = new BookValidator();

    @PostMapping("/addBook")
    public String addBook(@Valid @RequestBody Book book,
                          BindingResult result) {

        logger.info("Received request to add a new book");

        bookValidator.validate(book, result);

        if (result.hasErrors()) {
            logger.warn("Book validation failed: {}",
                    result.getAllErrors());

            return "Validation failed: " + result.getAllErrors();
        }

        bookRepository.save(book);

        logger.info("Book added successfully: {}", book.getTitle());

        return "Book added successfully";
    }
}

