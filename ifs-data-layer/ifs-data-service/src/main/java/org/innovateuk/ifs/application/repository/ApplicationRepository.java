package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.PreviousApplicationResource;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.fundingdecision.domain.DecisionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.innovateuk.ifs.application.transactional.ApplicationSummaryServiceImpl.CREATED_AND_OPEN_STATUSES;
import static org.innovateuk.ifs.application.transactional.ApplicationSummaryServiceImpl.SUBMITTED_AND_INELIGIBLE_STATES;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface ApplicationRepository extends PagingAndSortingRepository<Application, Long> {
    List<Application> findByName(@Param("name") String name);

    String COMP_NOT_STATUS_FILTER = "WHERE a.competition.id = :compId " +
            "AND (a.applicationProcess.activityState NOT IN :states) " +
            "AND (str(a.id) LIKE CONCAT('%', :filter, '%')) ";

    String EOICONFIG_JOIN = "LEFT JOIN a.applicationExpressionOfInterestConfig eoiConfig ";

    String APPLICATION_ONLY_WHERE = "AND (eoiConfig IS NULL OR eoiConfig.enabledForExpressionOfInterest = false) ";

    String EOI_ONLY_WHERE = "AND eoiConfig.enabledForExpressionOfInterest = true ";

    String APPLICATION_SELECT = "SELECT a FROM Application a ";

    String APPLICATION_ID_SELECT = "SELECT a.id FROM Application a ";

    String COMP_STATUS_COMMON_FILTER_WHERE = "WHERE " +
            "a.competition.id = :compId " +
            "AND (a.applicationProcess.activityState IN :states) " +
            "AND (:filter IS NULL OR str(a.id) LIKE CONCAT('%', :filter, '%') ) " +
            "AND (:funding IS NULL " +
            "OR ( str(:funding) = 'UNDECIDED' AND a.decision IS NULL AND a.applicationProcess.activityState <> org.innovateuk.ifs.application.resource.ApplicationState.APPROVED ) " +
            "OR a.decision = :funding " +
            "   OR ( str(:funding) = 'FUNDED' AND a.applicationProcess.activityState = org.innovateuk.ifs.application.resource.ApplicationState.APPROVED ) " +
            ")";

    String COMP_STATUS_FILTER_WHERE =  COMP_STATUS_COMMON_FILTER_WHERE +
            " AND (:inAssessmentReviewPanel IS NULL OR a.inAssessmentReviewPanel = :inAssessmentReviewPanel) ";

    String EOI_FILTER_WHERE = COMP_STATUS_COMMON_FILTER_WHERE +
            " AND a.applicationExpressionOfInterestConfig.enabledForExpressionOfInterest = true" +
            " AND (:sent IS NULL OR (:sent = true AND a.manageDecisionEmailDate IS NOT NULL) OR (:sent = false AND a.manageDecisionEmailDate IS NULL))";

    String ASSESSED_APPLICATION_FILTER_WHERE = "WHERE " +
            "a.competition.id = :compId " +
            "AND (a.assessmentPeriod.id IN :closedAssessmentPeriods) " +
            "AND (a.applicationProcess.activityState IN :states) " +
            "AND (:filter IS NULL OR str(a.id) LIKE CONCAT('%', :filter, '%') ) " +
            "AND (:funding IS NULL " +
            "OR ( str(:funding) = 'UNDECIDED' AND a.decision IS NULL AND a.applicationProcess.activityState <> org.innovateuk.ifs.application.resource.ApplicationState.APPROVED ) " +
            "OR a.decision = :funding " +
            "   OR ( str(:funding) = 'FUNDED' AND a.applicationProcess.activityState = org.innovateuk.ifs.application.resource.ApplicationState.APPROVED ) " +
            ") " +
            "AND (:inAssessmentReviewPanel IS NULL OR a.inAssessmentReviewPanel = :inAssessmentReviewPanel)";

    String COMP_FUNDING_FILTER = "WHERE " +
            "a.competition.id = :compId " +
            "AND (a.decision IS NOT NULL) " +
            "AND (str(a.id) LIKE CONCAT('%', :filter, '%')) " +
            "AND (:sent IS NULL " +
            "OR (:sent = true AND a.manageDecisionEmailDate IS NOT NULL) " +
            "OR (:sent = false AND a.manageDecisionEmailDate IS NULL)) " +
            "AND (" +
            "(:eoi = false AND (eoiConfig IS NULL OR eoiConfig.eoiConfig.enabledForExpressionOfInterest = false)) " +
            "OR (:eoi = true AND eoiConfig.enabledForExpressionOfInterest = true)) " +
            "AND (:funding IS NULL " +
            "OR (a.decision = :funding)) ";

    String SUBMITTED_APPLICATIONS_NOT_ON_INTERVIEW_PANEL = "SELECT a FROM Application a " +
            "WHERE " +
            "  a.competition.id = :competitionId AND " +
            "  a.applicationProcess.activityState = org.innovateuk.ifs.application.resource.ApplicationState.SUBMITTED AND " +
            "  NOT EXISTS (SELECT 1 FROM InterviewAssignment i WHERE i.target = a )";

    String SEARCH_BY_ID_LIKE = " SELECT a from Application a " +
            " WHERE str(a.id) LIKE CONCAT('%', :searchString, '%') ";

    String APPLICATION_SEARCH_BY_ID_PRE_SUBMISSION_LIKE = " SELECT a from Application a " +
            " WHERE str(a.id) LIKE CONCAT('%', :searchString, '%') " +
            "AND a.applicationProcess.activityState != org.innovateuk.ifs.application.resource.ApplicationState.CREATED " +
            "AND a.applicationProcess.activityState != org.innovateuk.ifs.application.resource.ApplicationState.OPENED ";

    String APPLICATION_SEARCH_BY_USER_ID_AND_INNOVATION_LEAD_ROLE = "SELECT a from Application a " +
            "INNER JOIN InnovationLead cp " +
            "ON cp.competition.id = a.competition.id " +
            "WHERE cp.user.id = :userId " +
            "AND a.applicationProcess.activityState != org.innovateuk.ifs.application.resource.ApplicationState.CREATED " +
            "AND a.applicationProcess.activityState != org.innovateuk.ifs.application.resource.ApplicationState.OPENED " +
            "AND str(a.id) LIKE CONCAT('%', :searchString, '%')";

    String APPLICATION_SEARCH_BY_USER_ID_AND_STAKEHOLDER_ROLE = "SELECT a from Application a " +
            "INNER JOIN Stakeholder cp " +
            "ON cp.competition.id = a.competition.id " +
            "WHERE cp.user.id = :userId " +
            "AND a.applicationProcess.activityState != org.innovateuk.ifs.application.resource.ApplicationState.CREATED " +
            "AND a.applicationProcess.activityState != org.innovateuk.ifs.application.resource.ApplicationState.OPENED " +
            "AND str(a.id) LIKE CONCAT('%', :searchString, '%')";

    String FIND_BY_ASSESSMENT = "SELECT app FROM Application app " +
            "INNER JOIN Assessment ass ON ass.target.id = app.id " +
            "WHERE ass.id = :assessmentId";

    String FIND_BY_PROJECT = "SELECT app FROM Application app " +
            "INNER JOIN Project p ON p.application = app " +
            "WHERE p.id = :projectId";

    @Override
    List<Application> findAll();

    Page<Application> findByCompetitionId(long competitionId, Pageable pageable);

    @Query(SEARCH_BY_ID_LIKE)
    Page<Application> searchByIdLike(@Param("searchString") String searchString, Pageable pageable);

    @Query(APPLICATION_SEARCH_BY_ID_PRE_SUBMISSION_LIKE)
    Page<Application> searchApplicationsByLikeAndExcludePreSubmissionStatuses(@Param("searchString") String searchString, Pageable pageable);

    @Query(value = APPLICATION_SEARCH_BY_USER_ID_AND_INNOVATION_LEAD_ROLE)
    Page<Application> searchApplicationsByUserIdAndInnovationLeadRole(@Param("userId") long userId,
                                                                      @Param("searchString") String searchString,
                                                                      Pageable pageable);

    @Query(value = APPLICATION_SEARCH_BY_USER_ID_AND_STAKEHOLDER_ROLE)
    Page<Application> searchApplicationsByUserIdAndStakeholderRole(@Param("userId") long userId,
                                                                   @Param("searchString") String searchString,
                                                                   Pageable pageable);

    List<Application> findByCompetitionId(long competitionId);

    List<Application> findByAssessmentPeriodId(long assessmentPeriodId);

    Optional<Application> findTopByCompetitionIdOrderByManageDecisionEmailDateDesc(long competitionId);

    @Query(APPLICATION_SELECT + COMP_STATUS_FILTER_WHERE)
    Page<Application> findByApplicationStateAndDecision(@Param("compId") long competitionId,
                                                               @Param("states") Collection<ApplicationState> applicationStates,
                                                               @Param("filter") String filter,
                                                               @Param("funding") DecisionStatus funding,
                                                               @Param("inAssessmentReviewPanel") Boolean inAssessmentReviewPanel,
                                                               Pageable pageable);

    @Query(APPLICATION_SELECT + EOICONFIG_JOIN + COMP_STATUS_FILTER_WHERE + APPLICATION_ONLY_WHERE)
    Page<Application> findApplicationsByApplicationStateAndDecision(@Param("compId") long competitionId,
                                                               @Param("states") Collection<ApplicationState> applicationStates,
                                                               @Param("filter") String filter,
                                                               @Param("funding") DecisionStatus funding,
                                                               @Param("inAssessmentReviewPanel") Boolean inAssessmentReviewPanel,
                                                               Pageable pageable);

    @Query(APPLICATION_SELECT + EOI_FILTER_WHERE)
    Page<Application> findEoiByApplicationStateAndDecision(@Param("compId") long competitionId,
                                                                  @Param("states") Collection<ApplicationState> applicationStates,
                                                                  @Param("filter") String filter,
                                                                  @Param("funding") DecisionStatus funding,
                                                                  @Param("sent") Boolean sent,
                                                                  Pageable pageable);

    @Query(APPLICATION_SELECT + COMP_STATUS_FILTER_WHERE)
    List<Application> findByApplicationStateAndDecision(@Param("compId") long competitionId,
                                                               @Param("states") Collection<ApplicationState> applicationStates,
                                                               @Param("filter") String filter,
                                                               @Param("funding") DecisionStatus funding,
                                                               @Param("inAssessmentReviewPanel") Boolean inAssessmentReviewPanel);

    @Query(APPLICATION_SELECT + EOICONFIG_JOIN + COMP_STATUS_FILTER_WHERE + APPLICATION_ONLY_WHERE)
    List<Application> findApplicationsByApplicationStateAndDecision(@Param("compId") long competitionId,
                                                               @Param("states") Collection<ApplicationState> applicationStates,
                                                               @Param("filter") String filter,
                                                               @Param("funding") DecisionStatus funding,
                                                               @Param("inAssessmentReviewPanel") Boolean inAssessmentReviewPanel);

    @Query(APPLICATION_SELECT + EOI_FILTER_WHERE)
    List<Application> findEoiByApplicationStateAndDecision(@Param("compId") long competitionId,
                                                                  @Param("states") Collection<ApplicationState> applicationStates,
                                                                  @Param("filter") String filter,
                                                                  @Param("funding") DecisionStatus funding,
                                                                  @Param("sent") Boolean sent);

    @Query(APPLICATION_ID_SELECT + COMP_STATUS_FILTER_WHERE)
    List<Long> findApplicationIdsByApplicationStateAndDecision(@Param("compId") long competitionId,
                                                               @Param("states") Collection<ApplicationState> applicationStates,
                                                               @Param("filter") String filter,
                                                               @Param("funding") DecisionStatus funding,
                                                               @Param("inAssessmentReviewPanel") Boolean inAssessmentReviewPanel);

    @Query(APPLICATION_ID_SELECT + EOI_FILTER_WHERE)
    List<Long> findEoiApplicationIdsByApplicationStateAndDecision(@Param("compId") long competitionId,
                                                                         @Param("states") Collection<ApplicationState> applicationStates,
                                                                         @Param("filter") String filter,
                                                                         @Param("funding") DecisionStatus funding,
                                                                         @Param("sent") Boolean sent);

    @Query(APPLICATION_SELECT + COMP_NOT_STATUS_FILTER)
    Page<Application> findByCompetitionIdAndApplicationProcessActivityStateNotIn(@Param("compId") long competitionId,
                                                                                 @Param("states") Collection<ApplicationState> applicationStates,
                                                                                 @Param("filter") String filter,
                                                                                 Pageable pageable);

    @Query(APPLICATION_SELECT + EOICONFIG_JOIN + COMP_NOT_STATUS_FILTER + APPLICATION_ONLY_WHERE)
    Page<Application> findApplicationsByCompetitionIdAndStateNotIn(@Param("compId") long competitionId,
                                                                                 @Param("states") Collection<ApplicationState> applicationStates,
                                                                                 @Param("filter") String filter,
                                                                                 Pageable pageable);

    List<Application> findByCompetitionIdAndApplicationProcessActivityStateIn(long competitionId, Collection<ApplicationState> applicationStates);

    List<Application> findByCompetitionIdAndAssessmentPeriodIdAndApplicationProcessActivityStateIn(long competitionId, long assessmentPeriodId, Collection<ApplicationState> applicationStates);

    Stream<Application> findByApplicationProcessActivityStateIn(Collection<ApplicationState> applicationStates);

    Page<Application> findByCompetitionIdAndApplicationProcessActivityStateIn(long competitionId, Collection<ApplicationState> applicationStates, Pageable pageable);

    @Query(APPLICATION_SELECT + COMP_NOT_STATUS_FILTER)
    List<Application> findByCompetitionIdAndApplicationProcessActivityStateNotIn(@Param("compId") long competitionId,
                                                                                 @Param("states") Collection<ApplicationState> applicationStates,
                                                                                 @Param("filter") String filter);
    @Query(APPLICATION_SELECT + EOICONFIG_JOIN + COMP_NOT_STATUS_FILTER + APPLICATION_ONLY_WHERE)
    List<Application> findApplicationsByCompetitionIdAndStateNotIn(@Param("compId") long competitionId,
                                                                                 @Param("states") Collection<ApplicationState> applicationStates,
                                                                                 @Param("filter") String filter);

    @Query("SELECT a FROM Application a LEFT JOIN a.applicationExpressionOfInterestConfig eoiConfig " + COMP_FUNDING_FILTER)
    Page<Application> findByCompetitionIdAndDecisionIsNotNull(@Param("compId") long competitionId,
                                                                     @Param("filter") String filter,
                                                                     @Param("sent") Boolean sent,
                                                                     @Param("funding") DecisionStatus funding,
                                                                     @Param("eoi") Boolean eoi,
                                                                     Pageable pageable);

    @Query("SELECT a FROM Application a LEFT JOIN a.applicationExpressionOfInterestConfig eoiConfig " + COMP_FUNDING_FILTER)
    List<Application> findByCompetitionIdAndDecisionIsNotNull(@Param("compId") long competitionId,
                                                                     @Param("filter") String filter,
                                                                     @Param("sent") Boolean sent,
                                                                     @Param("funding") DecisionStatus funding,
                                                                     @Param("eoi") Boolean eoi);

    @Query("SELECT a.id FROM Application a LEFT JOIN a.applicationExpressionOfInterestConfig eoiConfig LEFT JOIN a.projectToBeCreated projectToBeCreated " + COMP_FUNDING_FILTER + " AND NOT ((a.manageDecisionEmailDate != null AND a.decision = org.innovateuk.ifs.fundingdecision.domain.DecisionStatus.FUNDED) OR (projectToBeCreated IS NOT NULL AND a.competition.fundingType != org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP))")
    List<Long> getWithDecisionIsChangeableApplicationIdsByCompetitionId(@Param("compId") long competitionId,
                                                                     @Param("filter") String filter,
                                                                     @Param("sent") Boolean sent,
                                                                     @Param("funding") DecisionStatus funding,
                                                                     @Param("eoi") Boolean eoi);

    final String DECIDED_COUNT_SELECT = " SELECT COUNT(a.id) FROM Application a " +
            EOICONFIG_JOIN +
            " WHERE a.competition.id = :competitionId " +
            " AND a.decision IS NOT NULL ";

    final String NOTIFIED_FILTER_WHERE = " AND a.manageDecisionEmailDate IS NOT NULL ";
    final String AWAIT_NOTIFY_FILTER_WHERE = " AND a.manageDecisionEmailDate IS NULL ";

    @Query(DECIDED_COUNT_SELECT + APPLICATION_ONLY_WHERE + NOTIFIED_FILTER_WHERE)
    int countByDecidedAndSentApplications(long competitionId);

    @Query(DECIDED_COUNT_SELECT + APPLICATION_ONLY_WHERE + AWAIT_NOTIFY_FILTER_WHERE)
    int countByDecidedAndAwaitSendApplications(long competitionId);

    @Query(DECIDED_COUNT_SELECT + EOI_ONLY_WHERE + NOTIFIED_FILTER_WHERE)
    int countByDecidedAndSentEOI(long competitionId);

    @Query(DECIDED_COUNT_SELECT + EOI_ONLY_WHERE + AWAIT_NOTIFY_FILTER_WHERE)
    int countByDecidedAndAwaitSendEOI(long competitionId);

    int countByCompetitionIdAndDecision(long competitionId, DecisionStatus decision);

    int countByCompetitionId(long competitionId);

    int countByCompetitionIdAndApplicationProcessActivityState(long competitionId, ApplicationState applicationState);

    int countByCompetitionIdAndApplicationProcessActivityStateIn(long competitionId, Collection<ApplicationState> submittedStates);

    int countByCompetitionIdAndApplicationProcessActivityStateNotInAndCompletionGreaterThan(Long competitionId, Collection<ApplicationState> submittedStates, BigDecimal limit);

    int countByCompetitionIdAndApplicationProcessActivityStateInAndCompletionLessThanEqual(long competitionId, Collection<ApplicationState> submittedStates, BigDecimal limit);

    default int countApplicationsByCompetitionId(long competitionId) { return countByCompetitionIdAndApplicationExpressionOfInterestConfigEnabledForExpressionOfInterestFalse(competitionId); }
    int countByCompetitionIdAndApplicationExpressionOfInterestConfigEnabledForExpressionOfInterestFalse(long competitionId);

    default int countEOIByCompetitionId(long competitionId) { return countByCompetitionIdAndApplicationExpressionOfInterestConfigEnabledForExpressionOfInterestTrue(competitionId); }
    int countByCompetitionIdAndApplicationExpressionOfInterestConfigEnabledForExpressionOfInterestTrue(long competitionId);

    default int countEOIByCompetitionIdAndStateIn(long competitionId, Collection<ApplicationState> submittedStates) { return countByCompetitionIdAndApplicationExpressionOfInterestConfigEnabledForExpressionOfInterestTrueAndApplicationProcessActivityStateIn(competitionId, submittedStates); }
    int countByCompetitionIdAndApplicationExpressionOfInterestConfigEnabledForExpressionOfInterestTrueAndApplicationProcessActivityStateIn(long competitionId, Collection<ApplicationState> submittedStates);

    default int countInProgressApplicationsByCompetitionId(Long competitionId) {
        return countByCompetitionIdAndApplicationProcessActivityStateNotInAndCompletionGreaterThanAndApplicationExpressionOfInterestConfigEnabledForExpressionOfInterestFalse(competitionId, SUBMITTED_AND_INELIGIBLE_STATES, new BigDecimal(50L));
    }
    int countByCompetitionIdAndApplicationProcessActivityStateNotInAndCompletionGreaterThanAndApplicationExpressionOfInterestConfigEnabledForExpressionOfInterestFalse(Long competitionId, Collection<ApplicationState> submittedStates, BigDecimal limit);

    default int countStartedApplicationsByCompetitionId(Long competitionId) {
        return countByCompetitionIdAndApplicationProcessActivityStateInAndCompletionLessThanEqualAndApplicationExpressionOfInterestConfigEnabledForExpressionOfInterestFalse(competitionId, CREATED_AND_OPEN_STATUSES, new BigDecimal(50L));
    }
    int countByCompetitionIdAndApplicationProcessActivityStateInAndCompletionLessThanEqualAndApplicationExpressionOfInterestConfigEnabledForExpressionOfInterestFalse(long competitionId, Collection<ApplicationState> submittedStates, BigDecimal limit);

    default int countApplicationsByCompetitionIdAndStateIn(Long competitionId, Collection<ApplicationState> submittedStates) {
        return countByCompetitionIdAndApplicationProcessActivityStateInAndApplicationExpressionOfInterestConfigEnabledForExpressionOfInterestFalse(competitionId, submittedStates);
    }
    int countByCompetitionIdAndApplicationProcessActivityStateInAndApplicationExpressionOfInterestConfigEnabledForExpressionOfInterestFalse(long competitionId, Collection<ApplicationState> submittedStates);

    int countByProcessRolesUserIdAndCompetitionId(long userId, long competitionId);

    List<Application> findByCompetitionIdAndInAssessmentReviewPanelTrueAndApplicationProcessActivityState(long competitionId, ApplicationState applicationState);

    List<Application> findByCompetitionAndInAssessmentReviewPanelTrueAndApplicationProcessActivityState(Competition competition, ApplicationState applicationState);

    @Query(SUBMITTED_APPLICATIONS_NOT_ON_INTERVIEW_PANEL)
    Page<Application> findSubmittedApplicationsNotOnInterviewPanel(@Param("competitionId") long competitionId, Pageable pageable);

    @Query(SUBMITTED_APPLICATIONS_NOT_ON_INTERVIEW_PANEL)
    List<Application> findSubmittedApplicationsNotOnInterviewPanel(@Param("competitionId") long competitionId);

    @Query(FIND_BY_ASSESSMENT)
    Application findByAssessmentId(@Param("assessmentId") long assessmentId);

    @Query(FIND_BY_PROJECT)
    Application findByProjectId(@Param("projectId") long projectId);

    @Query("SELECT new org.innovateuk.ifs.application.resource.PreviousApplicationResource(" +
            "app.id, " +
            "app.name, " +
            "lead.name, " +
            "app.applicationProcess.activityState, " +
            "app.competition.id " +
            ") FROM Application app " +
            " LEFT JOIN Project project " +
            "   ON project.application.id = app.id " +
            " JOIN ProcessRole pr" +
            "   ON pr.applicationId = app.id  " +
            " JOIN Organisation lead " +
            "   ON lead.id = pr.organisationId" +
            PREVIOUS_WHERE_CLAUSE +
            " AND pr.role = org.innovateuk.ifs.user.resource.ProcessRoleType.LEADAPPLICANT ")
    List<PreviousApplicationResource> findPrevious(long competitionId);

    String PREVIOUS_WHERE_CLAUSE =  " WHERE project.id IS NULL " +
            " AND app.applicationProcess.activityState != org.innovateuk.ifs.application.resource.ApplicationState.CREATED " +
            " AND app.applicationProcess.activityState != org.innovateuk.ifs.application.resource.ApplicationState.OPENED " +
            " AND app.competition.id = :competitionId";

    @Query(" SELECT COUNT(app.id)" +
            " FROM Application app " +
           " LEFT JOIN Project project " +
           "   ON project.application.id = app.id " +
            PREVIOUS_WHERE_CLAUSE)
    int countPrevious(long competitionId);

    @Query(" SELECT DISTINCT app FROM Application app" +
            " LEFT JOIN ApplicationHiddenFromDashboard hidden " +
            "   ON app.id = hidden.application.id AND hidden.user.id = :userId " +
           " LEFT JOIN ProcessRole pr " +
           "    ON app.id = pr.applicationId " +
           "        AND pr.user.id=:userId " +
           "        AND pr.role in (org.innovateuk.ifs.user.resource.ProcessRoleType.LEADAPPLICANT, org.innovateuk.ifs.user.resource.ProcessRoleType.COLLABORATOR, org.innovateuk.ifs.user.resource.ProcessRoleType.KNOWLEDGE_TRANSFER_ADVISER) " +
           " LEFT JOIN Project proj " +
           "    ON proj.application.id = app.id " +
           " LEFT JOIN ProjectUser pu " +
           "    ON pu.project.id = proj.id " +
           "        AND pu.user.id=:userId " +
           "        AND type(pu) = ProjectUser " +
           " WHERE (proj.id IS NULL AND pr iS NOT NULL" + // No project exists and user has applicant process role
           "    OR  pu.id IS NOT NULL)" + // Or project exists and user is a project user.
            "   AND hidden IS NULL")
    List<Application> findApplicationsForDashboard(long userId);

    boolean existsByProcessRolesUserIdAndCompetitionId(long userId, long competitionId);

    @Query("select distinct a from Application a inner join ApplicationProcess p on p.target.id = a.id " +
            "where a.id in :ids" +
            " and  a.competition.id = :competitionId " +
            " and a.submittedDate is not null " +
            " and not (a.decision = org.innovateuk.ifs.fundingdecision.domain.DecisionStatus.FUNDED and a.manageDecisionEmailDate is not null)")
    List<Application> findAllowedApplicationsForCompetition(Set<Long> ids, long competitionId);

    Optional<Application> findByPreviousApplicationId(long previousApplicationId);

    @Query(APPLICATION_ID_SELECT + ASSESSED_APPLICATION_FILTER_WHERE)
    List<Long> findApplicationIdsByClosedAssessmentPeriodAndWaitingForFunding(@Param("compId") long competitionId,
                                                                                            @Param("states") Collection<ApplicationState> applicationStates,
                                                                                            @Param("filter") String filter,
                                                                                            @Param("funding") DecisionStatus funding,
                                                                                            @Param("inAssessmentReviewPanel") Boolean inAssessmentReviewPanel,
                                                                                            @Param("closedAssessmentPeriods") List<Long> closedAssessmentPeriods);
    @Query(APPLICATION_SELECT + ASSESSED_APPLICATION_FILTER_WHERE)
    List<Application> findApplicationsByClosedAssesmentPeriodAndWaitingForFunding(@Param("compId") long competitionId,
                                                                                      @Param("states") Collection<ApplicationState> applicationStates,
                                                                                      @Param("filter") String filter,
                                                                                      @Param("funding") DecisionStatus funding,
                                                                                      @Param("inAssessmentReviewPanel") Boolean inAssessmentReviewPanel,
                                                                                      @Param("closedAssessmentPeriods") List<Long> closedAssessmentPeriods);



    @Query(APPLICATION_SELECT + ASSESSED_APPLICATION_FILTER_WHERE)
    Page<Application> findApplicationsByClosedAssesmentPeriodAndWaitingForFunding(@Param("compId") long competitionId,
                                                                                      @Param("states") Collection<ApplicationState> applicationStates,
                                                                                      @Param("filter") String filter,
                                                                                      @Param("funding") DecisionStatus funding,
                                                                                      @Param("inAssessmentReviewPanel") Boolean inAssessmentReviewPanel,
                                                                                      @Param("closedAssessmentPeriods") List<Long> closedAssessmentPeriods,
                                                                                      Pageable pageable);

}