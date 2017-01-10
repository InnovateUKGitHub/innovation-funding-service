package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface ApplicationStatisticsRepository extends PagingAndSortingRepository<ApplicationStatistics, Long> {
    List<ApplicationStatistics> findByCompetition(Long competitionId);
}
