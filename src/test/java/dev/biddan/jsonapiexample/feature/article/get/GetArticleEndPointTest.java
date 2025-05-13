package dev.biddan.jsonapiexample.feature.article.get;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
class GetArticleEndPointTest {

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
    private Author testAuthor;
    private Category testCategory;
    private Tag testTag1;
    private Tag testTag2;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 생성
        testAuthor = Author.builder()
                .name("테스트 작가")
                .email("test@example.com")
                .build();
        authorRepository.save(testAuthor);

        testCategory = Category.builder()
                .name("테스트 카테고리")
                .build();
        categoryRepository.save(testCategory);

        testTag1 = Tag.builder()
                .name("테스트 태그 1")
                .build();
        tagRepository.save(testTag1);

        testTag2 = Tag.builder()
                .name("테스트 태그 2")
                .build();
        tagRepository.save(testTag2);

        Set<Tag> tags = new HashSet<>();
        tags.add(testTag1);
        tags.add(testTag2);

        testArticle = Article.builder()
                .title("테스트 게시물")
                .content("테스트 내용입니다.")
                .author(testAuthor)
                .category(testCategory)
                .tags(tags)
                .build();
        articleRepository.save(testArticle);
    }

    @Test
    void testGetArticle() throws Exception {
        mockMvc.perform(get("/articles/{id}", testArticle.getId())
                        .accept(MediaType.parseMediaType(JSON_API_VALUE)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_API_VALUE))
                .andExpect(jsonPath("$.data.type").value("articles"))
                .andExpect(jsonPath("$.data.id").value(testArticle.getId().toString()))
                .andExpect(jsonPath("$.data.attributes.title").value(testArticle.getTitle()))
                .andExpect(jsonPath("$.data.attributes.content").value(testArticle.getContent()))
                .andExpect(jsonPath("$.data.relationships.author").exists())
                .andExpect(jsonPath("$.data.relationships.category").exists())
                .andExpect(jsonPath("$.data.relationships.tags").exists());
    }

    @Test
    void testGetArticleWithInclude() throws Exception {
        mockMvc.perform(get("/articles/{id}", testArticle.getId())
                        .param("include", "author,category,tags")
                        .accept(MediaType.parseMediaType(JSON_API_VALUE)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_API_VALUE))
                .andExpect(jsonPath("$.included").isArray())
                .andExpect(jsonPath("$.included.length()").value(4)) // author, category, 2 tags
                .andExpect(jsonPath("$.included[?(@.type=='authors')]").exists())
                .andExpect(jsonPath("$.included[?(@.type=='categories')]").exists())
                .andExpect(jsonPath("$.included[?(@.type=='tags')]").exists());
    }

    @Test
    void testGetArticleWithSparseFieldsets() throws Exception {
        mockMvc.perform(get("/articles/{id}", testArticle.getId())
                        .param("fields[articles]", "title")
                        .accept(MediaType.parseMediaType(JSON_API_VALUE)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_API_VALUE))
                .andExpect(jsonPath("$.data.attributes.title").value(testArticle.getTitle()))
                .andExpect(jsonPath("$.data.attributes.content").doesNotExist());
    }

    @Test
    void testGetArticleWithIncludeAndSparseFieldsets() throws Exception {
        mockMvc.perform(get("/articles/{id}", testArticle.getId())
                        .param("include", "author,category")
                        .param("fields[articles]", "title")
                        .accept(MediaType.parseMediaType(JSON_API_VALUE)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_API_VALUE))
                .andExpect(jsonPath("$.data.attributes.title").value(testArticle.getTitle()))
                .andExpect(jsonPath("$.data.attributes.content").doesNotExist())
                .andExpect(jsonPath("$.included").isArray())
                .andExpect(jsonPath("$.included.length()").value(2)) // author, category
                .andExpect(jsonPath("$.included[?(@.type=='authors')]").exists())
                .andExpect(jsonPath("$.included[?(@.type=='categories')]").exists());
    }
}
