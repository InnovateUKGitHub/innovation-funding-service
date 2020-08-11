package org.innovateuk.ifs.organisation.repository;

import org.innovateuk.ifs.organisation.domain.Academic;
import org.innovateuk.ifs.organisation.domain.KnowledgeBase;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface KnowledgeBaseRepository extends CrudRepository<KnowledgeBase, Long> {
}
