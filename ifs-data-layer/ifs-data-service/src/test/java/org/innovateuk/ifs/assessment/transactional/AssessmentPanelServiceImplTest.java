package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.LambdaMatcher;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.assessment.review.domain.AssessmentReview;
import org.innovateuk.ifs.assessment.review.domain.AssessmentReviewRejectOutcome;
import org.innovateuk.ifs.assessment.review.resource.AssessmentReviewRejectOutcomeResource;
import org.innovateuk.ifs.assessment.review.resource.AssessmentReviewResource;
import org.innovateuk.ifs.assessment.review.resource.AssessmentReviewState;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.domain.competition.AssessmentPanelParticipant;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Value;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static junit.framework.TestCase.assertFalse;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.builder.AssessmentReviewRejectOutcomeResourceBuilder.newAssessmentReviewRejectOutcomeResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentReviewResourceBuilder.newAssessmentReviewResource;
import static org.innovateuk.ifs.assessment.review.builder.AssessmentPanelParticipantBuilder.newAssessmentPanelParticipant;
import static org.innovateuk.ifs.assessment.review.builder.AssessmentReviewBuilder.newAssessmentReview;
import static org.innovateuk.ifs.assessment.review.builder.AssessmentReviewRejectOutcomeBuilder.newAssessmentReviewRejectOutcome;
import static org.innovateuk.ifs.assessment.review.resource.AssessmentReviewState.CREATED;
import static org.innovateuk.ifs.assessment.transactional.AssessmentPanelServiceImpl.INVITE_DATE_FORMAT;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.ASSESSMENT_REVIEW_ACCEPT_FAILED;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.ASSESSMENT_REVIEW_REJECT_FAILED;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.MilestoneBuilder.newMilestone;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.workflow.domain.ActivityType.ASSESSMENT_PANEL_APPLICATION_INVITE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class AssessmentPanelServiceImplTest extends BaseServiceUnitTest<AssessmentPanelServiceImpl> {

    private static final long applicationId = 1L;
    private static final long competitionId = 1L;
    private static final long userId = 2L;
    private Application application;
    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    @Override
    protected AssessmentPanelServiceImpl supplyServiceUnderTest() {
        return new AssessmentPanelServiceImpl();
    }

    @Before
    public void setUp() {
        application = newApplication().withId(applicationId).build();
    }

    @Test
    public void assignApplicationsToPanel() throws Exception {
        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);

        ServiceResult<Void> result = service.assignApplicationToPanel(applicationId);
        assertTrue(result.isSuccess());
        assertTrue(application.isInAssessmentReviewPanel());

        verify(applicationRepositoryMock).findOne(applicationId);
        verifyNoMoreInteractions(applicationRepositoryMock);
    }

    @Test
    public void unAssignApplicationsFromPanel() throws Exception {
        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
        when(assessmentReviewRepositoryMock
                .findByTargetIdAndActivityStateStateNot(applicationId, State.WITHDRAWN))
                .thenReturn(emptyList());

        ServiceResult<Void> result = service.unassignApplicationFromPanel(applicationId);
        assertTrue(result.isSuccess());
        assertFalse(application.isInAssessmentReviewPanel());

        verify(applicationRepositoryMock).findOne(applicationId);
        verify(assessmentReviewRepositoryMock).findByTargetIdAndActivityStateStateNot(applicationId, State.WITHDRAWN);
        verifyNoMoreInteractions(applicationRepositoryMock, assessmentReviewRepositoryMock);
    }

    @Test
    public void unAssignApplicationsFromPanel_existingReviews() throws Exception {
        List<AssessmentReview> assessmentReviews = newAssessmentReview().withTarget(application).withState(AssessmentReviewState.WITHDRAWN).build(2);

        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
        when(assessmentReviewRepositoryMock
                .findByTargetIdAndActivityStateStateNot(applicationId, State.WITHDRAWN))
                .thenReturn(assessmentReviews);

        ServiceResult<Void> result = service.unassignApplicationFromPanel(applicationId);
        assertTrue(result.isSuccess());
        assertFalse(application.isInAssessmentReviewPanel());

        assessmentReviews.forEach(a -> assertEquals(State.WITHDRAWN, a.getActivityState().getBackingState()));

        verify(applicationRepositoryMock).findOne(applicationId);
        verifyNoMoreInteractions(applicationRepositoryMock);
    }

    @Test
    public void createAndNotifyReviews() {
        String competitionName = "Competition name";
        ZonedDateTime panelDate = ZonedDateTime.parse("2017-12-18T12:00:00+00:00");
        User assessor = newUser()
                .withEmailAddress("tom@poly.io")
                .withFirstName("Tom")
                .withLastName("Baldwin")
                .build();
        Competition competition = newCompetition()
                .withId(competitionId)
                .withName(competitionName)
                .withMilestones(asList(newMilestone()
                        .withType(MilestoneType.ASSESSMENT_PANEL)
                        .withDate(panelDate)
                        .build())
                )
                .build();

        List<AssessmentPanelParticipant> assessmentPanelParticipants =
                newAssessmentPanelParticipant()
                        .withUser(assessor)
                        .build(1);
        List<Application> applications = newApplication()
                .withCompetition(competition)
                .build(1);
        ActivityState acceptedActivityState = new ActivityState(ASSESSMENT_PANEL_APPLICATION_INVITE, State.ACCEPTED);


        List<ProcessRole> processRoles = newProcessRole()
                .withUser(assessor)
                .build(1);

        AssessmentReview assessmentReview = new AssessmentReview(applications.get(0), processRoles.get(0));
        assessmentReview.setActivityState(acceptedActivityState);

        when(assessmentPanelParticipantRepositoryMock
                .getPanelAssessorsByCompetitionAndStatusContains(competitionId, singletonList(ParticipantStatus.ACCEPTED)))
                .thenReturn(assessmentPanelParticipants);
        when(applicationRepositoryMock
                .findByCompetitionIdAndInAssessmentReviewPanelTrueAndApplicationProcessActivityStateState(competitionId, State.SUBMITTED))
                .thenReturn(applications);

        when(assessmentReviewRepositoryMock.existsByParticipantUserAndTargetAndActivityStateStateNot(assessor, application, State.WITHDRAWN))
                .thenReturn(true);

        when(processRoleRepositoryMock.save(isA(ProcessRole.class))).thenReturn(processRoles.get(0));

        when(activityStateRepositoryMock.findOneByActivityTypeAndState(ASSESSMENT_PANEL_APPLICATION_INVITE, State.CREATED))
                .thenReturn(acceptedActivityState);

        when(assessmentReviewRepositoryMock
                .findByTargetCompetitionIdAndActivityStateState(competitionId, CREATED.getBackingState()))
                .thenReturn(asList(assessmentReview));

        Notification expectedNotification = LambdaMatcher.createLambdaMatcher(n -> {
            Map<String, Object> globalArguments = n.getGlobalArguments();
            assertEquals(assessor.getEmail(), n.getTo().get(0).getEmailAddress());
            assertEquals(globalArguments.get("subject"), "Applications ready for review");
            assertEquals(globalArguments.get("name"), "Tom Baldwin");
            assertEquals(globalArguments.get("competitionName"),  competitionName);
            assertEquals(globalArguments.get("panelDate"), panelDate.format(INVITE_DATE_FORMAT));
            assertEquals(globalArguments.get("ifsUrl"), webBaseUrl);
        });

        when(notificationSenderMock.sendNotification(expectedNotification)).thenReturn(ServiceResult.serviceSuccess(expectedNotification));


        service.createAndNotifyReviews(competitionId).getSuccessObjectOrThrowException();


        InOrder inOrder = inOrder(assessmentPanelParticipantRepositoryMock, applicationRepositoryMock,
                assessmentReviewRepositoryMock, activityStateRepositoryMock,  assessmentReviewRepositoryMock,
                assessmentReviewRepositoryMock, assessmentReviewWorkflowHandlerMock, notificationSenderMock, processRoleRepositoryMock);
        inOrder.verify(assessmentPanelParticipantRepositoryMock)
                .getPanelAssessorsByCompetitionAndStatusContains(competitionId, singletonList(ParticipantStatus.ACCEPTED));
        inOrder.verify(applicationRepositoryMock)
                .findByCompetitionIdAndInAssessmentReviewPanelTrueAndApplicationProcessActivityStateState(competitionId, State.SUBMITTED);
        inOrder.verify(assessmentReviewRepositoryMock)
                .existsByParticipantUserAndTargetAndActivityStateStateNot(assessor, applications.get(0), (State.WITHDRAWN));
        inOrder.verify(processRoleRepositoryMock).save(isA(ProcessRole.class));
        inOrder.verify(activityStateRepositoryMock)
                .findOneByActivityTypeAndState(ASSESSMENT_PANEL_APPLICATION_INVITE, State.CREATED);
        inOrder.verify(assessmentReviewRepositoryMock)
                .save(assessmentReview);
        inOrder.verify(assessmentReviewRepositoryMock)
                .findByTargetCompetitionIdAndActivityStateState(competitionId, CREATED.getBackingState());
        inOrder.verify(assessmentReviewWorkflowHandlerMock)
                .notifyInvitation(assessmentReview);
        inOrder.verify(notificationSenderMock)
                .sendNotification(isA(Notification.class));

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void isPendingReviewNotifications() {
        boolean expectedPendingReviewNotifications = true;

        when(assessmentReviewRepositoryMock.notifiable(competitionId)).thenReturn(expectedPendingReviewNotifications);

        assertEquals(expectedPendingReviewNotifications, service.isPendingReviewNotifications(competitionId).getSuccessObjectOrThrowException());

        verify(assessmentReviewRepositoryMock, only()).notifiable(competitionId);
    }

    @Test
    public void isPendingReviewNotifications_none() {
        boolean expectedPendingReviewNotifications = false;

        when(assessmentReviewRepositoryMock.notifiable(competitionId)).thenReturn(expectedPendingReviewNotifications);

        assertEquals(expectedPendingReviewNotifications, service.isPendingReviewNotifications(competitionId).getSuccessObjectOrThrowException());

        verify(assessmentReviewRepositoryMock, only()).notifiable(competitionId);
    }

    @Test
    public void getAssessmentReviews() {
        List<AssessmentReview> assessmentReviews = newAssessmentReview().build(2);

        List<AssessmentReviewResource> assessmentReviewResources = newAssessmentReviewResource().build(2);

        when(assessmentReviewRepositoryMock.findByParticipantUserIdAndTargetCompetitionIdOrderByActivityStateStateAscIdAsc(userId, competitionId)).thenReturn(assessmentReviews);
        when(assessmentReviewMapperMock.mapToResource(same(assessmentReviews.get(0)))).thenReturn(assessmentReviewResources.get(0));
        when(assessmentReviewMapperMock.mapToResource(same(assessmentReviews.get(1)))).thenReturn(assessmentReviewResources.get(1));

        assertEquals(assessmentReviewResources, service.getAssessmentReviews(userId, competitionId).getSuccessObjectOrThrowException());

        InOrder inOrder = inOrder(assessmentReviewRepositoryMock, assessmentReviewMapperMock);
        inOrder.verify(assessmentReviewRepositoryMock).findByParticipantUserIdAndTargetCompetitionIdOrderByActivityStateStateAscIdAsc(userId, competitionId);
        inOrder.verify(assessmentReviewMapperMock).mapToResource(same(assessmentReviews.get(0)));
        inOrder.verify(assessmentReviewMapperMock).mapToResource(same(assessmentReviews.get(1)));
    }

    @Test
    public void acceptAssessmentReview() {
        AssessmentReview assessmentReview = newAssessmentReview().build();

        when(assessmentReviewRepositoryMock.findOne(assessmentReview.getId())).thenReturn(assessmentReview);
        when(assessmentReviewWorkflowHandlerMock.acceptInvitation(assessmentReview)).thenReturn(true);

        service.acceptAssessmentReview(assessmentReview.getId()).getSuccessObjectOrThrowException();

        InOrder inOrder = inOrder(assessmentReviewRepositoryMock, assessmentReviewWorkflowHandlerMock);
        inOrder.verify(assessmentReviewRepositoryMock).findOne(assessmentReview.getId());
        inOrder.verify(assessmentReviewWorkflowHandlerMock).acceptInvitation(assessmentReview);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptAssessmentReview_notFound() {
        AssessmentReview assessmentReview = newAssessmentReview().build();

        ServiceResult<Void> serviceResult = service.acceptAssessmentReview(assessmentReview.getId());

        assertTrue(serviceResult.isFailure());
        assertEquals(GENERAL_NOT_FOUND.getErrorKey(), serviceResult.getErrors().get(0).getErrorKey());

        InOrder inOrder = inOrder(assessmentReviewRepositoryMock, assessmentReviewWorkflowHandlerMock);
        inOrder.verify(assessmentReviewRepositoryMock).findOne(assessmentReview.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptAssessmentReview_invalidState() {
        AssessmentReview assessmentReview = newAssessmentReview().build();

        when(assessmentReviewRepositoryMock.findOne(assessmentReview.getId())).thenReturn(assessmentReview);
        when(assessmentReviewWorkflowHandlerMock.acceptInvitation(assessmentReview)).thenReturn(false);

        ServiceResult<Void> serviceResult = service.acceptAssessmentReview(assessmentReview.getId());
        assertTrue(serviceResult.isFailure());
        assertEquals(ASSESSMENT_REVIEW_ACCEPT_FAILED.getErrorKey(), serviceResult.getErrors().get(0).getErrorKey());

        InOrder inOrder = inOrder(assessmentReviewRepositoryMock, assessmentReviewWorkflowHandlerMock);
        inOrder.verify(assessmentReviewRepositoryMock).findOne(assessmentReview.getId());
        inOrder.verify(assessmentReviewWorkflowHandlerMock).acceptInvitation(assessmentReview);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectAssessmentReview() {
        AssessmentReviewRejectOutcomeResource rejectOutcomeResource =
                newAssessmentReviewRejectOutcomeResource().build();
        AssessmentReview assessmentReview = newAssessmentReview().build();
        AssessmentReviewRejectOutcome assessmentReviewRejectOutcome = newAssessmentReviewRejectOutcome().build();

        when(assessmentReviewRepositoryMock.findOne(assessmentReview.getId())).thenReturn(assessmentReview);
        when(assessmentReviewWorkflowHandlerMock.rejectInvitation(assessmentReview, assessmentReviewRejectOutcome)).thenReturn(true);
        when(assessmentReviewRejectOutcomeMapperMock.mapToDomain(rejectOutcomeResource)).thenReturn(assessmentReviewRejectOutcome);

        service.rejectAssessmentReview(assessmentReview.getId(), rejectOutcomeResource).getSuccessObjectOrThrowException();

        InOrder inOrder = inOrder(assessmentReviewRepositoryMock, assessmentReviewWorkflowHandlerMock, assessmentReviewRejectOutcomeMapperMock);
        inOrder.verify(assessmentReviewRepositoryMock).findOne(assessmentReview.getId());
        inOrder.verify(assessmentReviewRejectOutcomeMapperMock).mapToDomain(rejectOutcomeResource);
        inOrder.verify(assessmentReviewWorkflowHandlerMock).rejectInvitation(assessmentReview, assessmentReviewRejectOutcome);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectAssessmentReview_invalidState() {
        AssessmentReviewRejectOutcomeResource rejectOutcomeResource =
                newAssessmentReviewRejectOutcomeResource().build();
        AssessmentReview assessmentReview = newAssessmentReview().build();
        AssessmentReviewRejectOutcome assessmentReviewRejectOutcome = newAssessmentReviewRejectOutcome().build();

        when(assessmentReviewRepositoryMock.findOne(assessmentReview.getId())).thenReturn(assessmentReview);
        when(assessmentReviewWorkflowHandlerMock.rejectInvitation(assessmentReview, assessmentReviewRejectOutcome)).thenReturn(false);
        when(assessmentReviewRejectOutcomeMapperMock.mapToDomain(rejectOutcomeResource)).thenReturn(assessmentReviewRejectOutcome);

        ServiceResult<Void> serviceResult = service.rejectAssessmentReview(assessmentReview.getId(), rejectOutcomeResource);
        assertTrue(serviceResult.isFailure());
        assertEquals(ASSESSMENT_REVIEW_REJECT_FAILED.getErrorKey(), serviceResult.getErrors().get(0).getErrorKey());

        InOrder inOrder = inOrder(assessmentReviewRepositoryMock, assessmentReviewWorkflowHandlerMock, assessmentReviewRejectOutcomeMapperMock);
        inOrder.verify(assessmentReviewRepositoryMock).findOne(assessmentReview.getId());
        inOrder.verify(assessmentReviewRejectOutcomeMapperMock).mapToDomain(rejectOutcomeResource);
        inOrder.verify(assessmentReviewWorkflowHandlerMock).rejectInvitation(assessmentReview, assessmentReviewRejectOutcome);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getAssessmentReview() {
        AssessmentReviewResource assessmentReviewResource = newAssessmentReviewResource().build();
        AssessmentReview assessmentReview = newAssessmentReview().build();

        when(assessmentReviewRepositoryMock.findOne(assessmentReviewResource.getId())).thenReturn(assessmentReview);
        when(assessmentReviewMapperMock.mapToResource(assessmentReview)).thenReturn(assessmentReviewResource);

        AssessmentReviewResource result = service.getAssessmentReview(assessmentReviewResource.getId())
                .getSuccessObjectOrThrowException();

        assertEquals(assessmentReviewResource, result);

        InOrder inOrder = inOrder(assessmentReviewRepositoryMock, assessmentReviewMapperMock);
        inOrder.verify(assessmentReviewRepositoryMock).findOne(assessmentReviewResource.getId());
        inOrder.verify(assessmentReviewMapperMock).mapToResource(assessmentReview);
        inOrder.verifyNoMoreInteractions();
    }
}