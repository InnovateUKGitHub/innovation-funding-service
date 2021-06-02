package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessmentInvite;
import org.innovateuk.ifs.assessment.domain.AssessmentParticipant;
import org.innovateuk.ifs.assessment.mapper.AssessmentInviteMapper;
import org.innovateuk.ifs.assessment.mapper.AssessmentParticipantMapper;
import org.innovateuk.ifs.assessment.period.domain.AssessmentPeriod;
import org.innovateuk.ifs.assessment.period.mapper.AssessmentPeriodMapper;
import org.innovateuk.ifs.assessment.repository.AssessmentParticipantRepository;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.competition.mapper.CompetitionParticipantRoleMapper;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.domain.RejectionReason;
import org.innovateuk.ifs.invite.mapper.ParticipantStatusMapper;
import org.innovateuk.ifs.invite.mapper.RejectionReasonMapper;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.AssessmentInviteBuilder.newAssessmentInvite;
import static org.innovateuk.ifs.assessment.builder.AssessmentParticipantBuilder.newAssessmentParticipant;
import static org.innovateuk.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.OPEN;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.*;
import static org.innovateuk.ifs.competition.builder.AssessmentPeriodBuilder.newAssessmentPeriod;
import static org.innovateuk.ifs.competition.builder.AssessmentPeriodResourceBuilder.newAssessmentPeriodResource;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.*;
import static org.innovateuk.ifs.invite.builder.CompetitionParticipantResourceBuilder.newCompetitionParticipantResource;
import static org.innovateuk.ifs.invite.builder.RejectionReasonBuilder.newRejectionReason;
import static org.innovateuk.ifs.invite.builder.RejectionReasonResourceBuilder.newRejectionReasonResource;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.ACCEPTED;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

public class CompetitionParticipantServiceImplTest extends BaseUnitTestMocksTest {

    @Mock
    private AssessmentRepository assessmentRepositoryMock;

    @Mock
    private AssessmentParticipantRepository assessmentParticipantRepositoryMock;

    @Mock
    private AssessmentParticipantMapper assessmentParticipantMapperMock;

    @Mock
    private AssessmentInviteMapper assessmentInviteMapperMock;

    @Mock
    private RejectionReasonMapper rejectionReasonMapperMock;

    @Mock
    private CompetitionParticipantRoleMapper competitionParticipantRoleMapperMock;

    @Mock
    private ParticipantStatusMapper participantStatusMapperMock;

    @Mock
    private AssessmentPeriodMapper assessmentPeriodMapperMock;

    @InjectMocks
    private CompetitionParticipantService competitionParticipantService = new CompetitionParticipantServiceImpl();

