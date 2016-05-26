package com.worth.ifs.application.transactional;

import static com.worth.ifs.application.transactional.ApplicationSummaryServiceImpl.CREATED_AND_OPEN_STATUS_IDS;
import static com.worth.ifs.application.transactional.ApplicationSummaryServiceImpl.SUBMITTED_STATUS_IDS;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.application.resource.CompletedPercentageResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.transactional.BaseTransactionalService;

@Service
public class CompetitionSummaryServiceImpl extends BaseTransactionalService implements CompetitionSummaryService {

	@Autowired
	private ApplicationService applicationService;
	
	@Override
	public ServiceResult<CompetitionSummaryResource> getCompetitionSummaryByCompetitionId(Long competitionId){
		Competition competition = competitionRepository.findById(competitionId);

		CompetitionSummaryResource competitionSummaryResource = new CompetitionSummaryResource();
		competitionSummaryResource.setCompetitionId(competitionId);
		competitionSummaryResource.setCompetitionName(competition.getName());
		competitionSummaryResource.setCompetitionStatus(competition.getCompetitionStatus());
		competitionSummaryResource.setTotalNumberOfApplications(applicationRepository.countByCompetitionId(competitionId));
		competitionSummaryResource.setApplicationsStarted(getApplicationStartedCountByCompetitionId(competitionId));
		competitionSummaryResource.setApplicationsInProgress(getApplicationInProgressCountByCompetitionId(competitionId));
		competitionSummaryResource.setApplicationsSubmitted(applicationRepository.countByCompetitionIdAndApplicationStatusIdIn(competitionId, SUBMITTED_STATUS_IDS));
		competitionSummaryResource.setApplicationsNotSubmitted(competitionSummaryResource.getTotalNumberOfApplications() - competitionSummaryResource.getApplicationsSubmitted());
		competitionSummaryResource.setApplicationDeadline(competition.getEndDate());
		competitionSummaryResource.setApplicationsFunded(applicationRepository.countByCompetitionIdAndApplicationStatusId(competitionId, ApplicationStatusConstants.APPROVED.getId()));

		return serviceSuccess(competitionSummaryResource);
	}
	


	private long getApplicationStartedCountByCompetitionId(Long competitionId){

		Long startedCount = 0L;

		final List<Application> applications = applicationRepository.findByCompetitionIdAndApplicationStatusIdIn(competitionId, CREATED_AND_OPEN_STATUS_IDS);

		for(Application application : applications){
			final CompletedPercentageResource completedPercentageResource = applicationService.getProgressPercentageByApplicationId(application.getId()).getSuccessObject();
			if(completedPercentageResource.getCompletedPercentage().intValue() <= 50) {
				startedCount++;
			}
		}

		return startedCount;
	}

	private Long getApplicationInProgressCountByCompetitionId(Long competitionId) {

		Long inProgressCount = 0L;

		final List<Application> applications = applicationRepository.findByCompetitionIdAndApplicationStatusIdNotIn(competitionId, SUBMITTED_STATUS_IDS);

		for(Application application : applications){
			final CompletedPercentageResource completedPercentageResource = applicationService.getProgressPercentageByApplicationId(application.getId()).getSuccessObject();
			if(completedPercentageResource.getCompletedPercentage().intValue() > 50) {
				inProgressCount++;
			}
		}

		return inProgressCount;
	}
}
