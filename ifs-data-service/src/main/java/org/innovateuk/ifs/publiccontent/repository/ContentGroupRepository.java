package org.innovateuk.ifs.publiccontent.repository;

import org.innovateuk.ifs.publiccontent.domain.ContentGroup;
import org.springframework.data.repository.CrudRepository;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface ContentGroupRepository extends CrudRepository<ContentGroup, Long> {
}
