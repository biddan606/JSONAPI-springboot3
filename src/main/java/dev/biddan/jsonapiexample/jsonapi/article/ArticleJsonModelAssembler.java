package dev.biddan.jsonapiexample.jsonapi.article;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;
import dev.biddan.jsonapiexample.domain.article.Article;
import dev.biddan.jsonapiexample.domain.tag.Tag;
import dev.biddan.jsonapiexample.feature.article.get.GetArticleEndPoint;
import dev.biddan.jsonapiexample.jsonapi.author.AuthorJsonModelAssembler;
import dev.biddan.jsonapiexample.jsonapi.category.CategoryJsonModelAssembler;
import dev.biddan.jsonapiexample.jsonapi.tag.TagJsonModelAssembler;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleJsonModelAssembler {

    private final AuthorJsonModelAssembler authorJsonModelAssembler;
    private final CategoryJsonModelAssembler categoryJsonModelAssembler;
    private final TagJsonModelAssembler tagJsonModelAssembler;

    public RepresentationModel<?> toJsonApiModel(Article article) {
        return toJsonApiModel(article, null, null);
    }

    public RepresentationModel<?> toJsonApiModel(
            Article article,
            String include,
            String articleFields) {

        Set<String> articleFieldsSet = parseFields(articleFields);

        ArticleJsonModel articleModel = ArticleJsonModel.builder()
                .id(article.getId().toString())
                .title(articleFieldsSet.isEmpty() || articleFieldsSet.contains("title") ? article.getTitle() : null)
                .content(articleFieldsSet.isEmpty() || articleFieldsSet.contains("content") ? article.getContent()
                        : null)
                .created(articleFieldsSet.isEmpty() || articleFieldsSet.contains("created") ? article.getCreated()
                        : null)
                .updated(articleFieldsSet.isEmpty() || articleFieldsSet.contains("updated") ? article.getUpdated()
                        : null)
                .build();

        Link selfLink = linkTo(
                methodOn(GetArticleEndPoint.class).getArticle(articleModel.getId(), include, articleFields))
                .withSelfRel();

        JsonApiModelBuilder modelBuilder = JsonApiModelBuilder.jsonApiModel()
                .model(articleModel)
                .link(selfLink);

        // 관계 처리
        if (article.getAuthor() != null) {
            // JsonApiTypeForClass를 이용하여 다른 클래스명으로 추가 가능
            AuthorResourceIdentifier authorResourceIdentifier = new AuthorResourceIdentifier(
                    article.getAuthor().getId());

            modelBuilder.relationship("author", authorResourceIdentifier);
        }

        if (article.getCategory() != null) {
            modelBuilder.relationship("category", article.getCategory());
        }

        // many to many
        if (article.getTags() != null && !article.getTags().isEmpty()) {
            modelBuilder.relationship("tags", article.getTags());
        }

        if (include != null && !include.trim().isEmpty()) {
            Set<String> includedResources = parseInclude(include);

            // Author 포함
            if (includedResources.contains("author") && article.getAuthor() != null) {
                modelBuilder.included(authorJsonModelAssembler.toJsonApiModel(article.getAuthor()));
            }

            // Category 포함
            if (includedResources.contains("category") && article.getCategory() != null) {
                modelBuilder.included(categoryJsonModelAssembler.toJsonApiModel(article.getCategory()));
            }

            // Tags 포함
            if (includedResources.contains("tags") && article.getTags() != null && !article.getTags().isEmpty()) {
                for (Tag tag : article.getTags()) {
                    modelBuilder.included(tagJsonModelAssembler.toJsonApiModel(tag));
                }
            }
        }

        return modelBuilder.build();
    }

    private Set<String> parseFields(String fields) {
        if (fields == null || fields.trim().isEmpty()) {
            return new HashSet<>();
        }
        return Arrays.stream(fields.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
    }

    private Set<String> parseInclude(String include) {
        if (include == null || include.trim().isEmpty()) {
            return new HashSet<>();
        }
        return Arrays.stream(include.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
    }

    @JsonApiTypeForClass("authors")
    public record AuthorResourceIdentifier(
            @JsonApiId
            Long id
    ) {

    }
}
