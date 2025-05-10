package dev.biddan.jsonapiexample.persistence;

import dev.biddan.jsonapiexample.domain.tag.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {

}
