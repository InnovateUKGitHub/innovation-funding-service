package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.assessment.transactional.AssessmentService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * Exposes CRUD operations through a REST API to manage {@link org.innovateuk.ifs.assessment.domain.Assessment} related data.
 */
@RestController
@RequestMapping("/assessment")
public class AssessmentController {

    @Autowired
    private AssessmentService assessmentService;

    @RequestMapping(value = "/{id}", method = GET)
    public RestResult<AssessmentResource> findById(@PathVariable("id") long id) {
        return assessmentService.findById(id).toGetResponse();
    }

    @RequestMapping(value = "/{id}/assign", method = GET)
    public RestResult<AssessmentResource> findAssignableById(@PathVariable("id") long id) {
        return assessmentService.findAssignableById(id).toGetResponse();
    }

    @RequestMapping(value = "/{id}/rejectable", method = GET)
    public RestResult<AssessmentResource> findRejectableById(@PathVariable("id") long id) {
        return assessmentService.findRejectableById(id).toGetResponse();
    }

    @RequestMapping(value = "/user/{userId}/competition/{competitionId}", method = GET)
    public RestResult<List<AssessmentResource>> findByUserAndCompetition(
            @PathVariable("userId") long userId,
            @PathVariable("competitionId") long competitionId) {
        return assessmentService.findByUserAndCompetition(userId, competitionId).toGetResponse();
    }

    @RequestMapping(value = "/state/{state}/competition/{competitionId}/count", method = GET)
    public RestResult<Integer> countByStateAndCompetition(
            @PathVariable("state") AssessmentStates state,
            @PathVariable("competitionId") Long competitionId) {
        return assessmentService.countByStateAndCompetition(state, competitionId).toGetResponse();
    }

    @RequestMapping(value = "/{id}/score", method = GET)
    public RestResult<AssessmentTotalScoreResource> getTotalScore(@PathVariable("id") long id) {
        return assessmentService.getTotalScore(id).toGetResponse();
    }

    @RequestMapping(value = "/{id}/recommend", method = PUT)
    public RestResult<Void> recommend(@PathVariable("id") long id, @RequestBody @Valid AssessmentFundingDecisionOutcomeResource assessmentFundingDecision) {
        return assessmentService.recommend(id, assessmentFundingDecision).toPutResponse();
    }

    @RequestMapping(value = "/application/{applicationId}/feedback", method = GET)
    public RestResult<ApplicationAssessmentFeedbackResource> getApplicationFeedback(@PathVariable("applicationId") long applicationId) {
        return assessmentService.getApplicationFeedback(applicationId).toGetResponse();
    }

    @RequestMapping(value = "/{id}/rejectInvitation", method = PUT)
    public RestResult<Void> rejectInvitation(@PathVariable("id") long id, @RequestBody @Valid AssessmentRejectOutcomeResource assessmentRejectOutcomeResource) {
        return assessmentService.rejectInvitation(id, assessmentRejectOutcomeResource).toPutResponse();
    }

    @RequestMapping(value = "/{id}/acceptInvitation", method = PUT)
    public RestResult<Void> acceptInvitation(@PathVariable("id") long id) {
        return assessmentService.acceptInvitation(id).toPutResponse();
    }

    @RequestMapping(value = "/{id}/withdraw", method = PUT)
    public RestResult<Void> withdrawAssessment(@PathVariable("id") long id) {
        return assessmentService.withdrawAssessment(id).toPutResponse();
    }

    @RequestMapping(value = "/submitAssessments", method = PUT)
    public RestResult<Void> submitAssessments(@RequestBody @Valid AssessmentSubmissionsResource assessmentSubmissionsResource) {
        return assessmentService.submitAssessments(assessmentSubmissionsResource).toPutResponse();
    }

    @RequestMapping(method = POST)
    public RestResult<AssessmentResource> createAssessment(@RequestBody @Valid AssessmentCreateResource assessmentCreateResource) {
        return assessmentService.createAssessment(assessmentCreateResource).toPostCreateResponse();
    }
}
