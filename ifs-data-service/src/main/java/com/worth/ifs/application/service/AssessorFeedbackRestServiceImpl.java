package com.worth.ifs.application.service;

import com.worth.ifs.application.resource.AssessorFeedbackResource;
import com.worth.ifs.commons.service.BaseRestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AssessorFeedbackRestServiceImpl extends BaseRestService implements AssessorFeedbackRestService {
    @Value("${ifs.data.service.rest.assessorfeedback}")
    private String restUrl;


    @Override
    public AssessorFeedbackResource findOne(Long id) {
        return restGet(restUrl + "/" + id, AssessorFeedbackResource.class);
    }
}