package org.innovateuk.ifs.form.repository;

import org.innovateuk.ifs.form.domain.GuidanceRow;
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
