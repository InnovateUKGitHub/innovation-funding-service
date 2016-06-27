package com.worth.ifs.assessment.controller;

import com.worth.ifs.assessment.resource.AssessmentFeedbackResource;
import com.worth.ifs.assessment.transactional.AssessmentFeedbackService;
import com.worth.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = "/assessment/{assessmentId}/question/{questionId}/feedback-value", method = RequestMethod.POST)
    public RestResult<Void> updateFeedbackValue(@PathVariable("assessmentId") final Long assessmentId, @PathVariable("questionId") final Long questionId, @RequestParam("value") final String value) {
        return assessmentFeedbackService.updateFeedbackValue(assessmentId, questionId, value).toPostResponse();
    }

    @RequestMapping(value = "/assessment/{assessmentId}/question/{questionId}/feedback-score", method = RequestMethod.POST)
    public RestResult<Void> updateFeedbackScore(@PathVariable("assessmentId") final Long assessmentId, @PathVariable("questionId") final Long questionId, @RequestParam("score") final Integer score) {
        return assessmentFeedbackService.updateFeedbackScore(assessmentId, questionId, score).toPostResponse();
    }
}
