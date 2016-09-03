package com.worth.ifs.invite.builder;

import com.worth.ifs.BaseBuilderAmendFunctions;
import com.worth.ifs.invite.resource.*;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static com.worth.ifs.invite.builder.CompetitionParticipantResourceBuilder.newCompetitionParticipantResource;
import static org.junit.Assert.assertEquals;

public class CompetitionParticipantResourceBuilderTest {

    @Test
    public void buildOne() {
        Long expectedId = 1L;
        Long expectedUserId = 2L;
        Long expectedCompetitionId = 3L;
        Long expectedInviteId = 4L;
        RejectionReasonResource expectedRejectionReason = new RejectionReasonResource("conflict", true, 1);
        String expectedRejectionReasonComment = "Reason comment";
        CompetitionParticipantRoleResource expectedRole = CompetitionParticipantRoleResource.ASSESSOR;
        ParticipantStatusResource expectedStatus = ParticipantStatusResource.ACCEPTED;

        CompetitionParticipantResource competitionParticipant = newCompetitionParticipantResource()
                .withId(expectedId)
                .withUser(expectedUserId)
                .withCompetition(expectedCompetitionId)
                .withInvite(newCompetitionInviteResource().with(BaseBuilderAmendFunctions.id(expectedInviteId)).build())
                .withRejectionReason(expectedRejectionReason)
                .withRejectionReasonComment(expectedRejectionReasonComment)
                .withCompetitionParticipantRole(expectedRole)
                .withStatus(expectedStatus)
                .build();

        assertEquals(expectedId, competitionParticipant.getId());
        assertEquals(expectedUserId, competitionParticipant.getUserId());
        assertEquals(expectedCompetitionId, competitionParticipant.getCompetitionId());
        assertEquals(expectedInviteId, competitionParticipant.getInvite().getId());
        assertEquals(expectedRejectionReason, competitionParticipant.getRejectionReason());
        assertEquals(expectedRejectionReasonComment, competitionParticipant.getRejectionReasonComment());
        assertEquals(expectedRole, competitionParticipant.getRole());
        assertEquals(expectedStatus, competitionParticipant.getStatus());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        Long[] expectedUserIds = {3L, 4L};
        Long[] expectedCompetitionIds = {5L, 6L};
        Long[] expectedInviteIds = {1L, 2L};
        RejectionReasonResource[] expectedRejectionReasons = {new RejectionReasonResource("conflict", true, 1),
                new RejectionReasonResource("holiday", true, 1)};
        String[] expectedRejectionReasonComments = {"Reason 1", "Reason 2"};
        CompetitionParticipantRoleResource[] expectedRoles = {CompetitionParticipantRoleResource.ASSESSOR, CompetitionParticipantRoleResource.ASSESSOR};
        ParticipantStatusResource[] expectedStatuses = {ParticipantStatusResource.ACCEPTED, ParticipantStatusResource.ACCEPTED};


        CompetitionInviteResource[] inviteResources = newCompetitionInviteResource().withIds(expectedInviteIds).build(2).toArray(new CompetitionInviteResource[0]);

        List<CompetitionParticipantResource> competitionParticipants = newCompetitionParticipantResource()
                .withId(expectedIds)
                .withUser(expectedUserIds)
                .withCompetition(expectedCompetitionIds)
                .withInvite(inviteResources)
                .withRejectionReason(expectedRejectionReasons)
                .withRejectionReasonComment(expectedRejectionReasonComments)
                .withCompetitionParticipantRole(expectedRoles)
                .withStatus(expectedStatuses)
                .build(2);

        CompetitionParticipantResource first = competitionParticipants.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedUserIds[0], first.getUserId());
        assertEquals(expectedCompetitionIds[0], first.getCompetitionId());
        assertEquals(expectedInviteIds[0], first.getInvite().getId());
        assertEquals(expectedRejectionReasons[0], first.getRejectionReason());
        assertEquals(expectedRejectionReasonComments[0], first.getRejectionReasonComment());
        assertEquals(expectedRoles[0], first.getRole());
        assertEquals(expectedStatuses[0], first.getStatus());

        CompetitionParticipantResource second = competitionParticipants.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedUserIds[1], second.getUserId());
        assertEquals(expectedCompetitionIds[1], second.getCompetitionId());
        assertEquals(expectedInviteIds[1], second.getInvite().getId());
        assertEquals(expectedRejectionReasons[1], second.getRejectionReason());
        assertEquals(expectedRejectionReasonComments[1], second.getRejectionReasonComment());
        assertEquals(expectedRoles[1], second.getRole());
        assertEquals(expectedStatuses[1], second.getStatus());
    }
}
