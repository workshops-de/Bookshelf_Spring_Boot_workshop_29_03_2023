package de.workshops.bookshelf;

import de.workshops.bookshelf.config.BookshelfApplicationProperties;
import de.workshops.bookshelf.config.BookshelfProperties;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class BookshelfApplication {

	private final BookshelfApplicationProperties bookshelfApplicationProperties;

	private final BookshelfProperties bookshelfProperties;

	public BookshelfApplication(BookshelfApplicationProperties bookshelfApplicationProperties, BookshelfProperties bookshelfProperties) {
		this.bookshelfApplicationProperties = bookshelfApplicationProperties;
		this.bookshelfProperties = bookshelfProperties;
	}

	public static void main(String[] args) {
		SpringApplication.run(BookshelfApplication.class, args);
	}

	@PostConstruct
	private void printBookshelfProperties() {
		log.info(
				"Application title and version: {}, {}",
				bookshelfApplicationProperties.getTitle(),
				bookshelfApplicationProperties.getVersion()
		);
		log.info(
				"Bookshelf properties: {}, {}",
				bookshelfProperties.getSomeNumber(),
				bookshelfProperties.getSomeText()
		);
	}
}
