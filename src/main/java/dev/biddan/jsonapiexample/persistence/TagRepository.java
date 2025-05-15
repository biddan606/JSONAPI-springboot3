package dev.biddan.jsonapiexample.persistence;

import dev.biddan.jsonapiexample.domain.tag.Tag;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query("SELECT DISTINCT t FROM Article a JOIN a.tags t WHERE a.id IN :articleIds")
    Set<Tag> findDistinctByArticleIdIn(@Param("articleIds") List<Long> articleIds);
}
