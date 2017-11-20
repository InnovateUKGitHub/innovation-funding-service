package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionOpenQueryResource;
import org.innovateuk.ifs.competition.resource.SpendProfileStatusResource;
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

    String LIVE_QUERY_WHERE_CLAUSE = "WHERE CURRENT_TIMESTAMP >= " +
            "(SELECT m.date FROM Milestone m WHERE m.type = 'OPEN_DATE' AND m.competition.id = c.id) AND " +
            "NOT EXISTS (SELECT m.date FROM Milestone m WHERE m.type = 'FEEDBACK_RELEASED' AND m.competition.id = c.id) AND " +
            "c.setupComplete = TRUE AND c.template = FALSE AND c.nonIfs = FALSE";

    String LIVE_QUERY = "SELECT c FROM Competition c " + LIVE_QUERY_WHERE_CLAUSE;

    String LIVE_COUNT_QUERY = "SELECT COUNT(c) FROM Competition c " + LIVE_QUERY_WHERE_CLAUSE;

    String INNOVATION_LEAD_LIVE_COUNT_QUERY = "SELECT count(distinct cp.competition.id) " +
            "FROM CompetitionAssessmentParticipant cp " +
            "WHERE cp.user.id = :userId " +
            "AND cp.role = 'INNOVATION_LEAD' " +
            "AND CURRENT_TIMESTAMP >= (SELECT m.date FROM Milestone m WHERE m.type = 'OPEN_DATE' AND m.competition.id = cp.competition.id) " +
            "AND NOT EXISTS (SELECT m.date FROM Milestone m WHERE m.type = 'FEEDBACK_RELEASED' AND m.competition.id = cp.competition.id) " +
            "AND cp.competition.setupComplete = TRUE AND cp.competition.template = FALSE AND cp.competition.nonIfs = FALSE";

    // Assume competition cannot be in project setup until at least one application is funded and informed
    String PROJECT_SETUP_WHERE_CLAUSE = "WHERE ( " +
            "EXISTS (SELECT a.manageFundingEmailDate  FROM Application a WHERE a.competition.id = c.id AND a.fundingDecision = 'FUNDED' AND a.manageFundingEmailDate IS NOT NULL) " +
            ") AND c.setupComplete = TRUE AND c.template = FALSE AND c.nonIfs = FALSE";

    String PROJECT_SETUP_QUERY = "SELECT c FROM Competition c " + PROJECT_SETUP_WHERE_CLAUSE;

    String PROJECT_SETUP_COUNT_QUERY = "SELECT COUNT(c) FROM Competition c " + PROJECT_SETUP_WHERE_CLAUSE;

    String INNOVATION_LEAD_PROJECT_SETUP_WHERE_CLAUSE = "WHERE cp.user.id = :userId " +
            "AND cp.role = 'INNOVATION_LEAD' " +
            "AND EXISTS (SELECT a.manageFundingEmailDate  FROM Application a WHERE a.competition.id = cp.competition.id AND a.fundingDecision = 'FUNDED' AND a.manageFundingEmailDate IS NOT NULL) " +
            "AND cp.competition.setupComplete = TRUE AND cp.competition.template = FALSE AND cp.competition.nonIfs = FALSE";

    String INNOVATION_LEAD_PROJECT_SETUP_QUERY = "SELECT cp.competition FROM CompetitionAssessmentParticipant cp " + INNOVATION_LEAD_PROJECT_SETUP_WHERE_CLAUSE;

    String INNOVATION_LEAD_PROJECT_SETUP_COUNT_QUERY = "SELECT count(distinct cp.competition.id) FROM CompetitionAssessmentParticipant cp " + INNOVATION_LEAD_PROJECT_SETUP_WHERE_CLAUSE;

    /* Innovation leads should not access competitions in states: In preparation and Ready to open */
    String SEARCH_QUERY_LEAD_TECHNOLOGIST = "SELECT cp.competition FROM CompetitionAssessmentParticipant cp LEFT JOIN cp.competition.milestones m LEFT JOIN cp.competition.competitionType ct " +
            "WHERE (m.type = 'OPEN_DATE' AND m.date < NOW()) AND (cp.competition.name LIKE :searchQuery OR ct.name LIKE :searchQuery) AND cp.competition.template = FALSE AND cp.competition.nonIfs = FALSE " +
            "AND (cp.competition.setupComplete IS NOT NULL AND cp.competition.setupComplete != FALSE) " +
            "AND cp.user.id = :userId " +
            "ORDER BY m.date";

    String UPCOMING_CRITERIA = "FROM Competition c WHERE (CURRENT_TIMESTAMP <= " +
            "(SELECT m.date FROM Milestone m WHERE m.type = 'OPEN_DATE' AND m.competition.id = c.id) AND c.setupComplete = TRUE) OR " +
            "c.setupComplete = FALSE AND c.template = FALSE AND c.nonIfs = FALSE";

    String UPCOMING_QUERY = "SELECT c " + UPCOMING_CRITERIA;

    String UPCOMING_COUNT_QUERY = "SELECT count(c) " + UPCOMING_CRITERIA;

    String SEARCH_QUERY = "SELECT c FROM Competition c LEFT JOIN c.milestones m LEFT JOIN c.competitionType ct " +
            "WHERE (m.type = 'OPEN_DATE' OR m.type IS NULL) AND (c.name LIKE :searchQuery OR ct.name LIKE :searchQuery) AND c.template = FALSE AND c.nonIfs = FALSE " +
            "ORDER BY m.date";

    /* Support users should not be able to access competitions in preparation */
    String SEARCH_QUERY_SUPPORT_USER = "SELECT c FROM Competition c LEFT JOIN c.milestones m LEFT JOIN c.competitionType ct " +
            "WHERE (m.type = 'OPEN_DATE' OR m.type IS NULL) AND (c.name LIKE :searchQuery OR ct.name LIKE :searchQuery) AND c.template = FALSE AND c.nonIfs = FALSE " +
            "AND (c.setupComplete IS NOT NULL AND c.setupComplete != FALSE) " +
            "ORDER BY m.date";

    String NON_IFS_QUERY = "SELECT c FROM Competition c WHERE nonIfs = TRUE";

    String NON_IFS_COUNT_QUERY = "SELECT count(c) FROM Competition c WHERE nonIfs = TRUE";

    String FEEDBACK_RELEASED_QUERY = "SELECT c FROM Competition c WHERE " +
            "EXISTS (SELECT m.date FROM Milestone m WHERE m.type = 'FEEDBACK_RELEASED' and m.competition.id = c.id) AND " +
            "c.setupComplete = TRUE AND c.template = FALSE AND c.nonIfs = FALSE";

    String FEEDBACK_RELEASED_COUNT_QUERY = "SELECT COUNT(c) FROM Competition c WHERE " +
            "CURRENT_TIMESTAMP >= (SELECT m.date FROM Milestone m WHERE m.type = 'FEEDBACK_RELEASED' and m.competition.id = c.id) AND " +
            "c.setupComplete = TRUE AND c.template = FALSE AND c.nonIfs = FALSE";

    public static final String OPEN_QUERIES_WHERE_CLAUSE = "WHERE t.className = 'org.innovateuk.ifs.finance.domain.ProjectFinance' " +
            "AND TYPE(t) = Query " +
            "AND 0 = (SELECT COUNT(id) FROM u.roles r WHERE r.name = 'project_finance') " +
            "AND a.competition.id = :competitionId " +
            "AND (post.thread.id, post.createdOn) IN ( " +
            "    SELECT p.thread.id, MAX(p.createdOn) " +
            "    FROM Post p " +
            "    WHERE p.thread.id = t.id " +
            "    GROUP BY p.thread.id) "  +
            "AND NOT EXISTS (SELECT id FROM SpendProfileProcess sp WHERE sp.target.id = pr.id AND sp.event != 'project-created') ";

    public static final String COUNT_OPEN_QUERIES = "SELECT COUNT(DISTINCT t.classPk) " +
            "FROM Post post " +
            "JOIN post.thread t " +
            "JOIN post.author u " +
            "JOIN ProjectFinance pf ON pf.id = t.classPk " +
            "JOIN pf.project pr " +
            "JOIN pr.application a " +
            OPEN_QUERIES_WHERE_CLAUSE;

    public static final String GET_OPEN_QUERIES = "SELECT NEW org.innovateuk.ifs.competition.resource.CompetitionOpenQueryResource(pr.application.id, o.id, o.name, pr.id, pr.name) " +
            "FROM Post post " +
            "JOIN post.thread t " +
            "JOIN post.author u " +
            "JOIN ProjectFinance pf ON pf.id = t.classPk " +
            "JOIN pf.project pr " +
            "JOIN pr.application a " +
            "JOIN pf.organisation o " +
            OPEN_QUERIES_WHERE_CLAUSE +
            "GROUP BY pr.application.id, o.id, pr.id " +
            "ORDER BY pr.application.id, o.name";

    public static final String PENDING_SPEND_PROFILES_WHERE_CLAUSE = " WHERE c.id = :competitionId " +
            " AND NOT EXISTS (SELECT v.id FROM ViabilityProcess v WHERE v.target.id IN (SELECT po.id FROM PartnerOrganisation po WHERE po.project.id = p.id) AND " +
            "   v.event = 'project-created' )" +
            " AND NOT EXISTS (SELECT e.id FROM EligibilityProcess e WHERE e.target.id IN (SELECT po.id FROM PartnerOrganisation po WHERE po.project.id = p.id) AND " +
            "   e.event = 'project-created' )" +
            " AND NOT EXISTS (SELECT sp.id FROM SpendProfile sp WHERE sp.project.id = p.id) ";

    public static final String GET_PENDING_SPEND_PROFILES = "SELECT NEW org.innovateuk.ifs.competition.resource.SpendProfileStatusResource(" +
            "p.application.id, p.id, p.name) " +
            " FROM Project p " +
            " JOIN p.application.competition c " +
            PENDING_SPEND_PROFILES_WHERE_CLAUSE;

    public static final String COUNT_PENDING_SPEND_PROFILES = "SELECT COUNT(DISTINCT p.id)" +
            " FROM Project p " +
            " JOIN p.application.competition c " +
            PENDING_SPEND_PROFILES_WHERE_CLAUSE;


    @Query(LIVE_QUERY)
    List<Competition> findLive();

    @Query(INNOVATION_LEAD_LIVE_COUNT_QUERY)
    Long countLiveForInnovationLead(@Param("userId") Long userId);

    @Query(LIVE_COUNT_QUERY)
    Long countLive();

    @Query(PROJECT_SETUP_QUERY)
    List<Competition> findProjectSetup();

    @Query(INNOVATION_LEAD_PROJECT_SETUP_QUERY)
    List<Competition> findProjectSetupForInnovationLead(@Param("userId") Long userId);

    @Query(PROJECT_SETUP_COUNT_QUERY)
    Long countProjectSetup();

    @Query(INNOVATION_LEAD_PROJECT_SETUP_COUNT_QUERY)
    Long countProjectSetupForInnovationLead(@Param("userId") Long userId);

    @Query(UPCOMING_QUERY)
    List<Competition> findUpcoming();

    @Query(UPCOMING_COUNT_QUERY)
    Long countUpcoming();

    @Query(NON_IFS_QUERY)
    List<Competition> findNonIfs();

    @Query(NON_IFS_COUNT_QUERY)
    Long countNonIfs();

    @Query(SEARCH_QUERY)
    Page<Competition> search(@Param("searchQuery") String searchQuery, Pageable pageable);

    @Query(SEARCH_QUERY_LEAD_TECHNOLOGIST)
    Page<Competition> searchForLeadTechnologist(@Param("searchQuery") String searchQuery, @Param("userId") Long userId, Pageable pageable);

    @Query(SEARCH_QUERY_SUPPORT_USER)
    Page<Competition> searchForSupportUser(@Param("searchQuery") String searchQuery, Pageable pageable);

    List<Competition> findByName(String name);

    Competition findById(Long id);

    List<Competition> findByIdIsIn(List<Long> ids);

    @Override
    List<Competition> findAll();

    List<Competition> findByCodeLike(String code);

    List<Competition> findByInnovationSectorCategoryId(Long id);

    @Query(FEEDBACK_RELEASED_QUERY)
    List<Competition> findFeedbackReleased();

    @Query(FEEDBACK_RELEASED_COUNT_QUERY)
    Long countFeedbackReleased();

    @Query(COUNT_OPEN_QUERIES)
    Long countOpenQueries(@Param("competitionId") Long competitionId);

    @Query(GET_OPEN_QUERIES)
    List<CompetitionOpenQueryResource> getOpenQueryByCompetition(@Param("competitionId") long competitionId);

    @Query(GET_PENDING_SPEND_PROFILES)
    List<SpendProfileStatusResource> getPendingSpendProfiles(@Param("competitionId") long competitionId);

    @Query(COUNT_PENDING_SPEND_PROFILES)
    Long countPendingSpendProfiles(@Param("competitionId") Long competitionId);

}
