package org.innovateuk.ifs.review.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.review.domain.Review;
import org.innovateuk.ifs.review.resource.ReviewResource;
import org.innovateuk.ifs.review.resource.ReviewState;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;

import static org.innovateuk.ifs.review.resource.ReviewState.ACCEPTED;
import static org.innovateuk.ifs.review.resource.ReviewState.PENDING;


/**
 * Provides the permissions around CRUD operations for {@link Review} resources.
 */
@Component
@PermissionRules
public class ReviewPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "READ_PANEL_DASHBOARD", description = "Assessors can view all Assessment Reviews on the competition " +
            "dashboard, except those rejected or withdrawn")
    public boolean userCanReadAssessmentReviewOnDashboard(ReviewResource assessmentReview, UserResource user) {
        Set<ReviewState> allowedStates = EnumSet.of(PENDING, ACCEPTED);
        return isAssessorForAssessmentReview(assessmentReview, user, allowedStates);
    }

    @PermissionRule(value = "UPDATE", description = "An assessor may only update their own invites to assessment reviews")
    public boolean userCanUpdateAssessmentReview(ReviewResource assessmentReview, UserResource loggedInUser) {
        return isAssessorForAssessmentReview(assessmentReview, loggedInUser);
    }

    @PermissionRule(value = "READ", description = "An assessor may only read their own invites to assessment reviews")
    public boolean userCanReadAssessmentReviews(ReviewResource assessmentReview, UserResource loggedInUser) {
        return isAssessorForAssessmentReview(assessmentReview, loggedInUser);
    }

    private boolean isAssessorForAssessmentReview(ReviewResource assessmentReview, UserResource user, Set<ReviewState> allowedStates) {
        return isAssessorForAssessmentReview(assessmentReview, user) && assessmentReviewIsInState(assessmentReview, allowedStates);
    }

    private boolean isAssessorForAssessmentReview(ReviewResource assessmentReview, UserResource user) {
        Long assessmentUser = processRoleRepository.findById(assessmentReview.getProcessRole()).get().getUser().getId();
        return user.getId().equals(assessmentUser);
    }

    private boolean assessmentReviewIsInState(ReviewResource reviewResource, Set<ReviewState> allowedStates) {
        Review review = reviewRepository.findById(reviewResource.getId()).get();
        return allowedStates.contains(review.getProcessState());
    }
}