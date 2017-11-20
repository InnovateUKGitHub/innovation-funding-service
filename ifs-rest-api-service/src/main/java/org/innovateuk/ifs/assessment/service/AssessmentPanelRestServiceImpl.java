package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.innovateuk.ifs.invite.resource.*;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleJoiner;

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
}
