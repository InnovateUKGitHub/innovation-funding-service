package org.innovateuk.ifs.assessment.review.security;

import org.innovateuk.ifs.assessment.review.domain.AssessmentReview;
import org.innovateuk.ifs.assessment.review.resource.AssessmentReviewResource;
import org.innovateuk.ifs.assessment.review.resource.AssessmentReviewState;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;

import static org.innovateuk.ifs.assessment.review.resource.AssessmentReviewState.ACCEPTED;
import static org.innovateuk.ifs.assessment.review.resource.AssessmentReviewState.PENDING;


/**
 * Provides the permissions around CRUD operations for {@link org.innovateuk.ifs.assessment.review.domain.AssessmentReview} resources.
 */
@Component
@PermissionRules
public class AssessmentReviewPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "READ_PANEL_DASHBOARD", description = "Assessors can view all Assessment Reviews on the competition " +
            "dashboard, except those rejected or withdrawn")
    public boolean userCanReadAssessmentReviewOnDashboard(AssessmentReviewResource assessmentReview, UserResource user) {
        Set<AssessmentReviewState> allowedStates = EnumSet.of(PENDING, ACCEPTED);
        return isAssessorForAssessmentReview(assessmentReview, user, allowedStates);
    }

    @PermissionRule(value = "UPDATE", description = "An assessor may only update their own invites to assessment reviews")
    public boolean userCanUpdateAssessmentReview(AssessmentReviewResource assessmentReview, UserResource loggedInUser) {
        return isAssessorForAssessmentReview(assessmentReview, loggedInUser);
    }

    @PermissionRule(value = "READ", description = "An assessor may only read their own invites to assessment reviews")
    public boolean userCanReadAssessmentReviews(AssessmentReviewResource assessmentReview, UserResource loggedInUser) {
        return isAssessorForAssessmentReview(assessmentReview, loggedInUser);
    }

    private boolean isAssessorForAssessmentReview(AssessmentReviewResource assessmentReview, UserResource user, Set<AssessmentReviewState> allowedStates) {
        return isAssessorForAssessmentReview(assessmentReview, user) && assessmentReviewIsInState(assessmentReview, allowedStates);
    }

    private boolean isAssessorForAssessmentReview(AssessmentReviewResource assessmentReview, UserResource user) {
        Long assessmentUser = processRoleRepository.findOne(assessmentReview.getProcessRole()).getUser().getId();
        return user.getId().equals(assessmentUser);
    }

    private boolean assessmentReviewIsInState(AssessmentReviewResource assessmentReviewResource, Set<AssessmentReviewState> allowedStates) {
        AssessmentReview assessmentReview = assessmentReviewRepository.findOne(assessmentReviewResource.getId());
        return allowedStates.contains(assessmentReview.getActivityState());
    }
}