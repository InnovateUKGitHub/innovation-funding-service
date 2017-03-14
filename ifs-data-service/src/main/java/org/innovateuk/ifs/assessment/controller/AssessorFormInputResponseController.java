package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessmentFeedbackAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
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

    @RequestMapping(value = "/assessment/{assessmentId}", method = RequestMethod.GET)
    public RestResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponses(@PathVariable("assessmentId") Long assessmentId) {
        return assessorFormInputResponseService.getAllAssessorFormInputResponses(assessmentId).toGetResponse();
    }

    @RequestMapping(value = "/assessment/{assessmentId}/question/{questionId}", method = RequestMethod.GET)
    public RestResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponsesByAssessmentAndQuestion(@PathVariable("assessmentId") Long assessmentId, @PathVariable("questionId") Long questionId) {
        return assessorFormInputResponseService.getAllAssessorFormInputResponsesByAssessmentAndQuestion(assessmentId, questionId).toGetResponse();
    }

    @RequestMapping(method = RequestMethod.PUT)
    public RestResult<Void> updateFormInputResponse(@Valid @RequestBody AssessorFormInputResponseResource response) {
        return assessorFormInputResponseService.updateFormInputResponse(response).toPutResponse();
    }

    @RequestMapping(value = "/application/{applicationId}/scores", method = RequestMethod.GET)
    public RestResult<ApplicationAssessmentAggregateResource> getApplicationAggregateScores(@PathVariable("applicationId") long applicationId) {
        return assessorFormInputResponseService.getApplicationAggregateScores(applicationId).toGetResponse();
    }

    @RequestMapping(value = "/application/{applicationId}/question/{questionId}/feedback", method = RequestMethod.GET)
    public RestResult<AssessmentFeedbackAggregateResource> getAssessmentAggregateFeedback(@PathVariable("applicationId") long applicationId,
                                                                                          @PathVariable("questionId") long questionId) {
        return assessorFormInputResponseService.getAssessmentAggregateFeedback(applicationId, questionId).toGetResponse();
    }

}
