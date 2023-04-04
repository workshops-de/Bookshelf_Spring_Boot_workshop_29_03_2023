package de.workshops.bookshelf;

import de.workshops.bookshelf.config.BookshelfProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BookshelfApplicationTests {

	private final BookshelfProperties bookshelfProperties;

	@Autowired
	BookshelfApplicationTests(BookshelfProperties bookshelfProperties) {
		this.bookshelfProperties = bookshelfProperties;
	}

	@Test
	void contextLoads() {
		assertEquals(11, bookshelfProperties.getSomeNumber());
		assertEquals("More information", bookshelfProperties.getSomeText());
	}
}
