package org.innovateuk.ifs.analytics.service;

import org.innovateuk.ifs.commons.service.ServiceResult;

public interface GoogleAnalyticsDataLayerService {
    ServiceResult<String> getCompetitionNameByApplicationId(long applicationId);

    ServiceResult<String> getCompetitionName(long competitionId);

    ServiceResult<String> getCompetitionNameByProjectId(long projectId);

    ServiceResult<String> getCompetitionNameByAssessmentId(long assessmentId);
}
