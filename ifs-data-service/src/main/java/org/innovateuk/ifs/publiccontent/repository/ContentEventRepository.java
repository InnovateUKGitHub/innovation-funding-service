package org.innovateuk.ifs.publiccontent.repository;

import org.innovateuk.ifs.publiccontent.domain.ContentEvent;
import org.springframework.data.repository.CrudRepository;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface ContentEventRepository extends CrudRepository<ContentEvent, Long> {
    Long deleteByPublicContentId(Long publicContentId);
}
