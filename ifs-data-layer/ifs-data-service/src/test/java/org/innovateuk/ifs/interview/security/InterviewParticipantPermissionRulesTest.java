package org.innovateuk.ifs.interview.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.invite.resource.InterviewParticipantResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import static freemarker.template.utility.Collections12.singletonList;
import static org.innovateuk.ifs.interview.builder.InterviewInviteResourceBuilder.newInterviewInviteResource;
import static org.innovateuk.ifs.invite.builder.AssessmentInterviewPanelParticipantResourceBuilder.newAssessmentInterviewPanelParticipantResource;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InterviewParticipantPermissionRulesTest extends BasePermissionRulesTest<InterviewParticipantPermissionRules> {

    @Override
    protected InterviewParticipantPermissionRules supplyPermissionRulesUnderTest() {
        return new InterviewParticipantPermissionRules();
    }

    @Test
    public void userCanAcceptAssessmentPanelInvite() {
        InterviewParticipantResource interviewParticipantResource = newAssessmentInterviewPanelParticipantResource()
                .withUser(1L)
                .build();
        UserResource userResource = newUserResource()
                .withId(1L)
                .withRolesGlobal(singletonList(assessorRole()))
                .build();

        assertTrue(rules.userCanAcceptAssessmentInterviewPanelInvite(interviewParticipantResource, userResource));
    }

    @Test
    public void userCanAcceptAssessmentPanelInvite_differentParticipantUser() {
        InterviewParticipantResource interviewParticipantResource = newAssessmentInterviewPanelParticipantResource()
                .withUser(1L)
                .build();
        UserResource userResource = newUserResource()
                .withId(2L)
                .withRolesGlobal(singletonList(assessorRole()))
                .build();

        assertFalse(rules.userCanAcceptAssessmentInterviewPanelInvite(interviewParticipantResource, userResource));
    }

    @Test
    public void userCanAcceptAssessmentPanelInvite_noParticipantUserAndSameEmail() {
        InterviewParticipantResource interviewParticipantResource = newAssessmentInterviewPanelParticipantResource()
                .withInvite(newInterviewInviteResource().withEmail("tom@poly.io"))
                .build();
        UserResource userResource = newUserResource()
                .withEmail("tom@poly.io")
                .withRolesGlobal(singletonList(assessorRole()))
                .build();

        assertTrue(rules.userCanAcceptAssessmentInterviewPanelInvite(interviewParticipantResource, userResource));
    }

    @Test
    public void userCanAcceptCompetitionInvite_noParticipantUserAndDifferentEmail() {
        InterviewParticipantResource interviewParticipantResource = newAssessmentInterviewPanelParticipantResource()
                .withInvite(newInterviewInviteResource().withEmail("tom@poly.io"))
                .build();
        UserResource userResource = newUserResource()
                .withEmail("non-existent-email@poly.io")
                .withRolesGlobal(singletonList(assessorRole()))
                .build();

        assertFalse(rules.userCanAcceptAssessmentInterviewPanelInvite(interviewParticipantResource, userResource));
    }

    @Test
    public void userCanViewTheirOwnAssessmentPanelParticipation() {
        InterviewParticipantResource interviewParticipantResource = newAssessmentInterviewPanelParticipantResource()
                .withUser(7L)
                .withInvite(newInterviewInviteResource().withStatus(SENT).build())
                .build();
        UserResource userResource = newUserResource()
                .withId(7L)
                .withRolesGlobal(singletonList(assessorRole()))
                .build();

        assertTrue(rules.userCanViewTheirOwnAssessmentPanelParticipation(interviewParticipantResource, userResource));
    }

    @Test
    public void userCanViewTheirOwnAssessmentPanelParticipation_differentUser() {
        InterviewParticipantResource interviewParticipantResource = newAssessmentInterviewPanelParticipantResource()
                .withUser(7L)
                .build();
        UserResource userResource = newUserResource()
                .withId(11L)
                .withRolesGlobal(singletonList(assessorRole()))
                .build();

        assertFalse(rules.userCanViewTheirOwnAssessmentPanelParticipation(interviewParticipantResource, userResource));
    }
}
