package dev.biddan.jsonapiexample.feature.tag.get;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;

import dev.biddan.jsonapiexample.domain.tag.Tag;
import dev.biddan.jsonapiexample.jsonapi.tag.TagJsonModelAssembler;
import dev.biddan.jsonapiexample.persistence.TagRepository;
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
public class GetTagEndPoint {

    private final TagRepository tagRepository;
    private final TagJsonModelAssembler tagJsonModelAssembler;

    @GetMapping(path = "/tags/{id}", produces = JSON_API_VALUE)
    public ResponseEntity<RepresentationModel<?>> getTag(@PathVariable String id) {
        Tag tag = tagRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found"));

        RepresentationModel<?> tagModel = tagJsonModelAssembler.toJsonApiModel(tag);

        return ResponseEntity.ok(tagModel);
    }
}
