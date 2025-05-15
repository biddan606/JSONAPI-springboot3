package dev.biddan.jsonapiexample.feature.article.update;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.biddan.jsonapiexample.domain.article.Article;
import dev.biddan.jsonapiexample.domain.author.Author;
import dev.biddan.jsonapiexample.domain.category.Category;
import dev.biddan.jsonapiexample.domain.tag.Tag;
import dev.biddan.jsonapiexample.persistence.ArticleRepository;
import dev.biddan.jsonapiexample.persistence.AuthorRepository;
import dev.biddan.jsonapiexample.persistence.CategoryRepository;
import dev.biddan.jsonapiexample.persistence.TagRepository;
import java.util.HashSet;
import java.util.Set;
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
class UpdateArticleEndPointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    private Article testArticle;
    private Author originalAuthor;
    private Author newAuthor;
    private Category originalCategory;
    private Category newCategory;
    private Tag originalTag1;
    private Tag originalTag2;
    private Tag newTag;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 설정
        originalAuthor = Author.builder()
                .name("원본 작성자")
                .email("original@example.com")
                .build();
        authorRepository.save(originalAuthor);

        newAuthor = Author.builder()
                .name("새 작성자")
                .email("new@example.com")
                .build();
        authorRepository.save(newAuthor);

        originalCategory = Category.builder()
                .name("원본 카테고리")
                .build();
        categoryRepository.save(originalCategory);

        newCategory = Category.builder()
                .name("새 카테고리")
                .build();
        categoryRepository.save(newCategory);

        originalTag1 = Tag.builder()
                .name("원본 태그 1")
                .build();
        tagRepository.save(originalTag1);

        originalTag2 = Tag.builder()
                .name("원본 태그 2")
                .build();
        tagRepository.save(originalTag2);

        newTag = Tag.builder()
                .name("새 태그")
                .build();
        tagRepository.save(newTag);

        // 테스트 게시물 생성
        Set<Tag> originalTags = new HashSet<>();
        originalTags.add(originalTag1);
        originalTags.add(originalTag2);

        testArticle = Article.builder()
                .title("원본 제목")
                .content("원본 내용")
                .author(originalAuthor)
                .category(originalCategory)
                .tags(originalTags)
                .build();
        articleRepository.save(testArticle);
    }

    @Test
    void updateArticleAttributes() throws Exception {
        String jsonContent = String.format("""
                {
                  "data": {
                    "type": "articles",
                    "id": "%s",
                    "attributes": {
                      "title": "수정된 제목",
                      "content": "수정된 내용"
                    }
                  }
                }
                """, testArticle.getId());

        mockMvc.perform(patch("/articles/{id}", testArticle.getId())
                        .contentType(MediaType.parseMediaType(JSON_API_VALUE))
                        .accept(MediaType.parseMediaType(JSON_API_VALUE))
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_API_VALUE))
                .andExpect(jsonPath("$.data.type").value("articles"))
                .andExpect(jsonPath("$.data.id").value(testArticle.getId().toString()))
                .andExpect(jsonPath("$.data.attributes.title").value("수정된 제목"))
                .andExpect(jsonPath("$.data.attributes.content").value("수정된 내용"));
    }

    @Test
    void updateAuthorRelationship() throws Exception {
        String jsonContent = String.format("""
                {
                  "data": {
                    "type": "articles",
                    "id": "%s",
                    "relationships": {
                      "author": {
                        "data": {
                          "type": "authors",
                          "id": "%s"
                        }
                      }
                    }
                  }
                }
                """, testArticle.getId(), newAuthor.getId());

        mockMvc.perform(patch("/articles/{id}", testArticle.getId())
                        .contentType(MediaType.parseMediaType(JSON_API_VALUE))
                        .accept(MediaType.parseMediaType(JSON_API_VALUE))
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_API_VALUE))
                .andExpect(jsonPath("$.data.relationships.author.data.id").value(newAuthor.getId().toString()));
    }

    @Test
    void updateCategoryRelationship() throws Exception {
        String jsonContent = String.format("""
                {
                  "data": {
                    "type": "articles",
                    "id": "%s",
                    "relationships": {
                      "category": {
                        "data": {
                          "type": "categories",
                          "id": "%s"
                        }
                      }
                    }
                  }
                }
                """, testArticle.getId(), newCategory.getId());

        mockMvc.perform(patch("/articles/{id}", testArticle.getId())
                        .contentType(MediaType.parseMediaType(JSON_API_VALUE))
                        .accept(MediaType.parseMediaType(JSON_API_VALUE))
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_API_VALUE))
                .andExpect(jsonPath("$.data.relationships.category.data.id").value(newCategory.getId().toString()));
    }

    @Test
    void updateTagsRelationship() throws Exception {
        String jsonContent = String.format("""
                {
                  "data": {
                    "type": "articles",
                    "id": "%s",
                    "relationships": {
                      "tags": {
                        "data": [
                          {
                            "type": "tags",
                            "id": "%s"
                          }
                        ]
                      }
                    }
                  }
                }
                """, testArticle.getId(), newTag.getId());

        mockMvc.perform(patch("/articles/{id}", testArticle.getId())
                        .contentType(MediaType.parseMediaType(JSON_API_VALUE))
                        .accept(MediaType.parseMediaType(JSON_API_VALUE))
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_API_VALUE))
                .andExpect(jsonPath("$.data.relationships.tags.data[0].id").value(newTag.getId().toString()))
                .andExpect(jsonPath("$.data.relationships.tags.data.length()").value(1));
    }

    @Test
    void updateMultipleRelationships() throws Exception {
        String jsonContent = String.format("""
                {
                  "data": {
                    "type": "articles",
                    "id": "%s",
                    "attributes": {
                      "title": "관계가 포함된 업데이트"
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
                          }
                        ]
                      }
                    }
                  }
                }
                """, testArticle.getId(), newAuthor.getId(), newCategory.getId(), newTag.getId());

        mockMvc.perform(patch("/articles/{id}", testArticle.getId())
                        .contentType(MediaType.parseMediaType(JSON_API_VALUE))
                        .accept(MediaType.parseMediaType(JSON_API_VALUE))
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_API_VALUE))
                .andExpect(jsonPath("$.data.attributes.title").value("관계가 포함된 업데이트"))
                .andExpect(jsonPath("$.data.relationships.author.data.id").value(newAuthor.getId().toString()))
                .andExpect(jsonPath("$.data.relationships.category.data.id").value(newCategory.getId().toString()))
                .andExpect(jsonPath("$.data.relationships.tags.data[0].id").value(newTag.getId().toString()));
    }
}
