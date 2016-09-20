package com.worth.ifs.assessment.transactional;

import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import com.worth.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Transactional and secured service providing operations around {@link com.worth.ifs.assessment.domain.AssessorFormInputResponse} data.
 */
public interface AssessorFormInputResponseService {

    @PreAuthorize("hasPermission(#assessmentId, 'com.worth.ifs.assessment.resource.AssessmentResource', 'READ')")
    ServiceResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponses(Long assessmentId);

    @PreAuthorize("hasPermission(#assessmentId, 'com.worth.ifs.assessment.resource.AssessmentResource', 'READ')")
    ServiceResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponsesByAssessmentAndQuestion(Long assessmentId, Long questionId);

    @PreAuthorize("hasPermission(#response, 'UPDATE')")
    ServiceResult<Void> updateFormInputResponse(AssessorFormInputResponseResource response);
}