package dev.biddan.jsonapiexample.jsonapi.article;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@JsonApiTypeForClass("articles")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArticleJsonModel {

    @JsonApiId
    private String id;

    private String title;
    private String content;
    private LocalDateTime created;
    private LocalDateTime updated;

    @Builder
    public ArticleJsonModel(String id, String title, String content,
            LocalDateTime created, LocalDateTime updated) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.created = created;
        this.updated = updated;
    }
}
