package com.worth.ifs.user.repository;

import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface OrganisationRepository extends PagingAndSortingRepository<Organisation, Long> {
    Organisation findByProcessRoles(@Param("processRoles") ProcessRole processRole);

    Organisation findOneByName(String name);
}
