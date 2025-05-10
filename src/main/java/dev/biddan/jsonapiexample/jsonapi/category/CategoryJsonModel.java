package dev.biddan.jsonapiexample.jsonapi.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@JsonApiTypeForClass("categories")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryJsonModel {

    @JsonApiId
    private String id;

    private String name;

    @Builder
    public CategoryJsonModel(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
