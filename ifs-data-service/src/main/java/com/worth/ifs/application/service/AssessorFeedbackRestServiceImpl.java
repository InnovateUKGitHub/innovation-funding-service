package com.worth.ifs.application.service;

import com.worth.ifs.application.resource.AssessorFeedbackResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AssessorFeedbackRestServiceImpl extends BaseRestService implements AssessorFeedbackRestService {

    @Value("${ifs.data.service.rest.assessorfeedback}")
    private String restUrl;

    @Override
    public RestResult<AssessorFeedbackResource> findOne(Long id) {
        return getWithRestResult(restUrl + "/" + id, AssessorFeedbackResource.class);
    }

    @Override
    public RestResult<AssessorFeedbackResource> findByAssessorId(Long assessorId) {
        return getWithRestResult(restUrl + "/findByAssessor/" + assessorId, AssessorFeedbackResource.class);
    }
}