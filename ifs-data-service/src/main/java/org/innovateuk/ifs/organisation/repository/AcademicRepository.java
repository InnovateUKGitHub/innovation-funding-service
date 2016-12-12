package org.innovateuk.ifs.organisation.repository;

import org.innovateuk.ifs.organisation.domain.Academic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface AcademicRepository extends PagingAndSortingRepository<Academic, Long> {
    Academic findById(Long id);
    List<Academic> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
