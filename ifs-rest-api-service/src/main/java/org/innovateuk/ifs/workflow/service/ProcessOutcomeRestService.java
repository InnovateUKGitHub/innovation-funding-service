package org.innovateuk.ifs.workflow.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.workflow.resource.ProcessOutcomeResource;

public interface ProcessOutcomeRestService {

    RestResult<ProcessOutcomeResource> findOne(Long id);

    RestResult<ProcessOutcomeResource> findLatestByProcessId(Long processId);

    RestResult<ProcessOutcomeResource> findLatestByProcessIdAndType(Long processId, String type);
}
