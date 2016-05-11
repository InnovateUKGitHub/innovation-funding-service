package com.worth.ifs.workflow.transactional;

import com.worth.ifs.security.NotSecured;
import com.worth.ifs.workflow.domain.ProcessOutcome;

public interface ProcessOutcomeService {
    @NotSecured(value = "TODO", mustBeSecuredByOtherServices = false)
    ProcessOutcome findOne(Long id);
}