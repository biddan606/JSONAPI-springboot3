package dev.biddan.jsonapiexample.feature.author.get.one;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.biddan.jsonapiexample.domain.author.Author;
import dev.biddan.jsonapiexample.persistence.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class GetAuthorEndPointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthorRepository authorRepository;

    private Author existingAuthor;

    @BeforeEach
    void setUp() {
        existingAuthor = new Author("John Doe", "john@example.com");
        authorRepository.save(existingAuthor);
    }

    @Test
    void getAuthorById() throws Exception {
        mockMvc.perform(get("/authors/{id}", existingAuthor.getId())
                        .accept(MediaType.parseMediaType(JSON_API_VALUE)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_API_VALUE))
                .andExpect(jsonPath("$.data.type").value("authors"))
                .andExpect(jsonPath("$.data.id").value(existingAuthor.getId().toString()))
                .andExpect(jsonPath("$.data.attributes.name").value(existingAuthor.getName()))
                .andExpect(jsonPath("$.data.attributes.email").value(existingAuthor.getEmail()))
                .andExpect(jsonPath("$.links.self").value("http://localhost/authors/" + existingAuthor.getId()));
    }
}
