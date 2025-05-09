package dev.biddan.jsonapiexample.feature.author.create;

import dev.biddan.jsonapiexample.domain.author.Author;
import dev.biddan.jsonapiexample.persistence.AuthorRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CreateAuthorHandler {

    private final AuthorRepository authorRepository;

    @Transactional
    public Author createAuthor(CreateAuthorCommand command) {
        Author newAuthor = Author.builder()
                .name(command.name())
                .email(command.email()).build();

        return authorRepository.save(newAuthor);
    }

    @Builder
    public record CreateAuthorCommand(
            String name,
            String email
    ) {

    }
}
