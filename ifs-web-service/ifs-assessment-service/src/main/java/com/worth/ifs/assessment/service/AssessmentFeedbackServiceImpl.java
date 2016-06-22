package com.worth.ifs.assessment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class contains methods to retrieve and store {@link com.worth.ifs.assessment.resource.AssessmentFeedbackResource} related data,
 * through the RestService {@link AssessmentFeedbackRestService}.
 */
@Service
public class AssessmentFeedbackServiceImpl implements AssessmentFeedbackService {

    @Autowired
    private AssessmentFeedbackRestService assessmentFeedbackRestService;

}