package dev.biddan.jsonapiexample.feature.author.create;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;

import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import dev.biddan.jsonapiexample.domain.author.Author;
import dev.biddan.jsonapiexample.jsonapi.author.AuthorJsonModelAssembler;
import dev.biddan.jsonapiexample.feature.author.create.CreateAuthorHandler.CreateAuthorCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.net.URI;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class CreateAuthorEndPoint {

    private final CreateAuthorHandler createAuthorHandler;
    private final AuthorJsonModelAssembler authorJsonModelAssembler;

    @PostMapping(path = "/authors", produces = JSON_API_VALUE, consumes = JSON_API_VALUE)
    public ResponseEntity<RepresentationModel<?>> createAuthor(
            @Valid @RequestBody EntityModel<CreateAuthorRequest> requestModel) {
        CreateAuthorRequest content = Objects.requireNonNull(requestModel.getContent());
        CreateAuthorCommand command = CreateAuthorCommand.builder()
                .name(content.getName())
                .email(content.getEmail()).build();

        Author createdAuthor = createAuthorHandler.createAuthor(command);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdAuthor.getId())
                .toUri();

        RepresentationModel<?> authorModel = authorJsonModelAssembler.toJsonApiModel(createdAuthor);

        return ResponseEntity.status(HttpStatus.CREATED)
                .location(location)
                .body(authorModel);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateAuthorRequest {
        @JsonApiId
        private String id; // 라이브러리 요구사항으로 유지

        @NotBlank(message = "이름은 필수 입력값입니다")
        private String name;

        @NotBlank(message = "이메일은 필수 입력값입니다")
        @Email(message = "유효한 이메일 형식이어야 합니다")
        private String email;

        @Builder
        public CreateAuthorRequest(String name, String email) {
            this.name = name;
            this.email = email;
        }
    }
}
