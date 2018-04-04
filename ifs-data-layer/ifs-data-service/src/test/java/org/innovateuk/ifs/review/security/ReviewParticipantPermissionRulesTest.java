package org.innovateuk.ifs.review.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.invite.resource.ReviewParticipantResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import static freemarker.template.utility.Collections12.singletonList;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.review.builder.ReviewInviteResourceBuilder.newReviewInviteResource;
import static org.innovateuk.ifs.review.builder.ReviewParticipantResourceBuilder.newReviewParticipantResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.ASSESSOR;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReviewParticipantPermissionRulesTest extends BasePermissionRulesTest<ReviewParticipantPermissionRules> {

    @Override
    protected ReviewParticipantPermissionRules supplyPermissionRulesUnderTest() {
        return new ReviewParticipantPermissionRules();
    }

    @Test
    public void userCanAcceptAssessmentPanelInvite() {
        ReviewParticipantResource reviewParticipantResource = newReviewParticipantResource()
                .withUser(1L)
                .build();
        UserResource userResource = newUserResource()
                .withId(1L)
                .withRolesGlobal(singletonList(ASSESSOR))
                .build();

        assertTrue(rules.userCanAcceptAssessmentPanelInvite(reviewParticipantResource, userResource));
    }

    @Test
    public void userCanAcceptAssessmentPanelInvite_differentParticipantUser() {
        ReviewParticipantResource reviewParticipantResource = newReviewParticipantResource()
                .withUser(1L)
                .build();
        UserResource userResource = newUserResource()
                .withId(2L)
                .withRolesGlobal(singletonList(ASSESSOR))
                .build();

        assertFalse(rules.userCanAcceptAssessmentPanelInvite(reviewParticipantResource, userResource));
    }

    @Test
    public void userCanAcceptAssessmentPanelInvite_noParticipantUserAndSameEmail() {
        ReviewParticipantResource reviewParticipantResource = newReviewParticipantResource()
                .withInvite(newReviewInviteResource().withEmail("tom@poly.io"))
                .build();
        UserResource userResource = newUserResource()
                .withEmail("tom@poly.io")
                .withRolesGlobal(singletonList(ASSESSOR))
                .build();

        assertTrue(rules.userCanAcceptAssessmentPanelInvite(reviewParticipantResource, userResource));
    }

    @Test
    public void userCanAcceptCompetitionInvite_noParticipantUserAndDifferentEmail() {
        ReviewParticipantResource reviewParticipantResource = newReviewParticipantResource()
                .withInvite(newReviewInviteResource().withEmail("tom@poly.io"))
                .build();
        UserResource userResource = newUserResource()
                .withEmail("non-existent-email@poly.io")
                .withRolesGlobal(singletonList(ASSESSOR))
                .build();

        assertFalse(rules.userCanAcceptAssessmentPanelInvite(reviewParticipantResource, userResource));
    }

    @Test
    public void userCanViewTheirOwnAssessmentPanelParticipation() {
        ReviewParticipantResource reviewParticipantResource = newReviewParticipantResource()
                .withUser(7L)
                .withInvite(newReviewInviteResource().withStatus(SENT).build())
                .build();
        UserResource userResource = newUserResource()
                .withId(7L)
                .withRolesGlobal(singletonList(ASSESSOR))
                .build();

        assertTrue(rules.userCanViewTheirOwnAssessmentPanelParticipation(reviewParticipantResource, userResource));
    }

    @Test
    public void userCanViewTheirOwnAssessmentPanelParticipation_differentUser() {
        ReviewParticipantResource reviewParticipantResource = newReviewParticipantResource()
                .withUser(7L)
                .build();
        UserResource userResource = newUserResource()
                .withId(11L)
                .withRolesGlobal(singletonList(ASSESSOR))
                .build();

        assertFalse(rules.userCanViewTheirOwnAssessmentPanelParticipation(reviewParticipantResource, userResource));
    }
}
