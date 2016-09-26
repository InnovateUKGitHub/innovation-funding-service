package com.worth.ifs.workflow.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.commons.security.NotSecured;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;

public interface ProcessOutcomeService {
    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ServiceResult<ProcessOutcomeResource> findOne(Long id);
}