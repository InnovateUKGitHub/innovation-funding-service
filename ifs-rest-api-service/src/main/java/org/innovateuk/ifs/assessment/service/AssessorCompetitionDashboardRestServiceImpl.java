package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.assessment.resource.dashboard.AssessorCompetitionDashboardResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

@Service
public class AssessorCompetitionDashboardRestServiceImpl extends BaseRestService implements AssessorCompetitionDashboardRestService {

    private String assessorRestUrl = "/assessment";

    @Override
    public RestResult<AssessorCompetitionDashboardResource> getAssessorCompetitionDashboard(long competitionId, long userId) {
        return getWithRestResult(assessorRestUrl + "/user/" + userId + "/competition/" + competitionId + "/dashboard", AssessorCompetitionDashboardResource.class);
    }
}
