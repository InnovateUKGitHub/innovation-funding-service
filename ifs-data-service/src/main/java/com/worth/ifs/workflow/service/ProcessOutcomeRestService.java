package com.worth.ifs.workflow.service;

import com.worth.ifs.security.NotSecured;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;

public interface ProcessOutcomeRestService {
    @NotSecured("REST Service")
    ProcessOutcomeResource findOne(Long id);
}