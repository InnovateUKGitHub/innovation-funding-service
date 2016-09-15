package com.worth.ifs.assessment.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.workflow.AssessmentWorkflowEventHandler;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.workflow.domain.ActivityState;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static com.worth.ifs.assessment.builder.ProcessOutcomeBuilder.newProcessOutcome;
import static com.worth.ifs.assessment.builder.ProcessOutcomeResourceBuilder.newProcessOutcomeResource;
import static com.worth.ifs.assessment.resource.AssessmentStates.OPEN;
import static com.worth.ifs.commons.error.CommonFailureKeys.ASSESSMENT_RECOMMENDATION_FAILED;
import static com.worth.ifs.commons.error.CommonFailureKeys.ASSESSMENT_REJECTION_FAILED;
import static com.worth.ifs.commons.error.Error.fieldError;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static java.util.Collections.nCopies;
import static org.junit.Assert.*;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class AssessmentServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private AssessmentService assessmentService = new AssessmentServiceImpl();

    @Mock
    private AssessmentWorkflowEventHandler assessmentWorkflowEventHandler;

    @Test
    public void findById() throws Exception {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment().build();
        AssessmentResource expected = newAssessmentResource().build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(assessmentMapperMock.mapToResource(same(assessment))).thenReturn(expected);

        AssessmentResource found = assessmentService.findById(assessmentId).getSuccessObject();

        assertSame(expected, found);
        verify(assessmentRepositoryMock, only()).findOne(assessmentId);
    }

    @Test
    public void findByUserAndCompetition() throws Exception {
        Long userId = 2L;
        Long competitionId = 1L;

        List<Application> applications = newApplication().build(2);
        List<ProcessRole> processRoles = newProcessRole().withUser(newUser().withid(userId).build()).build(2);
        List<Assessment> assessments = newAssessment().build(2);
        List<AssessmentResource> expected = newAssessmentResource().build(2);

        when(processRoleRepositoryMock.findByUserIdAndApplicationId(userId, applications.get(0).getId())).thenReturn(processRoles.get(0));
        when(processRoleRepositoryMock.findByUserIdAndApplicationId(userId, applications.get(1).getId())).thenReturn(processRoles.get(1));
        when(assessmentRepositoryMock.findByParticipantIn(processRoles)).thenReturn(assessments);
        when(assessmentMapperMock.mapToResource(same(assessments.get(0)))).thenReturn(expected.get(0));
        when(assessmentMapperMock.mapToResource(same(assessments.get(1)))).thenReturn(expected.get(1));
        when(applicationRepositoryMock.findByCompetitionId(competitionId)).thenReturn(applications);

        List<AssessmentResource> found = assessmentService.findByUserAndCompetition(userId, competitionId).getSuccessObject();

        assertEquals(expected, found);
        InOrder inOrder = inOrder(applicationRepositoryMock, processRoleRepositoryMock, assessmentRepositoryMock, assessmentMapperMock);
        inOrder.verify(applicationRepositoryMock, calls(1)).findByCompetitionId(competitionId);
        inOrder.verify(processRoleRepositoryMock, calls(1)).findByUserIdAndApplicationId(userId, applications.get(0).getId());
        inOrder.verify(processRoleRepositoryMock, calls(1)).findByUserIdAndApplicationId(userId, applications.get(1).getId());
        inOrder.verify(assessmentRepositoryMock, calls(1)).findByParticipantIn(processRoles);
        inOrder.verify(assessmentMapperMock, calls(1)).mapToResource(assessments.get(0));
        inOrder.verify(assessmentMapperMock, calls(1)).mapToResource(assessments.get(1));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void recommend() throws Exception {
        Long assessmentId = 1L;
        Long processRoleId = 2L;

        ProcessRole processRole = newProcessRole().withId(processRoleId).build();
        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withParticipant(processRole)
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, OPEN.getBackingState()))
                .build();

        ProcessOutcome processOutcome = newProcessOutcome().build();

        ProcessOutcomeResource processOutcomeResource = newProcessOutcomeResource()
                .withOutcomeType(AssessmentOutcomes.RECOMMEND.getType())
                .build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(processOutcomeMapperMock.mapToDomain(processOutcomeResource)).thenReturn(processOutcome);
        when(assessmentWorkflowEventHandler.recommend(processRoleId, assessment, processOutcome)).thenReturn(true);

        ServiceResult<Void> result = assessmentService.recommend(assessmentId, processOutcomeResource);
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessmentRepositoryMock, processOutcomeMapperMock, assessmentWorkflowEventHandler);
        inOrder.verify(assessmentRepositoryMock, calls(1)).findOne(assessmentId);
        inOrder.verify(processOutcomeMapperMock, calls(1)).mapToDomain(processOutcomeResource);
        inOrder.verify(assessmentWorkflowEventHandler, calls(1)).recommend(processRoleId, assessment, processOutcome);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void recommend_exceedsWordLimit() throws Exception {
        Long assessmentId = 1L;
        Long processRoleId = 2L;
        String feedback = String.join(" ", nCopies(101, "feedback"));
        String comment = String.join(" ", nCopies(101, "comment"));

        ProcessRole processRole = newProcessRole().withId(processRoleId).build();
        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withParticipant(processRole)
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, OPEN.getBackingState()))
                .build();

        ProcessOutcome processOutcome = newProcessOutcome().build();

        ProcessOutcomeResource processOutcomeResource = newProcessOutcomeResource()
                .withDescription(feedback)
                .withComment(comment)
                .withOutcomeType(AssessmentOutcomes.RECOMMEND.getType())
                .build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(processOutcomeMapperMock.mapToDomain(processOutcomeResource)).thenReturn(processOutcome);

        ServiceResult<Void> result = assessmentService.recommend(assessmentId, processOutcomeResource);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(fieldError("feedback", feedback, "validation.field.max.word.count", 100), fieldError("comment", comment, "validation.field.max.word.count", 100)));
    }

    @Test
    public void recommend_eventNotAccepted() throws Exception {
        Long assessmentId = 1L;
        Long processRoleId = 2L;

        ProcessRole processRole = newProcessRole().withId(processRoleId).build();
        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withParticipant(processRole)
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, OPEN.getBackingState()))
                .build();

        ProcessOutcome processOutcome = newProcessOutcome().build();

        ProcessOutcomeResource processOutcomeResource = newProcessOutcomeResource()
                .withOutcomeType(AssessmentOutcomes.RECOMMEND.getType())
                .build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(processOutcomeMapperMock.mapToDomain(processOutcomeResource)).thenReturn(processOutcome);
        when(assessmentWorkflowEventHandler.recommend(processRoleId, assessment, processOutcome)).thenReturn(false);

        ServiceResult<Void> result = assessmentService.recommend(assessmentId, processOutcomeResource);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(ASSESSMENT_RECOMMENDATION_FAILED));

        InOrder inOrder = inOrder(assessmentRepositoryMock, processOutcomeMapperMock, assessmentWorkflowEventHandler);
        inOrder.verify(assessmentRepositoryMock, calls(1)).findOne(assessmentId);
        inOrder.verify(processOutcomeMapperMock, calls(1)).mapToDomain(processOutcomeResource);
        inOrder.verify(assessmentWorkflowEventHandler, calls(1)).recommend(processRoleId, assessment, processOutcome);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvitation() throws Exception {
        Long assessmentId = 1L;
        Long processRoleId = 2L;

        ProcessRole processRole = newProcessRole().withId(processRoleId).build();
        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withParticipant(processRole)
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, OPEN.getBackingState()))
                .build();

        ProcessOutcome processOutcome = newProcessOutcome().build();

        ProcessOutcomeResource processOutcomeResource = newProcessOutcomeResource()
                .withOutcomeType(AssessmentOutcomes.REJECT.getType())
                .build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(processOutcomeMapperMock.mapToDomain(processOutcomeResource)).thenReturn(processOutcome);
        when(assessmentWorkflowEventHandler.rejectInvitation(processRoleId, assessment, processOutcome)).thenReturn(true);

        ServiceResult<Void> result = assessmentService.rejectInvitation(assessmentId, processOutcomeResource);
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessmentRepositoryMock, processOutcomeMapperMock, assessmentWorkflowEventHandler);
        inOrder.verify(assessmentRepositoryMock, calls(1)).findOne(assessmentId);
        inOrder.verify(processOutcomeMapperMock, calls(1)).mapToDomain(processOutcomeResource);
        inOrder.verify(assessmentWorkflowEventHandler, calls(1)).rejectInvitation(processRoleId, assessment, processOutcome);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvitation_eventNotAccepted() throws Exception {
        Long assessmentId = 1L;
        Long processRoleId = 2L;

        ProcessRole processRole = newProcessRole().withId(processRoleId).build();
        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withParticipant(processRole)
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, OPEN.getBackingState()))
                .build();

        ProcessOutcome processOutcome = newProcessOutcome().build();

        ProcessOutcomeResource processOutcomeResource = newProcessOutcomeResource()
                .withOutcomeType(AssessmentOutcomes.REJECT.getType())
                .build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(processOutcomeMapperMock.mapToDomain(processOutcomeResource)).thenReturn(processOutcome);
        when(assessmentWorkflowEventHandler.rejectInvitation(processRoleId, assessment, processOutcome)).thenReturn(false);

        ServiceResult<Void> result = assessmentService.rejectInvitation(assessmentId, processOutcomeResource);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(ASSESSMENT_REJECTION_FAILED));

        InOrder inOrder = inOrder(assessmentRepositoryMock, processOutcomeMapperMock, assessmentWorkflowEventHandler);
        inOrder.verify(assessmentRepositoryMock, calls(1)).findOne(assessmentId);
        inOrder.verify(processOutcomeMapperMock, calls(1)).mapToDomain(processOutcomeResource);
        inOrder.verify(assessmentWorkflowEventHandler, calls(1)).rejectInvitation(processRoleId, assessment, processOutcome);
        inOrder.verifyNoMoreInteractions();
    }
}