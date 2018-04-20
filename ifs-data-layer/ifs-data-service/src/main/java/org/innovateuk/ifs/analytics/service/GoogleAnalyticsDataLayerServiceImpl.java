package org.innovateuk.ifs.analytics.service;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class GoogleAnalyticsDataLayerServiceImpl extends BaseTransactionalService implements GoogleAnalyticsDataLayerService {

    @Override
    public ServiceResult<String> getCompetitionNameByApplicationId(long applicationId) {
        return getApplication(applicationId)
                .andOnSuccess(application -> getCompetition(application.getCompetition().getId())
                        .andOnSuccessReturn(Competition::getName));
    }

    @Override
    public ServiceResult<String> getCompetitionName(long competitionId) {
        return find(getCompetition(competitionId))
                .andOnSuccessReturn(Competition::getName);
    }

    @Override
    public ServiceResult<String> getCompetitionNameByProjectId(long projectId) {
        Application application = applicationRepository.findByProjectId(projectId);

        return find(getCompetition(application.getCompetition().getId()))
                .andOnSuccessReturn(Competition::getName);
    }

    @Override
    public ServiceResult<String> getCompetitionNameByAssessmentId(long assessmentId) {
        Application application = applicationRepository.findByAssessmentId(assessmentId);
        return find(getCompetition(application.getCompetition().getId()))
                .andOnSuccessReturn(Competition::getName);
    }
}