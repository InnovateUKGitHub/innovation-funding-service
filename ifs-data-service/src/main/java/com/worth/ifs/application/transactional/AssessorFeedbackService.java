package com.worth.ifs.application.transactional;

import com.worth.ifs.application.resource.AssessorFeedbackResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.security.NotSecured;

public interface AssessorFeedbackService {

    @NotSecured("TODO")
    ServiceResult<AssessorFeedbackResource> findOne(Long id);
    @NotSecured("TODO")
    ServiceResult<AssessorFeedbackResource> findByAssessorId(Long assessorId);
}