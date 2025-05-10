package dev.biddan.jsonapiexample.jsonapi.article;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;
import dev.biddan.jsonapiexample.domain.article.Article;
import dev.biddan.jsonapiexample.feature.article.get.GetArticleEndPoint;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.stereotype.Component;

@Component
public class ArticleJsonModelAssembler {

    public RepresentationModel<?> toJsonApiModel(Article article) {
        ArticleJsonModel articleModel = ArticleJsonModel.builder()
                .id(article.getId().toString())
                .title(article.getTitle())
                .content(article.getContent())
                .created(article.getCreated())
                .updated(article.getUpdated())
                .build();

        Link selfLink = linkTo(methodOn(GetArticleEndPoint.class).getArticle(articleModel.getId()))
                .withSelfRel();

        JsonApiModelBuilder modelBuilder = JsonApiModelBuilder.jsonApiModel()
                .model(articleModel)
                .link(selfLink);

        if (article.getAuthor() != null) {
            // JsonApiTypeForClass를 이용하여 다른 클래스명으로 추가 가능
            AuthorResourceIdentifier authorResourceIdentifier = new AuthorResourceIdentifier(
                    article.getAuthor().getId());

            modelBuilder.relationship("author", authorResourceIdentifier);
        }

        // Add category relationship if exists
        if (article.getCategory() != null) {
            modelBuilder.relationship("category", article.getCategory());
        }

        // Add tags relationships (many-to-many)
        if (article.getTags() != null && !article.getTags().isEmpty()) {
            modelBuilder.relationship("tags", article.getTags());
        }

        return modelBuilder.build();
    }

    @JsonApiTypeForClass("authors")
    public record AuthorResourceIdentifier(
            @JsonApiId
            Long id
    ) {

    }
}
