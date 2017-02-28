package org.innovateuk.ifs.category.repository;

import org.innovateuk.ifs.category.domain.InnovationArea;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface InnovationAreaRepository extends CrudRepository<InnovationArea, Long> {

    InnovationArea findByName(String name);

    List<InnovationArea> findAllByOrderByPriorityAsc();

    List<InnovationArea> findAll();
}
