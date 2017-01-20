package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.assessment.workflow.configuration.AssessmentWorkflowHandler;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.builder.ApplicationRejectionResourceBuilder.newApplicationRejectionResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.AssessmentCreateResourceBuilder.newAssessmentCreateResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentFundingDecisionResourceBuilder.newAssessmentFundingDecisionResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentSubmissionsResourceBuilder.newAssessmentSubmissionsResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentTotalScoreResourceBuilder.newAssessmentTotalScoreResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.*;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.resource.UserRoleType.ASSESSOR;
import static org.innovateuk.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;
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
    public void findAssignableById() throws Exception {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment().build();
        AssessmentResource expected = newAssessmentResource().build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(assessmentMapperMock.mapToResource(same(assessment))).thenReturn(expected);

        AssessmentResource found = assessmentService.findAssignableById(assessmentId).getSuccessObject();

        assertSame(expected, found);
        verify(assessmentRepositoryMock, only()).findOne(assessmentId);
    }

    @Test
    public void findByUserAndCompetition() throws Exception {
        Long userId = 2L;
        Long competitionId = 1L;

        List<Assessment> assessments = newAssessment().build(2);
        List<AssessmentResource> expected = newAssessmentResource().build(2);

        when(assessmentRepositoryMock.findByParticipantUserIdAndTargetCompetitionIdOrderByActivityStateStateAscIdAsc(userId, competitionId)).thenReturn(assessments);
        when(assessmentMapperMock.mapToResource(same(assessments.get(0)))).thenReturn(expected.get(0));
        when(assessmentMapperMock.mapToResource(same(assessments.get(1)))).thenReturn(expected.get(1));

        List<AssessmentResource> found = assessmentService.findByUserAndCompetition(userId, competitionId).getSuccessObject();

        assertEquals(expected, found);
        verify(assessmentRepositoryMock, only()).findByParticipantUserIdAndTargetCompetitionIdOrderByActivityStateStateAscIdAsc(userId, competitionId);
    }

    @Test
    public void getTotalScore() throws Exception {
        Long assessmentId = 1L;

        AssessmentTotalScoreResource expected = newAssessmentTotalScoreResource()
                .withTotalScoreGiven(55)
                .withTotalScorePossible(100)
                .build();

        when(assessmentRepositoryMock.getTotalScore(assessmentId)).thenReturn(new AssessmentTotalScoreResource(55, 100));

        assertEquals(expected, assessmentService.getTotalScore(assessmentId).getSuccessObject());

        verify(assessmentRepositoryMock, only()).getTotalScore(assessmentId);
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
    public void withdrawAssessment() throws Exception {
        Assessment assessment = newAssessment()
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, OPEN.getBackingState()))
                .build();

        when(assessmentRepositoryMock.findOne(assessment.getId())).thenReturn(assessment);
        when(assessmentWorkflowHandler.withdrawAssessment(assessment)).thenReturn(true);

        ServiceResult<Void> result = assessmentService.withdrawAssessment(assessment.getId());
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentWorkflowHandler);
        inOrder.verify(assessmentRepositoryMock).findOne(assessment.getId());
        inOrder.verify(assessmentWorkflowHandler).withdrawAssessment(assessment);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void withdrawAssessment_eventNotAccepted() throws Exception {
        Assessment assessment = newAssessment()
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, OPEN.getBackingState()))
                .build();

        when(assessmentRepositoryMock.findOne(assessment.getId())).thenReturn(assessment);
        when(assessmentWorkflowHandler.withdrawAssessment(assessment)).thenReturn(false);

        ServiceResult<Void> result = assessmentService.withdrawAssessment(assessment.getId());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(ASSESSMENT_WITHDRAW_FAILED));

        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentWorkflowHandler);
        inOrder.verify(assessmentRepositoryMock).findOne(assessment.getId());
        inOrder.verify(assessmentWorkflowHandler).withdrawAssessment(assessment);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void notifyAssessor() throws Exception {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, CREATED.getBackingState()))
                .build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(assessmentWorkflowHandler.notify(assessment)).thenReturn(true);

        ServiceResult<Void> result = assessmentService.notify(assessmentId);
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentWorkflowHandler);
        inOrder.verify(assessmentRepositoryMock).findOne(assessmentId);
        inOrder.verify(assessmentWorkflowHandler).notify(assessment);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void notifyAssessor_eventNotAccepted() throws Exception {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, CREATED.getBackingState()))
                .build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(assessmentWorkflowHandler.notify(assessment)).thenReturn(false);

        ServiceResult<Void> result = assessmentService.notify(assessmentId);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(ASSESSMENT_NOTIFY_FAILED));

        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentWorkflowHandler);
        inOrder.verify(assessmentRepositoryMock).findOne(assessmentId);
        inOrder.verify(assessmentWorkflowHandler).notify(assessment);
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

    @Test
    public void submitAssessments() throws Exception {
        AssessmentSubmissionsResource assessmentSubmissions = newAssessmentSubmissionsResource()
                .withAssessmentIds(asList(1L, 2L))
                .build();
        List<Assessment> assessments = newAssessment()
                .withId(1L, 2L)
                .withActivityState(
                        new ActivityState(APPLICATION_ASSESSMENT, READY_TO_SUBMIT.getBackingState()),
                        new ActivityState(APPLICATION_ASSESSMENT, READY_TO_SUBMIT.getBackingState())
                )
                .build(2);

        assertEquals(2, assessmentSubmissions.getAssessmentIds().size());

        when(assessmentRepositoryMock.findAll(assessmentSubmissions.getAssessmentIds())).thenReturn(assessments);

        when(assessmentWorkflowHandler.submit(assessments.get(0))).thenAnswer(invocation -> {
            assessments.get(0).setActivityState(new ActivityState(APPLICATION_ASSESSMENT, SUBMITTED.getBackingState()));
            return Boolean.TRUE;
        });
        when(assessmentWorkflowHandler.submit(assessments.get(1))).thenAnswer(invocation -> {
            assessments.get(1).setActivityState(new ActivityState(APPLICATION_ASSESSMENT, SUBMITTED.getBackingState()));
            return Boolean.TRUE;
        });

        ServiceResult<Void> result = assessmentService.submitAssessments(assessmentSubmissions);
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentWorkflowHandler);
        inOrder.verify(assessmentRepositoryMock, calls(1)).findAll(assessmentSubmissions.getAssessmentIds());
        inOrder.verify(assessmentWorkflowHandler, calls(1)).submit(assessments.get(0));
        inOrder.verify(assessmentWorkflowHandler, calls(1)).submit(assessments.get(1));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitAssessments_eventNotAccepted() throws Exception {
        AssessmentSubmissionsResource assessmentSubmissions = newAssessmentSubmissionsResource()
                .withAssessmentIds(singletonList(1L))
                .build();

        Application application = new Application();
        application.setName("Test Application");

        Assessment assessment = newAssessment()
                .withId(1L)
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, PENDING.getBackingState()))
                .with((resource) -> resource.setTarget(application))
                .build();

        assertEquals(1, assessmentSubmissions.getAssessmentIds().size());

        when(assessmentRepositoryMock.findAll(assessmentSubmissions.getAssessmentIds())).thenReturn(singletonList(assessment));
        when(assessmentWorkflowHandler.submit(assessment)).thenReturn(false);

        ServiceResult<Void> result = assessmentService.submitAssessments(assessmentSubmissions);
        assertTrue(result.isFailure());
        assertEquals(result.getErrors().get(0), new Error(ASSESSMENT_SUBMIT_FAILED, 1L, "Test Application"));

        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentWorkflowHandler);
        inOrder.verify(assessmentRepositoryMock, calls(1)).findAll(assessmentSubmissions.getAssessmentIds());
        inOrder.verify(assessmentWorkflowHandler, calls(1)).submit(assessment);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitAssessments_notFound() throws Exception {
        AssessmentSubmissionsResource assessmentSubmissions = newAssessmentSubmissionsResource()
                .withAssessmentIds(asList(1L, 2L))
                .build();

        when(assessmentRepositoryMock.findAll(assessmentSubmissions.getAssessmentIds())).thenReturn(emptyList());

        ServiceResult<Void> result = assessmentService.submitAssessments(assessmentSubmissions);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(Assessment.class, 1L), notFoundError(Assessment.class, 2L)));

        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentWorkflowHandler);
        inOrder.verify(assessmentRepositoryMock, calls(1)).findAll(assessmentSubmissions.getAssessmentIds());
        inOrder.verify(assessmentWorkflowHandler, never()).submit(any());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitAssessments_notFoundAndEventNotAccepted() throws Exception {
        AssessmentSubmissionsResource assessmentSubmissions = newAssessmentSubmissionsResource()
                .withAssessmentIds(asList(1L, 2L))
                .build();

        Application application = newApplication()
                .withName("Test Application")
                .build();

        Assessment assessment = newAssessment()
                .withId(1L)
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, PENDING.getBackingState()))
                .with((resource) -> resource.setTarget(application))
                .build();

        assertEquals(2, assessmentSubmissions.getAssessmentIds().size());

        when(assessmentRepositoryMock.findAll(assessmentSubmissions.getAssessmentIds())).thenReturn(singletonList(assessment));
        when(assessmentWorkflowHandler.submit(assessment)).thenReturn(false);

        ServiceResult<Void> result = assessmentService.submitAssessments(assessmentSubmissions);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(new Error(ASSESSMENT_SUBMIT_FAILED, 1L, "Test Application"), notFoundError(Assessment.class, 2L)));

        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentWorkflowHandler);
        inOrder.verify(assessmentRepositoryMock, calls(1)).findAll(assessmentSubmissions.getAssessmentIds());
        inOrder.verify(assessmentWorkflowHandler, calls(1)).submit(assessment);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void createAssessment() throws Exception {
        State expectedBackingState = CREATED.getBackingState();

        Long assessorId = 1L;
        Long applicationId = 2L;

        User user = newUser().withId(assessorId).build();
        Application application = newApplication().withId(applicationId).build();
        Role role = newRole().withType(ASSESSOR).build();
        ActivityState activityState = new ActivityState(APPLICATION_ASSESSMENT, expectedBackingState);

        ProcessRole expectedProcessRole = newProcessRole()
                .with(id(null))
                .withApplication(application)
                .withUser(user)
                .withRole(role)
                .build();
        ProcessRole savedProcessRole = newProcessRole()
                .withId(10L)
                .withApplication(application)
                .withUser(user)
                .withRole(role)
                .build();

        Assessment expectedAssessment = newAssessment()
                .with(id(null))
                .withApplication(application)
                .withActivityState(activityState)
                .withParticipant(savedProcessRole)
                .build();
        Assessment savedAssessment = newAssessment()
                .withId(5L)
                .withApplication(application)
                .withActivityState(activityState)
                .withParticipant(savedProcessRole)
                .build();

        AssessmentResource expectedAssessmentResource = newAssessmentResource().build();

        when(userRepositoryMock.findByIdAndRolesName(assessorId, ASSESSOR.getName())).thenReturn(Optional.of(user));
        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
        when(assessmentRepositoryMock.findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(assessorId, applicationId)).thenReturn(Optional.empty());
        when(roleRepositoryMock.findOneByName(ASSESSOR.getName())).thenReturn(role);
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, expectedBackingState)).thenReturn(activityState);
        when(processRoleRepositoryMock.save(expectedProcessRole)).thenReturn(savedProcessRole);
        when(assessmentRepositoryMock.save(expectedAssessment)).thenReturn(savedAssessment);
        when(assessmentMapperMock.mapToResource(savedAssessment)).thenReturn(expectedAssessmentResource);

        AssessmentCreateResource assessmentCreateResource = newAssessmentCreateResource()
                .withApplicationId(applicationId)
                .withAssessorId(assessorId)
                .build();

        ServiceResult<AssessmentResource> serviceResult = assessmentService.createAssessment(assessmentCreateResource);

        InOrder inOrder = inOrder(
                userRepositoryMock, applicationRepositoryMock, roleRepositoryMock, activityStateRepositoryMock,
                processRoleRepositoryMock, assessmentRepositoryMock, assessmentMapperMock
        );

        inOrder.verify(userRepositoryMock).findByIdAndRolesName(assessorId, ASSESSOR.getName());
        inOrder.verify(applicationRepositoryMock).findOne(applicationId);
        inOrder.verify(assessmentRepositoryMock).findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(assessorId, applicationId);
        inOrder.verify(roleRepositoryMock).findOneByName(ASSESSOR.getName());
        inOrder.verify(activityStateRepositoryMock).findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, expectedBackingState);
        inOrder.verify(processRoleRepositoryMock).save(expectedProcessRole);
        inOrder.verify(assessmentRepositoryMock).save(expectedAssessment);
        inOrder.verify(assessmentMapperMock).mapToResource(expectedAssessment);
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isSuccess());
        assertEquals(expectedAssessmentResource, serviceResult.getSuccessObjectOrThrowException());
    }

    @Test
    public void createAssessment_existingWithdrawnAssessment() throws Exception {
        State expectedBackingState = WITHDRAWN.getBackingState();

        Long assessorId = 1L;
        Long applicationId = 2L;

        User user = newUser().withId(assessorId).build();
        Application application = newApplication().withId(applicationId).build();
        Role role = newRole().withName(ASSESSOR.getName()).build();

        ActivityState activityState = new ActivityState(APPLICATION_ASSESSMENT, expectedBackingState);

        Assessment existingAssessment = newAssessment()
                .withActivityState(activityState)
                .build();

        ProcessRole expectedProcessRole = newProcessRole()
                .with(id(null))
                .withApplication(application)
                .withUser(user)
                .withRole(role)
                .build();
        ProcessRole savedProcessRole = newProcessRole()
                .withId(10L)
                .withApplication(application)
                .withUser(user)
                .withRole(role)
                .build();

        Assessment expectedAssessment = newAssessment()
                .with(id(null))
                .withApplication(application)
                .withActivityState(activityState)
                .withParticipant(savedProcessRole)
                .build();
        Assessment savedAssessment = newAssessment()
                .withId(5L)
                .withApplication(application)
                .withActivityState(activityState)
                .withParticipant(savedProcessRole)
                .build();

        AssessmentResource expectedAssessmentResource = newAssessmentResource().build();

        when(userRepositoryMock.findByIdAndRolesName(assessorId, ASSESSOR.getName())).thenReturn(Optional.of(user));
        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
        when(assessmentRepositoryMock.findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(assessorId, applicationId)).thenReturn(Optional.of(existingAssessment));
        when(roleRepositoryMock.findOneByName(ASSESSOR.getName())).thenReturn(role);
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, CREATED.getBackingState())).thenReturn(activityState);
        when(processRoleRepositoryMock.save(expectedProcessRole)).thenReturn(savedProcessRole);
        when(assessmentRepositoryMock.save(expectedAssessment)).thenReturn(savedAssessment);
        when(assessmentMapperMock.mapToResource(savedAssessment)).thenReturn(expectedAssessmentResource);

        AssessmentCreateResource assessmentCreateResource = newAssessmentCreateResource()
                .withAssessorId(assessorId)
                .withApplicationId(applicationId)
                .build();

        ServiceResult<AssessmentResource> serviceResult = assessmentService.createAssessment(assessmentCreateResource);

        InOrder inOrder = inOrder(
                userRepositoryMock, applicationRepositoryMock, roleRepositoryMock, activityStateRepositoryMock,
                processRoleRepositoryMock, assessmentRepositoryMock, assessmentMapperMock
        );

        inOrder.verify(userRepositoryMock).findByIdAndRolesName(assessorId, ASSESSOR.getName());
        inOrder.verify(applicationRepositoryMock).findOne(applicationId);
        inOrder.verify(roleRepositoryMock).findOneByName(ASSESSOR.getName());
        inOrder.verify(activityStateRepositoryMock).findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, CREATED.getBackingState());
        inOrder.verify(processRoleRepositoryMock).save(expectedProcessRole);
        inOrder.verify(assessmentRepositoryMock).save(expectedAssessment);
        inOrder.verify(assessmentMapperMock).mapToResource(expectedAssessment);
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isSuccess());
        assertEquals(expectedAssessmentResource, serviceResult.getSuccessObject());
    }

    @Test
    public void createAssessment_noAssessor() throws Exception {
        Long assessorId = 100L;
        Long applicationId = 2L;

        when(userRepositoryMock.findByIdAndRolesName(assessorId, ASSESSOR.getName())).thenReturn(Optional.empty());

        AssessmentCreateResource assessmentCreateResource = newAssessmentCreateResource()
                .withAssessorId(assessorId)
                .withApplicationId(applicationId)
                .build();

        ServiceResult<AssessmentResource> serviceResult = assessmentService.createAssessment(assessmentCreateResource);

        InOrder inOrder = inOrder(userRepositoryMock, applicationRepositoryMock, roleRepositoryMock, activityStateRepositoryMock);
        inOrder.verify(userRepositoryMock).findByIdAndRolesName(assessorId, ASSESSOR.getName());
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isFailure());
        assertEquals(GENERAL_NOT_FOUND.getErrorKey(), serviceResult.getErrors().get(0).getErrorKey());
    }

    @Test
    public void createAssessment_noApplication() throws Exception {
        Long assessorId = 100L;
        Long applicationId = 2L;

        User user = newUser().withId(assessorId).build();

        when(userRepositoryMock.findByIdAndRolesName(assessorId, ASSESSOR.getName())).thenReturn(Optional.of(user));
        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(null);

        AssessmentCreateResource assessmentCreateResource = newAssessmentCreateResource()
                .withAssessorId(assessorId)
                .withApplicationId(applicationId)
                .build();

        ServiceResult<AssessmentResource> serviceResult = assessmentService.createAssessment(assessmentCreateResource);

        InOrder inOrder = inOrder(userRepositoryMock, applicationRepositoryMock, roleRepositoryMock, activityStateRepositoryMock);
        inOrder.verify(userRepositoryMock).findByIdAndRolesName(assessorId, ASSESSOR.getName());
        inOrder.verify(applicationRepositoryMock).findOne(applicationId);
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isFailure());
        assertEquals(GENERAL_NOT_FOUND.getErrorKey(), serviceResult.getErrors().get(0).getErrorKey());
    }

    @Test
    public void createAssessment_existingAssessment() throws Exception {
        Long assessorId = 100L;
        Long applicationId = 2L;

        User user = newUser().withId(assessorId).build();
        Application application = newApplication().withId(applicationId).build();

        Assessment existingAssessment = newAssessment()
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, PENDING.getBackingState()))
                .build();

        when(userRepositoryMock.findByIdAndRolesName(assessorId, ASSESSOR.getName())).thenReturn(Optional.of(user));
        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
        when(assessmentRepositoryMock.findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(user.getId(), application.getId()))
                .thenReturn(Optional.of(existingAssessment));

        AssessmentCreateResource assessmentCreateResource = newAssessmentCreateResource()
                .withAssessorId(assessorId)
                .withApplicationId(applicationId)
                .build();

        ServiceResult<AssessmentResource> serviceResult = assessmentService.createAssessment(assessmentCreateResource);

        InOrder inOrder = inOrder(userRepositoryMock, applicationRepositoryMock, assessmentRepositoryMock, roleRepositoryMock, activityStateRepositoryMock);
        inOrder.verify(userRepositoryMock).findByIdAndRolesName(assessorId, ASSESSOR.getName());
        inOrder.verify(applicationRepositoryMock).findOne(applicationId);
        inOrder.verify(assessmentRepositoryMock).findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(assessorId, applicationId);
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isFailure());
        assertEquals(1, serviceResult.getErrors().size());
        assertEquals(ASSESSMENT_CREATE_FAILED.getErrorKey(), serviceResult.getErrors().get(0).getErrorKey());
    }
}
