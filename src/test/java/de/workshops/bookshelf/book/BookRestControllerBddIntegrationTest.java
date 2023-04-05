package de.workshops.bookshelf.book;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class BookRestControllerBddIntegrationTest {

    @LocalServerPort
    private int port;

    private final BookRestController bookRestController;

    private final FilterChainProxy springSecurityFilterChain;

    private final ObjectMapper objectMapper;

    @Autowired
    BookRestControllerBddIntegrationTest(
            BookRestController bookRestController,
            FilterChainProxy springSecurityFilterChain,
            ObjectMapper objectMapper
    ) {
        this.bookRestController = bookRestController;
        this.springSecurityFilterChain = springSecurityFilterChain;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @WithMockUser
    void getAllBooksRestAssuredMockMvc() {
        RestAssuredMockMvc.standaloneSetup(
                MockMvcBuilders
                        .standaloneSetup(bookRestController)
                        .apply(SecurityMockMvcConfigurers.springSecurity(springSecurityFilterChain))
        );

        Book[] books = RestAssuredMockMvc.
                given().
                    log().all().
                when().
                    get("/book").
                    as(Book[].class);

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
                    auth().basic("dbUser", "workshops").
                when().
                    get("/book").
                then().
                    log().all().
                    statusCode(200).
                    body("author[0]", equalTo("Erich Gamma"));
    }


    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createBook() throws UnsupportedEncodingException, JsonProcessingException {
        RestAssuredMockMvc.standaloneSetup(
                MockMvcBuilders
                        .standaloneSetup(bookRestController)
                        .apply(SecurityMockMvcConfigurers.springSecurity(springSecurityFilterChain))
        );
        RestAssuredMockMvc.postProcessors(csrf());

        Book book = new Book();
        book.setAuthor("Eric Evans");
        book.setTitle("Domain-Driven Design: Tackling Complexity in the Heart of Software");
        book.setIsbn("978-0321125217");
        book.setDescription("This is not a book about specific technologies. It offers readers a systematic approach to domain-driven design, presenting an extensive set of design best practices, experience-based techniques, and fundamental principles that facilitate the development of software projects facing complex domains.");

        MockMvcResponse mockMvcResponse = RestAssuredMockMvc.
                given().
                    log().all().
                    body(book).
                    contentType(ContentType.JSON).
                    accept(ContentType.JSON).
                when().
                    post("/book").
                andReturn();
        mockMvcResponse.
                then().
                    log().all().
                    statusCode(HttpStatus.CREATED.value()).
                    body("author", equalTo("Eric Evans"));

        String jsonPayload = mockMvcResponse.mvcResult().getResponse().getContentAsString();
        String isbn = objectMapper.readValue(jsonPayload, Book.class).getIsbn();
        RestAssuredMockMvc.
                given().
                log().all().
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
                when().
                delete("/book/" + isbn).
                then().
                log().all().
                statusCode(200);
    }
}
