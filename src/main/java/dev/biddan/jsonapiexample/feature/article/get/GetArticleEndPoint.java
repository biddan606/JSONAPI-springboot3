package dev.biddan.jsonapiexample.feature.article.get;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public class GetArticleEndPoint {

    @GetMapping(path = "/articles/{id}", produces = JSON_API_VALUE)
    public ResponseEntity<RepresentationModel<?>> getArticle(@PathVariable String id) {
        return ResponseEntity.ok(new RepresentationModel<>());
    }
}
