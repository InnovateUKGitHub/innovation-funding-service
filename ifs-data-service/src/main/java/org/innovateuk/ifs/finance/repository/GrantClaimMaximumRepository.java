package org.innovateuk.ifs.finance.repository;

import org.innovateuk.ifs.finance.domain.GrantClaimMaximum;
import org.springframework.data.repository.CrudRepository;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface GrantClaimMaximumRepository extends CrudRepository<GrantClaimMaximum, Long> {
}
