package org.innovateuk.ifs.organisation.repository;

import org.innovateuk.ifs.organisation.domain.SimpleOrganisation;
import org.springframework.data.repository.CrudRepository;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface SimpleOrganisationRepository extends CrudRepository<SimpleOrganisation, Long> {
}
