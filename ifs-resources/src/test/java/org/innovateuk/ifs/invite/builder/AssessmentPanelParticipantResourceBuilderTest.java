package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.invite.resource.*;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static org.innovateuk.ifs.assessment.builder.AssessmentPanelInviteResourceBuilder.newAssessmentPanelInviteResource;
import static org.innovateuk.ifs.invite.builder.AssessmentPanelParticipantResourceBuilder.newAssessmentPanelParticipantResource;
import static org.junit.Assert.assertEquals;

public class AssessmentPanelParticipantResourceBuilderTest {

    @Test
    public void buildOne() {
        Long expectedId = 1L;
        Long expectedUserId = 2L;
        Long expectedCompetitionId = 3L;
        RejectionReasonResource expectedRejectionReason = new RejectionReasonResource("conflict", true, 1);
        String expectedRejectionReasonComment = "Reason comment";
        CompetitionParticipantRoleResource expectedRole = CompetitionParticipantRoleResource.ASSESSOR;
        ParticipantStatusResource expectedStatus = ParticipantStatusResource.ACCEPTED;
        String expectedCompetitionName = "Juggling Craziness";
        ZonedDateTime expectedAssessorAcceptsDate = ZonedDateTime.now().minusDays(1);
        ZonedDateTime expectedAssessorDeadlineDate = ZonedDateTime.now().plusDays(2);
        long expectedTotalAssessments = 4L;
        long expectedSubmittedAssessments = 1L;
        long expectedPendingAssessments = 2L;
        CompetitionStatus expectedCompetitionStatus = CompetitionStatus.IN_ASSESSMENT;

        AssessmentPanelParticipantResource competitionParticipant = newAssessmentPanelParticipantResource()
                .withId(expectedId)
                .withUser(expectedUserId)
                .withCompetition(expectedCompetitionId)
                .withInvite(newAssessmentPanelInviteResource().build())
                .withRejectionReason(expectedRejectionReason)
                .withRejectionReasonComment(expectedRejectionReasonComment)
                .withCompetitionParticipantRole(expectedRole)
                .withStatus(expectedStatus)
                .withCompetitionName(expectedCompetitionName)
                .withAssessorAcceptsDate(expectedAssessorAcceptsDate)
                .withAssessorDeadlineDate(expectedAssessorDeadlineDate)
                .withTotalAssessments(expectedTotalAssessments)
                .withSubmittedAssessments(expectedSubmittedAssessments)
                .withPendingAssessments(expectedPendingAssessments)
                .withCompetitionStatus(expectedCompetitionStatus)
                .build();

        assertEquals(expectedId, competitionParticipant.getId());
        assertEquals(expectedUserId, competitionParticipant.getUserId());
        assertEquals(expectedCompetitionId, competitionParticipant.getCompetitionId());
        assertEquals(expectedRejectionReason, competitionParticipant.getRejectionReason());
        assertEquals(expectedRejectionReasonComment, competitionParticipant.getRejectionReasonComment());
        assertEquals(expectedRole, competitionParticipant.getRole());
        assertEquals(expectedStatus, competitionParticipant.getStatus());
        assertEquals(expectedCompetitionName, competitionParticipant.getCompetitionName());
        assertEquals(expectedAssessorAcceptsDate, competitionParticipant.getAssessorAcceptsDate());
        assertEquals(expectedAssessorDeadlineDate, competitionParticipant.getAssessorDeadlineDate());
        assertEquals(expectedTotalAssessments, competitionParticipant.getTotalAssessments());
        assertEquals(expectedSubmittedAssessments, competitionParticipant.getSubmittedAssessments());
        assertEquals(expectedPendingAssessments, competitionParticipant.getPendingAssessments());
        assertEquals(expectedCompetitionStatus, competitionParticipant.getCompetitionStatus());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        Long[] expectedUserIds = {3L, 4L};
        Long[] expectedCompetitionIds = {5L, 6L};
        RejectionReasonResource[] expectedRejectionReasons = {new RejectionReasonResource("conflict", true, 1),
                new RejectionReasonResource("holiday", true, 1)};
        String[] expectedRejectionReasonComments = {"Reason 1", "Reason 2"};
        CompetitionParticipantRoleResource[] expectedRoles = {CompetitionParticipantRoleResource.ASSESSOR, CompetitionParticipantRoleResource.ASSESSOR};
        ParticipantStatusResource[] expectedStatuses = {ParticipantStatusResource.ACCEPTED, ParticipantStatusResource.ACCEPTED};
        String[] expectedCompetitionNames = {"Juggling Craziness", "Advanced Juggling"};
        ZonedDateTime[] expectedAssessorAcceptsDates = {ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(3)};
        ZonedDateTime[] expectedAssessorDeadlineDates = {ZonedDateTime.now().plusDays(2), ZonedDateTime.now().plusDays(5)};
        Long[] expectedTotalAssessments = {4L, 5L};
        Long[] expectedSubmittedAssessments = {1L, 2L};
        Long[] expectedPendingAssessments = {2L, 3L};
        CompetitionStatus[] expectedCompetitionStatuses = {CompetitionStatus.IN_ASSESSMENT, CompetitionStatus.FUNDERS_PANEL};

        List<AssessmentPanelParticipantResource> competitionParticipants = newAssessmentPanelParticipantResource()
                .withId(expectedIds)
                .withUser(expectedUserIds)
                .withCompetition(expectedCompetitionIds)
                .withInvite(newAssessmentPanelInviteResource().buildArray(2, AssessmentPanelInviteResource.class))
                .withRejectionReason(expectedRejectionReasons)
                .withRejectionReasonComment(expectedRejectionReasonComments)
                .withCompetitionParticipantRole(expectedRoles)
                .withStatus(expectedStatuses)
                .withCompetitionName(expectedCompetitionNames)
                .withAssessorAcceptsDate(expectedAssessorAcceptsDates)
                .withAssessorDeadlineDate(expectedAssessorDeadlineDates)
                .withTotalAssessments(expectedTotalAssessments)
                .withSubmittedAssessments(expectedSubmittedAssessments)
                .withPendingAssessments(expectedPendingAssessments)
                .withCompetitionStatus(expectedCompetitionStatuses)
                .build(2);

        AssessmentPanelParticipantResource first = competitionParticipants.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedUserIds[0], first.getUserId());
        assertEquals(expectedCompetitionIds[0], first.getCompetitionId());
        assertEquals(expectedRejectionReasons[0], first.getRejectionReason());
        assertEquals(expectedRejectionReasonComments[0], first.getRejectionReasonComment());
        assertEquals(expectedRoles[0], first.getRole());
        assertEquals(expectedStatuses[0], first.getStatus());
        assertEquals(expectedCompetitionNames[0], first.getCompetitionName());
        assertEquals(expectedAssessorAcceptsDates[0], first.getAssessorAcceptsDate());
        assertEquals(expectedAssessorDeadlineDates[0], first.getAssessorDeadlineDate());
        assertEquals(expectedTotalAssessments[0].longValue(), first.getTotalAssessments());
        assertEquals(expectedSubmittedAssessments[0].longValue(), first.getSubmittedAssessments());
        assertEquals(expectedPendingAssessments[0].longValue(), first.getPendingAssessments());
        assertEquals(expectedCompetitionStatuses[0], first.getCompetitionStatus());

        AssessmentPanelParticipantResource second = competitionParticipants.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedUserIds[1], second.getUserId());
        assertEquals(expectedCompetitionIds[1], second.getCompetitionId());
        assertEquals(expectedRejectionReasons[1], second.getRejectionReason());
        assertEquals(expectedRejectionReasonComments[1], second.getRejectionReasonComment());
        assertEquals(expectedRoles[1], second.getRole());
        assertEquals(expectedStatuses[1], second.getStatus());
        assertEquals(expectedCompetitionNames[1], second.getCompetitionName());
        assertEquals(expectedAssessorAcceptsDates[1], second.getAssessorAcceptsDate());
        assertEquals(expectedAssessorDeadlineDates[1], second.getAssessorDeadlineDate());
        assertEquals(expectedTotalAssessments[1].longValue(), second.getTotalAssessments());
        assertEquals(expectedSubmittedAssessments[1].longValue(), second.getSubmittedAssessments());
        assertEquals(expectedPendingAssessments[1].longValue(), second.getPendingAssessments());
        assertEquals(expectedCompetitionStatuses[1], second.getCompetitionStatus());
    }
}
