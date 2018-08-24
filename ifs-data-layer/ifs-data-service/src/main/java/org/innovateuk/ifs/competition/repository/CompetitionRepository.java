package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionOpenQueryResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface CompetitionRepository extends PagingAndSortingRepository<Competition, Long> {

    String EOI_COMPETITION_TYPE = "'Expression of interest'";

    /* Filters competitions to those in live state */
    String LIVE_QUERY_WHERE_CLAUSE = "WHERE CURRENT_TIMESTAMP >= " +
            "(SELECT m.date FROM Milestone m WHERE m.type = 'OPEN_DATE' AND m.competition.id = c.id) AND " +
            "NOT EXISTS (SELECT m.date FROM Milestone m WHERE m.type = 'FEEDBACK_RELEASED' AND m.competition.id = c.id) AND " +
            "c.setupComplete = TRUE AND c.template = FALSE AND c.nonIfs = FALSE";

    /* Assume competition cannot be in project setup until at least one application is funded and informed
       Filters competitions to those in project setup state */
    String PROJECT_SETUP_WHERE_CLAUSE = "WHERE ( " +
            "EXISTS (SELECT a.manageFundingEmailDate  FROM Application a WHERE a.competition.id = c.id AND a.fundingDecision = 'FUNDED' AND a.manageFundingEmailDate IS NOT NULL) " +
            ") AND c.setupComplete = TRUE AND c.template = FALSE AND c.nonIfs = FALSE " +
            "AND ct.name != " + EOI_COMPETITION_TYPE;

    /* Filters competitions to those in upcoming state */
    String UPCOMING_CRITERIA = "FROM Competition c WHERE (CURRENT_TIMESTAMP <= " +
            "(SELECT m.date FROM Milestone m WHERE m.type = 'OPEN_DATE' AND m.competition.id = c.id) AND c.setupComplete = TRUE) OR " +
            "c.setupComplete = FALSE AND c.template = FALSE AND c.nonIfs = FALSE";

    /* Filters competitions to those in feedback released state */
    String FEEDBACK_RELEASED_WHERE_CLAUSE = "WHERE " +
            "CURRENT_TIMESTAMP >= (SELECT m.date FROM Milestone m WHERE m.type = 'FEEDBACK_RELEASED' and m.competition.id = c.id) AND " +
            "c.setupComplete = TRUE AND c.template = FALSE AND c.nonIfs = FALSE";

    /* Filters by innovation lead id or stakeholder id and in project setup state */
    String INNOVATION_LEAD_STAKEHOLDER_PROJECT_SETUP_WHERE_CLAUSE = "WHERE ap.user.id = :userId " +
            "AND ap.role in ('INNOVATION_LEAD', 'STAKEHOLDER') " +
            "AND EXISTS (SELECT a.manageFundingEmailDate  FROM Application a WHERE a.competition.id = ap.competition.id AND a.fundingDecision = 'FUNDED' AND a.manageFundingEmailDate IS NOT NULL) " +
            "AND ap.competition.setupComplete = TRUE AND ap.competition.template = FALSE AND ap.competition.nonIfs = FALSE";

    /* Filters by innovation lead or stakeholder and in feedback released state */
    String INNOVATION_LEAD_STAKEHOLDER_FEEDBACK_RELEASED_WHERE_CLAUSE = "WHERE ap.user.id = :userId AND " +
            "CURRENT_TIMESTAMP >= (SELECT m.date FROM Milestone m WHERE m.type = 'FEEDBACK_RELEASED' and m.competition.id = ap.competition.id) AND " +
            "ap.competition.setupComplete = TRUE AND ap.competition.template = FALSE AND ap.competition.nonIfs = FALSE";

    /* Filters by innovation lead or stakeholder and in live state */
    String INNOVATION_LEAD_STAKEHOLDER_LIVE_WHERE_CLAUSE = "WHERE ap.user.id = :userId " +
            "AND ap.role in ('INNOVATION_LEAD', 'STAKEHOLDER') " +
            "AND CURRENT_TIMESTAMP >= (SELECT m.date FROM Milestone m WHERE m.type = 'OPEN_DATE' AND m.competition.id = ap.competition.id) " +
            "AND NOT EXISTS (SELECT m.date FROM Milestone m WHERE m.type = 'FEEDBACK_RELEASED' AND m.competition.id = ap.competition.id) " +
            "AND ap.competition.setupComplete = TRUE AND ap.competition.template = FALSE AND ap.competition.nonIfs = FALSE";


    /* Innovation leads should not access competitions in states: In preparation and Ready to open */
    String SEARCH_QUERY_LEAD_TECHNOLOGIST = "SELECT ap.competition FROM AssessmentParticipant ap LEFT JOIN ap.competition.milestones m LEFT JOIN ap.competition.competitionType ct " +
            "WHERE (m.type = 'OPEN_DATE' AND m.date < NOW()) AND (ap.competition.name LIKE :searchQuery OR ct.name LIKE :searchQuery) AND ap.competition.template = FALSE AND ap.competition.nonIfs = FALSE " +
            "AND (ap.competition.setupComplete IS NOT NULL AND ap.competition.setupComplete != FALSE) " +
            "AND ap.user.id = :userId " +
            "ORDER BY m.date";

    /* Support users should not be able to access competitions in preparation */
    String SEARCH_QUERY_SUPPORT_USER = "SELECT c FROM Competition c LEFT JOIN c.milestones m LEFT JOIN c.competitionType ct " +
            "WHERE (m.type = 'OPEN_DATE' OR m.type IS NULL) AND (c.name LIKE :searchQuery OR ct.name LIKE :searchQuery) AND c.template = FALSE AND c.nonIfs = FALSE " +
            "AND (c.setupComplete IS NOT NULL AND c.setupComplete != FALSE) " +
            "ORDER BY m.date";


    String UPCOMING_QUERY = "SELECT c " + UPCOMING_CRITERIA;

    String UPCOMING_COUNT_QUERY = "SELECT count(c) " + UPCOMING_CRITERIA;

    String LIVE_QUERY = "SELECT c FROM Competition c " + LIVE_QUERY_WHERE_CLAUSE;

    String LIVE_COUNT_QUERY = "SELECT COUNT(c) FROM Competition c " + LIVE_QUERY_WHERE_CLAUSE;

    String INNOVATION_LEAD_STAKEHOLDER_LIVE_COUNT_QUERY = "SELECT count(distinct ap.competition.id) " + "FROM AssessmentParticipant ap " + INNOVATION_LEAD_STAKEHOLDER_LIVE_WHERE_CLAUSE;

    String PROJECT_SETUP_QUERY = "SELECT c FROM Competition c LEFT JOIN c.competitionType ct " + PROJECT_SETUP_WHERE_CLAUSE;

    String PROJECT_SETUP_COUNT_QUERY = "SELECT COUNT(c) FROM Competition c LEFT JOIN c.competitionType ct " + PROJECT_SETUP_WHERE_CLAUSE;

    String INNOVATION_LEAD_STAKEHOLDER_PROJECT_SETUP_QUERY = "SELECT ap.competition FROM AssessmentParticipant ap " + INNOVATION_LEAD_STAKEHOLDER_PROJECT_SETUP_WHERE_CLAUSE;

    String INNOVATION_LEAD_STAKEHOLDER_PROJECT_SETUP_COUNT_QUERY = "SELECT count(distinct ap.competition.id) FROM AssessmentParticipant ap " + INNOVATION_LEAD_STAKEHOLDER_PROJECT_SETUP_WHERE_CLAUSE;

    String FEEDBACK_RELEASED_QUERY = "SELECT c FROM Competition c " + FEEDBACK_RELEASED_WHERE_CLAUSE;

    String FEEDBACK_RELEASED_COUNT_QUERY = "SELECT COUNT(c) FROM Competition c " + FEEDBACK_RELEASED_WHERE_CLAUSE;

    String INNOVATION_LEAD_STAKEHOLDER_FEEDBACK_RELEASED_COUNT_QUERY = "SELECT count(distinct ap.competition.id) FROM AssessmentParticipant ap " + INNOVATION_LEAD_STAKEHOLDER_FEEDBACK_RELEASED_WHERE_CLAUSE;

    String NON_IFS_QUERY = "SELECT c FROM Competition c WHERE nonIfs = TRUE";

    String NON_IFS_COUNT_QUERY = "SELECT count(c) FROM Competition c WHERE nonIfs = TRUE";


    String SEARCH_QUERY = "SELECT c FROM Competition c LEFT JOIN c.milestones m LEFT JOIN c.competitionType ct " +
            "WHERE (m.type = 'OPEN_DATE' OR m.type IS NULL) AND (c.name LIKE :searchQuery OR ct.name LIKE :searchQuery) AND c.template = FALSE AND c.nonIfs = FALSE " +
            "ORDER BY m.date";

    String OPEN_QUERIES_WHERE_CLAUSE = "WHERE a.competition.id = :competitionId " +
            "AND pr.id = pp.target.id AND pp.activityState NOT IN :states " +
            "AND t.closedDate IS NULL " +
            "AND t.className = 'org.innovateuk.ifs.finance.domain.ProjectFinance' " +
            "AND TYPE(t) = Query " +
            "AND NOT EXISTS (SELECT 1 FROM u.roles r WHERE r = org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE) " +
            "AND (post.thread.id, post.createdOn) IN ( " +
            "    SELECT p.thread.id, MAX(p.createdOn) " +
            "    FROM Post p " +
            "    WHERE p.thread.id = t.id " +
            "    GROUP BY p.thread.id) "  +
            "AND NOT EXISTS (SELECT id FROM SpendProfileProcess sp WHERE sp.target.id = pr.id AND sp.event != 'project-created') ";

    String COUNT_OPEN_QUERIES = "SELECT COUNT(DISTINCT t.classPk) " +
            "FROM Post post " +
            "JOIN post.thread t " +
            "JOIN post.author u " +
            "JOIN ProjectFinance pf ON pf.id = t.classPk " +
            "JOIN pf.project pr " +
            "JOIN pr.application a, ProjectProcess pp " +
            OPEN_QUERIES_WHERE_CLAUSE;

    String GET_OPEN_QUERIES = "SELECT NEW org.innovateuk.ifs.competition.resource.CompetitionOpenQueryResource(pr.application.id, o.id, o.name, pr.id, pr.name) " +
            "FROM Post post " +
            "JOIN post.thread t " +
            "JOIN post.author u " +
            "JOIN ProjectFinance pf ON pf.id = t.classPk " +
            "JOIN pf.project pr " +
            "JOIN pr.application a " +
            "JOIN pf.organisation o, ProjectProcess pp " +
            OPEN_QUERIES_WHERE_CLAUSE +
            "GROUP BY pr.application.id, o.id, pr.id " +
            "ORDER BY pr.application.id, o.name";

    String GET_PENDING_SPEND_PROFILES_CRITERIA =
                    " from project p, application a, competition c, process pp " +
                    " where c.id = :competitionId " +
                    " and a.competition = c.id " +
                    " and p.application_id = a.id " +
                    " and pp.target_id = p.id and pp.process_type = 'ProjectProcess' " +
                    " and pp.activity_state_id not in (select id from activity_state where activity_type = 'PROJECT_SETUP' and state = 'WITHDRAWN') " +

                    // where all Viability is either Approved or Not Required
                    " and not exists (select v.id from process v " +
                                    " where v.process_type = 'ViabilityProcess' " +
                                    " and v.target_id in (select po.id from partner_organisation po " +
                                                        " where po.project_id = p.id " +
                                                        " ) " +
                                    " and v.event = 'project-created' " +
                                    " ) " +

                    // and where all Eligibility is either Approved or Not Required
                    " and not exists (select e.id from process e " +
                                    " where e.process_type = 'EligibilityProcess' " +
                                    " and e.target_id in (select po.id from partner_organisation po " +
                                                        " where po.project_id = p.id " +
                                                        " ) " +
                                    " and e.event = 'project-created' " +
                                    " ) " +

                    // and where Spend Profile is not yet generated
                    " and not exists (select sp.id from spend_profile sp " +
                                    " where sp.project_id = p.id) " +
                    " and p.id not in " +
                    " ( " +
                        " select result.project_id " +
                        " from " +
                        " ( " +
                                // This is selection of all organisations which are seeking funding. In other words,
                                // whose grant claim percentage is > 0
                                " select po.* " +
                                " from project p, partner_organisation po, application_finance af, finance_row fr " +
                                " where po.project_id = p.id " +
                                " and af.application_id = p.application_id " +
                                " and af.organisation_id = po.organisation_id " +
                                " and fr.target_id = af.id " +
                                " and fr.row_type = 'ApplicationFinanceRow' " +
                                " and fr.name = 'grant-claim' " +
                                " and fr.quantity > 0 " +

                                // and this has to be combined with
                                " union " +

                                // This is a selection of all organisations which are Research Organisations.
                                // Research Organisations always seek funding and their 'seeking funding' nature is not
                                // determined by the grant claim percentage used in the previous query.
                                " select po.* " +
                                " from project p, partner_organisation po, organisation o, organisation_type ot " +
                                " where po.project_id = p.id " +
                                " and o.id = po.organisation_id " +
                                " and ot.id = o.organisation_type_id " +
                                " and ot.name = 'Research' " +
                        " ) as result " +

                        // For the above selection of organisations, bank details are either expected to be manually approved
                        // or automatically approved via Experian
                        " where not exists (select bd.id " +
                                            " from bank_details bd " +
                                            " where bd.project_id = result.project_id " +
                                            " and bd.organisation_id = result.organisation_id " +
                                            " and (bd.manual_approval = TRUE " +
                                                " or (bd.verified = TRUE and bd.registration_number_matched = TRUE and bd.company_name_score > 6 and bd.address_score > 6) " +
                                                " ) " +
                                         " ) " +
                    " ) ";


    String GET_PENDING_SPEND_PROFILES = " select p.application_id, p.id, p.name " +
            GET_PENDING_SPEND_PROFILES_CRITERIA;

    String COUNT_PENDING_SPEND_PROFILES = " select count(distinct p.id) " +
            GET_PENDING_SPEND_PROFILES_CRITERIA;

    @Query(LIVE_QUERY)
    List<Competition> findLive();

    @Query(INNOVATION_LEAD_STAKEHOLDER_LIVE_COUNT_QUERY)
    Long countLiveForInnovationLeadOrStakeholder(@Param("userId") Long userId);

    @Query(LIVE_COUNT_QUERY)
    Long countLive();

    @Query(PROJECT_SETUP_QUERY)
    List<Competition> findProjectSetup();

    @Query(INNOVATION_LEAD_STAKEHOLDER_PROJECT_SETUP_QUERY)
    List<Competition> findProjectSetupForInnovationLeadOrStakeholder(@Param("userId") Long userId);

    @Query(PROJECT_SETUP_COUNT_QUERY)
    Long countProjectSetup();

    @Query(INNOVATION_LEAD_STAKEHOLDER_PROJECT_SETUP_COUNT_QUERY)
    Long countProjectSetupForInnovationLeadOrStakeholder(@Param("userId") Long userId);

    @Query(UPCOMING_QUERY)
    List<Competition> findUpcoming();

    @Query(UPCOMING_COUNT_QUERY)
    Long countUpcoming();

    @Query(NON_IFS_QUERY)
    List<Competition> findNonIfs();

    @Query(NON_IFS_COUNT_QUERY)
    Long countNonIfs();

    @Query(FEEDBACK_RELEASED_QUERY)
    List<Competition> findFeedbackReleased();

    @Query(FEEDBACK_RELEASED_COUNT_QUERY)
    Long countFeedbackReleased();

    @Query(INNOVATION_LEAD_STAKEHOLDER_FEEDBACK_RELEASED_COUNT_QUERY)
    Long countFeedbackReleasedForInnovationLeadOrStakeholder(@Param("userId") Long userId);

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

    @Query(COUNT_OPEN_QUERIES)
    Long countOpenQueriesByCompetitionAndProjectStateNotIn(@Param("competitionId") Long competitionId, @Param("states") Collection<ProjectState> projectStates);

    @Query(GET_OPEN_QUERIES)
    List<CompetitionOpenQueryResource> getOpenQueryByCompetitionAndProjectStateNotIn(@Param("competitionId") long competitionId, @Param("states") Collection<ProjectState> projectStates);

    @Query(value = GET_PENDING_SPEND_PROFILES, nativeQuery = true)
    List<Object[]> getPendingSpendProfiles(@Param("competitionId") long competitionId);

    @Query(value = COUNT_PENDING_SPEND_PROFILES, nativeQuery = true)
    BigDecimal countPendingSpendProfiles(@Param("competitionId") Long competitionId);

}