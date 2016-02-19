package com.worth.ifs.workflow.transactional;

import com.worth.ifs.security.NotSecured;
import com.worth.ifs.workflow.domain.ProcessOutcome;

public interface ProcessOutcomeService {
    @NotSecured("TODO")
    ProcessOutcome findOne(Long id);
}