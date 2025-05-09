package dev.biddan.jsonapiexample.feature.author;

import com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder;
import dev.biddan.jsonapiexample.domain.author.Author;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.stereotype.Component;

@Component
public class AuthorJsonModelAssembler {

    public RepresentationModel<?> toJsonApiModel(Author author) {
        AuthorJsonModel authorModel = toAuthorModel(author);

        return JsonApiModelBuilder.jsonApiModel()
                .model(authorModel).build();
    }

    private static AuthorJsonModel toAuthorModel(Author author) {
        AuthorJsonModel authorModel = AuthorJsonModel.builder()
                .id(author.getId().toString())
                .name(author.getName())
                .email(author.getEmail()).build();

        return authorModel;
    }
}
