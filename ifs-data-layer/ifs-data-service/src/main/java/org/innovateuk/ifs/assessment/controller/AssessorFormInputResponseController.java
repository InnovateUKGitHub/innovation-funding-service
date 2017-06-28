package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.assessment.transactional.AssessorFormInputResponseService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Exposes CRUD operations through a REST API to manage {@link org.innovateuk.ifs.assessment.domain.AssessorFormInputResponse} related data.
 */
@RestController
@RequestMapping("/assessorFormInputResponse")
public class AssessorFormInputResponseController {

    @Autowired
    private AssessorFormInputResponseService assessorFormInputResponseService;

    @GetMapping("/assessment/{assessmentId}")
    public RestResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponses(@PathVariable("assessmentId") long assessmentId) {
        return assessorFormInputResponseService.getAllAssessorFormInputResponses(assessmentId).toGetResponse();
    }

    @GetMapping("/assessment/{assessmentId}/question/{questionId}")
    public RestResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponsesByAssessmentAndQuestion(@PathVariable("assessmentId") long assessmentId, @PathVariable("questionId") long questionId) {
        return assessorFormInputResponseService.getAllAssessorFormInputResponsesByAssessmentAndQuestion(assessmentId, questionId).toGetResponse();
    }

    @PutMapping
    public RestResult<Void> updateFormInputResponses(@Valid @RequestBody AssessorFormInputResponsesResource responses) {
        return assessorFormInputResponseService.updateFormInputResponses(responses).toPostWithBodyResponse();
    }

    @GetMapping("/application/{applicationId}/scores")
    public RestResult<ApplicationAssessmentAggregateResource> getApplicationAggregateScores(@PathVariable("applicationId") long applicationId) {
        return assessorFormInputResponseService.getApplicationAggregateScores(applicationId).toGetResponse();
    }

    @GetMapping("/application/{applicationId}/question/{questionId}/feedback")
    public RestResult<AssessmentFeedbackAggregateResource> getAssessmentAggregateFeedback(@PathVariable("applicationId") long applicationId,
                                                                                          @PathVariable("questionId") long questionId) {
        return assessorFormInputResponseService.getAssessmentAggregateFeedback(applicationId, questionId).toGetResponse();
    }

    @GetMapping("/assessment/{assessmentId}/details")
    public RestResult<AssessmentDetailsResource> getAssessmentDetails(@PathVariable("assessmentId") long assessmentId) {
        return assessorFormInputResponseService.getAssessmentDetails(assessmentId).toGetResponse();
    }
}
