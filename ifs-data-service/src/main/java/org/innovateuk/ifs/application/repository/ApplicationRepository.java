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

	String COMP_STATUS_FILTER = "SELECT a FROM Application a WHERE " +
			"a.competition.id = :compId " +
			"AND (a.applicationProcess.activityState.state IN :statuses) " +
			"AND (str(a.id) LIKE CONCAT('%', :filter, '%')) " +
			"AND (:funding IS NULL " +
			"	OR (str(:funding) = 'UNDECIDED' AND a.fundingDecision IS NULL)" +
			"	OR (a.fundingDecision = :funding))";

	static final String COMP_FUNDING_FILTER = "SELECT a FROM Application a WHERE " +
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
	
	Page<Application> findByCompetitionIdAndApplicationProcessActivityStateStateIn(long competitionId, Collection<State> applicationStatuses, Pageable pageable);

	@Query(COMP_STATUS_FILTER)
	Page<Application> findByCompetitionIdAndApplicationProcessActivityStateStateInAndIdLike(@Param("compId") long competitionId,
																							@Param("statuses") Collection<State> applicationStatuses,
																							@Param("filter") String filter,
																							@Param("funding") FundingDecisionStatus funding,
																							Pageable pageable);

	@Query(COMP_STATUS_FILTER)
	List<Application> findByCompetitionIdAndApplicationProcessActivityStateStateInAndIdLike(@Param("compId") long competitionId,
																							@Param("statuses") Collection<State> applicationStatuses,
																							@Param("filter") String filter,
																							@Param("funding") FundingDecisionStatus funding);

	Page<Application> findByCompetitionIdAndApplicationProcessActivityStateStateNotIn(long competitionId, Collection<State> applicationStatuses, Pageable pageable);

	List<Application> findByCompetitionIdAndApplicationProcessActivityStateStateIn(long competitionId, Collection<State> applicationStatuses);

	List<Application> findByCompetitionIdAndApplicationProcessActivityStateStateNotIn(long competitionId, Collection<State> applicationStatuses);

	Page<Application> findByCompetitionIdAndApplicationProcessActivityStateStateInAndAssessorFeedbackFileEntryIsNull(long competitionId, Collection<State> applicationStatus, Pageable pageable);

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
	
	List<Application> findByCompetitionIdAndApplicationProcessActivityStateStateInAndAssessorFeedbackFileEntryIsNull(long competitionId, Collection<State> applicationStatuses);

    int countByCompetitionId(long competitionId);

	int countByCompetitionIdAndApplicationProcessActivityStateState(long competitionId, State applicationStatus);

	int countByCompetitionIdAndApplicationProcessActivityStateStateIn(long competitionId, Collection<State> submittedStatuses);

	int countByCompetitionIdAndApplicationProcessActivityStateStateNotInAndCompletionGreaterThan(Long competitionId, Collection<State> submittedStatuses, BigDecimal limit);

	int countByCompetitionIdAndApplicationProcessActivityStateStateInAndCompletionLessThanEqual(long competitionId, Collection<State> submittedStatuses, BigDecimal limit);

	int countByProcessRolesUserIdAndCompetitionId(long userId, long competitionId);

}
