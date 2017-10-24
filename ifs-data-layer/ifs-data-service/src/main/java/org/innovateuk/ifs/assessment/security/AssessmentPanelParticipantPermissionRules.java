package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.invite.resource.AssessmentPanelParticipantResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

/**
 * Provides the permissions around CRUD operations for {@link org.innovateuk.ifs.invite.domain.AssessmentPanelParticipant} resources.
 */
@Component
@PermissionRules
public class AssessmentPanelParticipantPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "ACCEPT", description = "only the same user can accept a panel invitation")
    public boolean userCanAcceptAssessmentPanelInvite(AssessmentPanelParticipantResource assessmentPanelParticipant, UserResource user) {
        return user != null &&
                assessmentPanelParticipant != null &&
                isSameUser(assessmentPanelParticipant, user);
    }

    @PermissionRule(value = "READ", description = "only the same user can read their panel participation")
    public boolean userCanViewTheirOwnAssessmentPanelParticipation(AssessmentPanelParticipantResource assessmentPanelParticipant, UserResource user) {
        return isSameParticipant(assessmentPanelParticipant, user);
    }

    private static boolean isSameParticipant(AssessmentPanelParticipantResource assessmentPanelParticipant, UserResource user) {
        return user.getId().equals(assessmentPanelParticipant.getUserId());
    }

    private static boolean isSameUser(AssessmentPanelParticipantResource assessmentPanelParticipant, UserResource user) {
        if (isSameParticipant(assessmentPanelParticipant, user)) {
            return true;
        } else if (assessmentPanelParticipant.getUserId() == null &&
                assessmentPanelParticipant.getInvite() != null &&
                user.getEmail().equals(assessmentPanelParticipant.getInvite().getEmail())) {
            return true;
        }
        return false;
    }
}
