package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.PublicContent;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface PublicContentRepository extends PagingAndSortingRepository<PublicContent, Long> {

    Competition findByCompetitionId(Long id);
}
