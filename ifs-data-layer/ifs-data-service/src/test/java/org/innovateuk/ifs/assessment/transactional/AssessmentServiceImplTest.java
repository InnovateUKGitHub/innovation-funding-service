package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessmentFundingDecisionOutcome;
import org.innovateuk.ifs.assessment.domain.AssessmentRejectOutcome;
import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.Invite;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.competition.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.review.domain.ReviewInvite;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.review.resource.ReviewInviteStatisticsResource;
import org.innovateuk.ifs.review.resource.ReviewKeyStatisticsResource;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.builder.ApplicationAssessmentFeedbackResourceBuilder.newApplicationAssessmentFeedbackResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.AssessmentCreateResourceBuilder.newAssessmentCreateResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentFundingDecisionOutcomeBuilder.newAssessmentFundingDecisionOutcome;
import static org.innovateuk.ifs.assessment.builder.AssessmentFundingDecisionOutcomeResourceBuilder.newAssessmentFundingDecisionOutcomeResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentRejectOutcomeBuilder.newAssessmentRejectOutcome;
import static org.innovateuk.ifs.assessment.builder.AssessmentRejectOutcomeResourceBuilder.newAssessmentRejectOutcomeResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentSubmissionsResourceBuilder.newAssessmentSubmissionsResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentTotalScoreResourceBuilder.newAssessmentTotalScoreResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.*;
import static org.innovateuk.ifs.assessment.transactional.AssessmentServiceImpl.SUBMITTED_APPLICATION_STATES;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.error.CommonErrors.forbiddenError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.review.builder.ReviewInviteBuilder.newReviewInvite;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.resource.Role.ASSESSOR;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;
import static org.junit.Assert.*;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class AssessmentServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private AssessmentService assessmentService = new AssessmentServiceImpl();

    @Test
    public void findById() {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment().build();
        AssessmentResource expected = newAssessmentResource().build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(assessmentMapperMock.mapToResource(same(assessment))).thenReturn(expected);

        AssessmentResource found = assessmentService.findById(assessmentId).getSuccess();

        assertSame(expected, found);

        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentMapperMock);
        inOrder.verify(assessmentRepositoryMock).findOne(assessmentId);
        inOrder.verify(assessmentMapperMock).mapToResource(assessment);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void findAssignableById() {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withActivityState(PENDING)
                .build();
        AssessmentResource expected = newAssessmentResource()
                .build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(assessmentMapperMock.mapToResource(same(assessment))).thenReturn(expected);

        AssessmentResource found = assessmentService.findAssignableById(assessmentId)
                .getSuccess();

        assertSame(expected, found);
        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentMapperMock);
        inOrder.verify(assessmentRepositoryMock).findOne(assessmentId);
        inOrder.verify(assessmentMapperMock).mapToResource(assessment);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void findAssignableById_withdrawn() {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withActivityState(WITHDRAWN)
                .build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);

        ServiceResult<AssessmentResource> serviceResult = assessmentService.findAssignableById(assessmentId);

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(forbiddenError(ASSESSMENT_WITHDRAWN, singletonList(assessmentId))));

        verify(assessmentRepositoryMock).findOne(assessmentId);
        verifyZeroInteractions(assessmentMapperMock);
    }

    @Test
    public void findRejectableById() {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withActivityState(PENDING)
                .build();
        AssessmentResource expected = newAssessmentResource()
                .build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(assessmentMapperMock.mapToResource(same(assessment))).thenReturn(expected);

        AssessmentResource found = assessmentService.findRejectableById(assessmentId)
                .getSuccess();

        assertSame(expected, found);
        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentMapperMock);
        inOrder.verify(assessmentRepositoryMock).findOne(assessmentId);
        inOrder.verify(assessmentMapperMock).mapToResource(assessment);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void findRejectableById_withdrawn() {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withActivityState(WITHDRAWN)
                .build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);

        ServiceResult<AssessmentResource> serviceResult = assessmentService.findRejectableById(assessmentId);

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(forbiddenError(ASSESSMENT_WITHDRAWN, singletonList(assessmentId))));

        verify(assessmentRepositoryMock).findOne(assessmentId);
        verifyZeroInteractions(assessmentMapperMock);
    }

    @Test
    public void findByUserAndCompetition() {
        Long userId = 2L;
        Long competitionId = 1L;

        List<Assessment> assessments = newAssessment().build(2);
        List<AssessmentResource> expected = newAssessmentResource().build(2);

        when(assessmentRepositoryMock.findByParticipantUserIdAndTargetCompetitionIdOrderByActivityStateAscIdAsc(userId, competitionId)).thenReturn(assessments);
        when(assessmentMapperMock.mapToResource(same(assessments.get(0)))).thenReturn(expected.get(0));
        when(assessmentMapperMock.mapToResource(same(assessments.get(1)))).thenReturn(expected.get(1));

        List<AssessmentResource> found = assessmentService.findByUserAndCompetition(userId, competitionId).getSuccess();

        assertEquals(expected, found);
        verify(assessmentRepositoryMock, only()).findByParticipantUserIdAndTargetCompetitionIdOrderByActivityStateAscIdAsc(userId, competitionId);
    }

    @Test
    public void getTotalScore() {
        Long assessmentId = 1L;

        AssessmentTotalScoreResource expected = newAssessmentTotalScoreResource()
                .withTotalScoreGiven(55)
                .withTotalScorePossible(100)
                .build();

        when(assessmentRepositoryMock.getTotalScore(assessmentId)).thenReturn(new AssessmentTotalScoreResource(55, 100));

        assertEquals(expected, assessmentService.getTotalScore(assessmentId).getSuccess());

        verify(assessmentRepositoryMock, only()).getTotalScore(assessmentId);
    }

    @Test
    public void recommend() {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withActivityState(OPEN)
                .build();

        AssessmentFundingDecisionOutcome assessmentFundingDecisionOutcome = newAssessmentFundingDecisionOutcome().build();
        AssessmentFundingDecisionOutcomeResource assessmentFundingDecisionOutcomeResource = newAssessmentFundingDecisionOutcomeResource().build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(assessmentFundingDecisionOutcomeMapperMock.mapToDomain(assessmentFundingDecisionOutcomeResource)).thenReturn(assessmentFundingDecisionOutcome);
        when(assessmentWorkflowHandlerMock.fundingDecision(assessment, assessmentFundingDecisionOutcome)).thenReturn(true);

        ServiceResult<Void> result = assessmentService.recommend(assessmentId, assessmentFundingDecisionOutcomeResource);
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentFundingDecisionOutcomeMapperMock, assessmentWorkflowHandlerMock);
        inOrder.verify(assessmentRepositoryMock).findOne(assessmentId);
        inOrder.verify(assessmentFundingDecisionOutcomeMapperMock).mapToDomain(assessmentFundingDecisionOutcomeResource);
        inOrder.verify(assessmentWorkflowHandlerMock).fundingDecision(assessment, assessmentFundingDecisionOutcome);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void recommend_eventNotAccepted() {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withActivityState(OPEN)
                .build();

        AssessmentFundingDecisionOutcome assessmentFundingDecisionOutcome = newAssessmentFundingDecisionOutcome().build();
        AssessmentFundingDecisionOutcomeResource assessmentFundingDecisionOutcomeResource = newAssessmentFundingDecisionOutcomeResource().build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(assessmentFundingDecisionOutcomeMapperMock.mapToDomain(assessmentFundingDecisionOutcomeResource)).thenReturn(assessmentFundingDecisionOutcome);
        when(assessmentWorkflowHandlerMock.fundingDecision(assessment, assessmentFundingDecisionOutcome)).thenReturn(false);

        ServiceResult<Void> result = assessmentService.recommend(assessmentId, assessmentFundingDecisionOutcomeResource);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(ASSESSMENT_RECOMMENDATION_FAILED));

        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentFundingDecisionOutcomeMapperMock, assessmentWorkflowHandlerMock);
        inOrder.verify(assessmentRepositoryMock).findOne(assessmentId);
        inOrder.verify(assessmentFundingDecisionOutcomeMapperMock).mapToDomain(assessmentFundingDecisionOutcomeResource);
        inOrder.verify(assessmentWorkflowHandlerMock).fundingDecision(assessment, assessmentFundingDecisionOutcome);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getApplicationFeedback() {
        long applicationId = 1L;

        List<Assessment> expectedAssessments = newAssessment()
                .withFundingDecision(
                        newAssessmentFundingDecisionOutcome().withFeedback("Feedback 1").build(),
                        newAssessmentFundingDecisionOutcome().withFeedback("Feedback 2").build(),
                        newAssessmentFundingDecisionOutcome().withFeedback("Feedback 3").build()
                )
                .build(3);

        ApplicationAssessmentFeedbackResource expectedFeedbackResource = newApplicationAssessmentFeedbackResource()
                .withFeedback(asList("Feedback 1", "Feedback 2", "Feedback 3"))
                .build();

        when(assessmentRepositoryMock.findByTargetId(applicationId)).thenReturn(expectedAssessments);

        ServiceResult<ApplicationAssessmentFeedbackResource> result = assessmentService.getApplicationFeedback(applicationId);

        verify(assessmentRepositoryMock).findByTargetId(applicationId);

        assertTrue(result.isSuccess());
        assertEquals(expectedFeedbackResource, result.getSuccess());
    }

    @Test
    public void rejectInvitation() {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withActivityState(OPEN)
                .build();

        AssessmentRejectOutcome assessmentRejectOutcome = newAssessmentRejectOutcome().build();
        AssessmentRejectOutcomeResource assessmentRejectOutcomeResource = newAssessmentRejectOutcomeResource().build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(assessmentRejectOutcomeMapperMock.mapToDomain(assessmentRejectOutcomeResource)).thenReturn(assessmentRejectOutcome);
        when(assessmentWorkflowHandlerMock.rejectInvitation(assessment, assessmentRejectOutcome)).thenReturn(true);

        ServiceResult<Void> result = assessmentService.rejectInvitation(assessmentId, assessmentRejectOutcomeResource);
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentRejectOutcomeMapperMock, assessmentWorkflowHandlerMock);
        inOrder.verify(assessmentRepositoryMock).findOne(assessmentId);
        inOrder.verify(assessmentRejectOutcomeMapperMock).mapToDomain(assessmentRejectOutcomeResource);
        inOrder.verify(assessmentWorkflowHandlerMock).rejectInvitation(assessment, assessmentRejectOutcome);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvitation_eventNotAccepted() {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withActivityState(OPEN)
                .build();

        AssessmentRejectOutcome assessmentRejectOutcome = newAssessmentRejectOutcome().build();
        AssessmentRejectOutcomeResource assessmentRejectOutcomeResource = newAssessmentRejectOutcomeResource().build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(assessmentRejectOutcomeMapperMock.mapToDomain(assessmentRejectOutcomeResource)).thenReturn(assessmentRejectOutcome);
        when(assessmentWorkflowHandlerMock.rejectInvitation(assessment, assessmentRejectOutcome)).thenReturn(false);

        ServiceResult<Void> result = assessmentService.rejectInvitation(assessmentId, assessmentRejectOutcomeResource);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(ASSESSMENT_REJECTION_FAILED));

        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentRejectOutcomeMapperMock, assessmentWorkflowHandlerMock);
        inOrder.verify(assessmentRepositoryMock).findOne(assessmentId);
        inOrder.verify(assessmentRejectOutcomeMapperMock).mapToDomain(assessmentRejectOutcomeResource);
        inOrder.verify(assessmentWorkflowHandlerMock).rejectInvitation(assessment, assessmentRejectOutcome);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void withdrawAssessment() {
        Assessment assessment = newAssessment()
                .withActivityState(OPEN)
                .build();

        when(assessmentRepositoryMock.findOne(assessment.getId())).thenReturn(assessment);
        when(assessmentWorkflowHandlerMock.withdraw(assessment)).thenReturn(true);

        ServiceResult<Void> result = assessmentService.withdrawAssessment(assessment.getId());
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentWorkflowHandlerMock);
        inOrder.verify(assessmentRepositoryMock).findOne(assessment.getId());
        inOrder.verify(assessmentWorkflowHandlerMock).withdraw(assessment);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void withdrawAssessment_eventNotAccepted() {
        Assessment assessment = newAssessment()
                .withActivityState(OPEN)
                .build();

        when(assessmentRepositoryMock.findOne(assessment.getId())).thenReturn(assessment);
        when(assessmentWorkflowHandlerMock.withdraw(assessment)).thenReturn(false);

        ServiceResult<Void> result = assessmentService.withdrawAssessment(assessment.getId());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(ASSESSMENT_WITHDRAW_FAILED));

        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentWorkflowHandlerMock);
        inOrder.verify(assessmentRepositoryMock).findOne(assessment.getId());
        inOrder.verify(assessmentWorkflowHandlerMock).withdraw(assessment);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptInvitation() {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withActivityState(OPEN)
                .build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(assessmentWorkflowHandlerMock.acceptInvitation(assessment)).thenReturn(true);

        ServiceResult<Void> result = assessmentService.acceptInvitation(assessmentId);
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentWorkflowHandlerMock);
        inOrder.verify(assessmentRepositoryMock).findOne(assessmentId);
        inOrder.verify(assessmentWorkflowHandlerMock).acceptInvitation(assessment);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptInvitation_eventNotAccepted() {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withActivityState(PENDING)
                .build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(assessmentWorkflowHandlerMock.acceptInvitation(assessment)).thenReturn(false);

        ServiceResult<Void> result = assessmentService.acceptInvitation(assessmentId);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(ASSESSMENT_ACCEPT_FAILED));

        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentWorkflowHandlerMock);
        inOrder.verify(assessmentRepositoryMock, calls(1)).findOne(assessmentId);
        inOrder.verify(assessmentWorkflowHandlerMock, calls(1)).acceptInvitation(assessment);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitAssessments() {
        AssessmentSubmissionsResource assessmentSubmissions = newAssessmentSubmissionsResource()
                .withAssessmentIds(asList(1L, 2L))
                .build();
        List<Assessment> assessments = newAssessment()
                .withId(1L, 2L)
                .withActivityState(READY_TO_SUBMIT)
                .build(2);

        assertEquals(2, assessmentSubmissions.getAssessmentIds().size());

        when(assessmentRepositoryMock.findAll(assessmentSubmissions.getAssessmentIds())).thenReturn(assessments);

        when(assessmentWorkflowHandlerMock.submit(assessments.get(0))).thenAnswer(invocation -> {
            assessments.get(0).setActivityState(SUBMITTED);
            return Boolean.TRUE;
        });
        when(assessmentWorkflowHandlerMock.submit(assessments.get(1))).thenAnswer(invocation -> {
            assessments.get(1).setActivityState(SUBMITTED);
            return Boolean.TRUE;
        });

        ServiceResult<Void> result = assessmentService.submitAssessments(assessmentSubmissions);
        assertTrue(result.isSuccess());

        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentWorkflowHandlerMock);
        inOrder.verify(assessmentRepositoryMock, calls(1)).findAll(assessmentSubmissions.getAssessmentIds());
        inOrder.verify(assessmentWorkflowHandlerMock, calls(1)).submit(assessments.get(0));
        inOrder.verify(assessmentWorkflowHandlerMock, calls(1)).submit(assessments.get(1));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitAssessments_eventNotAccepted() {
        AssessmentSubmissionsResource assessmentSubmissions = newAssessmentSubmissionsResource()
                .withAssessmentIds(singletonList(1L))
                .build();

        Application application = new Application();
        application.setName("Test Application");

        Assessment assessment = newAssessment()
                .withId(1L)
                .withActivityState(PENDING)
                .with((resource) -> resource.setTarget(application))
                .build();

        assertEquals(1, assessmentSubmissions.getAssessmentIds().size());

        when(assessmentRepositoryMock.findAll(assessmentSubmissions.getAssessmentIds())).thenReturn(singletonList(assessment));
        when(assessmentWorkflowHandlerMock.submit(assessment)).thenReturn(false);

        ServiceResult<Void> result = assessmentService.submitAssessments(assessmentSubmissions);
        assertTrue(result.isFailure());
        assertEquals(result.getErrors().get(0), new Error(ASSESSMENT_SUBMIT_FAILED, 1L, "Test Application"));

        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentWorkflowHandlerMock);
        inOrder.verify(assessmentRepositoryMock, calls(1)).findAll(assessmentSubmissions.getAssessmentIds());
        inOrder.verify(assessmentWorkflowHandlerMock, calls(1)).submit(assessment);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitAssessments_notFound() {
        AssessmentSubmissionsResource assessmentSubmissions = newAssessmentSubmissionsResource()
                .withAssessmentIds(asList(1L, 2L))
                .build();

        when(assessmentRepositoryMock.findAll(assessmentSubmissions.getAssessmentIds())).thenReturn(emptyList());

        ServiceResult<Void> result = assessmentService.submitAssessments(assessmentSubmissions);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(Assessment.class, 1L), notFoundError(Assessment.class, 2L)));

        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentWorkflowHandlerMock);
        inOrder.verify(assessmentRepositoryMock, calls(1)).findAll(assessmentSubmissions.getAssessmentIds());
        inOrder.verify(assessmentWorkflowHandlerMock, never()).submit(any());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitAssessments_notFoundAndEventNotAccepted() {
        AssessmentSubmissionsResource assessmentSubmissions = newAssessmentSubmissionsResource()
                .withAssessmentIds(asList(1L, 2L))
                .build();

        Application application = newApplication()
                .withName("Test Application")
                .build();

        Assessment assessment = newAssessment()
                .withId(1L)
                .withActivityState(PENDING)
                .with((resource) -> resource.setTarget(application))
                .build();

        assertEquals(2, assessmentSubmissions.getAssessmentIds().size());

        when(assessmentRepositoryMock.findAll(assessmentSubmissions.getAssessmentIds())).thenReturn(singletonList(assessment));
        when(assessmentWorkflowHandlerMock.submit(assessment)).thenReturn(false);

        ServiceResult<Void> result = assessmentService.submitAssessments(assessmentSubmissions);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(new Error(ASSESSMENT_SUBMIT_FAILED, 1L, "Test Application"), notFoundError(Assessment.class, 2L)));

        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentWorkflowHandlerMock);
        inOrder.verify(assessmentRepositoryMock, calls(1)).findAll(assessmentSubmissions.getAssessmentIds());
        inOrder.verify(assessmentWorkflowHandlerMock, calls(1)).submit(assessment);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void createAssessment() {
        State expectedBackingState = CREATED.getBackingState();

        Long assessorId = 1L;
        Long applicationId = 2L;

        User user = newUser().withId(assessorId).build();
        Application application = newApplication().withId(applicationId).build();
        ActivityState activityState = new ActivityState(APPLICATION_ASSESSMENT, expectedBackingState);

        ProcessRole expectedProcessRole = newProcessRole()
                .with(id(null))
                .withApplication(application)
                .withUser(user)
                .withRole(ASSESSOR)
                .build();
        ProcessRole savedProcessRole = newProcessRole()
                .withId(10L)
                .withApplication(application)
                .withUser(user)
                .withRole(ASSESSOR)
                .build();

        Assessment expectedAssessment = newAssessment()
                .with(id(null))
                .withApplication(application)
                .withActivityState(CREATED)
                .withParticipant(savedProcessRole)
                .build();
        Assessment savedAssessment = newAssessment()
                .withId(5L)
                .withApplication(application)
                .withActivityState(CREATED)
                .withParticipant(savedProcessRole)
                .build();

        AssessmentResource expectedAssessmentResource = newAssessmentResource().build();

        when(userRepositoryMock.findByIdAndRoles(assessorId, ASSESSOR)).thenReturn(Optional.of(user));
        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
        when(assessmentRepositoryMock.findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(assessorId, applicationId)).thenReturn(Optional.empty());
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, expectedBackingState)).thenReturn(activityState);
        when(processRoleRepositoryMock.findOneByUserIdAndRoleInAndApplicationId(assessorId, singletonList(ASSESSOR), applicationId)).thenReturn(null);
        when(processRoleRepositoryMock.save(expectedProcessRole)).thenReturn(savedProcessRole);
        when(assessmentRepositoryMock.save(expectedAssessment)).thenReturn(savedAssessment);
        when(assessmentMapperMock.mapToResource(savedAssessment)).thenReturn(expectedAssessmentResource);

        AssessmentCreateResource assessmentCreateResource = newAssessmentCreateResource()
                .withApplicationId(applicationId)
                .withAssessorId(assessorId)
                .build();

        ServiceResult<AssessmentResource> serviceResult = assessmentService.createAssessment(assessmentCreateResource);

        InOrder inOrder = inOrder(
                userRepositoryMock, applicationRepositoryMock, activityStateRepositoryMock,
                processRoleRepositoryMock, assessmentRepositoryMock, assessmentMapperMock
        );

        inOrder.verify(userRepositoryMock).findByIdAndRoles(assessorId, ASSESSOR);
        inOrder.verify(applicationRepositoryMock).findOne(applicationId);
        inOrder.verify(assessmentRepositoryMock).findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(assessorId, applicationId);
        inOrder.verify(activityStateRepositoryMock).findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, expectedBackingState);
        inOrder.verify(processRoleRepositoryMock).findOneByUserIdAndRoleInAndApplicationId(assessorId, singletonList(ASSESSOR), applicationId);
        inOrder.verify(processRoleRepositoryMock).save(expectedProcessRole);
        inOrder.verify(assessmentRepositoryMock).save(expectedAssessment);
        inOrder.verify(assessmentMapperMock).mapToResource(savedAssessment);
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isSuccess());
        assertEquals(expectedAssessmentResource, serviceResult.getSuccess());
    }

    @Test
    public void createAssessment_existingProcessRole() {
        State expectedBackingState = CREATED.getBackingState();

        Long assessorId = 1L;
        Long applicationId = 2L;

        User user = newUser().withId(assessorId).build();
        Application application = newApplication().withId(applicationId).build();
        ActivityState activityState = new ActivityState(APPLICATION_ASSESSMENT, expectedBackingState);

        ProcessRole expectedProcessRole = newProcessRole()
                .withId(10L)
                .withApplication(application)
                .withUser(user)
                .withRole(ASSESSOR)
                .build();

        Assessment expectedAssessment = newAssessment()
                .with(id(null))
                .withApplication(application)
                .withActivityState(CREATED)
                .withParticipant(expectedProcessRole)
                .build();
        Assessment savedAssessment = newAssessment()
                .withId(5L)
                .withApplication(application)
                .withActivityState(CREATED)
                .withParticipant(expectedProcessRole)
                .build();

        AssessmentResource expectedAssessmentResource = newAssessmentResource().build();

        when(userRepositoryMock.findByIdAndRoles(assessorId, ASSESSOR)).thenReturn(Optional.of(user));
        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
        when(assessmentRepositoryMock.findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(assessorId, applicationId)).thenReturn(Optional.empty());
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, expectedBackingState)).thenReturn(activityState);
        when(processRoleRepositoryMock.findOneByUserIdAndRoleInAndApplicationId(assessorId, singletonList(ASSESSOR), applicationId)).thenReturn(expectedProcessRole);
        when(assessmentRepositoryMock.save(expectedAssessment)).thenReturn(savedAssessment);
        when(assessmentMapperMock.mapToResource(savedAssessment)).thenReturn(expectedAssessmentResource);

        AssessmentCreateResource assessmentCreateResource = newAssessmentCreateResource()
                .withApplicationId(applicationId)
                .withAssessorId(assessorId)
                .build();

        ServiceResult<AssessmentResource> serviceResult = assessmentService.createAssessment(assessmentCreateResource);

        InOrder inOrder = inOrder(
                userRepositoryMock, applicationRepositoryMock, activityStateRepositoryMock,
                processRoleRepositoryMock, assessmentRepositoryMock, assessmentMapperMock
        );

        inOrder.verify(userRepositoryMock).findByIdAndRoles(assessorId, ASSESSOR);
        inOrder.verify(applicationRepositoryMock).findOne(applicationId);
        inOrder.verify(assessmentRepositoryMock).findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(assessorId, applicationId);
        inOrder.verify(activityStateRepositoryMock).findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, expectedBackingState);
        inOrder.verify(processRoleRepositoryMock).findOneByUserIdAndRoleInAndApplicationId(assessorId, singletonList(ASSESSOR), applicationId);
        inOrder.verify(assessmentRepositoryMock).save(expectedAssessment);
        inOrder.verify(assessmentMapperMock).mapToResource(savedAssessment);
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isSuccess());
        assertEquals(expectedAssessmentResource, serviceResult.getSuccess());
    }


    @Test
    public void createAssessment_existingWithdrawnAssessment() {
        State expectedBackingState = WITHDRAWN.getBackingState();

        Long assessorId = 1L;
        Long applicationId = 2L;

        User user = newUser().withId(assessorId).build();
        Application application = newApplication().withId(applicationId).build();

        ActivityState activityState = new ActivityState(APPLICATION_ASSESSMENT, expectedBackingState);

        Assessment existingAssessment = newAssessment()
                .withActivityState(WITHDRAWN)
                .build();

        ProcessRole expectedProcessRole = newProcessRole()
                .with(id(null))
                .withApplication(application)
                .withUser(user)
                .withRole(ASSESSOR)
                .build();
        ProcessRole savedProcessRole = newProcessRole()
                .withId(10L)
                .withApplication(application)
                .withUser(user)
                .withRole(ASSESSOR)
                .build();

        Assessment expectedAssessment = newAssessment()
                .with(id(null))
                .withApplication(application)
                .withActivityState(WITHDRAWN)
                .withParticipant(savedProcessRole)
                .build();
        Assessment savedAssessment = newAssessment()
                .withId(5L)
                .withApplication(application)
                .withActivityState(WITHDRAWN)
                .withParticipant(savedProcessRole)
                .build();

        AssessmentResource expectedAssessmentResource = newAssessmentResource().build();

        when(userRepositoryMock.findByIdAndRoles(assessorId, ASSESSOR)).thenReturn(Optional.of(user));
        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
        when(assessmentRepositoryMock.findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(assessorId, applicationId)).thenReturn(Optional.of(existingAssessment));
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
                userRepositoryMock, applicationRepositoryMock, activityStateRepositoryMock,
                processRoleRepositoryMock, assessmentRepositoryMock, assessmentMapperMock
        );

        inOrder.verify(userRepositoryMock).findByIdAndRoles(assessorId, ASSESSOR);
        inOrder.verify(applicationRepositoryMock).findOne(applicationId);
        inOrder.verify(activityStateRepositoryMock).findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, CREATED.getBackingState());
        inOrder.verify(processRoleRepositoryMock).save(expectedProcessRole);
        inOrder.verify(assessmentRepositoryMock).save(expectedAssessment);
        inOrder.verify(assessmentMapperMock).mapToResource(savedAssessment);
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isSuccess());
        assertEquals(expectedAssessmentResource, serviceResult.getSuccess());
    }

    @Test
    public void createAssessment_noAssessor() {
        Long assessorId = 100L;
        Long applicationId = 2L;

        when(userRepositoryMock.findByIdAndRoles(assessorId, ASSESSOR)).thenReturn(Optional.empty());

        AssessmentCreateResource assessmentCreateResource = newAssessmentCreateResource()
                .withAssessorId(assessorId)
                .withApplicationId(applicationId)
                .build();

        ServiceResult<AssessmentResource> serviceResult = assessmentService.createAssessment(assessmentCreateResource);

        InOrder inOrder = inOrder(userRepositoryMock, applicationRepositoryMock, activityStateRepositoryMock);
        inOrder.verify(userRepositoryMock).findByIdAndRoles(assessorId, ASSESSOR);
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isFailure());
        assertEquals(GENERAL_NOT_FOUND.getErrorKey(), serviceResult.getErrors().get(0).getErrorKey());
    }

    @Test
    public void createAssessment_noApplication() {
        Long assessorId = 100L;
        Long applicationId = 2L;

        User user = newUser().withId(assessorId).build();

        when(userRepositoryMock.findByIdAndRoles(assessorId, ASSESSOR)).thenReturn(Optional.of(user));
        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(null);

        AssessmentCreateResource assessmentCreateResource = newAssessmentCreateResource()
                .withAssessorId(assessorId)
                .withApplicationId(applicationId)
                .build();

        ServiceResult<AssessmentResource> serviceResult = assessmentService.createAssessment(assessmentCreateResource);

        InOrder inOrder = inOrder(userRepositoryMock, applicationRepositoryMock, activityStateRepositoryMock);
        inOrder.verify(userRepositoryMock).findByIdAndRoles(assessorId, ASSESSOR);
        inOrder.verify(applicationRepositoryMock).findOne(applicationId);
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isFailure());
        assertEquals(GENERAL_NOT_FOUND.getErrorKey(), serviceResult.getErrors().get(0).getErrorKey());
    }

    @Test
    public void createAssessment_existingAssessment() {
        Long assessorId = 100L;
        Long applicationId = 2L;

        User user = newUser().withId(assessorId).build();
        Application application = newApplication().withId(applicationId).build();

        Assessment existingAssessment = newAssessment()
                .withActivityState(PENDING)
                .build();

        when(userRepositoryMock.findByIdAndRoles(assessorId, ASSESSOR)).thenReturn(Optional.of(user));
        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
        when(assessmentRepositoryMock.findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(user.getId(), application.getId()))
                .thenReturn(Optional.of(existingAssessment));

        AssessmentCreateResource assessmentCreateResource = newAssessmentCreateResource()
                .withAssessorId(assessorId)
                .withApplicationId(applicationId)
                .build();

        ServiceResult<AssessmentResource> serviceResult = assessmentService.createAssessment(assessmentCreateResource);

        InOrder inOrder = inOrder(userRepositoryMock, applicationRepositoryMock, assessmentRepositoryMock, activityStateRepositoryMock);
        inOrder.verify(userRepositoryMock).findByIdAndRoles(assessorId, ASSESSOR);
        inOrder.verify(applicationRepositoryMock).findOne(applicationId);
        inOrder.verify(assessmentRepositoryMock).findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(assessorId, applicationId);
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isFailure());
        assertEquals(1, serviceResult.getErrors().size());
        assertEquals(ASSESSMENT_CREATE_FAILED.getErrorKey(), serviceResult.getErrors().get(0).getErrorKey());
    }

    @Test
    public void getAssessmentPanelKeyStatistics() {
        Long competitionId = 1L;
        List<String> emails = asList("john@email.com", "peter@email.com");
        List<String> names = asList("John Barnes", "Peter Jones");
        Profile profile = newProfile().withId(7L).build();
        User user = newUser().withId(11L).withProfileId(profile.getId()).build();

        ZonedDateTime acceptsDate = ZonedDateTime.of(2016, 12, 20, 12, 0,0,0, ZoneId.systemDefault());
        ZonedDateTime deadlineDate = ZonedDateTime.of(2017, 1, 17, 12, 0,0,0, ZoneId.systemDefault());

        Competition competition = newCompetition()
                .withName("my competition")
                .withAssessorAcceptsDate(acceptsDate)
                .withAssessorDeadlineDate(deadlineDate)
                .build();

        List<ReviewInvite> panelInvites = newReviewInvite()
                .withCompetition(competition)
                .withEmail(emails.get(0), emails.get(1))
                .withHash(Invite.generateInviteHash())
                .withName(names.get(0), names.get(1))
                .withStatus(SENT)
                .withUser(user)
                .build(2);

        List<Long> panelInviteIds = simpleMap(panelInvites, ReviewInvite::getId);

        List<Application> applications = newApplication()
                .withCompetition(competition)
                .withApplicationState(ApplicationState.SUBMITTED)
                .build(2);

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateInAndIdLike(
                competitionId, SUBMITTED_APPLICATION_STATES, "",  null,true)).thenReturn(applications);
        when(reviewInviteRepositoryMock.getByCompetitionId(competitionId)).thenReturn(panelInvites);
        when(reviewParticipantRepositoryMock.countByCompetitionIdAndRoleAndStatusAndInviteIdIn(
                competitionId, CompetitionParticipantRole.PANEL_ASSESSOR, ParticipantStatus.ACCEPTED, panelInviteIds))
                .thenReturn(1);
        when(reviewInviteRepositoryMock.countByCompetitionIdAndStatusIn(competitionId, singleton(InviteStatus.SENT)))
                .thenReturn(1);
        when(reviewParticipantRepositoryMock.countByCompetitionIdAndRoleAndStatusAndInviteIdIn(
                competitionId, CompetitionParticipantRole.PANEL_ASSESSOR, ParticipantStatus.PENDING, panelInviteIds))
                .thenReturn(1);

        ServiceResult<ReviewKeyStatisticsResource> serviceResult = assessmentService.getAssessmentPanelKeyStatistics(competitionId);

        InOrder inOrder = inOrder(applicationRepositoryMock, reviewInviteRepositoryMock, reviewParticipantRepositoryMock);
        inOrder.verify(reviewInviteRepositoryMock).getByCompetitionId(competitionId);
        inOrder.verify(applicationRepositoryMock).findByCompetitionIdAndApplicationProcessActivityStateInAndIdLike(
                competitionId, SUBMITTED_APPLICATION_STATES, "",  null,true);

        inOrder.verify(reviewParticipantRepositoryMock).countByCompetitionIdAndRoleAndStatusAndInviteIdIn(competitionId, CompetitionParticipantRole.PANEL_ASSESSOR, ParticipantStatus.ACCEPTED, panelInviteIds);
        inOrder.verify(reviewInviteRepositoryMock).countByCompetitionIdAndStatusIn(competitionId, singleton(InviteStatus.SENT));
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isSuccess());

        ReviewKeyStatisticsResource result = serviceResult.getSuccess();
        assertEquals(2, result.getApplicationsInPanel());
        assertEquals(1, result.getAssessorsAccepted());
        assertEquals(1, result.getAssessorsPending());
    }

    @Test
    public void getAssessmentPanelInviteStatistics() {
        Long competitionId = 1L;
        List<String> emails = asList("john@email.com", "peter@email.com");
        List<String> names = asList("John Barnes", "Peter Jones");
        Profile profile = newProfile().withId(7L).build();
        User user = newUser().withId(11L).withProfileId(profile.getId()).build();

        ZonedDateTime acceptsDate = ZonedDateTime.of(2016, 12, 20, 12, 0,0,0, ZoneId.systemDefault());
        ZonedDateTime deadlineDate = ZonedDateTime.of(2017, 1, 17, 12, 0,0,0, ZoneId.systemDefault());

        Competition competition = newCompetition()
                .withName("my competition")
                .withAssessorAcceptsDate(acceptsDate)
                .withAssessorDeadlineDate(deadlineDate)
                .build();

        List<ReviewInvite> panelInvites = newReviewInvite()
                .withCompetition(competition)
                .withEmail(emails.get(0), emails.get(1))
                .withHash(Invite.generateInviteHash())
                .withName(names.get(0), names.get(1))
                .withStatus(SENT)
                .withUser(user)
                .build(2);

        List<Long> panelInviteIds = simpleMap(panelInvites, ReviewInvite::getId);

        when(reviewInviteRepositoryMock.countByCompetitionIdAndStatusIn(competitionId, EnumSet.of(OPENED, SENT))).thenReturn(2);
        when(reviewInviteRepositoryMock.getByCompetitionId(competitionId)).thenReturn(panelInvites);

        when(reviewParticipantRepositoryMock.countByCompetitionIdAndRoleAndStatusAndInviteIdIn(
                competitionId, CompetitionParticipantRole.PANEL_ASSESSOR, ParticipantStatus.ACCEPTED, panelInviteIds))
                .thenReturn(1);
        when(reviewParticipantRepositoryMock.countByCompetitionIdAndRoleAndStatusAndInviteIdIn(
                competitionId, CompetitionParticipantRole.PANEL_ASSESSOR, ParticipantStatus.REJECTED, panelInviteIds))
                .thenReturn(1);

        ServiceResult<ReviewInviteStatisticsResource> serviceResult = assessmentService.getAssessmentPanelInviteStatistics(competitionId);

        InOrder inOrder = inOrder(reviewInviteRepositoryMock, reviewParticipantRepositoryMock);
        inOrder.verify(reviewInviteRepositoryMock).getByCompetitionId(competitionId);
        inOrder.verify(reviewParticipantRepositoryMock).countByCompetitionIdAndRoleAndStatusAndInviteIdIn(competitionId, CompetitionParticipantRole.PANEL_ASSESSOR, ParticipantStatus.ACCEPTED, panelInviteIds);
        inOrder.verify(reviewParticipantRepositoryMock).countByCompetitionIdAndRoleAndStatusAndInviteIdIn(competitionId, CompetitionParticipantRole.PANEL_ASSESSOR, ParticipantStatus.REJECTED, panelInviteIds);
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isSuccess());

        ReviewInviteStatisticsResource result = serviceResult.getSuccess();
        assertEquals(2, result.getInvited());
        assertEquals(1, result.getAccepted());
        assertEquals(1, result.getDeclined());
        assertEquals(0, result.getPending());
    }
}