package dev.biddan.jsonapiexample.persistence;

import dev.biddan.jsonapiexample.domain.article.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {

}
