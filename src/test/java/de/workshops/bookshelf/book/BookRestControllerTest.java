package de.workshops.bookshelf.book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BookRestControllerTest {

    private final BookRestController bookRestController;

    @Autowired
    BookRestControllerTest(BookRestController bookRestController) {
        this.bookRestController = bookRestController;
    }

    @Test
    void getAllBooks() throws BookException {
        assertEquals(3, Objects.requireNonNull(bookRestController.getAllBooks()).size());
    }
}
