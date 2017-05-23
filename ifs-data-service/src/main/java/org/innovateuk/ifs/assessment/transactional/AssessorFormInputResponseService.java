package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Transactional and secured service providing operations around {@link org.innovateuk.ifs.assessment.domain.AssessorFormInputResponse} data.
 */
public interface AssessorFormInputResponseService {

    @PreAuthorize("hasPermission(#assessmentId, 'org.innovateuk.ifs.assessment.resource.AssessmentResource', 'READ')")
    ServiceResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponses(long assessmentId);

    @PreAuthorize("hasPermission(#assessmentId, 'org.innovateuk.ifs.assessment.resource.AssessmentResource', 'READ')")
    ServiceResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponsesByAssessmentAndQuestion(long assessmentId, long questionId);

    @PreAuthorize("hasPermission(#responses, 'UPDATE')")
    ServiceResult<Void> updateFormInputResponses(AssessorFormInputResponsesResource responses);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<ApplicationAssessmentAggregateResource> getApplicationAggregateScores(long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<AssessmentFeedbackAggregateResource> getAssessmentAggregateFeedback(long applicationId, long questionId);

    @PreAuthorize("hasPermission(#assessmentId, 'org.innovateuk.ifs.assessment.resource.AssessmentResource', 'READ')")
    ServiceResult<AssessmentDetailsResource> getAssessmentDetails(long assessmentId);
}

