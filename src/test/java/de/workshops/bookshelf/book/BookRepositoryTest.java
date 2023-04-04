package de.workshops.bookshelf.book;

import de.workshops.bookshelf.config.BookshelfApplicationProperties;
import de.workshops.bookshelf.config.BookshelfProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest(
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ANNOTATION,
                classes = Repository.class
        )
)
@Import({ BookshelfProperties.class, BookshelfApplicationProperties.class })
class BookRepositoryTest {

    private final BookRepository bookRepository;

    @Autowired
    BookRepositoryTest(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Test
    void createBook() {
        Book book = Book.builder()
                .title("Title")
                .author("Author")
                .description("Description")
                .isbn("123-4567890")
                .build();
        bookRepository.createBook(book);

        List<Book> books = bookRepository.findAllBooks();

        assertNotNull(books);
        assertEquals(4, books.size());
        assertEquals(book.getIsbn(), books.get(3).getIsbn());
    }
}
