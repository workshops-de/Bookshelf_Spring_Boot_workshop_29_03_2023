package de.workshops.bookshelf.book;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class BookRestControllerMockitoReflectionTestUtilsTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void getAllBooks() {
        ReflectionTestUtils.setField(bookRepository, "books", Collections.emptyList());

        assertNotNull(bookService.getBooks());
        assertEquals(0, bookService.getBooks().size());
    }
}
