package dev.biddan.jsonapiexample.persistence;

import dev.biddan.jsonapiexample.domain.category.Category;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT DISTINCT c FROM Category c JOIN Article a ON c.id = a.category.id WHERE a.id IN :articleIds")
    Set<Category> findDistinctByArticleIdIn(@Param("articleIds") List<Long> articleIds);
}
