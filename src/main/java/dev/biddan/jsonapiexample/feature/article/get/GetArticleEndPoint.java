package dev.biddan.jsonapiexample.feature.article.get;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;

import com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder;
import dev.biddan.jsonapiexample.domain.article.Article;
import dev.biddan.jsonapiexample.domain.author.Author;
import dev.biddan.jsonapiexample.domain.category.Category;
import dev.biddan.jsonapiexample.domain.tag.Tag;
import dev.biddan.jsonapiexample.jsonapi.article.ArticleJsonModelAssembler;
import dev.biddan.jsonapiexample.jsonapi.author.AuthorJsonModelAssembler;
import dev.biddan.jsonapiexample.jsonapi.category.CategoryJsonModelAssembler;
import dev.biddan.jsonapiexample.jsonapi.tag.TagJsonModelAssembler;
import dev.biddan.jsonapiexample.persistence.ArticleRepository;
import dev.biddan.jsonapiexample.persistence.AuthorRepository;
import dev.biddan.jsonapiexample.persistence.CategoryRepository;
import dev.biddan.jsonapiexample.persistence.TagRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
public class GetArticleEndPoint {

    private final ArticleRepository articleRepository;
    private final ArticleJsonModelAssembler articleJsonModelAssembler;
    private final AuthorRepository authorRepository;
    private final AuthorJsonModelAssembler authorJsonModelAssembler;
    private final CategoryRepository categoryRepository;
    private final CategoryJsonModelAssembler categoryJsonModelAssembler;
    private final TagRepository tagRepository;
    private final TagJsonModelAssembler tagJsonModelAssembler;

    @GetMapping(path = "/articles/{id}", produces = JSON_API_VALUE)
    public ResponseEntity<RepresentationModel<?>> getArticle(
            @PathVariable String id,
            @RequestParam(value = "include", required = false) String[] includeArr,
            @RequestParam(value = "fields[articles]", required = false) String[] articleFields) {

        Article article = articleRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));

        // JSON:API 라이브러리가 include 및 fields 파라미터를 처리
        JsonApiModelBuilder modelBuilder = JsonApiModelBuilder.jsonApiModel();
        RepresentationModel<?> articleModel = articleJsonModelAssembler.toJsonApiModel(article, articleFields);
        modelBuilder.model(articleModel);

        Set<String> includes = parseIncludes(includeArr);
        if (!includes.isEmpty()) {
            List<Long> articleIds = new ArrayList<>();
            articleIds.add(article.getId());

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

            // tags include 처리
            if (includes.contains("tags")) {
                Set<Tag> tags = tagRepository.findDistinctByArticleIdIn(articleIds);
                tags.forEach(tag ->
                        modelBuilder.included(tagJsonModelAssembler.toJsonApiModel(tag)));
            }
        }

        return ResponseEntity.ok(modelBuilder.build());
    }

    private Set<String> parseIncludes(String[] includeArr) {
        if (includeArr == null || includeArr.length == 0) {
            return Collections.emptySet();
        }

        return Arrays.stream(includeArr)
                .collect(Collectors.toSet());
    }
}
