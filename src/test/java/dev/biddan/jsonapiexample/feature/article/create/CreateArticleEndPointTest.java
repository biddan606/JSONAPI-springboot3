package dev.biddan.jsonapiexample.feature.article.create;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.biddan.jsonapiexample.domain.author.Author;
import dev.biddan.jsonapiexample.domain.category.Category;
import dev.biddan.jsonapiexample.domain.tag.Tag;
import dev.biddan.jsonapiexample.persistence.ArticleRepository;
import dev.biddan.jsonapiexample.persistence.AuthorRepository;
import dev.biddan.jsonapiexample.persistence.CategoryRepository;
import dev.biddan.jsonapiexample.persistence.TagRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
class CreateArticleEndPointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ArticleRepository articleRepository;

    private Author author;
    private Category category;
    private Tag tag1;
    private Tag tag2;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 생성
        author = Author.builder()
                .name("테스트 작가")
                .email("testauthor@example.com")
                .build();
        authorRepository.save(author);

        category = Category.builder()
                .name("테스트 카테고리")
                .build();
        categoryRepository.save(category);

        tag1 = Tag.builder()
                .name("태그 1")
                .build();
        tagRepository.save(tag1);

        tag2 = Tag.builder()
                .name("태그 2")
                .build();
        tagRepository.save(tag2);
    }

    @Test
    void createArticleWithAllRelationships() throws Exception {
        // 여기서는 JSON 문자열을 직접 만듭니다 (EntityModel 변환 오류 문제 해결)
        String jsonContent = String.format("""
                {
                  "data": {
                    "type": "articles",
                    "attributes": {
                      "title": "테스트 게시물 제목",
                      "content": "테스트 게시물 내용입니다."
                    },
                    "relationships": {
                      "author": {
                        "data": {
                          "type": "authors",
                          "id": "%s"
                        }
                      },
                      "category": {
                        "data": {
                          "type": "categories",
                          "id": "%s"
                        }
                      },
                      "tags": {
                        "data": [
                          {
                            "type": "tags",
                            "id": "%s"
                          },
                          {
                            "type": "tags",
                            "id": "%s"
                          }
                        ]
                      }
                    }
                  }
                }
                """, author.getId(), category.getId(), tag1.getId(), tag2.getId());

        mockMvc.perform(post("/articles")
                        .contentType(MediaType.parseMediaType(JSON_API_VALUE))
                        .accept(MediaType.parseMediaType(JSON_API_VALUE))
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(JSON_API_VALUE))
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.data.type").value("articles"))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.attributes.title").value("테스트 게시물 제목"))
                .andExpect(jsonPath("$.data.attributes.content").value("테스트 게시물 내용입니다."))
                .andExpect(jsonPath("$.data.attributes.created").exists())
                .andExpect(jsonPath("$.data.attributes.updated").exists())
                .andExpect(jsonPath("$.data.relationships.author.data.id").value(author.getId()))
                .andExpect(jsonPath("$.data.relationships.category.data.id").value(category.getId()))
                .andExpect(jsonPath("$.data.relationships.tags.data").isArray())
                .andExpect(jsonPath("$.data.relationships.tags.data.length()").value(2));
    }

    @Test
    void createArticleWithRequiredFieldsOnly() throws Exception {
        String jsonContent = """
                {
                  "data": {
                    "type": "articles",
                    "attributes": {
                      "title": "최소 필드만 있는 게시물",
                      "content": "필수 필드만 포함된 게시물 내용입니다."
                    }
                  }
                }
                """;

        mockMvc.perform(post("/articles")
                        .contentType(MediaType.parseMediaType(JSON_API_VALUE))
                        .accept(MediaType.parseMediaType(JSON_API_VALUE))
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(JSON_API_VALUE))
                .andExpect(jsonPath("$.data.type").value("articles"))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.attributes.title").value("최소 필드만 있는 게시물"))
                .andExpect(jsonPath("$.data.attributes.content").value("필수 필드만 포함된 게시물 내용입니다."))
                .andExpect(jsonPath("$.data.attributes.created").exists())
                .andExpect(jsonPath("$.data.attributes.updated").exists());
    }
}
