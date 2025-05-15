package dev.biddan.jsonapiexample.jsonapi.article;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;
import dev.biddan.jsonapiexample.domain.article.Article;
import dev.biddan.jsonapiexample.feature.article.get.GetArticleEndPoint;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleJsonModelAssembler {

    public RepresentationModel<?> toJsonApiModel(Article article) {
        return toJsonApiModel(article, null, null);
    }

    public RepresentationModel<?> toJsonApiModel(
            Article article,
            String[] include,
            String[] articleFields) {

        ArticleJsonModel articleModel = ArticleJsonModel.builder()
                .id(article.getId().toString())
                .title(article.getTitle())
                .content(article.getContent())
                .created(article.getCreated())
                .updated(article.getUpdated()).build();

        Link selfLink = linkTo(
                methodOn(GetArticleEndPoint.class).getArticle(articleModel.getId(), include, articleFields))
                .withSelfRel();

        JsonApiModelBuilder modelBuilder = JsonApiModelBuilder.jsonApiModel()
                .model(articleModel)
                .link(selfLink);

        if (articleFields != null && articleFields.length > 0) {
            modelBuilder.fields("articles", articleFields);
        }

        // 관계 처리
        if (include != null && include.length > 0) {
            Set<String> includeSet = Arrays.stream(include).collect(Collectors.toSet());
            if (article.getAuthor() != null
                    && (includeSet.isEmpty() || !includeSet.contains("author"))) {
                // JsonApiTypeForClass를 이용하여 다른 클래스명으로 추가 가능
                AuthorResourceIdentifier authorResourceIdentifier = new AuthorResourceIdentifier(
                        article.getAuthor().getId());

                modelBuilder.relationship("author", authorResourceIdentifier);
            }

            if (article.getCategory() != null
                    && (includeSet.isEmpty() || !includeSet.contains("category"))) {
                modelBuilder.relationship("category", article.getCategory());
            }

            // many to many
            if (!article.getTags().isEmpty()
                    && (includeSet.isEmpty() || !includeSet.contains("tags"))) {
                modelBuilder.relationship("tags", article.getTags());
            }
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
