package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.competition.domain.CompetitionParticipant;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

/**
 * Provides the permissions around CRUD operations for {@link CompetitionParticipant} resources.
 */
@Component
@PermissionRules
public class CompetitionParticipantPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "ACCEPT", description = "only the same user can accept an invitation")
    public boolean userCanAcceptCompetitionInvite(CompetitionParticipantResource competitionParticipant, UserResource user) {
        return user != null &&
                competitionParticipant != null &&
                isAssessor(user) &&
                isSameUser(competitionParticipant, user);
    }

    @PermissionRule(value = "READ", description = "only the same user can read their competition participation")
    public boolean userCanViewTheirOwnCompetitionParticipation(CompetitionParticipantResource competitionParticipant, UserResource user) {
        return isAssessor(user) && isSameParticipant(competitionParticipant, user);
    }

    private static boolean isSameParticipant(CompetitionParticipantResource competitionParticipant, UserResource user) {
        return user.getId().equals(competitionParticipant.getUserId());
    }

    private static boolean isAssessor(UserResource user) {
        return user.hasRole(Role.ASSESSOR);
    }

    private static boolean isSameUser(CompetitionParticipantResource competitionParticipant, UserResource user) {
        if (isSameParticipant(competitionParticipant, user) || (competitionParticipant.getUserId() == null &&
                competitionParticipant.getInvite() != null &&
                user.getEmail().equals(competitionParticipant.getInvite().getEmail()))) {
            return true;
        }
        return false;
    }
}
