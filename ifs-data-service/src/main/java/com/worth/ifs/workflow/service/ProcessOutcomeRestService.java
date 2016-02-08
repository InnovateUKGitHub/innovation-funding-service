package com.worth.ifs.workflow.service;

import com.worth.ifs.workflow.resource.ProcessOutcomeResource;

public interface ProcessOutcomeRestService {
    ProcessOutcomeResource findOne(Long id);
}