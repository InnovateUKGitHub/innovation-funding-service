package org.innovateuk.ifs.assessment.panel.security;

import org.innovateuk.ifs.assessment.panel.domain.AssessmentReview;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewResource;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewState;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import static org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewState.ACCEPTED;
import static org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewState.PENDING;


/**
 * Provides the permissions around CRUD operations for {@link org.innovateuk.ifs.assessment.panel.domain.AssessmentReview} resources.
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

    @PermissionRule(value = "READ_REVIEWS", description = "Assessors can directly read Assessment Reviews that are accepted")
    public boolean userCanReadAssessmentReview(AssessmentReviewResource assessment, UserResource user) {
        Set<AssessmentReviewState> allowedStates = EnumSet.of(ACCEPTED);
        return isAssessorForAssessmentReview(assessment, user, allowedStates);
    }

    @PermissionRule(value = "READ_REVIEWS_TO_ASSIGN", description = "Assessors can read pending Assessment Reviews to decide to " +
            "either to accept or reject")
    public boolean userCanReadToAssign(AssessmentReviewResource assessment, UserResource user) {
        Set<AssessmentReviewState> allowedStates = Collections.singleton(PENDING);
        return isAssessorForAssessmentReview(assessment, user, allowedStates);
    }

    @PermissionRule(value = "READ_REVIEWS_TO_REJECT", description = "Assessors can reject Assessment Reviews that are pending or accepted")
    public boolean userCanReadToReject(AssessmentReviewResource assessmentReview, UserResource user) {
        Set<AssessmentReviewState> allowedStates = EnumSet.of(PENDING, ACCEPTED);
        return isAssessorForAssessmentReview(assessmentReview, user, allowedStates);
    }

    @PermissionRule(value = "UPDATE", description = "An assessor may only update their own invites to assessment reviews")
    public boolean userCanAcceptAssessmentReviews(AssessmentReviewResource assessmentReview, UserResource loggedInUser) {
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