package com.worth.ifs.assessment.service;

import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

/**
 * AssessmentFeedbackRestServiceImpl is a utility for CRUD operations on {@link com.worth.ifs.assessment.domain.Assessment}.
 * This class connects to the {@link com.worth.ifs.assessment.controller.AssessmentController}
 * through a REST call.
 */
@Service
public class AssessmentRestServiceImpl extends BaseRestService implements AssessmentRestService {

    private String assessmentRestURL = "/assessment";

    protected void setAssessmentRestURL(final String assessmentRestURL) {
        this.assessmentRestURL = assessmentRestURL;
    }

    @Override
    public RestResult<AssessmentResource> getById(final Long id) {
        return getWithRestResult(format("%s/%s", assessmentRestURL, id), AssessmentResource.class);
    }

    @Override
    public RestResult<Void> updateStatus(Long id, ProcessOutcomeResource processOutcome) {
         return putWithRestResult(format("%s/%s", assessmentRestURL, id), processOutcome, Void.class);
    }
}