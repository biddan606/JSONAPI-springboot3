package dev.biddan.jsonapiexample.persistence;

import dev.biddan.jsonapiexample.domain.article.Article;
import dev.biddan.jsonapiexample.domain.tag.Tag;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    // 단일 엔티티 조회 - 주요 관계(author, category)만 JOIN
    @Query("SELECT a FROM Article a " +
            "LEFT JOIN FETCH a.author " +
            "LEFT JOIN FETCH a.category " +
            "WHERE a.id = :id")
    Optional<Article> findByIdWithCommonRelations(@Param("id") Long id);

    // 컬렉션 조회 - 페이징을 위해 카운트 쿼리 분리, 주요 관계(author, category)만 JOIN
    @Query(value = "SELECT a FROM Article a " +
            "LEFT JOIN FETCH a.author " +
            "LEFT JOIN FETCH a.category",
            countQuery = "SELECT COUNT(a) FROM Article a")
    Page<Article> findAllWithCommonRelations(Pageable pageable);

    @Query("SELECT t FROM Article a JOIN a.tags t WHERE a.id = :articleId")
    Set<Tag> findTagsByArticleId(@Param("articleId") Long articleId);
}
