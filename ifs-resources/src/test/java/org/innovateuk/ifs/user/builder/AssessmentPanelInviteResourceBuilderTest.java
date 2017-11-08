package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.AssessmentPanelInviteResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.user.builder.AssessmentPanelInviteResourceBuilder.newAssessmentPanelInviteResource;
import static org.junit.Assert.assertEquals;

public class AssessmentPanelInviteResourceBuilderTest {

    @Test
    public void buildOne() {
        InviteStatus expectedStatus = SENT;
        long expectedCompId = 1L;
        String expectedCompName = "Photonics for health";
        String expectedHash = "";
        long expectedUserId = 2L;

        AssessmentPanelInviteResource assessmentPanelInviteResource = newAssessmentPanelInviteResource()
                .withStatus(expectedStatus)
                .withCompetitionId(expectedCompId)
                .withCompetitionName(expectedCompName)
                .withInviteHash(expectedHash)
                .withUser(expectedUserId)
                .build();

        assertEquals(expectedStatus, assessmentPanelInviteResource.getStatus());
        assertEquals(expectedCompId, assessmentPanelInviteResource.getCompetitionId());
        assertEquals(expectedCompName, assessmentPanelInviteResource.getCompetitionName());
        assertEquals(expectedHash, assessmentPanelInviteResource.getHash());
        assertEquals(expectedUserId, assessmentPanelInviteResource.getUserId());
    }

    @Test
    public void buildMany() {
        InviteStatus[] expectedStatuses = {CREATED, SENT};
        Long[] expectedCompIds = {1L, 2L};
        String[] expectedCompNames = {"Photonics for health", "The unnamed competition"};
        String[] expectedHashes = {"12345", "abcde"};
        Long[] expectedUserIds = {3L, 4L};

        List<AssessmentPanelInviteResource> assessmentPanelInviteResources = newAssessmentPanelInviteResource()
                .withStatus(expectedStatuses)
                .withCompetitionId(expectedCompIds)
                .withCompetitionName(expectedCompNames)
                .withInviteHash(expectedHashes)
                .withUser(expectedUserIds)
                .build(2);

        AssessmentPanelInviteResource first = assessmentPanelInviteResources.get(0);
        assertEquals(expectedStatuses[0], first.getStatus());
        assertEquals((long)expectedCompIds[0], first.getCompetitionId());
        assertEquals(expectedCompNames[0], first.getCompetitionName());
        assertEquals(expectedHashes[0], first.getHash());
        assertEquals((long)expectedUserIds[0], first.getUserId());

        AssessmentPanelInviteResource second = assessmentPanelInviteResources.get(1);
        assertEquals(expectedStatuses[1], second.getStatus());
        assertEquals((long)expectedCompIds[1], second.getCompetitionId());
        assertEquals(expectedCompNames[1], second.getCompetitionName());
        assertEquals(expectedHashes[1], second.getHash());
        assertEquals((long)expectedUserIds[1], second.getUserId());
    }
}