package de.workshops.bookshelf.book;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping("/filter")
    public List<Book> filterBooks(
            @Parameter(
                    name = "author",
                    in = ParameterIn.QUERY,
                    description = "Author name as search parameter",
                    required = true
            )
            @RequestParam @NotBlank @Size(min = 3) String author
    ) throws BookException {
        return bookService.searchBooksByAuthor(author);
    }

    @GetMapping("/{isbn}")
    public Book getSingleBook(@PathVariable @NotBlank @Size(min = 10, max = 14) String isbn) throws BookException {
        return bookService.getSingleBook(isbn);
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        return new ResponseEntity<>(bookService.createBook(book), HttpStatus.CREATED);
    }

    @DeleteMapping("/{isbn}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteBook(@PathVariable @Size(min = 10, max = 14) String isbn) throws BookException {
        bookService.deleteBook(bookService.getSingleBook(isbn));
        return ResponseEntity.ok("OK");
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
