package dev.biddan.jsonapiexample.jsonapi.tag;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder;
import dev.biddan.jsonapiexample.domain.tag.Tag;
import dev.biddan.jsonapiexample.feature.tag.get.GetTagEndPoint;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.stereotype.Component;

@Component
public class TagJsonModelAssembler {

    public RepresentationModel<?> toJsonApiModel(Tag tag) {
        TagJsonModel tagModel = new TagJsonModel(
                tag.getId().toString(),
                tag.getName()
        );

        Link selfLink = linkTo(methodOn(GetTagEndPoint.class).getTag(tagModel.getId()))
                .withSelfRel();

        return JsonApiModelBuilder.jsonApiModel()
                .model(tagModel)
                .link(selfLink)
                .build();
    }
}
