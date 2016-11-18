package com.worth.ifs.workflow.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;

public interface ProcessOutcomeRestService {

    RestResult<ProcessOutcomeResource> findOne(Long id);

    RestResult<ProcessOutcomeResource> findLatestByProcessId(Long processId);

    RestResult<ProcessOutcomeResource> findLatestByProcessIdAndType(Long processId, String type);
}