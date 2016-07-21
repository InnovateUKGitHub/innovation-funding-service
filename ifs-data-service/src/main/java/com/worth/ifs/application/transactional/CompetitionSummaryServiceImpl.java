package com.worth.ifs.application.transactional;

import java.math.BigDecimal;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.transactional.BaseTransactionalService;

import org.springframework.stereotype.Service;

import static com.worth.ifs.application.transactional.ApplicationSummaryServiceImpl.CREATED_AND_OPEN_STATUS_IDS;
import static com.worth.ifs.application.transactional.ApplicationSummaryServiceImpl.SUBMITTED_STATUS_IDS;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class CompetitionSummaryServiceImpl extends BaseTransactionalService implements CompetitionSummaryService {
	
	@Override
	public ServiceResult<CompetitionSummaryResource> getCompetitionSummaryByCompetitionId(Long competitionId){
		Competition competition = competitionRepository.findById(competitionId);
		BigDecimal limit = new BigDecimal(50L);

		CompetitionSummaryResource competitionSummaryResource = new CompetitionSummaryResource();
		competitionSummaryResource.setCompetitionId(competitionId);
		competitionSummaryResource.setCompetitionName(competition.getName());
		competitionSummaryResource.setCompetitionStatus(competition.getCompetitionStatus());
		competitionSummaryResource.setTotalNumberOfApplications(applicationRepository.countByCompetitionId(competitionId));
		competitionSummaryResource.setApplicationsStarted(applicationRepository.countByCompetitionIdAndApplicationStatusIdInAndCompletionLessThanEqual(competitionId, CREATED_AND_OPEN_STATUS_IDS, limit));
		competitionSummaryResource.setApplicationsInProgress(applicationRepository.countByCompetitionIdAndApplicationStatusIdNotInAndCompletionGreaterThan(competitionId, SUBMITTED_STATUS_IDS, limit));
		competitionSummaryResource.setApplicationsSubmitted(applicationRepository.countByCompetitionIdAndApplicationStatusIdIn(competitionId, SUBMITTED_STATUS_IDS));
		competitionSummaryResource.setApplicationsNotSubmitted(competitionSummaryResource.getTotalNumberOfApplications() - competitionSummaryResource.getApplicationsSubmitted());
		competitionSummaryResource.setApplicationDeadline(competition.getEndDate());
		competitionSummaryResource.setApplicationsFunded(applicationRepository.countByCompetitionIdAndApplicationStatusId(competitionId, ApplicationStatusConstants.APPROVED.getId()));

		return serviceSuccess(competitionSummaryResource);
	}
}
