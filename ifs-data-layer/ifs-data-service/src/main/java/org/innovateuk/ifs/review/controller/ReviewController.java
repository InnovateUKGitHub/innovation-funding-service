package org.innovateuk.ifs.review.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.review.resource.ReviewRejectOutcomeResource;
import org.innovateuk.ifs.review.resource.ReviewResource;
import org.innovateuk.ifs.review.transactional.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller for managing applications on an assessment panel.
 */
@RestController
@RequestMapping("/assessmentpanel")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/assign-application/{applicationId}")
    public RestResult<Void> assignApplication(@PathVariable long applicationId) {
        return reviewService.assignApplicationToPanel(applicationId).toPostResponse();
    }

    @PostMapping("/unassign-application/{applicationId}")
    public RestResult<Void> unAssignApplication(@PathVariable long applicationId) {
        return reviewService.unassignApplicationFromPanel(applicationId).toPostResponse();
    }

    @PostMapping("/notify-assessors/{competitionId}")
    public RestResult<Void> notifyAssessors(@PathVariable("competitionId") long competitionId) {
        return reviewService.createAndNotifyReviews(competitionId).toPostResponse();
    }

    @GetMapping("/notify-assessors/{competitionId}")
    public RestResult<Boolean> isPendingReviewNotifications(@PathVariable("competitionId") long competitionId) {
        return reviewService.isPendingReviewNotifications(competitionId).toGetResponse();
    }

    @GetMapping("/user/{userId}/competition/{competitionId}")
    public RestResult<List<ReviewResource>> getAssessmentReviews(
            @PathVariable("userId") long userId,
            @PathVariable("competitionId") long competitionId) {
        return reviewService.getAssessmentReviews(userId, competitionId).toGetResponse();
    }

    @GetMapping("/review/{assessmentReviewId}")
    public RestResult<ReviewResource> getAssessmentReview(@PathVariable("assessmentReviewId") long assessmentReviewId) {
        return reviewService.getAssessmentReview(assessmentReviewId).toGetResponse();
    }

    @PutMapping("/review/{id}/accept")
    public RestResult<Void> acceptInvitation(@PathVariable("id") long id) {
        return reviewService.acceptAssessmentReview(id).toPutResponse();
    }

    @PutMapping("/review/{id}/reject")
    public RestResult<Void> rejectInvitation(@PathVariable("id") long id,
                                             @RequestBody @Valid ReviewRejectOutcomeResource reviewRejectOutcomeResource) {
        return reviewService.rejectAssessmentReview(id, reviewRejectOutcomeResource).toPutResponse();
    }
}