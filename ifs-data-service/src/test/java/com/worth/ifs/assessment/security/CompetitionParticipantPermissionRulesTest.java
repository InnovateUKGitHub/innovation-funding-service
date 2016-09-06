package com.worth.ifs.assessment.security;

import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.invite.resource.CompetitionParticipantResource;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Test;

import static com.worth.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static com.worth.ifs.invite.builder.CompetitionParticipantResourceBuilder.newCompetitionParticipantResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static freemarker.template.utility.Collections12.singletonList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CompetitionParticipantPermissionRulesTest extends BasePermissionRulesTest<CompetitionParticipantPermissionRules> {

    @Override
    protected CompetitionParticipantPermissionRules supplyPermissionRulesUnderTest() {
        return new CompetitionParticipantPermissionRules();
    }

    @Test
    public void isSameUser() {
        CompetitionParticipantResource competitionParticipantResource = newCompetitionParticipantResource()
                .withUser(1L)
                .build();
        UserResource userResource = newUserResource()
                .withId(1L)
                .withRolesGlobal(singletonList(assessorRole()))
                .build();

        assertTrue(rules.userCanAcceptCompetitionInvite(competitionParticipantResource, userResource));
    }

    @Test
    public void isSameUser_differentParticipantUser() {
        CompetitionParticipantResource competitionParticipantResource = newCompetitionParticipantResource()
                .withUser(1L)
                .build();
        UserResource userResource = newUserResource()
                .withId(2L)
                .withRolesGlobal(singletonList(assessorRole()))
                .build();

        assertFalse(rules.userCanAcceptCompetitionInvite(competitionParticipantResource, userResource));
    }

    @Test
    public void isSameUser_noParticipantUserAndSameEmail() {
        CompetitionParticipantResource competitionParticipantResource = newCompetitionParticipantResource()
                .withInvite(newCompetitionInviteResource().withEmail("tom@poly.io"))
                .build();
        UserResource userResource = newUserResource()
                .withEmail("tom@poly.io")
                .withRolesGlobal(singletonList(assessorRole()))
                .build();

        assertTrue(rules.userCanAcceptCompetitionInvite(competitionParticipantResource, userResource));
    }

    @Test
    public void isSameUser_noParticipantUserAndDifferentEmail() {
        CompetitionParticipantResource competitionParticipantResource = newCompetitionParticipantResource()
                .withInvite(newCompetitionInviteResource().withEmail("tom@poly.io"))
                .build();
        UserResource userResource = newUserResource()
                .withEmail("non-existent-email@poly.io")
                .withRolesGlobal(singletonList(assessorRole()))
                .build();

        assertFalse(rules.userCanAcceptCompetitionInvite(competitionParticipantResource, userResource));
    }
}
