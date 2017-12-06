package org.innovateuk.ifs.analytics.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class GoogleAnalyticsDataLayerServiceImpl extends BaseTransactionalService implements GoogleAnalyticsDataLayerService {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Override
    public ServiceResult<String> getCompetitionNameByApplicationId(long applicationId) {
        return find(competitionRepository
                .findByApplicationsId(applicationId), notFoundError(Competition.class, applicationId))
                .andOnSuccessReturn(Competition::getName);
    }

    @Override
    public ServiceResult<String> getCompetitionName(long competitionId) {
        return find(competitionRepository
                .findById(competitionId), notFoundError(Competition.class, competitionId))
                .andOnSuccessReturn(Competition::getName);
    }

    @Override
    public ServiceResult<String> getCompetitionNameByProjectId(long projectId) {
        return find(competitionRepository
                .findByProjectId(projectId), notFoundError(Competition.class, projectId))
                .andOnSuccessReturn(Competition::getName);
    }

    @Override
    public ServiceResult<String> getCompetitionNameByAssessmentId(long assessmentId) {
        return find(competitionRepository
                .findByAssessmentId(assessmentId), notFoundError(Competition.class, assessmentId))
                .andOnSuccessReturn(Competition::getName);
    }
}