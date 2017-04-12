package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessmentFeedbackAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Transactional and secured service providing operations around {@link org.innovateuk.ifs.assessment.domain.AssessorFormInputResponse} data.
 */
public interface AssessorFormInputResponseService {

    @PreAuthorize("hasPermission(#assessmentId, 'org.innovateuk.ifs.assessment.resource.AssessmentResource', 'READ')")
    ServiceResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponses(Long assessmentId);

    @PreAuthorize("hasPermission(#assessmentId, 'org.innovateuk.ifs.assessment.resource.AssessmentResource', 'READ')")
    ServiceResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponsesByAssessmentAndQuestion(Long assessmentId, Long questionId);

    @PreAuthorize("hasPermission(#response, 'UPDATE')")
    ServiceResult<Void> updateFormInputResponse(AssessorFormInputResponseResource response);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<ApplicationAssessmentAggregateResource> getApplicationAggregateScores(long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<AssessmentFeedbackAggregateResource> getAssessmentAggregateFeedback(long applicationId, long questionId);

    @PreAuthorize("hasPermission(#response, 'UPDATE')")
    FormInputResponse mapToFormInputResponse(AssessorFormInputResponseResource response);
}

