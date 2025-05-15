package dev.biddan.jsonapiexample.feature.article.pagination;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FindArticlesEndPointTest {

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

    private Author author1, author2;
    private Category category1, category2;
    private Tag tag1, tag2, tag3;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 생성
        author1 = Author.builder()
                .name("작가 1")
                .email("author1@example.com")
                .build();
        authorRepository.save(author1);

        author2 = Author.builder()
                .name("작가 2")
                .email("author2@example.com")
                .build();
        authorRepository.save(author2);

        category1 = Category.builder()
                .name("카테고리 1")
                .build();
        categoryRepository.save(category1);

        category2 = Category.builder()
                .name("카테고리 2")
                .build();
        categoryRepository.save(category2);

        tag1 = Tag.builder().name("태그 1").build();
        tagRepository.save(tag1);

        tag2 = Tag.builder().name("태그 2").build();
        tagRepository.save(tag2);

        tag3 = Tag.builder().name("태그 3").build();
        tagRepository.save(tag3);

        Set<Tag> tags1 = new HashSet<>();
        tags1.add(tag1);
        tags1.add(tag2);

        Set<Tag> tags2 = new HashSet<>();
        tags2.add(tag2);
        tags2.add(tag3);

        Set<Tag> tags3 = new HashSet<>();
        tags3.add(tag1);
        tags3.add(tag3);

        // 20개의 게시물 생성
        for (int i = 1; i <= 20; i++) {
            Article article = Article.builder()
                    .title("게시물 " + i)
                    .content("게시물 " + i + " 내용입니다.")
                    .author(i % 2 == 0 ? author1 : author2)
                    .category(i % 3 == 0 ? category2 : category1)
                    .tags(i % 3 == 0 ? tags3 : (i % 2 == 0 ? tags2 : tags1))
                    .build();
            articleRepository.save(article);
        }
    }

    @Test
    void testFirstPage() throws Exception {
        // 첫 페이지 요청 (페이지 크기 5)
        ResultActions actions = mockMvc.perform(get("/articles")
                        .param("page[number]", "0")
                        .param("page[size]", "5")
                        .accept(MediaType.parseMediaType(JSON_API_VALUE)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_API_VALUE))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(5))
                // 메타데이터 검증
                .andExpect(jsonPath("$.meta.page.totalElements").value(20))
                .andExpect(jsonPath("$.meta.page.totalPages").value(4))
                .andExpect(jsonPath("$.meta.page.number").value(0))
                .andExpect(jsonPath("$.meta.page.size").value(5))
                // 링크 검증
                .andExpect(jsonPath("$.links.self").exists())
                .andExpect(jsonPath("$.links.first").doesNotExist())
                .andExpect(jsonPath("$.links.prev").doesNotExist())
                .andExpect(jsonPath("$.links.last").exists())
                .andExpect(jsonPath("$.links.next").exists());
    }

    @Test
    void testMiddlePage() throws Exception {
        // 중간 페이지 요청 (페이지 크기 5, 2번째 페이지)
        mockMvc.perform(get("/articles")
                        .param("page[number]", "2")
                        .param("page[size]", "5")
                        .accept(MediaType.parseMediaType(JSON_API_VALUE)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_API_VALUE))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(5))
                // 메타데이터 검증
                .andExpect(jsonPath("$.meta.page.totalElements").value(20))
                .andExpect(jsonPath("$.meta.page.totalPages").value(4))
                .andExpect(jsonPath("$.meta.page.number").value(2))
                .andExpect(jsonPath("$.meta.page.size").value(5))
                // 링크 검증 - 중간 페이지에는 prev와 next 링크가 모두 있어야 함
                .andExpect(jsonPath("$.links.self").exists())
                .andExpect(jsonPath("$.links.first").exists())
                .andExpect(jsonPath("$.links.last").exists())
                .andExpect(jsonPath("$.links.next").exists())
                .andExpect(jsonPath("$.links.prev").exists());
    }

    @Test
    void testLastPage() throws Exception {
        // 마지막 페이지 요청
        ResultActions actions = mockMvc.perform(get("/articles")
                        .param("page[number]", "3")
                        .param("page[size]", "5")
                        .accept(MediaType.parseMediaType(JSON_API_VALUE)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_API_VALUE))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(5))
                // 메타데이터 검증
                .andExpect(jsonPath("$.meta.page.totalElements").value(20))
                .andExpect(jsonPath("$.meta.page.totalPages").value(4))
                .andExpect(jsonPath("$.meta.page.number").value(3))
                .andExpect(jsonPath("$.meta.page.size").value(5))
                // 링크 검증
                .andExpect(jsonPath("$.links.self").exists())
                .andExpect(jsonPath("$.links.first").exists())
                .andExpect(jsonPath("$.links.last").doesNotExist())
                .andExpect(jsonPath("$.links.next").doesNotExist())
                .andExpect(jsonPath("$.links.prev").exists());

        // next 링크가 없는지 명시적 검증
        actions.andExpect(jsonPath("$.links.next").doesNotExist());
    }

    @Test
    void testEmptyPage() throws Exception {
        // 존재하지 않는 페이지 요청 (빈 결과)
        ResultActions actions = mockMvc.perform(get("/articles")
                        .param("page[number]", "100")
                        .param("page[size]", "5")
                        .accept(MediaType.parseMediaType(JSON_API_VALUE)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_API_VALUE))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0))
                // 메타데이터 검증
                .andExpect(jsonPath("$.meta.page.totalElements").value(20))
                .andExpect(jsonPath("$.meta.page.totalPages").value(4))
                .andExpect(jsonPath("$.meta.page.number").value(100))
                .andExpect(jsonPath("$.meta.page.size").value(5))
                // 링크 검증
                .andExpect(jsonPath("$.links.self").exists())
                .andExpect(jsonPath("$.links.first").exists())
                .andExpect(jsonPath("$.links.last").doesNotExist())
                .andExpect(jsonPath("$.links.next").doesNotExist())
                .andExpect(jsonPath("$.links.prev").exists());
    }

    @Test
    void testIncludeResources() throws Exception {
        // include 파라미터 테스트
        mockMvc.perform(get("/articles")
                        .param("include", "author,category")
                        .param("page[size]", "3")
                        .accept(MediaType.parseMediaType(JSON_API_VALUE)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_API_VALUE))
                .andExpect(jsonPath("$.included").isArray())
                .andExpect(jsonPath("$.included[?(@.type=='authors')]").exists())
                .andExpect(jsonPath("$.included[?(@.type=='categories')]").exists());
    }

    @Test
    void testSparseFieldsets() throws Exception {
        // 스파스 필드셋 테스트 - 제목만 선택
        mockMvc.perform(get("/articles")
                        .param("fields[articles]", "title")
                        .param("page[size]", "3")
                        .accept(MediaType.parseMediaType(JSON_API_VALUE)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_API_VALUE))
                .andExpect(jsonPath("$.data[0].attributes.title").exists())
                .andExpect(jsonPath("$.data[0].attributes.content").doesNotExist());
    }

    @Test
    void testMultipleFieldsInSparseFieldsets() throws Exception {
        // 스파스 필드셋 테스트 - 제목과 내용 선택
        mockMvc.perform(get("/articles")
                        .param("fields[articles]", "title,content")
                        .param("page[size]", "3")
                        .accept(MediaType.parseMediaType(JSON_API_VALUE)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_API_VALUE))
                .andExpect(jsonPath("$.data[0].attributes.title").exists())
                .andExpect(jsonPath("$.data[0].attributes.content").exists())
                .andExpect(jsonPath("$.data[0].attributes.created").doesNotExist());
    }

    @Test
    void testSorting() throws Exception {
        // 제목 오름차순 정렬 테스트
        mockMvc.perform(get("/articles")
                        .param("sort", "title")
                        .param("page[size]", "5")
                        .accept(MediaType.parseMediaType(JSON_API_VALUE)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_API_VALUE))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(5));

        // 제목 내림차순 정렬 테스트
        mockMvc.perform(get("/articles")
                        .param("sort", "-title")
                        .param("page[size]", "5")
                        .accept(MediaType.parseMediaType(JSON_API_VALUE)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_API_VALUE))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(5));
    }
}
