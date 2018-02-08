package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.invite.domain.competition.AssessmentReviewPanelParticipant;
import org.innovateuk.ifs.invite.resource.AssessmentReviewPanelParticipantResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

/**
 * Provides the permissions around CRUD operations for {@link AssessmentReviewPanelParticipant} resources.
 */
@Component
@PermissionRules
public class AssessmentPanelParticipantPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "ACCEPT", description = "only the same user can accept a panel invitation")
    public boolean userCanAcceptAssessmentPanelInvite(AssessmentReviewPanelParticipantResource assessmentPanelParticipant, UserResource user) {
        return user != null &&
                assessmentPanelParticipant != null &&
                isSameUser(assessmentPanelParticipant, user);
    }

    @PermissionRule(value = "READ", description = "only the same user can read their panel participation")
    public boolean userCanViewTheirOwnAssessmentPanelParticipation(AssessmentReviewPanelParticipantResource assessmentPanelParticipant, UserResource user) {
        return isSameParticipant(assessmentPanelParticipant, user);
    }

    private static boolean isSameParticipant(AssessmentReviewPanelParticipantResource assessmentPanelParticipant, UserResource user) {
        return user.getId().equals(assessmentPanelParticipant.getUserId());
    }

    private static boolean isSameUser(AssessmentReviewPanelParticipantResource assessmentPanelParticipant, UserResource user) {
        if (isSameParticipant(assessmentPanelParticipant, user)) {
            return true;
        } else return assessmentPanelParticipant.getUserId() == null &&
                assessmentPanelParticipant.getInvite() != null &&
                user.getEmail().equals(assessmentPanelParticipant.getInvite().getEmail());
    }
}
