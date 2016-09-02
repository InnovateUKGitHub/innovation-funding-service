package com.worth.ifs.assessment.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.resource.AssessmentStates;
import com.worth.ifs.assessment.workflow.AssessmentWorkflowEventHandler;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;

import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static com.worth.ifs.assessment.builder.ProcessOutcomeBuilder.newProcessOutcome;
import static com.worth.ifs.assessment.builder.ProcessOutcomeResourceBuilder.newProcessOutcomeResource;
import static com.worth.ifs.commons.error.CommonFailureKeys.*;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
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

        Assessment assessment = newAssessment()
                .build();

        AssessmentResource expected = newAssessmentResource().build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(assessmentMapperMock.mapToResource(same(assessment))).thenReturn(expected);

        AssessmentResource found = assessmentService.findById(assessmentId).getSuccessObject();

        assertSame(expected, found);
        verify(assessmentRepositoryMock, only()).findOne(assessmentId);
    }

    @Test
    public void recommend() throws Exception {
        Long assessmentId = 1L;
        Long processRoleId = 2L;

        ProcessRole processRole = newProcessRole().withId(processRoleId).build();
        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withProcessStatus(AssessmentStates.OPEN)
                .withProcessRole(processRole)
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
    public void recommend_DescriptionExceedsWordLimit() throws Exception {
        Long assessmentId = 1L;
        Long processRoleId = 2L;
        String[] feedbackArray = new String[120];
        Arrays.fill(feedbackArray, "feedback ");
        String feedback = Arrays.toString(feedbackArray);

        ProcessRole processRole = newProcessRole().withId(processRoleId).build();
        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withProcessStatus(AssessmentStates.OPEN)
                .withProcessRole(processRole)
                .build();

        ProcessOutcome processOutcome = newProcessOutcome().build();

        ProcessOutcomeResource processOutcomeResource = newProcessOutcomeResource()
                .withDescription(feedback.substring(1, feedback.length()-1).replaceAll(",", ""))
                .withOutcomeType(AssessmentOutcomes.RECOMMEND.getType())
                .build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(processOutcomeMapperMock.mapToDomain(processOutcomeResource)).thenReturn(processOutcome);

        ServiceResult<Void> result = assessmentService.recommend(assessmentId, processOutcomeResource);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(new Error(ASSESSMENT_SUMMARY_FEEDBACK_WORD_LIMIT_EXCEEDED, 100)));
    }

    @Test
    public void recommend_CommentExceedsWordLimit() throws Exception {
        Long assessmentId = 1L;
        Long processRoleId = 2L;
        String[] commentArray = new String[120];
        Arrays.fill(commentArray, "comment ");
        String comment = Arrays.toString(commentArray);

        ProcessRole processRole = newProcessRole().withId(processRoleId).build();
        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withProcessStatus(AssessmentStates.OPEN)
                .withProcessRole(processRole)
                .build();

        ProcessOutcome processOutcome = newProcessOutcome().build();

        ProcessOutcomeResource processOutcomeResource = newProcessOutcomeResource()
                .withComment(comment.substring(1, comment.length()-1).replaceAll(",", ""))
                .withOutcomeType(AssessmentOutcomes.RECOMMEND.getType())
                .build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(processOutcomeMapperMock.mapToDomain(processOutcomeResource)).thenReturn(processOutcome);

        ServiceResult<Void> result = assessmentService.recommend(assessmentId, processOutcomeResource);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(new Error(ASSESSMENT_SUMMARY_COMMENT_WORD_LIMIT_EXCEEDED, 100)));
    }

    @Test
    public void recommend_eventNotAccepted() throws Exception {
        Long assessmentId = 1L;
        Long processRoleId = 2L;

        ProcessRole processRole = newProcessRole().withId(processRoleId).build();
        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withProcessStatus(AssessmentStates.OPEN)
                .withProcessRole(processRole)
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
                .withProcessStatus(AssessmentStates.OPEN)
                .withProcessRole(processRole)
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
                .withProcessStatus(AssessmentStates.OPEN)
                .withProcessRole(processRole)
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