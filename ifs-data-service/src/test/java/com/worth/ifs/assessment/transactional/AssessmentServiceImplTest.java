package com.worth.ifs.assessment.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.ApplicationRejectionResource;
import com.worth.ifs.assessment.resource.AssessmentFundingDecisionResource;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.workflow.configuration.AssessmentWorkflowHandler;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.workflow.domain.ActivityState;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static com.worth.ifs.assessment.builder.ApplicationRejectionResourceBuilder.newApplicationRejectionResource;
import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.assessment.builder.AssessmentFundingDecisionResourceBuilder.newAssessmentFundingDecisionResource;
import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static com.worth.ifs.assessment.resource.AssessmentStates.OPEN;
import static com.worth.ifs.assessment.resource.AssessmentStates.PENDING;
import static com.worth.ifs.commons.error.CommonFailureKeys.*;
import static com.worth.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;
import static org.junit.Assert.*;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class AssessmentServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private AssessmentService assessmentService = new AssessmentServiceImpl();

    @Mock
    private AssessmentWorkflowHandler assessmentWorkflowHandler;

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

        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, OPEN.getBackingState()))
                .build();

        AssessmentFundingDecisionResource assessmentFundingDecision = newAssessmentFundingDecisionResource().build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(assessmentWorkflowHandler.fundingDecision(assessment, assessmentFundingDecision)).thenReturn(true);

        ServiceResult<Void> result = assessmentService.recommend(assessmentId, assessmentFundingDecision);
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentWorkflowHandler);
        inOrder.verify(assessmentRepositoryMock, calls(1)).findOne(assessmentId);
        inOrder.verify(assessmentWorkflowHandler, calls(1)).fundingDecision(assessment, assessmentFundingDecision);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void recommend_eventNotAccepted() throws Exception {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, OPEN.getBackingState()))
                .build();

        AssessmentFundingDecisionResource assessmentFundingDecision = newAssessmentFundingDecisionResource().build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(assessmentWorkflowHandler.fundingDecision(assessment, assessmentFundingDecision)).thenReturn(false);

        ServiceResult<Void> result = assessmentService.recommend(assessmentId, assessmentFundingDecision);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(ASSESSMENT_RECOMMENDATION_FAILED));

        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentWorkflowHandler);
        inOrder.verify(assessmentRepositoryMock).findOne(assessmentId);
        inOrder.verify(assessmentWorkflowHandler).fundingDecision(assessment, assessmentFundingDecision);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvitation() throws Exception {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, OPEN.getBackingState()))
                .build();

        ApplicationRejectionResource applicationRejectionResource = newApplicationRejectionResource().build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(assessmentWorkflowHandler.rejectInvitation(assessment, applicationRejectionResource)).thenReturn(true);

        ServiceResult<Void> result = assessmentService.rejectInvitation(assessmentId, applicationRejectionResource);
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentWorkflowHandler);
        inOrder.verify(assessmentRepositoryMock).findOne(assessmentId);
        inOrder.verify(assessmentWorkflowHandler).rejectInvitation(assessment, applicationRejectionResource);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvitation_eventNotAccepted() throws Exception {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, OPEN.getBackingState()))
                .build();

        ApplicationRejectionResource applicationRejectionResource = newApplicationRejectionResource().build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(assessmentWorkflowHandler.rejectInvitation(assessment, applicationRejectionResource)).thenReturn(false);

        ServiceResult<Void> result = assessmentService.rejectInvitation(assessmentId, applicationRejectionResource);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(ASSESSMENT_REJECTION_FAILED));

        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentWorkflowHandler);
        inOrder.verify(assessmentRepositoryMock).findOne(assessmentId);
        inOrder.verify(assessmentWorkflowHandler).rejectInvitation(assessment, applicationRejectionResource);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptInvitation() throws Exception {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, PENDING.getBackingState()))
                .build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(assessmentWorkflowHandler.acceptInvitation(assessment)).thenReturn(true);

        ServiceResult<Void> result = assessmentService.acceptInvitation(assessmentId);
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentWorkflowHandler);
        inOrder.verify(assessmentRepositoryMock).findOne(assessmentId);
        inOrder.verify(assessmentWorkflowHandler).acceptInvitation(assessment);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptInvitation_eventNotAccepted() throws Exception {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, PENDING.getBackingState()))
                .build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(assessmentWorkflowHandler.acceptInvitation(assessment)).thenReturn(false);

        ServiceResult<Void> result = assessmentService.acceptInvitation(assessmentId);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(ASSESSMENT_ACCEPT_FAILED));

        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentWorkflowHandler);
        inOrder.verify(assessmentRepositoryMock, calls(1)).findOne(assessmentId);
        inOrder.verify(assessmentWorkflowHandler, calls(1)).acceptInvitation(assessment);
        inOrder.verifyNoMoreInteractions();
    }
}