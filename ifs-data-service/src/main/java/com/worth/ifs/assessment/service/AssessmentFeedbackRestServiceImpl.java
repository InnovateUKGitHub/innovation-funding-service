package com.worth.ifs.assessment.service;

import com.worth.ifs.assessment.resource.AssessmentFeedbackResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.assessmentFeedbackResourceListType;
import static java.lang.String.format;

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

    @Override
    public RestResult<List<AssessmentFeedbackResource>> getAllAssessmentFeedback(final Long assessmentId) {
        return getWithRestResult(format("%s/assessment/%s", assessmentFeedbackRestURL, assessmentId), assessmentFeedbackResourceListType());
    }

    @Override
    public RestResult<AssessmentFeedbackResource> getAssessmentFeedbackByAssessmentAndQuestion(final Long assessmentId, final Long questionId) {
        return getWithRestResult(format("%s/assessment/%s/question/%s", assessmentFeedbackRestURL, assessmentId, questionId), AssessmentFeedbackResource.class);
    }

    @Override
    public RestResult<Void> createAssessmentFeedback(final AssessmentFeedbackResource assessmentFeedback) {
        return postWithRestResult(format("%s/", assessmentFeedbackRestURL), assessmentFeedback, Void.class);
    }

    @Override
    public RestResult<Void> updateAssessmentFeedback(final Long assessmentFeedbackId, final AssessmentFeedbackResource assessmentFeedback) {
        return putWithRestResult(format("%s/%s", assessmentFeedbackRestURL, assessmentFeedbackId), assessmentFeedback, Void.class);
    }

    @Override
    public RestResult<Void> updateFeedbackValue(final Long assessmentId, final Long questionId, final String value) {
        return postWithRestResult(format("%s/assessment/%s/question/%s?feedback-value=%s", assessmentFeedbackRestURL, assessmentId, questionId, value), Void.class);
    }

    @Override
    public RestResult<Void> updateFeedbackScore(final Long assessmentId, final Long questionId, final Integer score) {
        return postWithRestResult(format("%s/assessment/%s/question/%s?feedback-score=%s", assessmentFeedbackRestURL, assessmentId, questionId, score), Void.class);
    }
}