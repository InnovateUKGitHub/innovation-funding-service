package org.innovateuk.ifs.analytics.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class GoogleAnalyticsDataLayerRestServiceImpl extends BaseRestService implements GoogleAnalyticsDataLayerRestService {

    private static final String ANALYTICS_BASE_URL = "/analytics";

    @Override
    public RestResult<String> getCompetitionNameForApplication(long applicationId) {
        return getWithRestResult(format("%s/application/%d/competition-name", ANALYTICS_BASE_URL, applicationId), String.class);
    }

    @Override
    public RestResult<String> getCompetitionName(long competitionId) {
        return getWithRestResultAnonymous(format("%s/competition/%d/competition-name", ANALYTICS_BASE_URL, competitionId), String.class);
    }

    @Override
    public RestResult<String> getCompetitionNameForProject(long projectId) {
        return getWithRestResult(format("%s/project/%d/competition-name", ANALYTICS_BASE_URL, projectId), String.class);
    }

    @Override
    public RestResult<String> getCompetitionNameForAssessment(long assessmentId) {
        return getWithRestResult(format("%s/assessment/%d/competition-name", ANALYTICS_BASE_URL, assessmentId), String.class);
    }
}