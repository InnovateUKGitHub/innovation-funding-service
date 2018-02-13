package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.invite.resource.AssessmentInterviewPanelParticipantResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

/**
 * Provides the permissions around CRUD operations for {@link org.innovateuk.ifs.invite.domain.competition.AssessmentInterviewPanelParticipant} resources.
 */
@Component
@PermissionRules
public class AssessmentInterviewPanelParticipantPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "ACCEPT", description = "only the same user can accept an interview panel invitation")
    public boolean userCanAcceptAssessmentInterviewPanelInvite(AssessmentInterviewPanelParticipantResource assessmentInterviewPanelParticipant, UserResource user) {
        return user != null &&
                assessmentInterviewPanelParticipant != null &&
                isSameUser(assessmentInterviewPanelParticipant, user);
    }

    @PermissionRule(value = "READ", description = "only the same user can read their interview panel participation")
    public boolean userCanViewTheirOwnAssessmentPanelParticipation(AssessmentInterviewPanelParticipantResource assessmentInterviewPanelParticipant, UserResource user) {
        return isSameParticipant(assessmentInterviewPanelParticipant, user);
    }

    private static boolean isSameParticipant(AssessmentInterviewPanelParticipantResource assessmentInterviewPanelParticipant, UserResource user) {
        return user.getId().equals(assessmentInterviewPanelParticipant.getUserId());
    }

    private static boolean isSameUser(AssessmentInterviewPanelParticipantResource assessmentInterviewPanelParticipant, UserResource user) {
        return isSameParticipant(assessmentInterviewPanelParticipant, user)
                || assessmentInterviewPanelParticipant.getUserId() == null
                && assessmentInterviewPanelParticipant.getInvite() != null
                && user.getEmail().equals(assessmentInterviewPanelParticipant.getInvite().getEmail());
    }
}
