package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.assessment.resource.dashboard.AssessorCompetitionDashboardResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class AssessorCompetitionDashboardRestServiceImpl extends BaseRestService implements AssessorCompetitionDashboardRestService {

    private final String baseUrl = "/assessment/user/%s/competition/%s";

    @Override
    public RestResult<AssessorCompetitionDashboardResource> getAssessorCompetitionDashboard(long competitionId, long userId) {
        return getWithRestResult(format(baseUrl + "/dashboard", userId, competitionId), AssessorCompetitionDashboardResource.class);
    }
}
