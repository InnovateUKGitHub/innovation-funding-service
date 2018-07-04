package org.innovateuk.ifs.review.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.invite.resource.ReviewParticipantResource;
import org.innovateuk.ifs.review.domain.ReviewParticipant;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

/**
 * Provides the permissions around CRUD operations for {@link ReviewParticipant} resources.
 */
@Component
@PermissionRules
public class ReviewParticipantPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "ACCEPT", description = "only the same user can accept a panel invitation")
    public boolean userCanAcceptAssessmentPanelInvite(ReviewParticipantResource assessmentPanelParticipant, UserResource user) {
        return user != null &&
                assessmentPanelParticipant != null &&
                isSameUser(assessmentPanelParticipant, user);
    }

    @PermissionRule(value = "READ", description = "only the same user can read their panel participation")
    public boolean userCanViewTheirOwnAssessmentPanelParticipation(ReviewParticipantResource assessmentPanelParticipant, UserResource user) {
        return isSameParticipant(assessmentPanelParticipant, user);
    }

    private static boolean isSameParticipant(ReviewParticipantResource assessmentPanelParticipant, UserResource user) {
        return user.getId().equals(assessmentPanelParticipant.getUserId());
    }

    private static boolean isSameUser(ReviewParticipantResource assessmentPanelParticipant, UserResource user) {
        return isSameParticipant(assessmentPanelParticipant, user) || assessmentPanelParticipant.getUserId() == null
                && assessmentPanelParticipant.getInvite() != null
                && user.getEmail().equals(assessmentPanelParticipant.getInvite().getEmail());
    }
}
