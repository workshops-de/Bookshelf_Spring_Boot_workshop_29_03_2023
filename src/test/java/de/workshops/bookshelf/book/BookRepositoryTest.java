package de.workshops.bookshelf.book;

import de.workshops.bookshelf.config.BookshelfApplicationProperties;
import de.workshops.bookshelf.config.BookshelfProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Import({ BookshelfProperties.class, BookshelfApplicationProperties.class })
class BookRepositoryTest {

    private final BookRepository bookRepository;

    @Autowired
    BookRepositoryTest(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Test
    void createBook() {
        Book book = buildAndSaveBook("123-4567890");

        List<Book> books = bookRepository.findAll();

        assertNotNull(books);
        assertEquals(4, books.size());
        assertEquals(book.getIsbn(), books.get(3).getIsbn());
    }

    @Test
    void findBookByIsbn() {
        Book book = buildAndSaveBook("123-4567891");

        Book newBook = bookRepository.findByIsbn(book.getIsbn());

        assertNotNull(newBook);
        assertEquals(book.getTitle(), newBook.getTitle());
    }

    private Book buildAndSaveBook(String isbn) {
        return bookRepository.save(
                Book.builder()
                        .title("Title")
                        .author("Author")
                        .description("Description")
                        .isbn(isbn)
                        .build()
        );
    }
}
