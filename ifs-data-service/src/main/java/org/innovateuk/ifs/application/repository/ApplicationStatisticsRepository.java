package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.innovateuk.ifs.application.resource.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface ApplicationStatisticsRepository extends PagingAndSortingRepository<ApplicationStatistics, Long> {
    List<ApplicationStatistics> findByCompetitionAndApplicationStatusIn(long competitionId, Collection<ApplicationStatus> applicationStatusIds);

    @Query("SELECT a FROM ApplicationStatistics a WHERE a.competition = :compId " +
            "AND (a.applicationStatus IN :status) " +
            "AND (str(a.id) LIKE CONCAT('%', :filter, '%'))")
    Page<ApplicationStatistics> findByCompetitionAndApplicationStatusIn(@Param("compId") long competitionId,
                                                                        @Param("status") Collection<ApplicationStatus> applicationStatusIds,
                                                                        @Param("filter") String filter,
                                                                        Pageable pageable);
}
