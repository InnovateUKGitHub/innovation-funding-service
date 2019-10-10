package org.innovateuk.ifs.assessment.service.dashboard;

import org.innovateuk.ifs.assessment.resource.dashboard.ApplicationAssessmentResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class ApplicationAssessmentRestServiceImpl extends BaseRestService implements ApplicationAssessmentRestService {

    private String url = "/assessment/assessor/dashboard/competition/%s";

    @Override
    public RestResult<ApplicationAssessmentResource> getApplicationAssessmentResource(long competitionId) {
        return getWithRestResult(format(url, competitionId), ApplicationAssessmentResource.class);
    }
}
