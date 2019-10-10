package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.assessment.resource.dashboard.AssessorCompetitionDashboardResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;

import static java.lang.String.format;

public class AssessorCompetitionDashboardRestServiceImpl extends BaseRestService implements AssessorCompetitionDashboardRestService{

    private final String baseUrl = "/assessor/dashboard/competition/%s";

    @Override
    public RestResult<AssessorCompetitionDashboardResource> getAssessorCompetitionDashboard(long competitionId) {
        return getWithRestResult(format(baseUrl, competitionId), AssessorCompetitionDashboardResource.class);
    }

}
