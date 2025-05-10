package dev.biddan.jsonapiexample.feature.article.create;

import dev.biddan.jsonapiexample.domain.article.Article;
import dev.biddan.jsonapiexample.domain.author.Author;
import dev.biddan.jsonapiexample.domain.category.Category;
import dev.biddan.jsonapiexample.domain.tag.Tag;
import dev.biddan.jsonapiexample.persistence.ArticleRepository;
import dev.biddan.jsonapiexample.persistence.AuthorRepository;
import dev.biddan.jsonapiexample.persistence.CategoryRepository;
import dev.biddan.jsonapiexample.persistence.TagRepository;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class CreateArticleHandler {

    private final ArticleRepository articleRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    @Transactional
    public Article createArticle(CreateArticleCommand command) {
        // Find author if provided
        Author author = null;
        if (command.authorId() != null) {
            author = authorRepository.findById(command.authorId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));
        }

        // Find category if provided
        Category category = null;
        if (command.categoryId() != null) {
            category = categoryRepository.findById(command.categoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        }

        // Find tags if provided
        Set<Tag> tags = new HashSet<>();
        if (command.tagIds() != null && !command.tagIds().isEmpty()) {
            tags = command.tagIds().stream()
                    .map(tagId -> tagRepository.findById(tagId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found")))
                    .collect(Collectors.toSet());
        }

        // Create and save the article
        Article newArticle = Article.builder()
                .title(command.title())
                .content(command.content())
                .author(author)
                .category(category)
                .tags(tags)
                .build();

        return articleRepository.save(newArticle);
    }

    @Builder
    public record CreateArticleCommand(
            String title,
            String content,
            Long authorId,
            Long categoryId,
            Set<Long> tagIds
    ) {
    }
}
