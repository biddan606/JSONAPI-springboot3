package dev.biddan.jsonapiexample.feature.article.create;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;

import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiRelationships;
import dev.biddan.jsonapiexample.domain.article.Article;
import dev.biddan.jsonapiexample.feature.article.create.CreateArticleHandler.CreateArticleCommand;
import dev.biddan.jsonapiexample.jsonapi.article.ArticleJsonModelAssembler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class CreateArticleEndPoint {

    private final CreateArticleHandler createArticleHandler;
    private final ArticleJsonModelAssembler articleJsonModelAssembler;

    @PostMapping(path = "/articles", produces = JSON_API_VALUE, consumes = JSON_API_VALUE)
    public ResponseEntity<RepresentationModel<?>> createArticle(
            @Valid @RequestBody EntityModel<CreateArticleRequest> requestModel) {
        CreateArticleRequest content = Objects.requireNonNull(requestModel.getContent());

        // Build the command
        CreateArticleCommand.CreateArticleCommandBuilder commandBuilder = CreateArticleCommand.builder()
                .title(content.getTitle())
                .content(content.getContent());

        // Add relationships if they exist
        if (content.getAuthor() != null) {
            commandBuilder.authorId(Long.parseLong(content.getAuthor().getId()));
        }

        if (content.getCategory() != null) {
            commandBuilder.categoryId(Long.parseLong(content.getCategory().getId()));
        }

        if (content.getTags() != null && !content.getTags().isEmpty()) {
            List<String> tagIds = content.getTags().stream()
                    .map(ResourceIdentifier::getId)
                    .toList();
            commandBuilder.tagIds(tagIds.stream()
                    .map(Long::parseLong)
                    .collect(Collectors.toSet()));
        }

        // Create the article
        Article createdArticle = createArticleHandler.createArticle(commandBuilder.build());

        // Build the location URI
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdArticle.getId())
                .toUri();

        // Create the response model
        RepresentationModel<?> articleModel = articleJsonModelAssembler.toJsonApiModel(createdArticle);

        // Return the response
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(location)
                .body(articleModel);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateArticleRequest {

        @JsonApiId
        private String id; // Library requirement

        @NotBlank(message = "제목은 필수 입력값입니다")
        private String title;

        @NotBlank(message = "내용은 필수 입력값입니다")
        private String content;

        @JsonApiRelationships("author")
        private ResourceIdentifier author;

        @JsonApiRelationships("category")
        private ResourceIdentifier category;

        @JsonApiRelationships("tags")
        private List<ResourceIdentifier> tags;

        @Builder
        public CreateArticleRequest(String title, String content, ResourceIdentifier author,
                ResourceIdentifier category, List<ResourceIdentifier> tags) {
            this.title = title;
            this.content = content;
            this.author = author;
            this.category = category;
            this.tags = tags;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ResourceIdentifier {
        @JsonApiId
        String id;
        String type;
    }

}
