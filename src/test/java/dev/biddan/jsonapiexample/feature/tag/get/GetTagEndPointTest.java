package dev.biddan.jsonapiexample.feature.tag.get;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.biddan.jsonapiexample.domain.tag.Tag;
import dev.biddan.jsonapiexample.persistence.TagRepository;
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
class GetTagEndPointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TagRepository tagRepository;

    private Tag testTag;

    @BeforeEach
    void setUp() {
        // 테스트 태그 생성
        testTag = new Tag("Integration Test Tag");
        tagRepository.save(testTag);
    }

    @Test
    void testGetTag() throws Exception {
        mockMvc.perform(get("/tags/{id}", testTag.getId())
                        .accept(MediaType.parseMediaType(JSON_API_VALUE)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_API_VALUE))
                .andExpect(jsonPath("$.data.type").value("tags"))
                .andExpect(jsonPath("$.data.id").value(testTag.getId().toString()))
                .andExpect(jsonPath("$.data.attributes.name").value(testTag.getName()))
                .andExpect(jsonPath("$.links.self").exists());
    }
}
