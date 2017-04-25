package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.FundingDecisionStatus;
import org.innovateuk.ifs.workflow.resource.State;
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
public interface ApplicationRepository extends PagingAndSortingRepository<Application, Long> {
    List<Application> findByName(@Param("name") String name);

	String COMP_FILTER = "SELECT a FROM Application a WHERE " +
			"a.competition.id = :compId " +
			"AND (str(a.id) LIKE CONCAT('%', :filter, '%'))";

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
			"	OR (a.fundingDecision = :funding))";

	String COMP_FUNDING_FILTER = "SELECT a FROM Application a WHERE " +
			"a.competition.id = :compId " +
			"AND (a.fundingDecision IS NOT NULL) " +
			"AND (str(a.id) LIKE CONCAT('%', :filter, '%')) " +
			"AND (:sent IS NULL " +
			"	OR (:sent = true AND a.manageFundingEmailDate IS NOT NULL) " +
			"	OR (:sent = false AND a.manageFundingEmailDate IS NULL))" +
			"AND (:funding IS NULL " +
			"	OR (a.fundingDecision = :funding))";

    @Override
    List<Application> findAll();
    Page<Application> findByCompetitionId(long competitionId, Pageable pageable);

    @Query(COMP_FILTER)
    Page<Application> findByCompetitionIdAndIdLike(@Param("compId") long competitionId, @Param("filter") String filter, Pageable pageable);

    List<Application> findByCompetitionId(long competitionId);

    @Query(COMP_FILTER)
    List<Application> findByCompetitionIdAndIdLike(@Param("compId") long competitionId, @Param("filter") String filter);

	@Query(COMP_STATUS_FILTER)
	Page<Application> findByCompetitionIdAndApplicationProcessActivityStateStateInAndIdLike(@Param("compId") long competitionId,
																							@Param("states") Collection<State> applicationStates,
																							@Param("filter") String filter,
																							@Param("funding") FundingDecisionStatus funding,
																							Pageable pageable);

	@Query(COMP_STATUS_FILTER)
	List<Application> findByCompetitionIdAndApplicationProcessActivityStateStateInAndIdLike(@Param("compId") long competitionId,
																							@Param("states") Collection<State> applicationStates,
																							@Param("filter") String filter,
																							@Param("funding") FundingDecisionStatus funding);

	@Query(COMP_NOT_STATUS_FILTER)
	Page<Application> findByCompetitionIdAndApplicationProcessActivityStateStateNotIn(@Param("compId") long competitionId,
																					  @Param("states") Collection<State> applicationStates,
																					  @Param("filter") String filter,
																					  Pageable pageable);

	List<Application> findByCompetitionIdAndApplicationProcessActivityStateStateIn(long competitionId, Collection<State> applicationStates);

	@Query(COMP_NOT_STATUS_FILTER)
	List<Application> findByCompetitionIdAndApplicationProcessActivityStateStateNotIn(@Param("compId") long competitionId,
																					  @Param("states") Collection<State> applicationStates,
																					  @Param("filter") String filter);

	Page<Application> findByCompetitionIdAndApplicationProcessActivityStateStateInAndAssessorFeedbackFileEntryIsNull(long competitionId, Collection<State> applicationStates, Pageable pageable);

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
	
	List<Application> findByCompetitionIdAndApplicationProcessActivityStateStateInAndAssessorFeedbackFileEntryIsNull(long competitionId, Collection<State> applicationStates);

    int countByCompetitionId(long competitionId);

	int countByCompetitionIdAndApplicationProcessActivityStateState(long competitionId, State applicationStates);

	int countByCompetitionIdAndApplicationProcessActivityStateStateIn(long competitionId, Collection<State> submittedStates);

	int countByCompetitionIdAndApplicationProcessActivityStateStateNotInAndCompletionGreaterThan(Long competitionId, Collection<State> submittedStates, BigDecimal limit);

	int countByCompetitionIdAndApplicationProcessActivityStateStateInAndCompletionLessThanEqual(long competitionId, Collection<State> submittedStates, BigDecimal limit);

	int countByProcessRolesUserIdAndCompetitionId(long userId, long competitionId);

}
