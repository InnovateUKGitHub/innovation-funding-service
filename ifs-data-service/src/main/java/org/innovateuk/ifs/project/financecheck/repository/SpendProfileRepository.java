package org.innovateuk.ifs.project.financecheck.repository;

import org.innovateuk.ifs.project.financecheck.domain.SpendProfile;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;
import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface SpendProfileRepository extends PagingAndSortingRepository<SpendProfile, Long> {
    Optional<SpendProfile> findOneByProjectIdAndOrganisationId(Long projectId, Long organisationId);
    List<SpendProfile> findByProjectId(Long projectId);
}
