package com.worth.ifs.application.transactional;

import com.worth.ifs.application.resource.AssessorFeedbackResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.security.NotSecured;

public interface AssessorFeedbackService {

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<AssessorFeedbackResource> findOne(Long id);
    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<AssessorFeedbackResource> findByAssessorId(Long assessorId);
}