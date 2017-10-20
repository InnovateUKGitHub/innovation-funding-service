package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.invite.resource.AssessmentPanelInviteResource;
import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.assessment.builder.AssessmentPanelInviteResourceBuilder.newAssessmentPanelInviteResource;
import static org.junit.Assert.assertEquals;

/**
 * Builder for {@link CompetitionInviteResource}
 */
public class AssessmentPanelInviteResourceBuilderTest {
    @Test
    public void buildOne() {
        String expectedCompetitionName = "Juggling craziness";
        long expectedCompId = 2L;
        String expectedEmail = "tom@poly.io";
        String expectedHash = "inviteHash";

        AssessmentPanelInviteResource invite = newAssessmentPanelInviteResource()
                .withCompetitionName(expectedCompetitionName)
                .withCompetitionId(expectedCompId)
                .withEmail(expectedEmail)
                .withHash(expectedHash)
                .build();

        assertEquals(expectedCompetitionName, invite.getCompetitionName());
        assertEquals(expectedCompId, invite.getCompetitionId());
        assertEquals(expectedEmail, invite.getEmail());
        assertEquals(expectedHash, invite.getHash());
    }

    @Test
    public void buildMany() {
        String[] expectedCompetitionNames = {"Juggling craziness", "Intermediate Juggling"};
        long[] expectedCompIds = {1L, 2L};
        String[] expectedEmails = {"tom@poly.io", "steve.smith@empire.com"};
        String[] expectedHashes = {"hash1", "hash2"};

        List<AssessmentPanelInviteResource> invites = newAssessmentPanelInviteResource()
                .withCompetitionName(expectedCompetitionNames)
                .withCompetitionId(1L, 2L)
                .withEmail(expectedEmails)
                .withHash(expectedHashes)
                .build(2);

        AssessmentPanelInviteResource first = invites.get(0);
        assertEquals(expectedCompetitionNames[0], first.getCompetitionName());
        assertEquals(expectedCompIds[0], first.getCompetitionId());
        assertEquals(expectedEmails[0], first.getEmail());
        assertEquals(expectedHashes[0], first.getHash());

        AssessmentPanelInviteResource second = invites.get(1);
        assertEquals(expectedCompetitionNames[1], second.getCompetitionName());
        assertEquals(expectedCompIds[1], second.getCompetitionId());
        assertEquals(expectedEmails[1], second.getEmail());
        assertEquals(expectedHashes[1], second.getHash());
    }
}
