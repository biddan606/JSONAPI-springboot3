package dev.biddan.jsonapiexample.feature.article.update;

import dev.biddan.jsonapiexample.domain.article.Article;
import dev.biddan.jsonapiexample.domain.author.Author;
import dev.biddan.jsonapiexample.domain.category.Category;
import dev.biddan.jsonapiexample.domain.tag.Tag;
import dev.biddan.jsonapiexample.persistence.ArticleRepository;
import dev.biddan.jsonapiexample.persistence.AuthorRepository;
import dev.biddan.jsonapiexample.persistence.CategoryRepository;
import dev.biddan.jsonapiexample.persistence.TagRepository;
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
public class UpdateArticleHandler {

    private final ArticleRepository articleRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    @Transactional
    public Article updateArticle(UpdateArticleCommand command) {
        // 수정할 게시물 조회
        Article article = articleRepository.findById(command.articleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));

        // 제공된 속성이 있으면 업데이트
        if (command.title() != null) {
            article.setTitle(command.title());
        }

        if (command.content() != null) {
            article.setContent(command.content());
        }

        // 작성자 관계 업데이트 (제공된 경우)
        if (command.authorId() != null) {
            Author author = authorRepository.findById(command.authorId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));
            article.setAuthor(author);
        }

        // 카테고리 관계 업데이트 (제공된 경우)
        if (command.categoryId() != null) {
            Category category = categoryRepository.findById(command.categoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
            article.setCategory(category);
        }

        // 태그 관계 업데이트 (제공된 경우)
        if (command.tagIds() != null) {
            Set<Tag> tags = command.tagIds().stream()
                    .map(tagId -> tagRepository.findById(tagId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found")))
                    .collect(Collectors.toSet());
            article.setTags(tags);
        }

        return articleRepository.save(article);
    }

    @Builder
    public record UpdateArticleCommand(
            Long articleId,
            String title,
            String content,
            Long authorId,
            Long categoryId,
            Set<Long> tagIds
    ) {
    }
}
