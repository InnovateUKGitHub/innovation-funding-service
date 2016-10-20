package com.worth.ifs.assessment.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.AssessmentFundingDecisionResource;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.workflow.configuration.AssessmentWorkflowHandler;
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

import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.assessment.builder.AssessmentFundingDecisionResourceBuilder.newAssessmentFundingDecisionResource;
import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static com.worth.ifs.assessment.builder.ProcessOutcomeBuilder.newProcessOutcome;
import static com.worth.ifs.assessment.builder.ProcessOutcomeResourceBuilder.newProcessOutcomeResource;
import static com.worth.ifs.assessment.resource.AssessmentStates.OPEN;
import static com.worth.ifs.commons.error.CommonFailureKeys.ASSESSMENT_RECOMMENDATION_FAILED;
import static com.worth.ifs.commons.error.CommonFailureKeys.ASSESSMENT_REJECTION_FAILED;
import static com.worth.ifs.commons.error.Error.fieldError;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;
import static java.util.Collections.nCopies;
import static org.junit.Assert.*;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class AssessmentServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private AssessmentService assessmentService = new AssessmentServiceImpl();

    @Mock
    private AssessmentWorkflowHandler assessmentWorkflowService;

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

        List<Assessment> assessments = newAssessment().build(2);
        List<AssessmentResource> expected = newAssessmentResource().build(2);

        when(assessmentRepositoryMock.findByParticipantUserIdAndParticipantApplicationCompetitionId(userId, competitionId)).thenReturn(assessments);
        when(assessmentMapperMock.mapToResource(same(assessments.get(0)))).thenReturn(expected.get(0));
        when(assessmentMapperMock.mapToResource(same(assessments.get(1)))).thenReturn(expected.get(1));

        List<AssessmentResource> found = assessmentService.findByUserAndCompetition(userId, competitionId).getSuccessObject();

        assertEquals(expected, found);
        verify(assessmentRepositoryMock, only()).findByParticipantUserIdAndParticipantApplicationCompetitionId(userId, competitionId);
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

        AssessmentFundingDecisionResource assessmentFundingDecision = newAssessmentFundingDecisionResource().build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(assessmentWorkflowService.recommend(processRoleId, assessment, assessmentFundingDecision)).thenReturn(true);

        ServiceResult<Void> result = assessmentService.recommend(assessmentId, assessmentFundingDecision);
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentWorkflowService);
        inOrder.verify(assessmentRepositoryMock, calls(1)).findOne(assessmentId);
        inOrder.verify(assessmentWorkflowService, calls(1)).recommend(processRoleId, assessment, assessmentFundingDecision);
        inOrder.verifyNoMoreInteractions();
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

        AssessmentFundingDecisionResource assessmentFundingDecision = newAssessmentFundingDecisionResource().build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(assessmentWorkflowService.recommend(processRoleId, assessment, assessmentFundingDecision)).thenReturn(false);

        ServiceResult<Void> result = assessmentService.recommend(assessmentId, assessmentFundingDecision);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(ASSESSMENT_RECOMMENDATION_FAILED));

        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentWorkflowService);
        inOrder.verify(assessmentRepositoryMock, calls(1)).findOne(assessmentId);
        inOrder.verify(assessmentWorkflowService, calls(1)).recommend(processRoleId, assessment, assessmentFundingDecision);
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
        when(assessmentWorkflowService.rejectInvitation(processRoleId, assessment, processOutcome)).thenReturn(true);

        ServiceResult<Void> result = assessmentService.rejectInvitation(assessmentId, processOutcomeResource);
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessmentRepositoryMock, processOutcomeMapperMock, assessmentWorkflowService);
        inOrder.verify(assessmentRepositoryMock, calls(1)).findOne(assessmentId);
        inOrder.verify(processOutcomeMapperMock, calls(1)).mapToDomain(processOutcomeResource);
        inOrder.verify(assessmentWorkflowService, calls(1)).rejectInvitation(processRoleId, assessment, processOutcome);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvitation_exceedsWordLimit() throws Exception {
        String reason = "reason";
        String comment = String.join(" ", nCopies(101, "comment"));

        ProcessOutcomeResource processOutcomeResource = newProcessOutcomeResource()
                .withComment(comment)
                .withDescription(reason)
                .withOutcomeType(AssessmentOutcomes.REJECT.getType())
                .build();

        ServiceResult<Void> result = assessmentService.rejectInvitation(1L, processOutcomeResource);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(fieldError("comment", comment, "validation.field.max.word.count", "", 100)));
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
        when(assessmentWorkflowService.rejectInvitation(processRoleId, assessment, processOutcome)).thenReturn(false);

        ServiceResult<Void> result = assessmentService.rejectInvitation(assessmentId, processOutcomeResource);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(ASSESSMENT_REJECTION_FAILED));

        InOrder inOrder = inOrder(assessmentRepositoryMock, processOutcomeMapperMock, assessmentWorkflowService);
        inOrder.verify(assessmentRepositoryMock, calls(1)).findOne(assessmentId);
        inOrder.verify(processOutcomeMapperMock, calls(1)).mapToDomain(processOutcomeResource);
        inOrder.verify(assessmentWorkflowService, calls(1)).rejectInvitation(processRoleId, assessment, processOutcome);
        inOrder.verifyNoMoreInteractions();
    }
}