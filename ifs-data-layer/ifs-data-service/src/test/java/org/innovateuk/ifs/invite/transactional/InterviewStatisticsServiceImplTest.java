package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.competition.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.interview.domain.InterviewInvite;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentRepository;
import org.innovateuk.ifs.interview.repository.InterviewInviteRepository;
import org.innovateuk.ifs.interview.repository.InterviewParticipantRepository;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentKeyStatisticsResource;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentState;
import org.innovateuk.ifs.interview.resource.InterviewInviteStatisticsResource;
import org.innovateuk.ifs.interview.resource.InterviewStatisticsResource;
import org.innovateuk.ifs.interview.transactional.InterviewStatisticsService;
import org.innovateuk.ifs.interview.transactional.InterviewStatisticsServiceImpl;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.EnumSet;
import java.util.List;

import static org.innovateuk.ifs.application.controller.ApplicationSummaryControllerIntegrationTest.COMPETITION_ID;
import static org.innovateuk.ifs.interview.builder.InterviewAssignmentKeyStatisticsResourceBuilder.newInterviewAssignmentKeyStatisticsResource;
import static org.innovateuk.ifs.interview.builder.InterviewInviteBuilder.newInterviewInvite;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.util.CollectionFunctions.asLinkedSet;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

public class InterviewStatisticsServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private InterviewStatisticsService interviewStatisticsService = new InterviewStatisticsServiceImpl();

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Mock
    private InterviewAssignmentRepository interviewAssignmentRepositoryMock;

    @Mock
    private InterviewInviteRepository interviewInviteRepositoryMock;

    @Mock
    private InterviewParticipantRepository interviewParticipantRepositoryMock;

    @Test
    public void getInterviewPanelKeyStatistics() {
        int applicationsInCompetition = 7;
        int applicationsAssigned = 11;

        InterviewAssignmentKeyStatisticsResource expectedKeyStatisticsResource =
                newInterviewAssignmentKeyStatisticsResource()
                        .withApplicationsInCompetition(applicationsInCompetition)
                        .withApplicationsAssigned(applicationsAssigned)
                        .build();

        when(applicationRepositoryMock.countByCompetitionIdAndApplicationProcessActivityState(COMPETITION_ID, ApplicationState.SUBMITTED))
                .thenReturn(applicationsInCompetition);

        when(interviewAssignmentRepositoryMock.countByTargetCompetitionIdAndActivityStateIn(COMPETITION_ID,
                asLinkedSet(InterviewAssignmentState.ASSIGNED_STATES)))
                .thenReturn(applicationsAssigned);

        InterviewAssignmentKeyStatisticsResource keyStatisticsResource = interviewStatisticsService.getInterviewAssignmentPanelKeyStatistics(COMPETITION_ID).getSuccess();

        assertEquals(expectedKeyStatisticsResource, keyStatisticsResource);

        InOrder inOrder = inOrder(applicationRepositoryMock, interviewAssignmentRepositoryMock);
        inOrder.verify(applicationRepositoryMock).countByCompetitionIdAndApplicationProcessActivityState(COMPETITION_ID, ApplicationState.SUBMITTED);
        inOrder.verify(interviewAssignmentRepositoryMock).countByTargetCompetitionIdAndActivityStateIn(COMPETITION_ID,
                asLinkedSet(InterviewAssignmentState.ASSIGNED_STATES));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInterviewInviteStatistics() {
        long competitionId = 1L;

        InterviewInviteStatisticsResource expectedInviteStatisticsResource =
                new InterviewInviteStatisticsResource(12, 5, 7);

        List<InterviewInvite> panelInvites = newInterviewInvite().build(expectedInviteStatisticsResource.getAssessorsInvited());

        List<Long> panelInviteIds = simpleMap(panelInvites, InterviewInvite::getId);

        when(interviewInviteRepositoryMock.getByCompetitionId(competitionId)).thenReturn(panelInvites);

        when(interviewInviteRepositoryMock.countByCompetitionIdAndStatusIn(competitionId, EnumSet.of(OPENED, SENT)))
                .thenReturn(expectedInviteStatisticsResource.getAssessorsInvited());

        when(interviewParticipantRepositoryMock.countByCompetitionIdAndRoleAndStatusAndInviteIdIn(
                competitionId, CompetitionParticipantRole.INTERVIEW_ASSESSOR, ParticipantStatus.ACCEPTED, panelInviteIds))
                .thenReturn(expectedInviteStatisticsResource.getAssessorsAccepted());
        when(interviewParticipantRepositoryMock.countByCompetitionIdAndRoleAndStatusAndInviteIdIn(
                competitionId, CompetitionParticipantRole.INTERVIEW_ASSESSOR, ParticipantStatus.REJECTED, panelInviteIds))
                .thenReturn(expectedInviteStatisticsResource.getAssessorsRejected());

        InterviewInviteStatisticsResource inviteStatisticsResource = interviewStatisticsService.getInterviewInviteStatistics(competitionId).getSuccess();

        assertEquals(expectedInviteStatisticsResource, inviteStatisticsResource);

        InOrder inOrder = inOrder(interviewInviteRepositoryMock, interviewParticipantRepositoryMock);
        inOrder.verify(interviewInviteRepositoryMock).getByCompetitionId(competitionId);
        inOrder.verify(interviewInviteRepositoryMock).countByCompetitionIdAndStatusIn(competitionId, EnumSet.of(OPENED, SENT));
        inOrder.verify(interviewParticipantRepositoryMock).countByCompetitionIdAndRoleAndStatusAndInviteIdIn(competitionId, CompetitionParticipantRole.INTERVIEW_ASSESSOR, ParticipantStatus.ACCEPTED, panelInviteIds);
        inOrder.verify(interviewParticipantRepositoryMock).countByCompetitionIdAndRoleAndStatusAndInviteIdIn(competitionId, CompetitionParticipantRole.INTERVIEW_ASSESSOR, ParticipantStatus.REJECTED, panelInviteIds);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInterviewStatistics() {
        long competitionId = 1L;

        InterviewStatisticsResource expectedInviteStatisticsResource =
                new InterviewStatisticsResource(12, 5, 7);

        List<InterviewInvite> panelInvites = newInterviewInvite().build(expectedInviteStatisticsResource.getAssessorsAccepted());

        List<Long> panelInviteIds = simpleMap(panelInvites, InterviewInvite::getId);

        when(interviewInviteRepositoryMock.getByCompetitionId(competitionId)).thenReturn(panelInvites);

        when(interviewParticipantRepositoryMock.countByCompetitionIdAndRoleAndStatusAndInviteIdIn(
                competitionId, CompetitionParticipantRole.INTERVIEW_ASSESSOR, ParticipantStatus.ACCEPTED, panelInviteIds))
                .thenReturn(expectedInviteStatisticsResource.getAssessorsAccepted());
        when(interviewAssignmentRepositoryMock.countByTargetCompetitionIdAndActivityStateIn(COMPETITION_ID,
                asLinkedSet(InterviewAssignmentState.ASSIGNED_STATES)))
                .thenReturn(expectedInviteStatisticsResource.getApplicationsAssigned());
        when(interviewAssignmentRepositoryMock.countByTargetCompetitionIdAndActivityStateIn(COMPETITION_ID,
                asLinkedSet(InterviewAssignmentState.SUBMITTED_FEEDBACK_RESPONSE)))
                .thenReturn(expectedInviteStatisticsResource.getRespondedToFeedback());

        InterviewStatisticsResource inviteStatisticsResource = interviewStatisticsService.getInterviewStatistics(competitionId).getSuccess();

        assertEquals(expectedInviteStatisticsResource, inviteStatisticsResource);

        InOrder inOrder = inOrder(interviewInviteRepositoryMock, interviewParticipantRepositoryMock, interviewAssignmentRepositoryMock);
        inOrder.verify(interviewInviteRepositoryMock).getByCompetitionId(competitionId);
        inOrder.verify(interviewParticipantRepositoryMock).countByCompetitionIdAndRoleAndStatusAndInviteIdIn(competitionId, CompetitionParticipantRole.INTERVIEW_ASSESSOR, ParticipantStatus.ACCEPTED, panelInviteIds);
        inOrder.verify(interviewAssignmentRepositoryMock).countByTargetCompetitionIdAndActivityStateIn(COMPETITION_ID,
                asLinkedSet(InterviewAssignmentState.ASSIGNED_STATES));
        inOrder.verify(interviewAssignmentRepositoryMock).countByTargetCompetitionIdAndActivityStateIn(COMPETITION_ID,
                asLinkedSet(InterviewAssignmentState.SUBMITTED_FEEDBACK_RESPONSE));
        inOrder.verifyNoMoreInteractions();
    }
}