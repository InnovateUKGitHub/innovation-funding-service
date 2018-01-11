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
        return postWithRestResult(format("%s/%s/%s", assessmentPanelRestUrl, "assign-application", applicationId), Void.class);
    }

    @Override
    public RestResult<Void> unassignFromPanel(long applicationId) {
        return postWithRestResult(format("%s/%s/%s", assessmentPanelRestUrl, "unassign-application", applicationId), Void.class);
    }

    @Override
    public RestResult<Void> notifyAssessors(long competitionId) {
        return postWithRestResult(format("%s/%s/%s", assessmentPanelRestUrl, "notify-assessors", competitionId), Void.class);
    }

    @Override
    public RestResult<Boolean> isPendingReviewNotifications(long competitionId) {
        return getWithRestResult(format("%s/%s/%s", assessmentPanelRestUrl, "notify-assessors", competitionId), Boolean.class);
    }
}