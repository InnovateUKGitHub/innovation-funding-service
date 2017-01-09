package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ApplicationStatisticsRepository extends PagingAndSortingRepository<ApplicationStatistics, Long> {
    List<ApplicationStatistics> findByCompetition(Long competitionId);
}
