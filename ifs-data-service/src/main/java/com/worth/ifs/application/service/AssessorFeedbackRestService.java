package com.worth.ifs.application.service;

import com.worth.ifs.application.resource.AssessorFeedbackResource;

public interface AssessorFeedbackRestService {
    AssessorFeedbackResource findOne(Long id);
}