package dev.biddan.jsonapiexample.feature.article.get;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;

import dev.biddan.jsonapiexample.domain.article.Article;
import dev.biddan.jsonapiexample.jsonapi.article.ArticleJsonModelAssembler;
import dev.biddan.jsonapiexample.persistence.ArticleRepository;
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

    @GetMapping(path = "/articles/{id}", produces = JSON_API_VALUE)
    public ResponseEntity<RepresentationModel<?>> getArticle(
            @PathVariable String id,
            @RequestParam(value = "include", required = false) String[] include,
            @RequestParam(value = "fields[articles]", required = false) String[] articleFields) {

        Article article = articleRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));

        // JSON:API 라이브러리가 include 및 fields 파라미터를 처리
        RepresentationModel<?> articleModel = articleJsonModelAssembler.toJsonApiModel(article, include, articleFields);

        return ResponseEntity.ok(articleModel);
    }
}
