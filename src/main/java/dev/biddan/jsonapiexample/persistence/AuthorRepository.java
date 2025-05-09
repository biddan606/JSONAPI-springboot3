package dev.biddan.jsonapiexample.persistence;

import dev.biddan.jsonapiexample.domain.author.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {

}
