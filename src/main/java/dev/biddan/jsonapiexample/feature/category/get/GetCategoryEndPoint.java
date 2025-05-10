package dev.biddan.jsonapiexample.feature.category.get;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;

import dev.biddan.jsonapiexample.domain.category.Category;
import dev.biddan.jsonapiexample.jsonapi.category.CategoryJsonModelAssembler;
import dev.biddan.jsonapiexample.persistence.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
public class GetCategoryEndPoint {

    private final CategoryRepository categoryRepository;
    private final CategoryJsonModelAssembler categoryJsonModelAssembler;

    @GetMapping(path = "/categories/{id}", produces = JSON_API_VALUE)
    public ResponseEntity<RepresentationModel<?>> getCategory(@PathVariable String id) {
        Category category = categoryRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        RepresentationModel<?> categoryModel = categoryJsonModelAssembler.toJsonApiModel(category);

        return ResponseEntity.ok(categoryModel);
    }


}
