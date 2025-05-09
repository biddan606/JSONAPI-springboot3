package dev.biddan.jsonapiexample.feature.author;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@JsonApiTypeForClass("authors")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthorJsonModel extends RepresentationModel<AuthorJsonModel> {

    @JsonApiId
    private String id;

    private String name;
    private String email;

    @Builder
    public AuthorJsonModel(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
