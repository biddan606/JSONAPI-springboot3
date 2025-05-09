package dev.biddan.jsonapiexample.feature.author;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder;
import dev.biddan.jsonapiexample.domain.author.Author;
import dev.biddan.jsonapiexample.feature.author.get.one.GetAuthorEndPoint;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.stereotype.Component;

@Component
public class AuthorJsonModelAssembler {

    public RepresentationModel<?> toJsonApiModel(Author author) {
        AuthorJsonModel authorModel = AuthorJsonModel.builder()
                .id(author.getId().toString())
                .name(author.getName())
                .email(author.getEmail()).build();

        Link selfLink = linkTo(methodOn(GetAuthorEndPoint.class).getAuthor(authorModel.getId()))
                .withSelfRel();

        return JsonApiModelBuilder.jsonApiModel()
                .model(authorModel)
                .link(selfLink)
                .build();
    }
}
