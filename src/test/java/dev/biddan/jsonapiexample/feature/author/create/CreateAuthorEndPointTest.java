package dev.biddan.jsonapiexample.feature.author.create;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
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
class CreateAuthorEndPointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createAuthor() throws Exception {
        Map<String, Object> requestBody = createAuthorRequestBody("Jane Smith", "jane@example.com");

        mockMvc.perform(post("/authors")
                        .contentType(MediaType.parseMediaType(JSON_API_VALUE))
                        .accept(MediaType.parseMediaType(JSON_API_VALUE))
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(JSON_API_VALUE))
                .andExpect(jsonPath("$.data.type").value("authors"))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.attributes.name").value("Jane Smith"))
                .andExpect(jsonPath("$.data.attributes.email").value("jane@example.com"));
    }

    private Map<String, Object> createAuthorRequestBody(String name, String email) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", "authors");

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", name);
        attributes.put("email", email);
        data.put("attributes", attributes);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("data", data);

        return requestBody;
    }
}
