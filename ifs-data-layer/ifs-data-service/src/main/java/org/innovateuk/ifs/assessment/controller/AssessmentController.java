package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.assessment.transactional.AssessmentService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Exposes CRUD operations through a REST API to manage {@link org.innovateuk.ifs.assessment.domain.Assessment} related data.
 */
@RestController
@RequestMapping("/assessment")
public class AssessmentController {

    @Autowired
    private AssessmentService assessmentService;

    @GetMapping("/{id}")
    public RestResult<AssessmentResource> findById(@PathVariable("id") long id) {
        return assessmentService.findById(id).toGetResponse();
    }

    @GetMapping("/{id}/assign")
    public RestResult<AssessmentResource> findAssignableById(@PathVariable("id") long id) {
        return assessmentService.findAssignableById(id).toGetResponse();
    }

    @GetMapping("/{id}/rejectable")
    public RestResult<AssessmentResource> findRejectableById(@PathVariable("id") long id) {
        return assessmentService.findRejectableById(id).toGetResponse();
    }

    @GetMapping("/user/{userId}/competition/{competitionId}")
    public RestResult<List<AssessmentResource>> findByUserAndCompetition(
            @PathVariable("userId") long userId,
            @PathVariable("competitionId") long competitionId) {
        return assessmentService.findByUserAndCompetition(userId, competitionId).toGetResponse();
    }

    @GetMapping("/state/{state}/competition/{competitionId}/count")
    public RestResult<Integer> countByStateAndCompetition(
            @PathVariable("state") AssessmentStates state,
            @PathVariable("competitionId") Long competitionId) {
        return assessmentService.countByStateAndCompetition(state, competitionId).toGetResponse();
    }

    @GetMapping("/{id}/score")
    public RestResult<AssessmentTotalScoreResource> getTotalScore(@PathVariable("id") long id) {
        return assessmentService.getTotalScore(id).toGetResponse();
    }

    @PutMapping("/{id}/recommend")
    public RestResult<Void> recommend(@PathVariable("id") long id, @RequestBody @Valid AssessmentFundingDecisionOutcomeResource assessmentFundingDecision) {
        return assessmentService.recommend(id, assessmentFundingDecision).toPutResponse();
    }

    @GetMapping("/application/{applicationId}/feedback")
    public RestResult<ApplicationAssessmentFeedbackResource> getApplicationFeedback(@PathVariable("applicationId") long applicationId) {
        return assessmentService.getApplicationFeedback(applicationId).toGetResponse();
    }

    @PutMapping("/{id}/rejectInvitation")
    public RestResult<Void> rejectInvitation(@PathVariable("id") long id, @RequestBody @Valid AssessmentRejectOutcomeResource assessmentRejectOutcomeResource) {
        return assessmentService.rejectInvitation(id, assessmentRejectOutcomeResource).toPutResponse();
    }

    @PutMapping("/{id}/acceptInvitation")
    public RestResult<Void> acceptInvitation(@PathVariable("id") long id) {
        return assessmentService.acceptInvitation(id).toPutResponse();
    }

    @PutMapping("/{id}/withdraw")
    public RestResult<Void> withdrawAssessment(@PathVariable("id") long id) {
        return assessmentService.withdrawAssessment(id).toPutResponse();
    }

    @PutMapping("/submitAssessments")
    public RestResult<Void> submitAssessments(@RequestBody @Valid AssessmentSubmissionsResource assessmentSubmissionsResource) {
        return assessmentService.submitAssessments(assessmentSubmissionsResource).toPutResponse();
    }

    @PostMapping
    public RestResult<AssessmentResource> createAssessment(@RequestBody @Valid AssessmentCreateResource assessmentCreateResource) {
        return assessmentService.createAssessment(assessmentCreateResource).toPostCreateResponse();
    }
}
