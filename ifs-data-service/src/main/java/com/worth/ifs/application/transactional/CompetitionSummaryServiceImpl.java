package com.worth.ifs.application.transactional;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

import static com.worth.ifs.application.transactional.ApplicationSummaryServiceImpl.CREATED_AND_OPEN_STATUS_IDS;
import static com.worth.ifs.application.transactional.ApplicationSummaryServiceImpl.SUBMITTED_STATUS_IDS;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class CompetitionSummaryServiceImpl extends BaseTransactionalService implements CompetitionSummaryService {
	
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
		final List<Application> applications = applicationRepository.findByCompetitionIdAndApplicationStatusIdIn(competitionId, CREATED_AND_OPEN_STATUS_IDS);

        return countApplicationsByProgressFilter(applications, percentage -> percentage <= 50);
	}

	private Long getApplicationInProgressCountByCompetitionId(Long competitionId) {
		final List<Application> applications = applicationRepository.findByCompetitionIdAndApplicationStatusIdNotIn(competitionId, SUBMITTED_STATUS_IDS);
        return countApplicationsByProgressFilter(applications, percentage -> percentage > 50);
	}

    private Long countApplicationsByProgressFilter(List<Application> applications, Predicate<Integer> predicate){
        return applications.stream()
            .map(a -> a.getCompletion().intValue())
            .filter(predicate)
            .count();
    }
}
