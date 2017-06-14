package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.AssessorStatistics;
import org.innovateuk.ifs.workflow.resource.State;
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
public interface AssessorStatisticsRepository extends PagingAndSortingRepository<AssessorStatistics, Long> {

    String APPLICATION_FILTER = "SELECT a FROM ApplicationStatistics a WHERE a.competition = :compId " +
            "AND (a.applicationProcess.activityState.state IN :states) " +
            "AND (str(a.id) LIKE CONCAT('%', :filter, '%'))";

    List<AssessorStatistics> findByCompetitionAndApplicationProcessActivityStateStateIn(long competitionId, Collection<State> applicationStates);

    @Query(APPLICATION_FILTER)
    Page<AssessorStatistics> findByCompetitionAndApplicationProcessActivityStateStateIn(@Param("compId") long competitionId,
                                                                                           @Param("states") Collection<State> applicationStates,
                                                                                           @Param("filter") String filter,
                                                                                           Pageable pageable);
}