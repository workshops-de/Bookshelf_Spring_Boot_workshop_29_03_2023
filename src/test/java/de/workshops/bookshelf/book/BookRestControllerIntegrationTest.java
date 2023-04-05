package de.workshops.bookshelf.book;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
class BookRestControllerIntegrationTest {

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    @Autowired
    BookRestControllerIntegrationTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @TestConfiguration
    static class JacksonTestConfiguration {

        @Bean
        public ObjectMapper mapper() {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

            return mapper;
        }
    }

    @Test
    @WithMockUser
    void getAllBooks() throws Exception {
        assertTrue(objectMapper.getSerializationConfig().isEnabled(SerializationFeature.INDENT_OUTPUT));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/book"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].title").value("Clean Code"))
                .andReturn();
        String jsonPayload = mvcResult.getResponse().getContentAsString();

        List<Book> bookList = objectMapper.readValue(jsonPayload, new TypeReference<>() {});
        assertEquals(3, bookList.size());
        assertEquals("Clean Code", bookList.get(1).getTitle());

        assertThat(bookList).hasSize(3);
        assertThat(bookList.get(1).getTitle()).isEqualTo("Clean Code");
    }

    @Test
    @WithMockUser(username = "dbUser", password = "workshops", roles = "USER")
    void filterBooks() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .get("/book/filter")
                                .queryParam("author", "Erich")
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Design Patterns"))
                .andReturn();
    }
}
