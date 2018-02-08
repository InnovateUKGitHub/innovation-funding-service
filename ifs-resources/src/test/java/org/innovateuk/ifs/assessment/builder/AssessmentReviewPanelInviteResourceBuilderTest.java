package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.invite.resource.AssessmentReviewPanelInviteResource;
import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static org.innovateuk.ifs.assessment.builder.AssessmentReviewPanelInviteResourceBuilder.newAssessmentReviewPanelInviteResource;
import static org.junit.Assert.assertEquals;

/**
 * Builder for {@link CompetitionInviteResource}
 */
public class AssessmentReviewPanelInviteResourceBuilderTest {
    @Test
    public void buildOne() {
        String expectedCompetitionName = "Juggling craziness";
        long expectedCompId = 2L;
        String expectedEmail = "tom@poly.io";
        String expectedHash = "inviteHash";
        long expectedUserId = 1L;
        ZonedDateTime expectedPanelDate = ZonedDateTime.now();

        AssessmentReviewPanelInviteResource invite = newAssessmentReviewPanelInviteResource()
                .withCompetitionName(expectedCompetitionName)
                .withCompetitionId(expectedCompId)
                .withEmail(expectedEmail)
                .withInviteHash(expectedHash)
                .withPanelDate(expectedPanelDate)
                .withUserId(expectedUserId)
                .build();

        assertEquals(expectedCompetitionName, invite.getCompetitionName());
        assertEquals(expectedCompId, invite.getCompetitionId());
        assertEquals(expectedEmail, invite.getEmail());
        assertEquals(expectedHash, invite.getHash());
        assertEquals(expectedPanelDate, invite.getPanelDate());
        assertEquals(expectedUserId, invite.getUserId());
    }

    @Test
    public void buildMany() {
        String[] expectedCompetitionNames = {"Juggling craziness", "Intermediate Juggling"};
        long[] expectedCompIds = {1L, 2L};
        String[] expectedEmails = {"tom@poly.io", "steve.smith@empire.com"};
        String[] expectedHashes = {"hash1", "hash2"};
        ZonedDateTime[] expectedPanelDates = {ZonedDateTime.now(), ZonedDateTime.now().plusHours(1)};
        long[] expectedUserIds = {3L, 4L};

        List<AssessmentReviewPanelInviteResource> invites = newAssessmentReviewPanelInviteResource()
                .withCompetitionName(expectedCompetitionNames)
                .withCompetitionId(1L, 2L)
                .withEmail(expectedEmails)
                .withInviteHash(expectedHashes)
                .withPanelDate(expectedPanelDates)
                .withUserId(3L, 4L)
                .build(2);

        AssessmentReviewPanelInviteResource first = invites.get(0);
        assertEquals(expectedCompetitionNames[0], first.getCompetitionName());
        assertEquals(expectedCompIds[0], first.getCompetitionId());
        assertEquals(expectedEmails[0], first.getEmail());
        assertEquals(expectedHashes[0], first.getHash());
        assertEquals(expectedPanelDates[0], first.getPanelDate());
        assertEquals(expectedUserIds[0], first.getUserId());

        AssessmentReviewPanelInviteResource second = invites.get(1);
        assertEquals(expectedCompetitionNames[1], second.getCompetitionName());
        assertEquals(expectedCompIds[1], second.getCompetitionId());
        assertEquals(expectedEmails[1], second.getEmail());
        assertEquals(expectedHashes[1], second.getHash());
        assertEquals(expectedPanelDates[1], second.getPanelDate());
        assertEquals(expectedUserIds[1], second.getUserId());
    }
}
