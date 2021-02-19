package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.assessorFormInputResponseResourceListType;

/**
 * AssessorFormInputResponseRestServiceImpl is a utility for CRUD operations on {org.innovateuk.ifs.assessment.domain.AssessorFormInputResponse}.
 * This class connects to the {org.innovateuk.ifs.assessment.controller.AssessorFormInputResponseController}
 * through a REST call.
 */
@Service
public class AssessorFormInputResponseRestServiceImpl extends BaseRestService implements AssessorFormInputResponseRestService {

    static final String ASSESSOR_FORM_INPUT_RESPONSE_REST_URL = "/assessor-form-input-response";

    @Override
    public RestResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponses(long assessmentId) {
        return getWithRestResult(format("%s/assessment/%s", ASSESSOR_FORM_INPUT_RESPONSE_REST_URL, assessmentId), assessorFormInputResponseResourceListType());
    }

    @Override
    public RestResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponsesByAssessmentAndQuestion(long assessmentId, long questionId) {
        return getWithRestResult(format("%s/assessment/%s/question/%s", ASSESSOR_FORM_INPUT_RESPONSE_REST_URL, assessmentId, questionId), assessorFormInputResponseResourceListType());
    }

    @Override
    public RestResult<Void> updateFormInputResponse(long assessmentId, long formInputId, String value) {
        return updateFormInputResponses(new AssessorFormInputResponsesResource(
                new AssessorFormInputResponseResource(assessmentId, formInputId, value)));
    }

    @Override
    public RestResult<Void> updateFormInputResponses(AssessorFormInputResponsesResource assessorFormInputResponseResources) {
        return putWithRestResult(format("%s", ASSESSOR_FORM_INPUT_RESPONSE_REST_URL), assessorFormInputResponseResources, Void.class);
    }

    @Override
    public RestResult<ApplicationAssessmentsResource> getApplicationAssessments(long applicationId) {
        return getWithRestResult(format("%s/application/%d", ASSESSOR_FORM_INPUT_RESPONSE_REST_URL, applicationId), ApplicationAssessmentsResource.class);
    }

    @Override
    public RestResult<ApplicationAssessmentResource> getApplicationAssessment(long applicationId, long assessment) {
        return getWithRestResult(format("%s/application/%d/assessment/%d", ASSESSOR_FORM_INPUT_RESPONSE_REST_URL, applicationId, assessment), ApplicationAssessmentResource.class);
    }

    @Override
    public RestResult<ApplicationAssessmentAggregateResource> getApplicationAssessmentAggregate(long applicationId) {
        return getWithRestResult(format("%s/application/%s/scores", ASSESSOR_FORM_INPUT_RESPONSE_REST_URL, applicationId), ApplicationAssessmentAggregateResource.class);
    }

    @Override
    public RestResult<AssessmentFeedbackAggregateResource> getAssessmentAggregateFeedback(long applicationId, long questionId) {
        return getWithRestResult(format("%s/application/%s/question/%s/feedback", ASSESSOR_FORM_INPUT_RESPONSE_REST_URL, applicationId, questionId), AssessmentFeedbackAggregateResource.class);
    }

    @Override
    public RestResult<AssessmentDetailsResource> getAssessmentDetails(long assessmentId) {
        return getWithRestResult(format("%s/assessment/%s/details", ASSESSOR_FORM_INPUT_RESPONSE_REST_URL, assessmentId), AssessmentDetailsResource.class);
    }
}
