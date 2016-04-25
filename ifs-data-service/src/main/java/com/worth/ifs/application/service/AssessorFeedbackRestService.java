package com.worth.ifs.application.service;

import com.worth.ifs.application.resource.AssessorFeedbackResource;
import com.worth.ifs.commons.rest.RestResult;

public interface AssessorFeedbackRestService {

    RestResult<AssessorFeedbackResource> findOne(Long id);
    RestResult<AssessorFeedbackResource> findByAssessorId(Long assessorId);
}