package org.innovateuk.ifs.category.repository;

import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface ResearchCategoryRepository extends CrudRepository<ResearchCategory, Long> {

    ResearchCategory findById(long id);

    ResearchCategory findByName(String name);

    List<ResearchCategory> findAll();

    List<ResearchCategory> findAllByOrderByPriorityAsc();
}
