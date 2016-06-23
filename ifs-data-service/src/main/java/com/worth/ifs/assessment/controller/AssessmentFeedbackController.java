package com.worth.ifs.assessment.controller;

import com.worth.ifs.assessment.resource.AssessmentFeedbackResource;
import com.worth.ifs.assessment.transactional.AssessmentFeedbackService;
import com.worth.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Exposes CRUD operations through a REST API to manage {@link com.worth.ifs.assessment.domain.AssessmentFeedback} related data.
 */
@RestController
@RequestMapping("/assessment-feedback")
public class AssessmentFeedbackController {

    @Autowired
    private AssessmentFeedbackService assessmentFeedbackService;

    @RequestMapping(value = "/assessment/{assessmentId}", method = RequestMethod.GET)
    public RestResult<List<AssessmentFeedbackResource>> getAllAssessmentFeedback(@PathVariable("assessmentId") final Long assessmentId) {
        return assessmentFeedbackService.getAllAssessmentFeedback(assessmentId).toGetResponse();
    }

    @RequestMapping(value = "/assessment/{assessmentId}/question/{questionId}", method = RequestMethod.GET)
    public RestResult<AssessmentFeedbackResource> getAssessmentFeedbackByAssessmentAndQuestion(@PathVariable("assessmentId") final Long assessmentId, @PathVariable("questionId") final Long questionId) {
        return assessmentFeedbackService.getAssessmentFeedbackByAssessmentAndQuestion(assessmentId, questionId).toGetResponse();
    }
}