    @Test
    public void getCompetitionParticipants_withPending() {
        Long assessorId = 1L;
        Long userId = 1L;
        Long competitionId = 2L;

        List<AssessmentParticipant> competitionParticipants = new ArrayList<>();
        AssessmentParticipant competitionParticipant = new AssessmentParticipant();
        competitionParticipants.add(competitionParticipant);

        CompetitionParticipantResource expected = newCompetitionParticipantResource()
                .withUser(userId)
                .withCompetition(competitionId)
                .withStatus(ParticipantStatusResource.PENDING)
                .withCompetitionStatus(IN_ASSESSMENT)
                .build();

        when(assessmentParticipantRepositoryMock.getByAssessorId(userId)).thenReturn(competitionParticipants);
        when(assessmentParticipantMapperMock.mapToResource(same(competitionParticipant))).thenReturn(expected);

        ServiceResult<List<CompetitionParticipantResource>> competitionParticipantServiceResult =
                competitionParticipantService.getCompetitionAssessors(assessorId);

        List<CompetitionParticipantResource> found = competitionParticipantServiceResult.getSuccess();

        assertEquals(1, found.size());
        assertSame(expected, found.get(0));
        assertEquals(0L, found.get(0).getSubmittedAssessments());
        assertEquals(0L, found.get(0).getTotalAssessments());
        assertEquals(0L, found.get(0).getPendingAssessments());

        InOrder inOrder = inOrder(assessmentParticipantRepositoryMock,
                assessmentParticipantMapperMock, assessmentRepositoryMock);
        inOrder.verify(assessmentParticipantRepositoryMock, calls(1)).getByAssessorId(assessorId);
        inOrder.verify(assessmentParticipantMapperMock, calls(1)).mapToResource(any(AssessmentParticipant.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getCompetitionParticipants_withAcceptedAndAssessments() {
        Long assessorId = 1L;
        Long userId = 1L;
        Long competitionId = 2L;
        Long assessmentPeriodId = 3L;

        List<AssessmentParticipant> competitionParticipants = new ArrayList<>();
        AssessmentParticipant competitionParticipant = new AssessmentParticipant();
        competitionParticipants.add(competitionParticipant);

        AssessmentPeriodResource assessmentPeriodResource = newAssessmentPeriodResource()
                .withId(assessmentPeriodId)
                .withOpen(true)
                .withInAssessment(true)
                .withAssessmentClosed(false)
                .build();

        CompetitionParticipantResource expected = newCompetitionParticipantResource()
                .withUser(userId)
                .withCompetition(competitionId)
                .withStatus(ParticipantStatusResource.ACCEPTED)
                .withCompetitionStatus(IN_ASSESSMENT)
                .withAssessmentPeriod(assessmentPeriodResource)
                .withCompetitionAlwaysOpen(false)
                .build();

        List<Assessment> assessments = newAssessment()
                .withProcessState(OPEN, SUBMITTED, PENDING)
                .build(3);

        when(assessmentParticipantRepositoryMock.getByAssessorId(userId)).thenReturn(competitionParticipants);
        when(assessmentParticipantMapperMock.mapToResource(same(competitionParticipant))).thenReturn(expected);
        when(assessmentRepositoryMock.findByParticipantUserIdAndTargetCompetitionId(userId, competitionId)).thenReturn(assessments);

        ServiceResult<List<CompetitionParticipantResource>> competitionParticipantServiceResult =
                competitionParticipantService.getCompetitionAssessors(assessorId);

        List<CompetitionParticipantResource> found = competitionParticipantServiceResult.getSuccess();

        assertSame(expected, found.get(0));
        assertEquals(1L, found.get(0).getSubmittedAssessments());
        assertEquals(2L, found.get(0).getTotalAssessments());
        assertEquals(1L, found.get(0).getPendingAssessments());

        InOrder inOrder = inOrder(assessmentParticipantRepositoryMock,
                assessmentParticipantMapperMock, assessmentRepositoryMock);
        inOrder.verify(assessmentParticipantRepositoryMock, calls(1)).getByAssessorId(assessorId);
        inOrder.verify(assessmentParticipantMapperMock, calls(1)).mapToResource(any(AssessmentParticipant.class));
        inOrder.verify(assessmentRepositoryMock, calls(1)).findByParticipantUserIdAndTargetCompetitionId(userId, competitionId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getCompetitionParticipants_withRejected() {
        Long assessorId = 1L;
        Long userId = 1L;
        Long competitionId = 2L;

        List<AssessmentParticipant> competitionParticipants = new ArrayList<>();
        AssessmentParticipant competitionParticipant = new AssessmentParticipant();
        competitionParticipants.add(competitionParticipant);

        CompetitionParticipantResource expected = newCompetitionParticipantResource()
                .withUser(userId)
                .withCompetition(competitionId)
                .withStatus(ParticipantStatusResource.REJECTED)
                .withCompetitionStatus(IN_ASSESSMENT)
                .build();

        when(assessmentParticipantRepositoryMock.getByAssessorId(userId)).thenReturn(competitionParticipants);
        when(assessmentParticipantMapperMock.mapToResource(same(competitionParticipant))).thenReturn(expected);

        ServiceResult<List<CompetitionParticipantResource>> competitionParticipantServiceResult =
                competitionParticipantService.getCompetitionAssessors(assessorId);

        List<CompetitionParticipantResource> found = competitionParticipantServiceResult.getSuccess();

        assertSame(0, found.size());

        InOrder inOrder = inOrder(assessmentParticipantRepositoryMock,
                assessmentParticipantMapperMock
        );
        inOrder.verify(assessmentParticipantRepositoryMock, calls(1)).getByAssessorId(1L);
        inOrder.verify(assessmentParticipantMapperMock, calls(1)).mapToResource(any(AssessmentParticipant.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getCompetitionParticipantsWithAssessmentPeriod_withAcceptedAndAssessments() {
        Long assessorId = 1L;
        Long userId = 1L;
        Long competitionId = 2L;
        Long assessmentPeriodId = 3L;

        AssessmentPeriod assessmentPeriod = newAssessmentPeriod()
                .withId(assessmentPeriodId)
                .build();

        Competition competition = newCompetition()
                .withId(competitionId)
                .withAssessmentPeriods(Collections.singletonList(assessmentPeriod))
                .withAlwaysOpen(true)
                .build();

        Application application = newApplication()
                .withCompetition(competition)
                .withAssessmentPeriod(assessmentPeriod)
                .build();

        User user = newUser()
                .withId(userId)
                .build();

        AssessmentInvite assessmentInvite = newAssessmentInvite()
                .build();

        AssessmentParticipant competitionParticipant = newAssessmentParticipant()
                .withCompetition(competition)
                .withInvite(assessmentInvite)
                .withUser(user)
                .withStatus(ParticipantStatus.ACCEPTED)
                .withRole(CompetitionParticipantRole.ASSESSOR)
                .build();

        AssessmentPeriodResource assessmentPeriodResource = newAssessmentPeriodResource()
                .withId(assessmentPeriodId)
                .withOpen(true)
                .withInAssessment(true)
                .withAssessmentClosed(false)
                .build();

        List<Assessment> assessments = newAssessment()
                .withProcessState(OPEN, SUBMITTED, PENDING)
                .withApplication(application, application, application)
                .build(3);

        CompetitionInviteResource competitionInviteResource = newCompetitionInviteResource()
                .build();

        RejectionReasonResource rejectionReasonResource = newRejectionReasonResource()
                .build();

        when(assessmentParticipantRepositoryMock.getByAssessorId(userId)).thenReturn(Collections.singletonList(competitionParticipant));
        when(assessmentInviteMapperMock.mapToResource(any(AssessmentInvite.class))).thenReturn(competitionInviteResource);
        when(competitionParticipantRoleMapperMock.mapToResource(any(CompetitionParticipantRole.class))).thenReturn(CompetitionParticipantRoleResource.ASSESSOR);
        when(participantStatusMapperMock.mapToResource(any(ParticipantStatus.class))).thenReturn(ACCEPTED);
        when(assessmentPeriodMapperMock.mapToResource(any(AssessmentPeriod.class))).thenReturn(assessmentPeriodResource);
        when(assessmentRepositoryMock.findByParticipantUserIdAndTargetCompetitionId(userId, competitionId)).thenReturn(assessments);

        ServiceResult<List<CompetitionParticipantResource>> competitionParticipantServiceResult =
                competitionParticipantService.getCompetitionAssessorsWithAssessmentPeriod(assessorId);

        List<CompetitionParticipantResource> found = competitionParticipantServiceResult.getSuccess();

        assertEquals(userId, found.get(0).getUserId());
        assertEquals(competitionId, found.get(0).getCompetitionId());
        assertEquals(ParticipantStatusResource.ACCEPTED, found.get(0).getStatus());
        assertEquals(CompetitionParticipantRoleResource.ASSESSOR, found.get(0).getRole());
        assertTrue(found.get(0).getCompetitionAlwaysOpen());
        assertEquals(assessmentPeriodResource, found.get(0).getAssessmentPeriod());
        assertEquals(1L, found.get(0).getAssessmentPeriodNumber());
        assertEquals(1L, found.get(0).getSubmittedAssessments());
        assertEquals(2L, found.get(0).getTotalAssessments());
        assertEquals(1L, found.get(0).getPendingAssessments());

        InOrder inOrder = inOrder(assessmentParticipantRepositoryMock, assessmentInviteMapperMock, competitionParticipantRoleMapperMock,
                participantStatusMapperMock, assessmentPeriodMapperMock, assessmentRepositoryMock);
        inOrder.verify(assessmentParticipantRepositoryMock, calls(1)).getByAssessorId(assessorId);
        inOrder.verify(assessmentInviteMapperMock, calls(1)).mapToResource(any(AssessmentInvite.class));
        inOrder.verify(competitionParticipantRoleMapperMock, calls(1)).mapToResource(any(CompetitionParticipantRole.class));
        inOrder.verify(participantStatusMapperMock, calls(1)).mapToResource(any(ParticipantStatus.class));
        inOrder.verify(assessmentPeriodMapperMock, calls(1)).mapToResource(any(AssessmentPeriod.class));
        inOrder.verify(assessmentRepositoryMock, calls(1)).findByParticipantUserIdAndTargetCompetitionId(userId, competitionId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getCompetitionParticipantsWithAssessmentPeriod_withRejected() {
        Long assessorId = 1L;
        Long userId = 1L;
        Long competitionId = 2L;
        Long assessmentPeriodId = 3L;

        AssessmentPeriod assessmentPeriod = newAssessmentPeriod()
                .withId(assessmentPeriodId)
                .build();

        Competition competition = newCompetition()
                .withId(competitionId)
                .withAssessmentPeriods(Collections.singletonList(assessmentPeriod))
                .withAlwaysOpen(true)
                .build();

        User user = newUser()
                .withId(userId)
                .build();

        AssessmentInvite assessmentInvite = newAssessmentInvite()
                .build();

        RejectionReason rejectionReason = newRejectionReason()
                .withReason("Unavailable")
                .build();

        AssessmentParticipant competitionParticipant = newAssessmentParticipant()
                .withCompetition(competition)
                .withInvite(assessmentInvite)
                .withUser(user)
                .withStatus(ParticipantStatus.REJECTED)
                .withRole(CompetitionParticipantRole.ASSESSOR)
                .withRejectionReason(rejectionReason)
                .withRejectionComment("Not able to assess")
                .build();

        AssessmentPeriodResource assessmentPeriodResource = newAssessmentPeriodResource()
                .withId(assessmentPeriodId)
                .withOpen(true)
                .withInAssessment(true)
                .withAssessmentClosed(false)
                .build();

        CompetitionInviteResource competitionInviteResource = newCompetitionInviteResource()
                .build();

        RejectionReasonResource rejectionReasonResource = newRejectionReasonResource()
                .build();

        when(assessmentParticipantRepositoryMock.getByAssessorId(userId)).thenReturn(Collections.singletonList(competitionParticipant));
        when(assessmentInviteMapperMock.mapToResource(any(AssessmentInvite.class))).thenReturn(competitionInviteResource);
        when(rejectionReasonMapperMock.mapToResource(any(RejectionReason.class))).thenReturn(rejectionReasonResource);
        when(competitionParticipantRoleMapperMock.mapToResource(any(CompetitionParticipantRole.class))).thenReturn(CompetitionParticipantRoleResource.ASSESSOR);
        when(participantStatusMapperMock.mapToResource(any(ParticipantStatus.class))).thenReturn(ParticipantStatusResource.REJECTED);
        when(assessmentPeriodMapperMock.mapToResource(any(AssessmentPeriod.class))).thenReturn(assessmentPeriodResource);

        ServiceResult<List<CompetitionParticipantResource>> competitionParticipantServiceResult =
                competitionParticipantService.getCompetitionAssessorsWithAssessmentPeriod(assessorId);

        List<CompetitionParticipantResource> found = competitionParticipantServiceResult.getSuccess();

        assertSame(0, found.size());

        InOrder inOrder = inOrder(assessmentParticipantRepositoryMock, assessmentInviteMapperMock, rejectionReasonMapperMock,
                competitionParticipantRoleMapperMock, participantStatusMapperMock, assessmentPeriodMapperMock);
        inOrder.verify(assessmentParticipantRepositoryMock, calls(1)).getByAssessorId(assessorId);
        inOrder.verify(assessmentInviteMapperMock, calls(1)).mapToResource(any(AssessmentInvite.class));
        inOrder.verify(rejectionReasonMapperMock, calls(1)).mapToResource(any(RejectionReason.class));
        inOrder.verify(competitionParticipantRoleMapperMock, calls(1)).mapToResource(any(CompetitionParticipantRole.class));
        inOrder.verify(participantStatusMapperMock, calls(1)).mapToResource(any(ParticipantStatus.class));
        inOrder.verify(assessmentPeriodMapperMock, calls(1)).mapToResource(any(AssessmentPeriod.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getCompetitionParticipants_upcomingAssessment() {
        Long assessorId = 1L;
        Long userId = 1L;
        Long competitionId = 2L;

        List<AssessmentParticipant> competitionParticipants = new ArrayList<>();
        AssessmentParticipant competitionParticipant = new AssessmentParticipant();
        competitionParticipants.add(competitionParticipant);

        CompetitionParticipantResource expected = newCompetitionParticipantResource()
                .withUser(userId)
                .withCompetition(competitionId)
                .withStatus(ParticipantStatusResource.ACCEPTED)
                .withCompetitionStatus(READY_TO_OPEN)
                .withCompetitionAlwaysOpen(false)
                .build();

        when(assessmentParticipantRepositoryMock.getByAssessorId(userId)).thenReturn(competitionParticipants);
        when(assessmentParticipantMapperMock.mapToResource(same(competitionParticipant))).thenReturn(expected);

        ServiceResult<List<CompetitionParticipantResource>> competitionParticipantServiceResult =
                competitionParticipantService.getCompetitionAssessors(assessorId);

        List<CompetitionParticipantResource> found = competitionParticipantServiceResult.getSuccess();

        assertEquals(1, found.size());
        assertSame(expected, found.get(0));
        assertEquals(0L, found.get(0).getSubmittedAssessments());
        assertEquals(0L, found.get(0).getTotalAssessments());
        assertEquals(0L, found.get(0).getPendingAssessments());

        InOrder inOrder = inOrder(assessmentParticipantRepositoryMock,
                assessmentParticipantMapperMock, assessmentRepositoryMock);
        inOrder.verify(assessmentParticipantRepositoryMock, calls(1)).getByAssessorId(assessorId);
        inOrder.verify(assessmentParticipantMapperMock, calls(1)).mapToResource(any(AssessmentParticipant.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getCompetitionParticipants_inCompetitionSetup() {
        Long assessorId = 1L;
        Long userId = 1L;
        Long competitionId = 2L;

        List<AssessmentParticipant> competitionParticipants = new ArrayList<>();
        AssessmentParticipant competitionParticipant1 = newAssessmentParticipant().withId(1L).build();
        AssessmentParticipant competitionParticipant2 = newAssessmentParticipant().withId(2L).build();

        competitionParticipants.add(competitionParticipant1);
        competitionParticipants.add(competitionParticipant2);

        CompetitionParticipantResource expected1 = newCompetitionParticipantResource()
                .withUser(userId)
                .withCompetition(competitionId)
                .withCompetitionStatus(COMPETITION_SETUP)
                .withStatus(ParticipantStatusResource.PENDING)
                .build();
        CompetitionParticipantResource expected2 = newCompetitionParticipantResource()
                .withUser(userId)
                .withCompetition(competitionId)
                .withCompetitionStatus(COMPETITION_SETUP)
                .withStatus(ACCEPTED)
                .build();

        when(assessmentParticipantRepositoryMock.getByAssessorId(userId)).thenReturn(competitionParticipants);
        when(assessmentParticipantMapperMock.mapToResource(same(competitionParticipant1))).thenReturn(expected1);
        when(assessmentParticipantMapperMock.mapToResource(same(competitionParticipant2))).thenReturn(expected2);

        ServiceResult<List<CompetitionParticipantResource>> competitionParticipantServiceResult =
                competitionParticipantService.getCompetitionAssessors(assessorId);

        List<CompetitionParticipantResource> found = competitionParticipantServiceResult.getSuccess();

        assertSame(0, found.size());

        InOrder inOrder = inOrder(assessmentParticipantRepositoryMock,
                assessmentParticipantMapperMock
        );
        inOrder.verify(assessmentParticipantRepositoryMock, calls(1)).getByAssessorId(assessorId);
        inOrder.verify(assessmentParticipantMapperMock, calls(2)).mapToResource(any(AssessmentParticipant.class));
        inOrder.verifyNoMoreInteractions();
    }
}