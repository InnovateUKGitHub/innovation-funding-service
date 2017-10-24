package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.invite.resource.AssessmentPanelParticipantResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import static freemarker.template.utility.Collections12.singletonList;
import static org.innovateuk.ifs.assessment.builder.AssessmentPanelInviteResourceBuilder.newAssessmentPanelInviteResource;
import static org.innovateuk.ifs.invite.builder.AssessmentPanelParticipantResourceBuilder.newAssessmentPanelParticipantResource;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AssessmentPanelParticipantPermissionRulesTest extends BasePermissionRulesTest<AssessmentPanelParticipantPermissionRules> {

    @Override
    protected AssessmentPanelParticipantPermissionRules supplyPermissionRulesUnderTest() {
        return new AssessmentPanelParticipantPermissionRules();
    }

    @Test
    public void userCanAcceptAssessmentPanelInvite() {
        AssessmentPanelParticipantResource assessmentPanelParticipantResource = newAssessmentPanelParticipantResource()
                .withUser(1L)
                .build();
        UserResource userResource = newUserResource()
                .withId(1L)
                .withRolesGlobal(singletonList(assessorRole()))
                .build();

        assertTrue(rules.userCanAcceptAssessmentPanelInvite(assessmentPanelParticipantResource, userResource));
    }

    @Test
    public void userCanAcceptAssessmentPanelInvite_differentParticipantUser() {
        AssessmentPanelParticipantResource assessmentPanelParticipantResource = newAssessmentPanelParticipantResource()
                .withUser(1L)
                .build();
        UserResource userResource = newUserResource()
                .withId(2L)
                .withRolesGlobal(singletonList(assessorRole()))
                .build();

        assertFalse(rules.userCanAcceptAssessmentPanelInvite(assessmentPanelParticipantResource, userResource));
    }

    @Test
    public void userCanAcceptAssessmentPanelInvite_noParticipantUserAndSameEmail() {
        AssessmentPanelParticipantResource assessmentPanelParticipantResource = newAssessmentPanelParticipantResource()
                .withInvite(newAssessmentPanelInviteResource().withEmail("tom@poly.io"))
                .build();
        UserResource userResource = newUserResource()
                .withEmail("tom@poly.io")
                .withRolesGlobal(singletonList(assessorRole()))
                .build();

        assertTrue(rules.userCanAcceptAssessmentPanelInvite(assessmentPanelParticipantResource, userResource));
    }

    @Test
    public void userCanAcceptCompetitionInvite_noParticipantUserAndDifferentEmail() {
        AssessmentPanelParticipantResource assessmentPanelParticipantResource = newAssessmentPanelParticipantResource()
                .withInvite(newAssessmentPanelInviteResource().withEmail("tom@poly.io"))
                .build();
        UserResource userResource = newUserResource()
                .withEmail("non-existent-email@poly.io")
                .withRolesGlobal(singletonList(assessorRole()))
                .build();

        assertFalse(rules.userCanAcceptAssessmentPanelInvite(assessmentPanelParticipantResource, userResource));
    }

    @Test
    public void userCanViewTheirOwnAssessmentPanelParticipation() {
        AssessmentPanelParticipantResource assessmentPanelParticipantResource = newAssessmentPanelParticipantResource()
                .withUser(7L)
                .withInvite(newAssessmentPanelInviteResource().withStatus(SENT).build())
                .build();
        UserResource userResource = newUserResource()
                .withId(7L)
                .withRolesGlobal(singletonList(assessorRole()))
                .build();

        assertTrue(rules.userCanViewTheirOwnAssessmentPanelParticipation(assessmentPanelParticipantResource, userResource));
    }

    @Test
    public void userCanViewTheirOwnAssessmentPanelParticipation_differentUser() {
        AssessmentPanelParticipantResource assessmentPanelParticipantResource = newAssessmentPanelParticipantResource()
                .withUser(7L)
                .build();
        UserResource userResource = newUserResource()
                .withId(11L)
                .withRolesGlobal(singletonList(assessorRole()))
                .build();

        assertFalse(rules.userCanViewTheirOwnAssessmentPanelParticipation(assessmentPanelParticipantResource, userResource));
    }

    @Test
    public void userCanViewTheirOwnAssessmentPanelParticipation_notAssessor() {
        AssessmentPanelParticipantResource assessmentPanelParticipantResource = newAssessmentPanelParticipantResource()
                .withUser(7L)
                .build();
        UserResource userResource = newUserResource()
                .withId(7L)
                .build();

        assertFalse(rules.userCanViewTheirOwnAssessmentPanelParticipation(assessmentPanelParticipantResource, userResource));
    }
}
