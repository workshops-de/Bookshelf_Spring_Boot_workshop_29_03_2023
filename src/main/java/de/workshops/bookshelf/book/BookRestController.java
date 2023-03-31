package de.workshops.bookshelf.book;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/book")
@Validated
@RequiredArgsConstructor
public class BookRestController {

    private final BookService bookService;

    @GetMapping
    public List<Book> getAllBooks() throws BookException {
        List<Book> bookList = bookService.getBooks();
        if (bookList == null) {
            throw new BookException();
        }

        return bookList;
    }

    @GetMapping("/{isbn}")
    public Book getSingleBook(@PathVariable @Size(min = 10, max = 14) String isbn) throws BookException {
        return bookService.getSingleBook(isbn);
    }

    @GetMapping(params = "author")
    public Book searchBookByAuthor(@RequestParam @NotBlank @Size(min = 3) String author) throws BookException {
        return bookService.searchBookByAuthor(author);
    }

    @PostMapping("/search")
    public List<Book> searchBooks(@RequestBody @Valid BookSearchRequest bookSearchRequest) throws BookException {
        List<Book> bookList = bookService.searchBooks(bookSearchRequest);

        if (bookList.isEmpty()) {
            throw new BookException();
        }

        return bookList;
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        return new ResponseEntity<>(bookService.createBook(book), HttpStatus.CREATED);
    }

    @ExceptionHandler(BookException.class)
    public ProblemDetail error(BookException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Book Not Found");
        problemDetail.setType(URI.create("http://localhost:8080/book_exception.html"));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }
}
