package de.workshops.bookshelf.book;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class BookRestControllerBddIntegrationTest {

    @LocalServerPort
    private int port;

    private final BookRestController bookRestController;

    @Autowired
    BookRestControllerBddIntegrationTest(BookRestController bookRestController) {
        this.bookRestController = bookRestController;
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void getAllBooksRestAssuredMockMvc() {
        RestAssuredMockMvc.standaloneSetup(bookRestController);
        Book[] books = RestAssuredMockMvc.
                given().
                    log().all().
                when().
                    get("/book")
                    .as(Book[].class);

        assertEquals(3, books.length);
        assertEquals("Clean Code", books[1].getTitle());

        assertThat(books).hasSize(3);
        assertThat(books[1].getTitle()).isEqualTo("Clean Code");
    }

    @Test
    void getAllBooksWithRestAssured() {
        RestAssured.
                given().
                    log().all().
                when().
                    get("/book").
                then().
                    log().all().
                    statusCode(200).
                    body("author[0]", equalTo("Erich Gamma"));
    }


    @Test
    void createBook() {
        RestAssuredMockMvc.standaloneSetup(bookRestController);

        Book book = new Book();
        book.setAuthor("Eric Evans");
        book.setTitle("Domain-Driven Design: Tackling Complexity in the Heart of Software");
        book.setIsbn("978-0321125217");
        book.setDescription("This is not a book about specific technologies. It offers readers a systematic approach to domain-driven design, presenting an extensive set of design best practices, experience-based techniques, and fundamental principles that facilitate the development of software projects facing complex domains.");

        RestAssuredMockMvc.
                given().
                    log().all().
                    body(book).
                    contentType(ContentType.JSON).
                    accept(ContentType.JSON).
                when().
                    post("/book").
                then().
                    log().all().
                    statusCode(HttpStatus.CREATED.value()).
                    body("author", equalTo("Eric Evans"));
    }
}
