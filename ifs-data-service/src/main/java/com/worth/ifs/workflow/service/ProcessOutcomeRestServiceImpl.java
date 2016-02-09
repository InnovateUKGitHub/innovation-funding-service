package com.worth.ifs.workflow.service;

import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ProcessOutcomeRestServiceImpl extends BaseRestService implements ProcessOutcomeRestService {
    @Value("${ifs.data.service.rest.processoutcome}")
    private String restUrl;


    @Override
    public ProcessOutcomeResource findOne(Long id) {
        return restGet(restUrl + "/" + id, ProcessOutcomeResource.class);
    }
}