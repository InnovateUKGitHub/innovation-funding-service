package com.worth.ifs.assessment.transactional;

import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.security.NotSecured;
import com.worth.ifs.workflow.domain.ProcessOutcome;

/**
 * Transactional and secured service providing operations around {@link com.worth.ifs.assessment.domain.Assessment} data.
 */
public interface AssessmentService {

    @NotSecured(value="TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<AssessmentResource> findById(final Long id);

    @NotSecured(value="TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> updateStatus(final Long assessmentId, final ProcessOutcome processOutcome);

}
