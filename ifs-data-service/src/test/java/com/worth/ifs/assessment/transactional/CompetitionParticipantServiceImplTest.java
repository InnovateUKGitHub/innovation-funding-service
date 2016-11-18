package com.worth.ifs.assessment.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.domain.CompetitionParticipant;
import com.worth.ifs.invite.domain.CompetitionParticipantRole;
import com.worth.ifs.invite.domain.ParticipantStatus;
import com.worth.ifs.invite.resource.CompetitionParticipantResource;
import com.worth.ifs.invite.resource.CompetitionParticipantRoleResource;
import com.worth.ifs.invite.resource.ParticipantStatusResource;
import com.worth.ifs.workflow.domain.ActivityState;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.assessment.resource.AssessmentStates.OPEN;
import static com.worth.ifs.assessment.resource.AssessmentStates.SUBMITTED;
import static com.worth.ifs.invite.builder.CompetitionParticipantResourceBuilder.newCompetitionParticipantResource;
import static com.worth.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;
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
        when(competitionParticipantRepositoryMock.getByUserIdAndRoleAndStatus(1L, CompetitionParticipantRole.ASSESSOR, ParticipantStatus.PENDING)).thenReturn(competitionParticipants);
        when(competitionParticipantMapperMock.mapToResource(same(competitionParticipant))).thenReturn(expected);
        when(competitionParticipantRoleMapperMock.mapToDomain(CompetitionParticipantRoleResource.ASSESSOR)).thenReturn(CompetitionParticipantRole.ASSESSOR);
        when(participantStatusMapperMock.mapToDomain(ParticipantStatusResource.PENDING)).thenReturn(ParticipantStatus.PENDING);

        ServiceResult<List<CompetitionParticipantResource>> competitionParticipantServiceResult = competitionParticipantService.getCompetitionParticipants(1L, CompetitionParticipantRoleResource.ASSESSOR, ParticipantStatusResource.PENDING);

        List<CompetitionParticipantResource> found = competitionParticipantServiceResult.getSuccessObject();
        assertSame(expected, found.get(0));
        assertEquals(0L, found.get(0).getSubmittedAssessments());
        assertEquals(0L, found.get(0).getTotalAssessments());
        InOrder inOrder = inOrder(competitionParticipantRepositoryMock, competitionParticipantMapperMock);
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByUserIdAndRoleAndStatus(1L, CompetitionParticipantRole.ASSESSOR, ParticipantStatus.PENDING);
        inOrder.verify(competitionParticipantMapperMock, calls(1)).mapToResource(any(CompetitionParticipant.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getCompetitionParticipantsWithAssessments() throws Exception {
        Long userId = 1L;
        Long competitionId = 2L;

        List<CompetitionParticipant> competitionParticipants = new ArrayList<>();
        CompetitionParticipant competitionParticipant = new CompetitionParticipant();
        competitionParticipants.add(competitionParticipant);

        CompetitionParticipantResource expected = newCompetitionParticipantResource()
                .withUser(userId)
                .withCompetition(competitionId)
                .build();
        when(competitionParticipantRepositoryMock.getByUserIdAndRoleAndStatus(userId, CompetitionParticipantRole.ASSESSOR, ParticipantStatus.ACCEPTED)).thenReturn(competitionParticipants);
        when(competitionParticipantMapperMock.mapToResource(same(competitionParticipant))).thenReturn(expected);
        when(competitionParticipantRoleMapperMock.mapToDomain(CompetitionParticipantRoleResource.ASSESSOR)).thenReturn(CompetitionParticipantRole.ASSESSOR);
        when(participantStatusMapperMock.mapToDomain(ParticipantStatusResource.ACCEPTED)).thenReturn(ParticipantStatus.ACCEPTED);

        List<Assessment> assessments = newAssessment()
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, OPEN.getBackingState()),new ActivityState(APPLICATION_ASSESSMENT, SUBMITTED.getBackingState()))
                .build(2);
        when(assessmentRepositoryMock.findByParticipantUserIdAndParticipantApplicationCompetitionIdOrderByActivityStateStateAscIdAsc(userId,competitionId)).thenReturn(assessments);

        ServiceResult<List<CompetitionParticipantResource>> competitionParticipantServiceResult = competitionParticipantService.getCompetitionParticipants(1L, CompetitionParticipantRoleResource.ASSESSOR, ParticipantStatusResource.ACCEPTED);

        List<CompetitionParticipantResource> found = competitionParticipantServiceResult.getSuccessObject();
        assertSame(expected, found.get(0));
        assertEquals(1L, found.get(0).getSubmittedAssessments());
        assertEquals(2L, found.get(0).getTotalAssessments());
        InOrder inOrder = inOrder(competitionParticipantRoleMapperMock, participantStatusMapperMock, competitionParticipantRepositoryMock, competitionParticipantMapperMock, assessmentRepositoryMock);
        inOrder.verify(competitionParticipantRoleMapperMock, calls(1)).mapToDomain(any(CompetitionParticipantRoleResource.class));
        inOrder.verify(participantStatusMapperMock, calls(1)).mapToDomain(any(ParticipantStatusResource.class));
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByUserIdAndRoleAndStatus(1L, CompetitionParticipantRole.ASSESSOR, ParticipantStatus.ACCEPTED);
        inOrder.verify(competitionParticipantMapperMock, calls(1)).mapToResource(any(CompetitionParticipant.class));
        inOrder.verify(assessmentRepositoryMock, calls(1)).findByParticipantUserIdAndParticipantApplicationCompetitionIdOrderByActivityStateStateAscIdAsc(userId,competitionId);
        inOrder.verifyNoMoreInteractions();
    }
}