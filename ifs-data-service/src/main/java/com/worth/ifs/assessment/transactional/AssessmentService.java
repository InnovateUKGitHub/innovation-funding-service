package com.worth.ifs.assessment.transactional;

import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Transactional and secured service providing operations around {@link com.worth.ifs.assessment.domain.Assessment} data.
 */
public interface AssessmentService {

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<AssessmentResource> findById(Long id);

    @PreAuthorize("hasPermission(#assessmentId, 'com.worth.ifs.assessment.resource.AssessmentResource', 'UPDATE')")
    ServiceResult<Void> recommend(Long assessmentId, ProcessOutcomeResource processOutcome);

    @PreAuthorize("hasPermission(#assessmentId, 'com.worth.ifs.assessment.resource.AssessmentResource', 'UPDATE')")
    ServiceResult<Void> rejectInvitation(Long assessmentId, ProcessOutcomeResource processOutcome);
}
