package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.commons.rest.RestResult;

import java.util.List;

/**
 * Interface for CRUD operations on {org.innovateuk.ifs.assessment.domain.AssessorFormInputResponse} related data.
 */
public interface AssessorFormInputResponseRestService {

    RestResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponses(long assessmentId);

    RestResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponsesByAssessmentAndQuestion(long assessmentId, long questionId);

    RestResult<Void> updateFormInputResponse(long assessmentId, long formInputId, String value);

    RestResult<Void> updateFormInputResponses(AssessorFormInputResponsesResource assessorFormInputResponseResources);

    RestResult<ApplicationAssessmentAggregateResource> getApplicationAssessmentAggregate(long applicationId);

    RestResult<AssessmentFeedbackAggregateResource> getAssessmentAggregateFeedback(long applicationId, long questionId);

    RestResult<AssessmentDetailsResource> getAssessmentDetails(long assessmentId);
}
