package com.worth.ifs.invite.builder;

import com.worth.ifs.invite.resource.*;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.worth.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static com.worth.ifs.invite.builder.CompetitionParticipantResourceBuilder.newCompetitionParticipantResource;
import static org.junit.Assert.assertEquals;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.id;

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
        String expectedCompetitionName = "Juggling Craziness";
        LocalDateTime expectedAssessmentStartDate = LocalDateTime.now().minusDays(1);
        LocalDateTime expectedAssessmentEndDate = LocalDateTime.now().plusDays(2);

        CompetitionParticipantResource competitionParticipant = newCompetitionParticipantResource()
                .withId(expectedId)
                .withUser(expectedUserId)
                .withCompetition(expectedCompetitionId)
                .withInvite(newCompetitionInviteResource().with(id(expectedInviteId)).build())
                .withRejectionReason(expectedRejectionReason)
                .withRejectionReasonComment(expectedRejectionReasonComment)
                .withCompetitionParticipantRole(expectedRole)
                .withStatus(expectedStatus)
                .withCompetitionName(expectedCompetitionName)
                .withAssessmentStartDate(expectedAssessmentStartDate)
                .withAssessmentEndDate(expectedAssessmentEndDate)
                .build();

        assertEquals(expectedId, competitionParticipant.getId());
        assertEquals(expectedUserId, competitionParticipant.getUserId());
        assertEquals(expectedCompetitionId, competitionParticipant.getCompetitionId());
        assertEquals(expectedInviteId, competitionParticipant.getInvite().getId());
        assertEquals(expectedRejectionReason, competitionParticipant.getRejectionReason());
        assertEquals(expectedRejectionReasonComment, competitionParticipant.getRejectionReasonComment());
        assertEquals(expectedRole, competitionParticipant.getRole());
        assertEquals(expectedStatus, competitionParticipant.getStatus());
        assertEquals(expectedCompetitionName, competitionParticipant.getCompetitionName());
        assertEquals(expectedAssessmentStartDate, competitionParticipant.getAssessmentStartDate());
        assertEquals(expectedAssessmentEndDate, competitionParticipant.getAssessmentEndDate());
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
        String[] expectedCompetitionNames = {"Juggling Craziness", "Advanced Juggling"};
        LocalDateTime[] expectedAssessmentStartDates = {LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(3)};
        LocalDateTime[] expectedAssessmentEndDates = {LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(5)};

        List<CompetitionParticipantResource> competitionParticipants = newCompetitionParticipantResource()
                .withId(expectedIds)
                .withUser(expectedUserIds)
                .withCompetition(expectedCompetitionIds)
                .withInvite(newCompetitionInviteResource().withIds(expectedInviteIds).buildArray(2, CompetitionInviteResource.class))
                .withRejectionReason(expectedRejectionReasons)
                .withRejectionReasonComment(expectedRejectionReasonComments)
                .withCompetitionParticipantRole(expectedRoles)
                .withStatus(expectedStatuses)
                .withCompetitionName(expectedCompetitionNames)
                .withAssessmentStartDate(expectedAssessmentStartDates)
                .withAssessmentEndDate(expectedAssessmentEndDates)
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
        assertEquals(expectedCompetitionNames[0], first.getCompetitionName());
        assertEquals(expectedAssessmentStartDates[0], first.getAssessmentStartDate());
        assertEquals(expectedAssessmentEndDates[0], first.getAssessmentEndDate());

        CompetitionParticipantResource second = competitionParticipants.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedUserIds[1], second.getUserId());
        assertEquals(expectedCompetitionIds[1], second.getCompetitionId());
        assertEquals(expectedInviteIds[1], second.getInvite().getId());
        assertEquals(expectedRejectionReasons[1], second.getRejectionReason());
        assertEquals(expectedRejectionReasonComments[1], second.getRejectionReasonComment());
        assertEquals(expectedRoles[1], second.getRole());
        assertEquals(expectedStatuses[1], second.getStatus());
        assertEquals(expectedCompetitionNames[1], second.getCompetitionName());
        assertEquals(expectedAssessmentStartDates[1], second.getAssessmentStartDate());
        assertEquals(expectedAssessmentEndDates[1], second.getAssessmentEndDate());
    }
}
