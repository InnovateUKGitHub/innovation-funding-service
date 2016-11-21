package com.worth.ifs.workflow.transactional;

import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.commons.security.NotSecured;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.springframework.security.access.prepost.PostAuthorize;

public interface ProcessOutcomeService {
    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<ProcessOutcomeResource> findOne(Long id);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<ProcessOutcomeResource> findLatestByProcess(Long processId);

    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<ProcessOutcomeResource> findLatestByProcessAndOutcomeType(Long processId, String outcomeType);
}