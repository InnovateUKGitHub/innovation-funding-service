package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.CompetitionParticipant;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantResource;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantRoleResource;
import org.innovateuk.ifs.invite.resource.ParticipantStatusResource;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.CompetitionParticipantBuilder.newCompetitionParticipant;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.OPEN;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.SUBMITTED;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.COMPETITION_SETUP;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.READY_TO_OPEN;
import static org.innovateuk.ifs.invite.builder.CompetitionParticipantResourceBuilder.newCompetitionParticipantResource;
import static org.innovateuk.ifs.invite.domain.CompetitionParticipantRole.ASSESSOR;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.ACCEPTED;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.PENDING;
import static org.innovateuk.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class CompetitionParticipantServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private CompetitionParticipantService competitionParticipantService = new CompetitionParticipantServiceImpl();

    @Test
    public void getCompetitionParticipants_withPending() throws Exception {
        Long assessorId = 1L;
        Long userId = 1L;
        Long competitionId = 2L;

        List<CompetitionParticipant> competitionParticipants = new ArrayList<>();
        CompetitionParticipant competitionParticipant = new CompetitionParticipant();
        competitionParticipants.add(competitionParticipant);

        CompetitionParticipantResource expected = newCompetitionParticipantResource()
                .withUser(userId)
                .withCompetition(competitionId)
                .withStatus(PENDING)
                .withCompetitionStatus(IN_ASSESSMENT)
                .build();

        when(competitionParticipantRepositoryMock.getByUserIdAndRole(userId, ASSESSOR)).thenReturn(competitionParticipants);
        when(competitionParticipantMapperMock.mapToResource(same(competitionParticipant))).thenReturn(expected);
        when(competitionParticipantRoleMapperMock.mapToDomain(CompetitionParticipantRoleResource.ASSESSOR)).thenReturn(ASSESSOR);

        ServiceResult<List<CompetitionParticipantResource>> competitionParticipantServiceResult =
                competitionParticipantService.getCompetitionParticipants(assessorId, CompetitionParticipantRoleResource.ASSESSOR);

        List<CompetitionParticipantResource> found = competitionParticipantServiceResult.getSuccessObject();

        assertEquals(1, found.size());
        assertSame(expected, found.get(0));
        assertEquals(0L, found.get(0).getSubmittedAssessments());
        assertEquals(0L, found.get(0).getTotalAssessments());
        assertEquals(0L, found.get(0).getPendingAssessments());

        InOrder inOrder = inOrder(competitionParticipantRoleMapperMock, competitionParticipantRepositoryMock, competitionParticipantMapperMock, assessmentRepositoryMock);
        inOrder.verify(competitionParticipantRoleMapperMock, calls(1)).mapToDomain(any(CompetitionParticipantRoleResource.class));
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByUserIdAndRole(assessorId, ASSESSOR);
        inOrder.verify(competitionParticipantMapperMock, calls(1)).mapToResource(any(CompetitionParticipant.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getCompetitionParticipants_withAcceptedAndAssessments() throws Exception {
        Long assessorId = 1L;
        Long userId = 1L;
        Long competitionId = 2L;

        List<CompetitionParticipant> competitionParticipants = new ArrayList<>();
        CompetitionParticipant competitionParticipant = new CompetitionParticipant();
        competitionParticipants.add(competitionParticipant);

        CompetitionParticipantResource expected = newCompetitionParticipantResource()
                .withUser(userId)
                .withCompetition(competitionId)
                .withStatus(ParticipantStatusResource.ACCEPTED)
                .withCompetitionStatus(IN_ASSESSMENT)
                .build();

        List<Assessment> assessments = newAssessment()
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, OPEN.getBackingState()),
                        new ActivityState(APPLICATION_ASSESSMENT, SUBMITTED.getBackingState()),
                        new ActivityState(APPLICATION_ASSESSMENT, AssessmentStates.PENDING.getBackingState()))
                .build(3);

        when(competitionParticipantRepositoryMock.getByUserIdAndRole(userId, ASSESSOR)).thenReturn(competitionParticipants);
        when(competitionParticipantMapperMock.mapToResource(same(competitionParticipant))).thenReturn(expected);
        when(competitionParticipantRoleMapperMock.mapToDomain(CompetitionParticipantRoleResource.ASSESSOR)).thenReturn(ASSESSOR);
        when(assessmentRepositoryMock.findByParticipantUserIdAndTargetCompetitionIdOrderByActivityStateStateAscIdAsc(userId, competitionId)).thenReturn(assessments);

        ServiceResult<List<CompetitionParticipantResource>> competitionParticipantServiceResult =
                competitionParticipantService.getCompetitionParticipants(assessorId, CompetitionParticipantRoleResource.ASSESSOR);

        List<CompetitionParticipantResource> found = competitionParticipantServiceResult.getSuccessObject();

        assertSame(expected, found.get(0));
        assertEquals(1L, found.get(0).getSubmittedAssessments());
        assertEquals(2L, found.get(0).getTotalAssessments());
        assertEquals(1L, found.get(0).getPendingAssessments());

        InOrder inOrder = inOrder(competitionParticipantRoleMapperMock, competitionParticipantRepositoryMock, competitionParticipantMapperMock, assessmentRepositoryMock);
        inOrder.verify(competitionParticipantRoleMapperMock, calls(1)).mapToDomain(any(CompetitionParticipantRoleResource.class));
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByUserIdAndRole(assessorId, ASSESSOR);
        inOrder.verify(competitionParticipantMapperMock, calls(1)).mapToResource(any(CompetitionParticipant.class));
        inOrder.verify(assessmentRepositoryMock, calls(1)).findByParticipantUserIdAndTargetCompetitionIdOrderByActivityStateStateAscIdAsc(userId, competitionId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getCompetitionParticipants_withRejected() throws Exception {
        Long assessorId = 1L;
        Long userId = 1L;
        Long competitionId = 2L;

        List<CompetitionParticipant> competitionParticipants = new ArrayList<>();
        CompetitionParticipant competitionParticipant = new CompetitionParticipant();
        competitionParticipants.add(competitionParticipant);

        CompetitionParticipantResource expected = newCompetitionParticipantResource()
                .withUser(userId)
                .withCompetition(competitionId)
                .withStatus(ParticipantStatusResource.REJECTED)
                .withCompetitionStatus(IN_ASSESSMENT)
                .build();

        when(competitionParticipantRepositoryMock.getByUserIdAndRole(userId, ASSESSOR)).thenReturn(competitionParticipants);
        when(competitionParticipantMapperMock.mapToResource(same(competitionParticipant))).thenReturn(expected);
        when(competitionParticipantRoleMapperMock.mapToDomain(CompetitionParticipantRoleResource.ASSESSOR)).thenReturn(ASSESSOR);

        ServiceResult<List<CompetitionParticipantResource>> competitionParticipantServiceResult =
                competitionParticipantService.getCompetitionParticipants(assessorId, CompetitionParticipantRoleResource.ASSESSOR);

        List<CompetitionParticipantResource> found = competitionParticipantServiceResult.getSuccessObject();

        assertSame(0, found.size());

        InOrder inOrder = inOrder(competitionParticipantRoleMapperMock, competitionParticipantRepositoryMock, competitionParticipantMapperMock);
        inOrder.verify(competitionParticipantRoleMapperMock, calls(1)).mapToDomain(any(CompetitionParticipantRoleResource.class));
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByUserIdAndRole(1L, ASSESSOR);
        inOrder.verify(competitionParticipantMapperMock, calls(1)).mapToResource(any(CompetitionParticipant.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getCompetitionParticipants_upcomingAssessment() throws Exception {
        Long assessorId = 1L;
        Long userId = 1L;
        Long competitionId = 2L;

        List<CompetitionParticipant> competitionParticipants = new ArrayList<>();
        CompetitionParticipant competitionParticipant = new CompetitionParticipant();
        competitionParticipants.add(competitionParticipant);

        CompetitionParticipantResource expected = newCompetitionParticipantResource()
                .withUser(userId)
                .withCompetition(competitionId)
                .withStatus(ParticipantStatusResource.ACCEPTED)
                .withCompetitionStatus(READY_TO_OPEN)
                .build();

        when(competitionParticipantRepositoryMock.getByUserIdAndRole(userId, ASSESSOR)).thenReturn(competitionParticipants);
        when(competitionParticipantMapperMock.mapToResource(same(competitionParticipant))).thenReturn(expected);
        when(competitionParticipantRoleMapperMock.mapToDomain(CompetitionParticipantRoleResource.ASSESSOR)).thenReturn(ASSESSOR);

        ServiceResult<List<CompetitionParticipantResource>> competitionParticipantServiceResult =
                competitionParticipantService.getCompetitionParticipants(assessorId, CompetitionParticipantRoleResource.ASSESSOR);

        List<CompetitionParticipantResource> found = competitionParticipantServiceResult.getSuccessObject();

        assertEquals(1, found.size());
        assertSame(expected, found.get(0));
        assertEquals(0L, found.get(0).getSubmittedAssessments());
        assertEquals(0L, found.get(0).getTotalAssessments());
        assertEquals(0L, found.get(0).getPendingAssessments());

        InOrder inOrder = inOrder(competitionParticipantRoleMapperMock, competitionParticipantRepositoryMock, competitionParticipantMapperMock, assessmentRepositoryMock);
        inOrder.verify(competitionParticipantRoleMapperMock, calls(1)).mapToDomain(any(CompetitionParticipantRoleResource.class));
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByUserIdAndRole(assessorId, ASSESSOR);
        inOrder.verify(competitionParticipantMapperMock, calls(1)).mapToResource(any(CompetitionParticipant.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getCompetitionParticipants_inCompetitionSetup() throws Exception {
        Long assessorId = 1L;
        Long userId = 1L;
        Long competitionId = 2L;

        List<CompetitionParticipant> competitionParticipants = new ArrayList<>();
        CompetitionParticipant competitionParticipant1 = newCompetitionParticipant().withId(1L).build();
        CompetitionParticipant competitionParticipant2 = newCompetitionParticipant().withId(2L).build();

        competitionParticipants.add(competitionParticipant1);
        competitionParticipants.add(competitionParticipant2);

        CompetitionParticipantResource expected1 = newCompetitionParticipantResource()
                .withUser(userId)
                .withCompetition(competitionId)
                .withCompetitionStatus(COMPETITION_SETUP)
                .withStatus(PENDING)
                .build();
        CompetitionParticipantResource expected2 = newCompetitionParticipantResource()
                .withUser(userId)
                .withCompetition(competitionId)
                .withCompetitionStatus(COMPETITION_SETUP)
                .withStatus(ACCEPTED)
                .build();

        when(competitionParticipantRepositoryMock.getByUserIdAndRole(userId, ASSESSOR)).thenReturn(competitionParticipants);
        when(competitionParticipantMapperMock.mapToResource(same(competitionParticipant1))).thenReturn(expected1);
        when(competitionParticipantMapperMock.mapToResource(same(competitionParticipant2))).thenReturn(expected2);
        when(competitionParticipantRoleMapperMock.mapToDomain(CompetitionParticipantRoleResource.ASSESSOR)).thenReturn(ASSESSOR);

        ServiceResult<List<CompetitionParticipantResource>> competitionParticipantServiceResult =
                competitionParticipantService.getCompetitionParticipants(assessorId, CompetitionParticipantRoleResource.ASSESSOR);

        List<CompetitionParticipantResource> found = competitionParticipantServiceResult.getSuccessObject();

        assertSame(0, found.size());

        InOrder inOrder = inOrder(competitionParticipantRoleMapperMock, competitionParticipantRepositoryMock, competitionParticipantMapperMock);
        inOrder.verify(competitionParticipantRoleMapperMock, calls(1)).mapToDomain(any(CompetitionParticipantRoleResource.class));
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByUserIdAndRole(assessorId, ASSESSOR);
        inOrder.verify(competitionParticipantMapperMock, calls(2)).mapToResource(any(CompetitionParticipant.class));
        inOrder.verifyNoMoreInteractions();
    }
}
