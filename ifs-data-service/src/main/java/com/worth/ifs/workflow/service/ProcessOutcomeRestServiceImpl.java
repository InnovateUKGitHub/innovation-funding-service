package com.worth.ifs.workflow.service;

import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.springframework.stereotype.Service;

@Service
public class ProcessOutcomeRestServiceImpl extends BaseRestService implements ProcessOutcomeRestService {

    private String restUrl = "/processoutcome";

    @Override
    public ProcessOutcomeResource findOne(Long id) {
        return restGet(restUrl + "/" + id, ProcessOutcomeResource.class);
    }
}