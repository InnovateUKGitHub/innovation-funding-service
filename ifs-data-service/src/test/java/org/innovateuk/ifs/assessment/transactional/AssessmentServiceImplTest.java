package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessmentFundingDecisionOutcome;
import org.innovateuk.ifs.assessment.domain.AssessmentRejectOutcome;
import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.email.resource.EmailContent;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.time.ZonedDateTime.now;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
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
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.*;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.error.CommonErrors.forbiddenError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.email.builders.EmailContentResourceBuilder.newEmailContentResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.resource.UserRoleType.ASSESSOR;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.innovateuk.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;
import static org.junit.Assert.*;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class AssessmentServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private AssessmentService assessmentService = new AssessmentServiceImpl();

    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(assessmentService, "webBaseUrl", "https://ifs-local-dev");
    }

    @Test
    public void findById() throws Exception {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment().build();
        AssessmentResource expected = newAssessmentResource().build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(assessmentMapperMock.mapToResource(same(assessment))).thenReturn(expected);

        AssessmentResource found = assessmentService.findById(assessmentId).getSuccessObjectOrThrowException();

        assertSame(expected, found);

        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentMapperMock);
        inOrder.verify(assessmentRepositoryMock).findOne(assessmentId);
        inOrder.verify(assessmentMapperMock).mapToResource(assessment);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void findAssignableById() throws Exception {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, PENDING
                        .getBackingState()))
                .build();
        AssessmentResource expected = newAssessmentResource()
                .build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(assessmentMapperMock.mapToResource(same(assessment))).thenReturn(expected);

        AssessmentResource found = assessmentService.findAssignableById(assessmentId)
                .getSuccessObjectOrThrowException();

        assertSame(expected, found);
        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentMapperMock);
        inOrder.verify(assessmentRepositoryMock).findOne(assessmentId);
        inOrder.verify(assessmentMapperMock).mapToResource(assessment);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void findAssignableById_withdrawn() throws Exception {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, WITHDRAWN
                        .getBackingState()))
                .build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);

        ServiceResult<AssessmentResource> serviceResult = assessmentService.findAssignableById(assessmentId);

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(forbiddenError(ASSESSMENT_WITHDRAWN, singletonList(assessmentId))));

        verify(assessmentRepositoryMock).findOne(assessmentId);
        verifyZeroInteractions(assessmentMapperMock);
    }

    @Test
    public void findRejectableById() throws Exception {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, PENDING
                        .getBackingState()))
                .build();
        AssessmentResource expected = newAssessmentResource()
                .build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);
        when(assessmentMapperMock.mapToResource(same(assessment))).thenReturn(expected);

        AssessmentResource found = assessmentService.findRejectableById(assessmentId)
                .getSuccessObjectOrThrowException();

        assertSame(expected, found);
        InOrder inOrder = inOrder(assessmentRepositoryMock, assessmentMapperMock);
        inOrder.verify(assessmentRepositoryMock).findOne(assessmentId);
        inOrder.verify(assessmentMapperMock).mapToResource(assessment);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void findRejectableById_withdrawn() throws Exception {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, WITHDRAWN
                        .getBackingState()))
                .build();

        when(assessmentRepositoryMock.findOne(assessmentId)).thenReturn(assessment);

        ServiceResult<AssessmentResource> serviceResult = assessmentService.findRejectableById(assessmentId);

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(forbiddenError(ASSESSMENT_WITHDRAWN, singletonList(assessmentId))));

        verify(assessmentRepositoryMock).findOne(assessmentId);
        verifyZeroInteractions(assessmentMapperMock);
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
    public void recommend_eventNotAccepted() throws Exception {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, OPEN.getBackingState()))
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
    public void getApplicationFeedback() throws Exception {
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
        assertEquals(expectedFeedbackResource, result.getSuccessObject());
    }

    @Test
    public void rejectInvitation() throws Exception {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, OPEN.getBackingState()))
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
    public void rejectInvitation_eventNotAccepted() throws Exception {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, OPEN.getBackingState()))
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
    public void withdrawAssessment() throws Exception {
        Assessment assessment = newAssessment()
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, OPEN.getBackingState()))
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
    public void withdrawAssessment_eventNotAccepted() throws Exception {
        Assessment assessment = newAssessment()
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, OPEN.getBackingState()))
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
    public void notifyAssessorsByCompetition() throws Exception {
        Long competitionId = 1L;

        ActivityState activityState = new ActivityState(APPLICATION_ASSESSMENT, State.CREATED);
        Competition competition = newCompetition()
                .withId(competitionId)
                .withName("Test Competition")
                .withAssessorAcceptsDate(now().minusDays(2))
                .withAssessorDeadlineDate(now().minusDays(1))
                .build();

        List<User> users = newUser()
                .withFirstName("Johnny", "Mary")
                .withLastName("Doe", "Poppins")
                .build(2);
        List<Assessment> assessments = newAssessment()
                .withId(2L, 3L)
                .withActivityState(activityState)
                .withApplication(
                        newApplication().withCompetition(competition).build(),
                        newApplication().withCompetition(competition).build()
                )
                .withParticipant(
                        newProcessRole().withUser(users.get(0)).build(),
                        newProcessRole().withUser(users.get(1)).build()
                )
                .build(2);

        List<EmailContent> emailContents = newEmailContentResource()
                .build(2);

        List<NotificationTarget> recipients = asList(
                new UserNotificationTarget(users.get(0)),
                new UserNotificationTarget(users.get(1))
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");

        Notification expectedNotification1 = new Notification(
                systemNotificationSourceMock,
                singletonList(recipients.get(0)),
                AssessmentServiceImpl.Notifications.ASSESSOR_HAS_ASSESSMENTS,
                asMap(
                        "name", users.get(0).getName(),
                        "competitionName", competition.getName(),
                        "acceptsDeadline", competition.getAssessorAcceptsDate().format(formatter),
                        "assessmentDeadline", competition.getAssessorDeadlineDate().format(formatter),
                        "competitionUrl", format("%s/assessor/dashboard/competition/%s", "https://ifs-local-dev/assessment", competition.getId()))
        );

        Notification expectedNotification2 = new Notification(
                systemNotificationSourceMock,
                singletonList(recipients.get(1)),
                AssessmentServiceImpl.Notifications.ASSESSOR_HAS_ASSESSMENTS,
                asMap(
                        "name", users.get(1).getName(),
                        "competitionName", competition.getName(),
                        "acceptsDeadline", competition.getAssessorAcceptsDate().format(formatter),
                        "assessmentDeadline", competition.getAssessorDeadlineDate().format(formatter),
                        "competitionUrl", format("%s/assessor/dashboard/competition/%s", "https://ifs-local-dev/assessment", competition.getId()))
        );

        when(competitionRepositoryMock.findOne(competitionId)).thenReturn(competition);
        when(assessmentRepositoryMock.findByActivityStateStateAndTargetCompetitionId(State.CREATED, competitionId)).thenReturn(assessments);
        when(assessmentWorkflowHandlerMock.notify(same(assessments.get(0)))).thenReturn(true);
        when(assessmentWorkflowHandlerMock.notify(same(assessments.get(1)))).thenReturn(true);

        when(notificationSender.renderTemplates(expectedNotification1))
                .thenReturn(serviceSuccess(asMap(recipients.get(0), emailContents.get(0))));
        when(notificationSender.renderTemplates(expectedNotification2))
                .thenReturn(serviceSuccess(asMap(recipients.get(1), emailContents.get(1))));
        when(notificationSender.sendEmailWithContent(expectedNotification1, recipients.get(0), emailContents.get(0)))
                .thenReturn(serviceSuccess(emptyList()));
        when(notificationSender.sendEmailWithContent(expectedNotification2, recipients.get(1), emailContents.get(1)))
                .thenReturn(serviceSuccess(emptyList()));

        ServiceResult<Void> serviceResult = assessmentService.notifyAssessorsByCompetition(competitionId);

        InOrder inOrder = inOrder(assessmentRepositoryMock, competitionRepositoryMock, assessmentWorkflowHandlerMock, notificationSender);
        inOrder.verify(competitionRepositoryMock).findOne(competitionId);
        inOrder.verify(assessmentRepositoryMock).findByActivityStateStateAndTargetCompetitionId(State.CREATED, competitionId);
        inOrder.verify(assessmentWorkflowHandlerMock).notify(same(assessments.get(0)));
        inOrder.verify(assessmentWorkflowHandlerMock).notify(same(assessments.get(1)));
        inOrder.verify(notificationSender).renderTemplates(expectedNotification1);
        inOrder.verify(notificationSender).sendEmailWithContent(expectedNotification1, recipients.get(0), emailContents.get(0));
        inOrder.verify(notificationSender).renderTemplates(expectedNotification2);
        inOrder.verify(notificationSender).sendEmailWithContent(expectedNotification2, recipients.get(1), emailContents.get(1));
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isSuccess());
        assertTrue(serviceResult.getErrors().isEmpty());
    }

    @Test
    public void notifyAssessorsByCompetition_oneEmailPerUser() throws Exception {
        Long competitionId = 1L;

        ActivityState activityState = new ActivityState(APPLICATION_ASSESSMENT, State.CREATED);
        Competition competition = newCompetition()
                .withId(competitionId)
                .withName("Test Competition")
                .withAssessorAcceptsDate(now().minusDays(2))
                .withAssessorDeadlineDate(now().minusDays(1))
                .build();
        User user = newUser().build();

        List<Assessment> assessments = newAssessment()
                .withId(2L, 3L)
                .withActivityState(activityState)
                .withApplication(
                        newApplication().withCompetition(competition).build(),
                        newApplication().withCompetition(competition).build()
                )
                .withParticipant(
                        newProcessRole().withUser(user).build(),
                        newProcessRole().withUser(user).build()
                )
                .build(2);

        EmailContent emailContent = newEmailContentResource().build();
        NotificationTarget recipient = new UserNotificationTarget(user);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");

        Notification expectedNotification = new Notification(
                systemNotificationSourceMock,
                singletonList(recipient),
                AssessmentServiceImpl.Notifications.ASSESSOR_HAS_ASSESSMENTS,
                asMap(
                        "name", user.getName(),
                        "competitionName", competition.getName(),
                        "acceptsDeadline", competition.getAssessorAcceptsDate().format(formatter),
                        "assessmentDeadline", competition.getAssessorDeadlineDate().format(formatter),
                        "competitionUrl", format("%s/assessor/dashboard/competition/%s", "https://ifs-local-dev/assessment", competition.getId()))
        );

        when(competitionRepositoryMock.findOne(competitionId)).thenReturn(competition);
        when(assessmentRepositoryMock.findByActivityStateStateAndTargetCompetitionId(State.CREATED, competitionId)).thenReturn(assessments);
        when(assessmentWorkflowHandlerMock.notify(same(assessments.get(0)))).thenReturn(true);
        when(assessmentWorkflowHandlerMock.notify(same(assessments.get(1)))).thenReturn(true);

        when(notificationSender.renderTemplates(expectedNotification))
                .thenReturn(serviceSuccess(asMap(recipient, emailContent)));
        when(notificationSender.sendEmailWithContent(expectedNotification, recipient, emailContent))
                .thenReturn(serviceSuccess(emptyList()));

        ServiceResult<Void> serviceResult = assessmentService.notifyAssessorsByCompetition(competitionId);

        InOrder inOrder = inOrder(assessmentRepositoryMock, competitionRepositoryMock, assessmentWorkflowHandlerMock, notificationSender);
        inOrder.verify(competitionRepositoryMock).findOne(competitionId);
        inOrder.verify(assessmentRepositoryMock).findByActivityStateStateAndTargetCompetitionId(State.CREATED, competitionId);
        inOrder.verify(assessmentWorkflowHandlerMock).notify(same(assessments.get(0)));
        inOrder.verify(assessmentWorkflowHandlerMock).notify(same(assessments.get(1)));
        inOrder.verify(notificationSender).renderTemplates(expectedNotification);
        inOrder.verify(notificationSender).sendEmailWithContent(expectedNotification, recipient, emailContent);
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isSuccess());
        assertTrue(serviceResult.getErrors().isEmpty());

    }

    @Test
    public void notifyAssessorsByCompetition_competitionNotFound() throws Exception {
        long competitionId = 1L;

        when(competitionRepositoryMock.findOne(competitionId)).thenReturn(null);

        ServiceResult<Void> serviceResult = assessmentService.notifyAssessorsByCompetition(competitionId);

        verify(competitionRepositoryMock).findOne(competitionId);
        verifyNoMoreInteractions(assessmentRepositoryMock, competitionRepositoryMock, assessmentWorkflowHandlerMock, notificationSender);

        assertTrue(serviceResult.isFailure());
        assertEquals(1, serviceResult.getErrors().size());
        assertEquals(GENERAL_NOT_FOUND.getErrorKey(), serviceResult.getErrors().get(0).getErrorKey());
    }

    @Test
    public void notifyAssessorsByCompetition_oneTransitionFails() throws Exception {
        Long competitionId = 1L;

        ActivityState activityState = new ActivityState(APPLICATION_ASSESSMENT, State.CREATED);

        Competition competition = newCompetition()
                .withId(competitionId)
                .build();
        List<Assessment> assessments = newAssessment()
                .withActivityState(activityState)
                .withId(2L, 3L)
                .build(2);

        when(assessmentRepositoryMock.findByActivityStateStateAndTargetCompetitionId(State.CREATED, competitionId))
                .thenReturn(assessments);
        when(competitionRepositoryMock.findOne(competitionId)).thenReturn(competition);
        when(assessmentWorkflowHandlerMock.notify(same(assessments.get(0)))).thenReturn(true);
        when(assessmentWorkflowHandlerMock.notify(same(assessments.get(1)))).thenReturn(false);

        ServiceResult<Void> serviceResult = assessmentService.notifyAssessorsByCompetition(competitionId);

        InOrder inOrder = inOrder(assessmentRepositoryMock, competitionRepositoryMock, assessmentWorkflowHandlerMock, notificationSender);
        inOrder.verify(competitionRepositoryMock).findOne(competitionId);
        inOrder.verify(assessmentRepositoryMock).findByActivityStateStateAndTargetCompetitionId(State.CREATED, competitionId);
        inOrder.verify(assessmentWorkflowHandlerMock).notify(same(assessments.get(0)));
        inOrder.verify(assessmentWorkflowHandlerMock).notify(same(assessments.get(1)));
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isFailure());
        assertEquals(1, serviceResult.getErrors().size());
        assertEquals(ASSESSMENT_NOTIFY_FAILED.getErrorKey(), serviceResult.getErrors().get(0).getErrorKey());
    }

    @Test
    public void notifyAssessorsByCompetition_allTransitionsFail() throws Exception {
        Long competitionId = 1L;

        ActivityState activityState = new ActivityState(APPLICATION_ASSESSMENT, State.CREATED);

        Competition competition = newCompetition()
                .withId(competitionId)
                .build();
        List<Assessment> assessments = newAssessment()
                .withActivityState(activityState)
                .withId(2L, 3L)
                .build(2);

        when(assessmentRepositoryMock.findByActivityStateStateAndTargetCompetitionId(State.CREATED, competitionId))
                .thenReturn(assessments);
        when(competitionRepositoryMock.findOne(competitionId)).thenReturn(competition);
        when(assessmentWorkflowHandlerMock.notify(same(assessments.get(0)))).thenReturn(false);
        when(assessmentWorkflowHandlerMock.notify(same(assessments.get(1)))).thenReturn(false);

        ServiceResult<Void> serviceResult = assessmentService.notifyAssessorsByCompetition(competitionId);

        InOrder inOrder = inOrder(assessmentRepositoryMock, competitionRepositoryMock, assessmentWorkflowHandlerMock, notificationSender);
        inOrder.verify(competitionRepositoryMock).findOne(competitionId);
        inOrder.verify(assessmentRepositoryMock).findByActivityStateStateAndTargetCompetitionId(State.CREATED, competitionId);
        inOrder.verify(assessmentWorkflowHandlerMock).notify(same(assessments.get(0)));
        inOrder.verify(assessmentWorkflowHandlerMock).notify(same(assessments.get(1)));
        inOrder.verifyNoMoreInteractions();

        assertTrue(serviceResult.isFailure());
        assertEquals(2, serviceResult.getErrors().size());
        assertEquals(ASSESSMENT_NOTIFY_FAILED.getErrorKey(), serviceResult.getErrors().get(0).getErrorKey());
        assertEquals(ASSESSMENT_NOTIFY_FAILED.getErrorKey(), serviceResult.getErrors().get(1).getErrorKey());
    }

    @Test
    public void acceptInvitation() throws Exception {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, PENDING.getBackingState()))
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
    public void acceptInvitation_eventNotAccepted() throws Exception {
        Long assessmentId = 1L;

        Assessment assessment = newAssessment()
                .withId(assessmentId)
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, PENDING.getBackingState()))
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

        when(assessmentWorkflowHandlerMock.submit(assessments.get(0))).thenAnswer(invocation -> {
            assessments.get(0).setActivityState(new ActivityState(APPLICATION_ASSESSMENT, SUBMITTED.getBackingState()));
            return Boolean.TRUE;
        });
        when(assessmentWorkflowHandlerMock.submit(assessments.get(1))).thenAnswer(invocation -> {
            assessments.get(1).setActivityState(new ActivityState(APPLICATION_ASSESSMENT, SUBMITTED.getBackingState()));
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
    public void submitAssessments_notFound() throws Exception {
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
        inOrder.verify(assessmentMapperMock).mapToResource(savedAssessment);
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
        inOrder.verify(assessmentMapperMock).mapToResource(savedAssessment);
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
