package com.worth.ifs.assessment.service;

import com.worth.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

/**
 * AssessmentFeedbackRestServiceImpl is a utility for CRUD operations on {@link com.worth.ifs.assessment.domain.AssessmentFeedback}.
 * This class connects to the {@link com.worth.ifs.assessment.controller.AssessmentFeedbackController}
 * through a REST call.
 */
@Service
public class AssessmentFeedbackRestServiceImpl extends BaseRestService implements AssessmentFeedbackRestService {

    private String assessmentFeedbackRestURL = "/assessment-feedback";

    protected void setAssessmentFeedbackRestURL(final String assessmentFeedbackRestURL) {
        this.assessmentFeedbackRestURL = assessmentFeedbackRestURL;
    }
}