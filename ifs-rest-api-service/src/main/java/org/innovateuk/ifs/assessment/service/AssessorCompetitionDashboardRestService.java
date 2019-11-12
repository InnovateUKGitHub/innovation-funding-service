package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.assessment.resource.dashboard.AssessorCompetitionDashboardResource;
import org.innovateuk.ifs.commons.rest.RestResult;

public interface AssessorCompetitionDashboardRestService {
    RestResult<AssessorCompetitionDashboardResource> getAssessorCompetitionDashboard(long competitionId, long userId);
}
