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

    static final String assessorFormInputResponseRestUrl = "/assessorFormInputResponse";

    @Override
    public RestResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponses(long assessmentId) {
        return getWithRestResult(format("%s/assessment/%s", assessorFormInputResponseRestUrl, assessmentId), assessorFormInputResponseResourceListType());
    }

    @Override
    public RestResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponsesByAssessmentAndQuestion(long assessmentId, long questionId) {
        return getWithRestResult(format("%s/assessment/%s/question/%s", assessorFormInputResponseRestUrl, assessmentId, questionId), assessorFormInputResponseResourceListType());
    }

    @Override
    public RestResult<Void> updateFormInputResponse(long assessmentId, long formInputId, String value) {
        return updateFormInputResponses(new AssessorFormInputResponsesResource(
                new AssessorFormInputResponseResource(assessmentId, formInputId, value)));
    }

    @Override
    public RestResult<Void> updateFormInputResponses(AssessorFormInputResponsesResource assessorFormInputResponseResources) {
        return putWithRestResult(format("%s", assessorFormInputResponseRestUrl), assessorFormInputResponseResources, Void.class);
    }

    @Override
    public RestResult<ApplicationAssessmentAggregateResource> getApplicationAssessmentAggregate(long applicationId) {
        return getWithRestResult(format("%s/application/%s/scores", assessorFormInputResponseRestUrl, applicationId), ApplicationAssessmentAggregateResource.class);
    }

    @Override
    public RestResult<AssessmentFeedbackAggregateResource> getAssessmentAggregateFeedback(long applicationId, long questionId) {
        return getWithRestResult(format("%s/application/%s/question/%s/feedback", assessorFormInputResponseRestUrl, applicationId, questionId), AssessmentFeedbackAggregateResource.class);
    }

    @Override
    public RestResult<AssessmentDetailsResource> getAssessmentDetails(long assessmentId) {
        return getWithRestResult(format("%s/assessment/%s/details", assessorFormInputResponseRestUrl, assessmentId), AssessmentDetailsResource.class);
    }
}
