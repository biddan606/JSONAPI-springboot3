package dev.biddan.jsonapiexample.feature.author.get.one;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;

import dev.biddan.jsonapiexample.domain.author.Author;
import dev.biddan.jsonapiexample.feature.author.AuthorJsonModelAssembler;
import dev.biddan.jsonapiexample.persistence.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
public class GetAuthorEndPoint {

    private final AuthorRepository authorRepository;
    private final AuthorJsonModelAssembler authorJsonModelAssembler;

    @GetMapping(path = "/authors/{authorId}", produces = JSON_API_VALUE)
    public ResponseEntity<RepresentationModel<?>> getAuthor(@PathVariable("authorId") String authorId) {
        Author foundAuthor = authorRepository.findById(Long.valueOf(authorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author를 찾지 못했습니다."));

        return ResponseEntity.status(HttpStatus.OK)
                .body(authorJsonModelAssembler.toJsonApiModel(foundAuthor));
    }
}
