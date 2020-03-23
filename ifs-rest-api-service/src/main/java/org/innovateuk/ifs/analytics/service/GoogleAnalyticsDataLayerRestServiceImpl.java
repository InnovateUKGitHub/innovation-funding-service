package org.innovateuk.ifs.analytics.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.user.resource.Role;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.roleListType;

@Service
public class GoogleAnalyticsDataLayerRestServiceImpl extends BaseRestService implements GoogleAnalyticsDataLayerRestService {

    private static final String ANALYTICS_BASE_URL = "/analytics";

    @Override
    public RestResult<String> getCompetitionNameForInvite(String inviteHash) {
        return getWithRestResultAnonymous(format("%s/invite/%s/competition-name", ANALYTICS_BASE_URL, inviteHash),
                String.class
        );
    }

    @Override
    public RestResult<String> getCompetitionNameForApplication(long applicationId) {
        return getWithRestResult(format("%s/application/%d/competition-name", ANALYTICS_BASE_URL, applicationId),
                                 String.class
        );
    }

    @Override
    public RestResult<String> getCompetitionName(long competitionId) {
        return getWithRestResultAnonymous(format("%s/competition/%d/competition-name", ANALYTICS_BASE_URL, competitionId),
                                          String.class);
    }

    @Override
    public RestResult<String> getCompetitionNameForProject(long projectId) {
        return getWithRestResult(format("%s/project/%d/competition-name", ANALYTICS_BASE_URL, projectId),
                                 String.class);
    }

    @Override
    public RestResult<String> getCompetitionNameForAssessment(long assessmentId) {
        return getWithRestResult(format("%s/assessment/%d/competition-name", ANALYTICS_BASE_URL, assessmentId),
                                 String.class
        );
    }

    @Override
    public RestResult<List<Role>> getRolesByApplicationId(long applicationId) {
        return getWithRestResult(format("%s/application/%d/user-roles", ANALYTICS_BASE_URL, applicationId),
                                 roleListType()
        );
    }

    @Override
    public RestResult<List<Role>> getRolesByProjectId(long projectId) {
        return getWithRestResult(format("%s/project/%d/user-roles", ANALYTICS_BASE_URL, projectId),
                                 roleListType());
    }

    @Override
    public RestResult<Long> getApplicationIdForProject(long projectId) {
        return getWithRestResult(format("%s/project/%d/application-id", ANALYTICS_BASE_URL, projectId),
                                 Long.class);
    }

    @Override
    public RestResult<Long> getApplicationIdForAssessment(long assessmentId) {
        return getWithRestResult(format("%s/assessment/%d/application-id", ANALYTICS_BASE_URL, assessmentId),
                Long.class);
    }
}