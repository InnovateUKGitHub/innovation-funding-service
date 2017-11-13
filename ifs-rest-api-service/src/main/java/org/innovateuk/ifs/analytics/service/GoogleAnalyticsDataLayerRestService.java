package org.innovateuk.ifs.analytics.service;

import org.innovateuk.ifs.commons.rest.RestResult;

public interface GoogleAnalyticsDataLayerRestService {

    RestResult<String> getCompetitionNameForApplication(long applicationId);

    RestResult<String> getCompetitionName(long competitionId);

    RestResult<String> getCompetitionNameForProject(long projectId);

    RestResult<String> getCompetitionNameForAssessment(long assessmentId);
}