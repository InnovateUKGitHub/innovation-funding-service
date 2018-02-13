package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.invite.resource.AssessmentReviewPanelParticipantResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import static freemarker.template.utility.Collections12.singletonList;
import static org.innovateuk.ifs.assessment.builder.AssessmentReviewPanelInviteResourceBuilder.newAssessmentReviewPanelInviteResource;
import static org.innovateuk.ifs.invite.builder.AssessmentReviewPanelParticipantResourceBuilder.newAssessmentReviewPanelParticipantResource;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AssessmentReviewPanelParticipantPermissionRulesTest extends BasePermissionRulesTest<AssessmentReviewPanelParticipantPermissionRules> {

    @Override
    protected AssessmentReviewPanelParticipantPermissionRules supplyPermissionRulesUnderTest() {
        return new AssessmentReviewPanelParticipantPermissionRules();
    }

    @Test
    public void userCanAcceptAssessmentPanelInvite() {
        AssessmentReviewPanelParticipantResource assessmentReviewPanelParticipantResource = newAssessmentReviewPanelParticipantResource()
                .withUser(1L)
                .build();
        UserResource userResource = newUserResource()
                .withId(1L)
                .withRolesGlobal(singletonList(assessorRole()))
                .build();

        assertTrue(rules.userCanAcceptAssessmentPanelInvite(assessmentReviewPanelParticipantResource, userResource));
    }

    @Test
    public void userCanAcceptAssessmentPanelInvite_differentParticipantUser() {
        AssessmentReviewPanelParticipantResource assessmentReviewPanelParticipantResource = newAssessmentReviewPanelParticipantResource()
                .withUser(1L)
                .build();
        UserResource userResource = newUserResource()
                .withId(2L)
                .withRolesGlobal(singletonList(assessorRole()))
                .build();

        assertFalse(rules.userCanAcceptAssessmentPanelInvite(assessmentReviewPanelParticipantResource, userResource));
    }

    @Test
    public void userCanAcceptAssessmentPanelInvite_noParticipantUserAndSameEmail() {
        AssessmentReviewPanelParticipantResource assessmentReviewPanelParticipantResource = newAssessmentReviewPanelParticipantResource()
                .withInvite(newAssessmentReviewPanelInviteResource().withEmail("tom@poly.io"))
                .build();
        UserResource userResource = newUserResource()
                .withEmail("tom@poly.io")
                .withRolesGlobal(singletonList(assessorRole()))
                .build();

        assertTrue(rules.userCanAcceptAssessmentPanelInvite(assessmentReviewPanelParticipantResource, userResource));
    }

    @Test
    public void userCanAcceptCompetitionInvite_noParticipantUserAndDifferentEmail() {
        AssessmentReviewPanelParticipantResource assessmentReviewPanelParticipantResource = newAssessmentReviewPanelParticipantResource()
                .withInvite(newAssessmentReviewPanelInviteResource().withEmail("tom@poly.io"))
                .build();
        UserResource userResource = newUserResource()
                .withEmail("non-existent-email@poly.io")
                .withRolesGlobal(singletonList(assessorRole()))
                .build();

        assertFalse(rules.userCanAcceptAssessmentPanelInvite(assessmentReviewPanelParticipantResource, userResource));
    }

    @Test
    public void userCanViewTheirOwnAssessmentPanelParticipation() {
        AssessmentReviewPanelParticipantResource assessmentReviewPanelParticipantResource = newAssessmentReviewPanelParticipantResource()
                .withUser(7L)
                .withInvite(newAssessmentReviewPanelInviteResource().withStatus(SENT).build())
                .build();
        UserResource userResource = newUserResource()
                .withId(7L)
                .withRolesGlobal(singletonList(assessorRole()))
                .build();

        assertTrue(rules.userCanViewTheirOwnAssessmentPanelParticipation(assessmentReviewPanelParticipantResource, userResource));
    }

    @Test
    public void userCanViewTheirOwnAssessmentPanelParticipation_differentUser() {
        AssessmentReviewPanelParticipantResource assessmentReviewPanelParticipantResource = newAssessmentReviewPanelParticipantResource()
                .withUser(7L)
                .build();
        UserResource userResource = newUserResource()
                .withId(11L)
                .withRolesGlobal(singletonList(assessorRole()))
                .build();

        assertFalse(rules.userCanViewTheirOwnAssessmentPanelParticipation(assessmentReviewPanelParticipantResource, userResource));
    }
}
