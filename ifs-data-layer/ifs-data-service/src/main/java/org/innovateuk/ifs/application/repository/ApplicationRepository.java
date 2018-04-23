package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.FundingDecisionStatus;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.workflow.resource.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface ApplicationRepository extends PagingAndSortingRepository<Application, Long> {
    List<Application> findByName(@Param("name") String name);

	String COMP_NOT_STATUS_FILTER = "SELECT a FROM Application a WHERE " +
			"a.competition.id = :compId " +
			"AND (a.applicationProcess.activityState.state NOT IN :states) " +
			"AND (str(a.id) LIKE CONCAT('%', :filter, '%'))";

	String COMP_STATUS_FILTER = "SELECT a FROM Application a WHERE " +
			"a.competition.id = :compId " +
			"AND (a.applicationProcess.activityState.state IN :states) " +
			"AND (str(a.id) LIKE CONCAT('%', :filter, '%')) " +
			"AND (:funding IS NULL " +
			"	OR (str(:funding) = 'UNDECIDED' AND a.fundingDecision IS NULL)" +
			"	OR (a.fundingDecision = :funding)) " +
			"AND (:inAssessmentReviewPanel IS NULL OR a.inAssessmentReviewPanel = :inAssessmentReviewPanel)";

	String COMP_FUNDING_FILTER = "SELECT a FROM Application a WHERE " +
			"a.competition.id = :compId " +
			"AND (a.fundingDecision IS NOT NULL) " +
			"AND (str(a.id) LIKE CONCAT('%', :filter, '%')) " +
			"AND (:sent IS NULL " +
			"	OR (:sent = true AND a.manageFundingEmailDate IS NOT NULL) " +
			"	OR (:sent = false AND a.manageFundingEmailDate IS NULL))" +
			"AND (:funding IS NULL " +
			"	OR (a.fundingDecision = :funding))";

	String SUBMITTED_APPLICATIONS_NOT_ON_INTERVIEW_PANEL = "SELECT a FROM Application a " +
            "WHERE " +
            "  a.competition.id = :competitionId AND " +
            "  a.applicationProcess.activityState.state = org.innovateuk.ifs.workflow.resource.State.SUBMITTED AND " +
            "  NOT EXISTS (SELECT 1 FROM InterviewAssignment i WHERE i.target = a )";

    String SEARCH_BY_ID_LIKE = " SELECT a from Application a " +
                               " WHERE str(a.id) LIKE CONCAT('%', :searchString, '%') ";

    String FIND_BY_ASSESSMENT = "SELECT app FROM Application app " +
			"INNER JOIN Assessment ass ON ass.target.id = app.id " +
			"WHERE ass.id = :assessmentId";

	String FIND_BY_PROJECT = "SELECT app FROM Application app " +
			"INNER JOIN Project p ON p.application = app " +
			"WHERE p.id = :projectId";

    Application findById(long applicationId);

    @Override
    List<Application> findAll();
    Page<Application> findByCompetitionId(long competitionId, Pageable pageable);

    @Query(SEARCH_BY_ID_LIKE)
    Page<Application> searchByIdLike(@Param("searchString") String searchString, Pageable pageable);

    List<Application> findByCompetitionId(long competitionId);

	@Query(COMP_STATUS_FILTER)
	Page<Application> findByCompetitionIdAndApplicationProcessActivityStateStateInAndIdLike(@Param("compId") long competitionId,
																							@Param("states") Collection<State> applicationStates,
																							@Param("filter") String filter,
																							@Param("funding") FundingDecisionStatus funding,
																							@Param("inAssessmentReviewPanel") Boolean inAssessmentReviewPanel,
																							Pageable pageable);

	@Query(COMP_STATUS_FILTER)
	List<Application> findByCompetitionIdAndApplicationProcessActivityStateStateInAndIdLike(@Param("compId") long competitionId,
																							@Param("states") Collection<State> applicationStates,
																							@Param("filter") String filter,
																							@Param("funding") FundingDecisionStatus funding,
																							@Param("inAssessmentReviewPanel") Boolean inAssessmentReviewPanel);

	@Query(COMP_NOT_STATUS_FILTER)
	Page<Application> findByCompetitionIdAndApplicationProcessActivityStateStateNotIn(@Param("compId") long competitionId,
																					  @Param("states") Collection<State> applicationStates,
																					  @Param("filter") String filter,
																					  Pageable pageable);

	List<Application> findByCompetitionIdAndApplicationProcessActivityStateStateIn(long competitionId, Collection<State> applicationStates);

	Stream<Application> findByApplicationProcessActivityStateStateIn(Collection<State> applicationStates);

	Page<Application> findByCompetitionIdAndApplicationProcessActivityStateStateIn(long competitionId, Collection<State> applicationStates, Pageable pageable);

	@Query(COMP_NOT_STATUS_FILTER)
	List<Application> findByCompetitionIdAndApplicationProcessActivityStateStateNotIn(@Param("compId") long competitionId,
																					  @Param("states") Collection<State> applicationStates,
																					  @Param("filter") String filter);

	@Query(COMP_FUNDING_FILTER)
	Page<Application> findByCompetitionIdAndFundingDecisionIsNotNull(@Param("compId") long competitionId,
																	 @Param("filter") String filter,
																	 @Param("sent") Boolean sent,
																	 @Param("funding") FundingDecisionStatus funding,
																	 Pageable pageable);

	@Query(COMP_FUNDING_FILTER)
	List<Application> findByCompetitionIdAndFundingDecisionIsNotNull(@Param("compId") long competitionId,
																	 @Param("filter") String filter,
																	 @Param("sent") Boolean sent,
																	 @Param("funding") FundingDecisionStatus funding);

	int countByCompetitionIdAndFundingDecisionIsNotNullAndManageFundingEmailDateIsNotNull(long competitionId);

	int countByCompetitionIdAndFundingDecisionIsNotNullAndManageFundingEmailDateIsNull(long competitionId);

    int countByCompetitionId(long competitionId);

	int countByCompetitionIdAndApplicationProcessActivityStateState(long competitionId, State applicationState);

	int countByCompetitionIdAndApplicationProcessActivityStateStateIn(long competitionId, Collection<State> submittedStates);

	int countByCompetitionIdAndApplicationProcessActivityStateStateNotInAndCompletionGreaterThan(Long competitionId, Collection<State> submittedStates, BigDecimal limit);

	int countByCompetitionIdAndApplicationProcessActivityStateStateInAndCompletionLessThanEqual(long competitionId, Collection<State> submittedStates, BigDecimal limit);

	int countByProcessRolesUserIdAndCompetitionId(long userId, long competitionId);

	List<Application> findByCompetitionIdAndInAssessmentReviewPanelTrueAndApplicationProcessActivityStateState(long competitionId, State applicationState);
	List<Application> findByCompetitionAndInAssessmentReviewPanelTrueAndApplicationProcessActivityStateState(Competition competition, State applicationState);

	@Query(SUBMITTED_APPLICATIONS_NOT_ON_INTERVIEW_PANEL)
    Page<Application> findSubmittedApplicationsNotOnInterviewPanel(@Param("competitionId") long competitionId, Pageable pageable);
    @Query(SUBMITTED_APPLICATIONS_NOT_ON_INTERVIEW_PANEL)
    List<Application> findSubmittedApplicationsNotOnInterviewPanel(@Param("competitionId") long competitionId);

    @Query(FIND_BY_ASSESSMENT)
	Application findByAssessmentId(@Param("assessmentId") long assessmentId);

    @Query(FIND_BY_PROJECT)
	Application findByProjectId(@Param("projectId") long projectId);
}