package com.worth.ifs.competition.repository;

import com.worth.ifs.competition.domain.Competition;
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
public interface CompetitionRepository extends PagingAndSortingRepository<Competition, Long> {

    public static final String LIVE_QUERY = "SELECT c FROM Competition c WHERE CURRENT_TIMESTAMP BETWEEN " +
            "(SELECT m.date FROM Milestone m WHERE m.type = 'OPEN_DATE' AND m.competition.id = c.id) AND " +
            "(SELECT m.date FROM Milestone m WHERE m.type = 'ASSESSOR_DEADLINE' AND m.competition.id = c.id) AND " +
            "c.setupComplete = TRUE AND c.template = FALSE";

    public static final String LIVE_COUNT_QUERY = "SELECT COUNT(c) FROM Competition c WHERE CURRENT_TIMESTAMP BETWEEN " +
            "(SELECT m.date FROM Milestone m WHERE m.type = 'OPEN_DATE' AND m.competition.id = c.id) AND " +
            "(SELECT m.date FROM Milestone m WHERE m.type = 'ASSESSOR_DEADLINE' AND m.competition.id = c.id) AND " +
            "c.setupComplete = TRUE AND c.template = FALSE";

    public static final String PROJECT_SETUP_QUERY = "SELECT c FROM Competition c WHERE CURRENT_TIMESTAMP >= " +
            "(SELECT m.date FROM Milestone m WHERE m.type = 'ASSESSOR_DEADLINE' and m.competition.id = c.id) AND " +
            "c.setupComplete = TRUE AND c.template = FALSE";

    public static final String PROJECT_SETUP_COUNT_QUERY = "SELECT COUNT(c) FROM Competition c WHERE " +
            "CURRENT_TIMESTAMP >= (SELECT m.date FROM Milestone m WHERE m.type = 'ASSESSOR_DEADLINE' and m.competition.id = c.id) AND " +
            "c.setupComplete = TRUE AND c.template = FALSE";

    public static final String UPCOMING_QUERY = "SELECT c FROM Competition c WHERE (CURRENT_TIMESTAMP <= " +
            "(SELECT m.date FROM Milestone m WHERE m.type = 'OPEN_DATE' AND m.competition.id = c.id) AND c.setupComplete = TRUE) OR " +
            "c.setupComplete = FALSE AND c.template = FALSE";

    public static final String UPCOMING_COUNT_QUERY = "SELECT count(c) FROM Competition c WHERE (CURRENT_TIMESTAMP <= " +
            "(SELECT m.date FROM Milestone m WHERE m.type = 'OPEN_DATE' AND m.competition.id = c.id) AND c.setupComplete = TRUE) OR " +
            "c.setupComplete = FALSE AND c.template = FALSE";

    public static final String SEARCH_QUERY = "SELECT c FROM Competition c LEFT JOIN c.milestones m LEFT JOIN c.competitionType ct " +
            "WHERE (m.type = 'OPEN_DATE' OR m.type IS NULL) AND (c.name LIKE :searchQuery OR ct.name LIKE :searchQuery) AND c.template = FALSE " +
            "ORDER BY m.date";

    @Query(LIVE_QUERY)
    List<Competition> findLive();

    @Query(LIVE_COUNT_QUERY)
    Long countLive();

    @Query(PROJECT_SETUP_QUERY)
    List<Competition> findProjectSetup();

    @Query(PROJECT_SETUP_COUNT_QUERY)
    Long countProjectSetup();

    @Query(UPCOMING_QUERY)
    List<Competition> findUpcoming();

    @Query(UPCOMING_COUNT_QUERY)
    Long countUpcoming();

    @Query(SEARCH_QUERY)
    Page<Competition> search(@Param("searchQuery") String searchQuery, Pageable pageable);

    List<Competition> findByName(String name);

    Competition findById(Long id);

    @Override
    List<Competition> findAll();

    List<Competition> findByCodeLike(String code);

    Competition findByTemplateForTypeId(Long id);
}
