package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.assessment.domain.Assessment;
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
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.OPEN;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.SUBMITTED;
import static org.innovateuk.ifs.invite.builder.CompetitionParticipantResourceBuilder.newCompetitionParticipantResource;
import static org.innovateuk.ifs.invite.domain.CompetitionParticipantRole.ASSESSOR;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.PENDING;
import static org.innovateuk.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class CompetitionParticipantServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private CompetitionParticipantService competitionParticipantService = new CompetitionParticipantServiceImpl();

    @Test
    public void getCompetitionParticipants() throws Exception {
        List<CompetitionParticipant> competitionParticipants = new ArrayList<>();
        CompetitionParticipant competitionParticipant = new CompetitionParticipant();
        competitionParticipants.add(competitionParticipant);
        CompetitionParticipantResource expected = newCompetitionParticipantResource().build();

        when(competitionParticipantRepositoryMock.getByUserIdAndRole(1L, ASSESSOR)).thenReturn(competitionParticipants);
        when(competitionParticipantMapperMock.mapToResource(same(competitionParticipant))).thenReturn(expected);
        when(competitionParticipantRoleMapperMock.mapToDomain(CompetitionParticipantRoleResource.ASSESSOR)).thenReturn(ASSESSOR);
        when(participantStatusMapperMock.mapToDomain(ParticipantStatusResource.PENDING)).thenReturn(PENDING);

        ServiceResult<List<CompetitionParticipantResource>> competitionParticipantServiceResult = competitionParticipantService.getCompetitionParticipants(1L, CompetitionParticipantRoleResource.ASSESSOR);

        List<CompetitionParticipantResource> found = competitionParticipantServiceResult.getSuccessObject();

        assertSame(expected, found.get(0));
        assertEquals(0L, found.get(0).getSubmittedAssessments());
        assertEquals(0L, found.get(0).getTotalAssessments());

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock, competitionParticipantMapperMock);
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByUserIdAndRole(1L, ASSESSOR);
        inOrder.verify(competitionParticipantMapperMock, calls(1)).mapToResource(any(CompetitionParticipant.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getCompetitionParticipants_withAcceptedAndAssessments() throws Exception {
        Long userId = 1L;
        Long competitionId = 2L;

        List<CompetitionParticipant> competitionParticipants = new ArrayList<>();
        CompetitionParticipant competitionParticipant = new CompetitionParticipant();
        competitionParticipants.add(competitionParticipant);

        CompetitionParticipantResource expected = newCompetitionParticipantResource()
                .withUser(userId)
                .withCompetition(competitionId)
                .withStatus(ParticipantStatusResource.ACCEPTED)
                .build();

        List<Assessment> assessments = newAssessment()
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, OPEN.getBackingState()), new ActivityState(APPLICATION_ASSESSMENT, SUBMITTED.getBackingState()))
                .build(2);

        when(competitionParticipantRepositoryMock.getByUserIdAndRole(userId, ASSESSOR)).thenReturn(competitionParticipants);
        when(competitionParticipantMapperMock.mapToResource(same(competitionParticipant))).thenReturn(expected);
        when(competitionParticipantRoleMapperMock.mapToDomain(CompetitionParticipantRoleResource.ASSESSOR)).thenReturn(ASSESSOR);
        when(assessmentRepositoryMock.findByParticipantUserIdAndParticipantApplicationCompetitionIdOrderByActivityStateStateAscIdAsc(userId, competitionId)).thenReturn(assessments);

        ServiceResult<List<CompetitionParticipantResource>> competitionParticipantServiceResult = competitionParticipantService.getCompetitionParticipants(1L, CompetitionParticipantRoleResource.ASSESSOR);

        List<CompetitionParticipantResource> found = competitionParticipantServiceResult.getSuccessObject();

        assertSame(expected, found.get(0));
        assertEquals(1L, found.get(0).getSubmittedAssessments());
        assertEquals(2L, found.get(0).getTotalAssessments());

        InOrder inOrder = inOrder(competitionParticipantRoleMapperMock, participantStatusMapperMock, competitionParticipantRepositoryMock, competitionParticipantMapperMock, assessmentRepositoryMock);
        inOrder.verify(competitionParticipantRoleMapperMock, calls(1)).mapToDomain(any(CompetitionParticipantRoleResource.class));
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByUserIdAndRole(1L, ASSESSOR);
        inOrder.verify(competitionParticipantMapperMock, calls(1)).mapToResource(any(CompetitionParticipant.class));
        inOrder.verify(assessmentRepositoryMock, calls(1)).findByParticipantUserIdAndParticipantApplicationCompetitionIdOrderByActivityStateStateAscIdAsc(userId, competitionId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getCompetitionParticipants_withRejected() throws Exception {
        Long userId = 1L;
        Long competitionId = 2L;

        List<CompetitionParticipant> competitionParticipants = new ArrayList<>();
        CompetitionParticipant competitionParticipant = new CompetitionParticipant();
        competitionParticipants.add(competitionParticipant);

        CompetitionParticipantResource expected = newCompetitionParticipantResource()
                .withUser(userId)
                .withCompetition(competitionId)
                .withStatus(ParticipantStatusResource.REJECTED)
                .build();

        when(competitionParticipantRepositoryMock.getByUserIdAndRole(userId, ASSESSOR)).thenReturn(competitionParticipants);
        when(competitionParticipantMapperMock.mapToResource(same(competitionParticipant))).thenReturn(expected);
        when(competitionParticipantRoleMapperMock.mapToDomain(CompetitionParticipantRoleResource.ASSESSOR)).thenReturn(ASSESSOR);

        ServiceResult<List<CompetitionParticipantResource>> competitionParticipantServiceResult = competitionParticipantService.getCompetitionParticipants(1L, CompetitionParticipantRoleResource.ASSESSOR);

        List<CompetitionParticipantResource> found = competitionParticipantServiceResult.getSuccessObject();

        assertSame(0, found.size());

        InOrder inOrder = inOrder(competitionParticipantRoleMapperMock, participantStatusMapperMock, competitionParticipantRepositoryMock, competitionParticipantMapperMock, assessmentRepositoryMock);
        inOrder.verify(competitionParticipantRoleMapperMock, calls(1)).mapToDomain(any(CompetitionParticipantRoleResource.class));
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByUserIdAndRole(1L, ASSESSOR);
        inOrder.verify(competitionParticipantMapperMock, calls(1)).mapToResource(any(CompetitionParticipant.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getCompetitionParticipants_withPending() throws Exception {
        Long userId = 1L;
        Long competitionId = 2L;

        List<CompetitionParticipant> competitionParticipants = new ArrayList<>();
        CompetitionParticipant competitionParticipant = new CompetitionParticipant();
        competitionParticipants.add(competitionParticipant);

        CompetitionParticipantResource expected = newCompetitionParticipantResource()
                .withUser(userId)
                .withCompetition(competitionId)
                .withStatus(ParticipantStatusResource.PENDING)
                .build();

        when(competitionParticipantRepositoryMock.getByUserIdAndRole(userId, ASSESSOR)).thenReturn(competitionParticipants);
        when(competitionParticipantMapperMock.mapToResource(same(competitionParticipant))).thenReturn(expected);
        when(competitionParticipantRoleMapperMock.mapToDomain(CompetitionParticipantRoleResource.ASSESSOR)).thenReturn(ASSESSOR);

        ServiceResult<List<CompetitionParticipantResource>> competitionParticipantServiceResult = competitionParticipantService.getCompetitionParticipants(1L, CompetitionParticipantRoleResource.ASSESSOR);

        List<CompetitionParticipantResource> found = competitionParticipantServiceResult.getSuccessObject();

        assertSame(expected, found.get(0));
        assertEquals(0L, found.get(0).getSubmittedAssessments());
        assertEquals(0L, found.get(0).getTotalAssessments());

        InOrder inOrder = inOrder(competitionParticipantRoleMapperMock, participantStatusMapperMock, competitionParticipantRepositoryMock, competitionParticipantMapperMock, assessmentRepositoryMock);
        inOrder.verify(competitionParticipantRoleMapperMock, calls(1)).mapToDomain(any(CompetitionParticipantRoleResource.class));
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByUserIdAndRole(1L, ASSESSOR);
        inOrder.verify(competitionParticipantMapperMock, calls(1)).mapToResource(any(CompetitionParticipant.class));
        inOrder.verifyNoMoreInteractions();
    }
}
