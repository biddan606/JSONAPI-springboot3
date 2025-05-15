package dev.biddan.jsonapiexample.feature.article.pagination;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;

import com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder;
import dev.biddan.jsonapiexample.domain.article.Article;
import dev.biddan.jsonapiexample.domain.author.Author;
import dev.biddan.jsonapiexample.domain.category.Category;
import dev.biddan.jsonapiexample.jsonapi.article.ArticleJsonModelAssembler;
import dev.biddan.jsonapiexample.jsonapi.author.AuthorJsonModelAssembler;
import dev.biddan.jsonapiexample.jsonapi.category.CategoryJsonModelAssembler;
import dev.biddan.jsonapiexample.persistence.ArticleRepository;
import dev.biddan.jsonapiexample.persistence.AuthorRepository;
import dev.biddan.jsonapiexample.persistence.CategoryRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class FindArticlesEndPoint {

    private final ArticleRepository articleRepository;
    private final ArticleJsonModelAssembler articleJsonModelAssembler;
    private final AuthorRepository authorRepository;
    private final AuthorJsonModelAssembler authorJsonModelAssembler;
    private final CategoryRepository categoryRepository;
    private final CategoryJsonModelAssembler categoryJsonModelAssembler;

    @GetMapping(path = "/articles", produces = JSON_API_VALUE)
    public ResponseEntity<RepresentationModel<?>> getAllArticles(
            @RequestParam(value = "include", required = false) String[] includeArr,
            @RequestParam(value = "fields[articles]", required = false) String[] articleFields,
            @RequestParam(value = "sort", required = false, defaultValue = "-created") String sort,
            @RequestParam(value = "page[number]", required = false, defaultValue = "0") int pageNumber,
            @RequestParam(value = "page[size]", required = false, defaultValue = "10") int pageSize) {

        // 1. 기본 페이지네이션 쿼리 실행
        Page<Article> articlesPage = articleRepository.findAllWithCommonRelations(
                PageRequest.of(pageNumber, pageSize, parseSort(sort)));

        // 2. PagedModel 생성 - 기본 데이터 구조
        List<RepresentationModel<?>> articleModels = articlesPage.getContent().stream()
                .map(article -> articleJsonModelAssembler.toJsonApiModel(article, articleFields))
                .collect(Collectors.toList());

        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                pageSize, pageNumber, articlesPage.getTotalElements(), articlesPage.getTotalPages());

        PagedModel<RepresentationModel<?>> pagedModel = PagedModel.of(articleModels, metadata);

        // 기본 URL 생성
        String baseUrl = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .build()
                .toUriString();

        // JSON:API 모델 구성
        JsonApiModelBuilder modelBuilder = JsonApiModelBuilder.jsonApiModel()
                .model(pagedModel)
                .link(Link.of(baseUrl))
                .pageLinks(baseUrl)
                .pageMeta();

        // 4. include에 따라 필요한 관계 데이터 배치 로딩 및 추가
        Set<String> includes = parseIncludes(includeArr);
        if (!includes.isEmpty() && !articlesPage.isEmpty()) {
            List<Long> articleIds = articlesPage.getContent().stream()
                    .map(Article::getId)
                    .collect(Collectors.toList());

            addIncludedResources(modelBuilder, articleIds, includes);
        }

        return ResponseEntity.ok(modelBuilder.build());
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isEmpty()) {
            return Sort.unsorted();
        }

        boolean isDesc = sort.startsWith("-");
        String field = isDesc ? sort.substring(1) : sort;

        return isDesc ?
                Sort.by(Sort.Direction.DESC, field) :
                Sort.by(Sort.Direction.ASC, field);
    }

    private Set<String> parseIncludes(String[] includeArr) {
        if (includeArr == null || includeArr.length == 0) {
            return Collections.emptySet();
        }

        return Arrays.stream(includeArr)
                .collect(Collectors.toSet());
    }

    private void addIncludedResources(JsonApiModelBuilder modelBuilder, List<Long> articleIds, Set<String> includes) {
        // author include 처리
        if (includes.contains("author")) {
            Set<Author> authors = authorRepository.findDistinctByArticleIdIn(articleIds);
            authors.forEach(author ->
                    modelBuilder.included(authorJsonModelAssembler.toJsonApiModel(author)));
        }

        // category include 처리
        if (includes.contains("category")) {
            Set<Category> categories = categoryRepository.findDistinctByArticleIdIn(articleIds);
            categories.forEach(category ->
                    modelBuilder.included(categoryJsonModelAssembler.toJsonApiModel(category)));
        }
    }
}
