package org.innovateuk.ifs.finance.repository;

import org.innovateuk.ifs.finance.domain.OrganisationSize;
import org.springframework.data.repository.CrudRepository;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface OrganisationSizeRepository extends CrudRepository<OrganisationSize, Long> {
}
