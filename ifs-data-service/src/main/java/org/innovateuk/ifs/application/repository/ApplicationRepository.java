package org.innovateuk.ifs.application.repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.innovateuk.ifs.application.domain.Application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface ApplicationRepository extends PagingAndSortingRepository<Application, Long> {
    List<Application> findByName(@Param("name") String name);
    
    @Override
    List<Application> findAll();
    
    Page<Application> findByCompetitionId(Long competitionId, Pageable pageable);

    List<Application> findByCompetitionId(Long competitionId);

	Page<Application> findByCompetitionIdAndApplicationStatusIdIn(Long competitionId, Collection<Long> applicationStatusIds, Pageable pageable);

	Page<Application> findByCompetitionIdAndApplicationStatusIdNotIn(Long competitionId, Collection<Long> applicationStatusIds, Pageable pageable);
	
	List<Application> findByCompetitionIdAndApplicationStatusId(Long competitionId, Long applicationStatusId);

	Page<Application> findByCompetitionIdAndApplicationStatusId(Long competitionId, Long applicationStatusId, Pageable pageable);
	
	List<Application> findByCompetitionIdAndApplicationStatusIdIn(Long competitionId, Collection<Long> applicationStatusIds);

	List<Application> findByCompetitionIdAndApplicationStatusIdNotIn(Long competitionId, Collection<Long> applicationStatusIds);

	Page<Application> findByCompetitionIdAndApplicationStatusIdInAndAssessorFeedbackFileEntryIsNull(Long competitionId, Collection<Long> applicationStatusIds, Pageable pageable);
	
	List<Application> findByCompetitionIdAndApplicationStatusIdInAndAssessorFeedbackFileEntryIsNull(Long competitionId, Collection<Long> applicationStatusIds);

    int countByCompetitionId(Long competitionId);

	int countByCompetitionIdAndApplicationStatusId(Long competitionId, Long applicationStatusId);

	int countByCompetitionIdAndApplicationStatusIdIn(Long competitionId, Collection<Long> submittedStatusIds);

	int countByCompetitionIdAndApplicationStatusIdInAndAssessorFeedbackFileEntryIsNull(Long competitionId, Collection<Long> applicationStatusIds);

	int countByCompetitionIdAndApplicationStatusIdNotInAndCompletionGreaterThan(Long competitionId, Collection<Long> submittedStatusIds, BigDecimal limit);

	int countByCompetitionIdAndApplicationStatusIdInAndCompletionLessThanEqual(Long competitionId, Collection<Long> submittedStatusIds, BigDecimal limit);

	int countByProcessRolesUserIdAndCompetitionId(Long userId, Long competitionId);

}
