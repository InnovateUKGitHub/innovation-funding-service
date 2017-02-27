package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface ApplicationStatisticsRepository extends PagingAndSortingRepository<ApplicationStatistics, Long> {
    List<ApplicationStatistics> findByCompetition(Long competitionId);

    @Query("SELECT a FROM ApplicationStatistics a WHERE a.competition = :compId " +
            "AND (str(a.id) LIKE CONCAT('%', :filter, '%'))")
    Page<ApplicationStatistics> findByCompetition(@Param("compId") long competitionId,
                                                  @Param("filter") String filter,
                                                  Pageable pageable);
}
