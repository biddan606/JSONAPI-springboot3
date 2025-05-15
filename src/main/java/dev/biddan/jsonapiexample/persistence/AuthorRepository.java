package dev.biddan.jsonapiexample.persistence;

import dev.biddan.jsonapiexample.domain.author.Author;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    @Query("SELECT DISTINCT a FROM Author a WHERE a.id IN " +
            "(SELECT art.author.id FROM Article art WHERE art.id IN :articleIds) " +
            "OR a.id IN " +
            "(SELECT rev.id FROM Article art JOIN art.reviewers rev WHERE art.id IN :articleIds)")
    Set<Author> findDistinctByArticleIdIn(@Param("articleIds") List<Long> articleIds);
}
