package com.worth.ifs.assessment.controller;

import com.worth.ifs.assessment.transactional.AssessmentFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes CRUD operations through a REST API to manage {@link com.worth.ifs.assessment.domain.AssessmentFeedback} related data.
 */
@RestController
@RequestMapping("/assessment-feedback")
public class AssessmentFeedbackController {

    @Autowired
    private AssessmentFeedbackService assessmentFeedbackService;

}
