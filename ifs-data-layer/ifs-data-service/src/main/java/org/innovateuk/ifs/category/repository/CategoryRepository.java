package org.innovateuk.ifs.category.repository;

import org.innovateuk.ifs.category.domain.Category;
import org.springframework.data.repository.CrudRepository;


/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface CategoryRepository extends CrudRepository<Category, Long> {

}
