package dev.biddan.jsonapiexample.persistence;

import dev.biddan.jsonapiexample.domain.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
