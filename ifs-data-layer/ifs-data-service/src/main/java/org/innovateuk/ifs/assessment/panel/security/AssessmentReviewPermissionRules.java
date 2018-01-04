package org.innovateuk.ifs.assessment.panel.security;

import org.innovateuk.ifs.assessment.panel.domain.AssessmentReview;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewResource;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.springframework.stereotype.Component;

/**
 * Provides the permissions around CRUD operations for {@link AssessmentReview}s resources.
 */
@Component
@PermissionRules
public class AssessmentReviewPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "UPDATE", description = "An assessor may only update their own invites to assessment reviews")
    public boolean userCanAcceptAssessmentReviews(AssessmentReviewResource assessmentReview, UserResource loggedInUser) {
        return assessmentReview != null &&
                loggedInUser != null &&
                isAssessor(loggedInUser) &&
                processRoleRepository.findOne(assessmentReview.getProcessRole()).getUser().equals(loggedInUser);
    }

    private static boolean isAssessor(UserResource user) {
        return user.hasRole(UserRoleType.ASSESSOR);
    }
}