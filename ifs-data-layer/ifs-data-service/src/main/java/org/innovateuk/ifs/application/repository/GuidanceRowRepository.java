package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.GuidanceRow;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface GuidanceRowRepository extends CrudRepository<GuidanceRow, Long> {
    List<GuidanceRow> findByFormInputId(Long formInputId);
}
