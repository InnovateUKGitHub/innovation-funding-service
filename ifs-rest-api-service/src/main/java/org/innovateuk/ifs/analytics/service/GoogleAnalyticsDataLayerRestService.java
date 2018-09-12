package org.innovateuk.ifs.analytics.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.Role;

import java.util.List;

public interface GoogleAnalyticsDataLayerRestService {

    RestResult<String> getCompetitionNameForApplication(long applicationId);

    RestResult<String> getCompetitionName(long competitionId);

    RestResult<String> getCompetitionNameForProject(long projectId);

    RestResult<String> getCompetitionNameForAssessment(long assessmentId);

    RestResult<List<Role>> getRolesByApplicationId(long applicationId);

    RestResult<List<Role>> getRolesByProjectId(long projectId);

    RestResult<Long> getApplicationIdForProject(long projectId);
}