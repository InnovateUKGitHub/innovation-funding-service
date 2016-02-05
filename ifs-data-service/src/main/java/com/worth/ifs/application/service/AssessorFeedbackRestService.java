package com.worth.ifs.application.service;

import com.worth.ifs.application.resource.AssessorFeedbackResource;
import com.worth.ifs.security.NotSecured;

public interface AssessorFeedbackRestService {
    @NotSecured("REST Service")
    AssessorFeedbackResource findOne(Long id);
}