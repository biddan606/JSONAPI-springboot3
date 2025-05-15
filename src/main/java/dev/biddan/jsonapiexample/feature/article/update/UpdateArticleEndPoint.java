package dev.biddan.jsonapiexample.feature.article.update;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;

import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiRelationships;
import dev.biddan.jsonapiexample.domain.article.Article;
import dev.biddan.jsonapiexample.feature.article.update.UpdateArticleHandler.UpdateArticleCommand;
import dev.biddan.jsonapiexample.jsonapi.article.ArticleJsonModelAssembler;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UpdateArticleEndPoint {

    private final UpdateArticleHandler updateArticleHandler;
    private final ArticleJsonModelAssembler articleJsonModelAssembler;

    @PatchMapping(path = "/articles/{id}", produces = JSON_API_VALUE, consumes = JSON_API_VALUE)
    public ResponseEntity<RepresentationModel<?>> updateArticle(
            @PathVariable("id") String id,
            @RequestBody EntityModel<UpdateArticleRequest> requestModel) {

        UpdateArticleRequest content = Objects.requireNonNull(requestModel.getContent());

        // 커맨드 객체 생성
        UpdateArticleCommand.UpdateArticleCommandBuilder commandBuilder = UpdateArticleCommand.builder()
                .articleId(Long.valueOf(id))
                .title(content.getTitle())
                .content(content.getContent());

        // 관계 데이터가 있으면 추가
        if (content.getAuthor() != null) {
            commandBuilder.authorId(Long.parseLong(content.getAuthor().getId()));
        }

        if (content.getCategory() != null) {
            commandBuilder.categoryId(Long.parseLong(content.getCategory().getId()));
        }

        if (content.getTags() != null && !content.getTags().isEmpty()) {
            Set<Long> tagIds = content.getTags().stream()
                    .map(ResourceIdentifier::getId)
                    .map(Long::parseLong)
                    .collect(Collectors.toSet());
            commandBuilder.tagIds(tagIds);
        }

        // 게시물 업데이트
        Article updatedArticle = updateArticleHandler.updateArticle(commandBuilder.build());

        // 응답 모델 생성
        RepresentationModel<?> articleModel = articleJsonModelAssembler.toJsonApiModel(updatedArticle);

        // 응답 반환
        return ResponseEntity.ok(articleModel);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UpdateArticleRequest {
        @JsonApiId
        private String id;

        private String title;
        private String content;

        @JsonApiRelationships("author")
        private ResourceIdentifier author;

        @JsonApiRelationships("category")
        private ResourceIdentifier category;

        @JsonApiRelationships("tags")
        private List<ResourceIdentifier> tags;

        @Builder
        public UpdateArticleRequest(String title, String content, ResourceIdentifier author,
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
