package dev.biddan.jsonapiexample.jsonapi.category;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder;
import dev.biddan.jsonapiexample.domain.category.Category;
import dev.biddan.jsonapiexample.feature.category.get.GetCategoryEndPoint;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.stereotype.Component;

@Component
public class CategoryJsonModelAssembler {

    public RepresentationModel<?> toJsonApiModel(Category category) {
        CategoryJsonModel categoryModel = CategoryJsonModel.builder()
                .id(category.getId().toString())
                .name(category.getName()).build();

        Link selfLink = linkTo(methodOn(GetCategoryEndPoint.class).getCategory(categoryModel.getId()))
                .withSelfRel();

        return JsonApiModelBuilder.jsonApiModel()
                .model(categoryModel)
                .link(selfLink)
                .build();
    }
}
