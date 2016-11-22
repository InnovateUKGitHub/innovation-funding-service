package com.worth.ifs.workflow.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.springframework.stereotype.Service;

@Service
public class ProcessOutcomeRestServiceImpl extends BaseRestService implements ProcessOutcomeRestService {

    private String restUrl = "/processoutcome";

    @Override
    public RestResult<ProcessOutcomeResource> findOne(Long id) {
        return getWithRestResult(restUrl + "/" + id, ProcessOutcomeResource.class);
    }

    @Override
    public RestResult<ProcessOutcomeResource> findLatestByProcessId(Long processId) {
        return getWithRestResult(restUrl + "/process/" + processId, ProcessOutcomeResource.class);
    }

    @Override
    public RestResult<ProcessOutcomeResource> findLatestByProcessIdAndType(Long processId, String type) {
        return getWithRestResult(restUrl + "/process/" + processId + "/type/" + type, ProcessOutcomeResource.class);
    }
}