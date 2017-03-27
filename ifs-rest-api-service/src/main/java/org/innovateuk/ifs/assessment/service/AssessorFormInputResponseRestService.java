package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessmentFeedbackAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.commons.rest.RestResult;

import java.util.List;

/**
 * Interface for CRUD operations on {org.innovateuk.ifs.assessment.domain.AssessorFormInputResponse} related data.
 */
public interface AssessorFormInputResponseRestService {

    RestResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponses(Long assessmentId);

    RestResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponsesByAssessmentAndQuestion(Long assessmentId, Long questionId);

    RestResult<Void> updateFormInputResponse(AssessorFormInputResponseResource response);

    RestResult<ApplicationAssessmentAggregateResource> getApplicationAssessmentAggregate(long applicationId);

    RestResult<AssessmentFeedbackAggregateResource> getAssessmentAggregateFeedback(long applicationId, long questionId);

}
