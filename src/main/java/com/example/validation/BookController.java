
package com.example.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
public class BookController {

    private static final Logger logger =
            LoggerFactory.getLogger(BookController.class);

    @Autowired
    private BookRepository bookRepository;

    private final BookValidator bookValidator = new BookValidator();

    // CREATE - Add a new book
    @PostMapping("/addBook")
    public ResponseEntity<String> addBook(
            @Valid @RequestBody Book book,
            BindingResult result) {

        logger.info("Received request to add a book");

        bookValidator.validate(book, result);

        if (result.hasErrors()) {
            logger.warn("Book validation failed: {}", result.getAllErrors());
            return ResponseEntity.badRequest()
                    .body("Validation failed: " + result.getAllErrors());
        }

        bookRepository.save(book);

        logger.info("Book added successfully: {}", book.getTitle());

        return ResponseEntity.ok("Book added successfully");
    }

    // READ - Get all books
    @GetMapping("/books")
    public List<Book> getAllBooks() {

        logger.info("Fetching all books");

        return bookRepository.findAll();
    }

    // READ - Get a book by ID
    @GetMapping("/books/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {

        logger.info("Fetching book with ID: {}", id);

        return bookRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("Book not found with ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    // UPDATE - Update an existing book
    @PutMapping("/books/{id}")
    public ResponseEntity<String> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody Book book,
            BindingResult result) {

        logger.info("Updating book with ID: {}", id);

        bookValidator.validate(book, result);

        if (result.hasErrors()) {
            logger.warn("Book update validation failed: {}", result.getAllErrors());
            return ResponseEntity.badRequest()
                    .body("Validation failed: " + result.getAllErrors());
        }

        return bookRepository.findById(id)
                .map(existingBook -> {
                    existingBook.setTitle(book.getTitle());
                    existingBook.setAuthor(book.getAuthor());
                    existingBook.setPrice(book.getPrice());

                    bookRepository.save(existingBook);

                    logger.info("Book updated successfully: {}", id);

                    return ResponseEntity.ok("Book updated successfully");
                })
                .orElseGet(() -> {
                    logger.warn("Book not found for update: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    // DELETE - Delete a book
    @DeleteMapping("/books/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {

        logger.info("Deleting book with ID: {}", id);

        if (!bookRepository.existsById(id)) {
            logger.warn("Book not found for deletion: {}", id);
            return ResponseEntity.notFound().build();
        }

        bookRepository.deleteById(id);

        logger.info("Book deleted successfully: {}", id);

        return ResponseEntity.ok("Book deleted successfully");
    }
}
