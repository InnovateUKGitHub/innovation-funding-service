package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class AssessmentPanelRestServiceImpl extends BaseRestService implements AssessmentPanelRestService {

    private static final String assessmentPanelRestUrl = "/assessmentpanel";

    @Override
    public RestResult<Void> assignToPanel(long applicationId) {
        return postWithRestResult(format("%s/%s/%s", assessmentPanelRestUrl, "assignApplication", applicationId), Void.class);
    }

    @Override
    public RestResult<Void> unassignFromPanel(long applicationId) {
        return postWithRestResult(format("%s/%s/%s", assessmentPanelRestUrl, "unassignApplication", applicationId), Void.class);
    }

    @Override
    public RestResult<Void> notifyAssessors(long competitionId) {
        return postWithRestResult(format("%s/%s/%s", assessmentPanelRestUrl, "notify-assessors", competitionId), Void.class);
    }
}
