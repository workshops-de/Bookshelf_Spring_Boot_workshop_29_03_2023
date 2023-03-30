package de.workshops.bookshelf.book;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/book")
@Validated
@RequiredArgsConstructor
public class BookRestController {

    private final ObjectMapper mapper;

    private final ResourceLoader resourceLoader;

    private List<Book> books;

    @PostConstruct
    public void init() throws IOException {
        this.books = mapper.readValue(
                resourceLoader
                        .getResource("classpath:books.json")
                        .getInputStream(),
                new TypeReference<>() {}
        );
    }

    @GetMapping
    public List<Book> getAllBooks() {
        return books;
    }

    @GetMapping("/{isbn}")
    public Book getSingleBook(@PathVariable @Size(min = 10, max = 14) String isbn) throws BookException {
        return this.books
                .stream()
                .filter(book -> hasIsbn(book, isbn))
                .findFirst()
                .orElseThrow(BookException::new);
    }

    @GetMapping(params = "author")
    public Book searchBookByAuthor(@RequestParam @NotBlank @Size(min = 3) String author) throws BookException {
        return this.books.stream()
                .filter(book -> hasAuthor(book, author))
                .findFirst()
                .orElseThrow(BookException::new);
    }

    @PostMapping("/search")
    public List<Book> searchBooks(@RequestBody @Valid BookSearchRequest request) throws BookException {
        List<Book> bookList = this.books.stream()
                .filter(book -> hasAuthor(book, request.author()))
                .filter(book -> hasIsbn(book, request.isbn()))
                .toList();

        if (bookList.isEmpty()) {
            throw new BookException();
        }

        return bookList;
    }

    @ExceptionHandler(BookException.class)
    public ProblemDetail error(BookException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Book Not Found");
        problemDetail.setType(URI.create("http://localhost:8080/book_exception.html"));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    private boolean hasIsbn(Book book, String isbn) {
        return book.getIsbn().equals(isbn);
    }

    private boolean hasAuthor(Book book, String author) {
        return book.getAuthor().contains(author);
    }
}
