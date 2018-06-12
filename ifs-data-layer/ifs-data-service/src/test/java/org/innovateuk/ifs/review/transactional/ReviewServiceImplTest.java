package org.innovateuk.ifs.review.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.notifications.service.senders.NotificationSender;
import org.innovateuk.ifs.review.domain.ReviewParticipant;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.review.domain.Review;
import org.innovateuk.ifs.review.domain.ReviewRejectOutcome;
import org.innovateuk.ifs.review.mapper.ReviewMapper;
import org.innovateuk.ifs.review.mapper.ReviewRejectOutcomeMapper;
import org.innovateuk.ifs.review.repository.ReviewParticipantRepository;
import org.innovateuk.ifs.review.repository.ReviewRepository;
import org.innovateuk.ifs.review.resource.ReviewRejectOutcomeResource;
import org.innovateuk.ifs.review.resource.ReviewResource;
import org.innovateuk.ifs.review.resource.ReviewState;
import org.innovateuk.ifs.review.workflow.configuration.ReviewWorkflowHandler;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static junit.framework.TestCase.assertFalse;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.MilestoneBuilder.newMilestone;
import static org.innovateuk.ifs.review.builder.ReviewBuilder.newReview;
import static org.innovateuk.ifs.review.builder.ReviewParticipantBuilder.newReviewParticipant;
import static org.innovateuk.ifs.review.builder.ReviewRejectOutcomeBuilder.newReviewRejectOutcome;
import static org.innovateuk.ifs.review.builder.ReviewRejectOutcomeResourceBuilder.newReviewRejectOutcomeResource;
import static org.innovateuk.ifs.review.builder.ReviewResourceBuilder.newReviewResource;
import static org.innovateuk.ifs.review.resource.ReviewState.CREATED;
import static org.innovateuk.ifs.review.transactional.ReviewServiceImpl.INVITE_DATE_FORMAT;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ReviewServiceImplTest extends BaseServiceUnitTest<ReviewServiceImpl> {

    private static final long applicationId = 1L;
    private static final long competitionId = 1L;
    private static final long userId = 2L;
    private Application application;


    @Mock
    private ReviewParticipantRepository reviewParticipantRepositoryMock;
    @Mock
    private NotificationSender notificationSenderMock;
    @Mock
    private ApplicationRepository applicationRepositoryMock;
    @Mock
    private ReviewRepository reviewRepositoryMock;
    @Mock
    private ProcessRoleRepository processRoleRepositoryMock;
    @Mock
    private ReviewWorkflowHandler reviewWorkflowHandlerMock;
    @Mock
    private ReviewMapper reviewMapperMock;
    @Mock
    private ReviewRejectOutcomeMapper reviewRejectOutcomeMapperMock;



    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    @Override
    protected ReviewServiceImpl supplyServiceUnderTest() {
        return new ReviewServiceImpl();
    }

    @Before
    public void setUp() {
        application = newApplication().withId(applicationId).build();
    }

    @Test
    public void assignApplicationsToPanel() {
        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);

        ServiceResult<Void> result = service.assignApplicationToPanel(applicationId);
        assertTrue(result.isSuccess());
        assertTrue(application.isInAssessmentReviewPanel());

        verify(applicationRepositoryMock).findOne(applicationId);
        verifyNoMoreInteractions(applicationRepositoryMock);
    }

    @Test
    public void unAssignApplicationsFromPanel() {
        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
        when(reviewRepositoryMock
                .findByTargetIdAndActivityStateNot(applicationId, ReviewState.WITHDRAWN))
                .thenReturn(emptyList());

        ServiceResult<Void> result = service.unassignApplicationFromPanel(applicationId);
        assertTrue(result.isSuccess());
        assertFalse(application.isInAssessmentReviewPanel());

        verify(applicationRepositoryMock).findOne(applicationId);
        verify(reviewRepositoryMock).findByTargetIdAndActivityStateNot(applicationId, ReviewState.WITHDRAWN);
        verifyNoMoreInteractions(applicationRepositoryMock, reviewRepositoryMock);
    }

    @Test
    public void unAssignApplicationsFromPanel_existingReviews() {
        List<Review> reviews = newReview().withTarget(application).withState(ReviewState.WITHDRAWN).build(2);

        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
        when(reviewRepositoryMock
                .findByTargetIdAndActivityStateNot(applicationId, ReviewState.WITHDRAWN))
                .thenReturn(reviews);

        ServiceResult<Void> result = service.unassignApplicationFromPanel(applicationId);
        assertTrue(result.isSuccess());
        assertFalse(application.isInAssessmentReviewPanel());

        reviews.forEach(a -> assertEquals(ReviewState.WITHDRAWN, a.getProcessState()));

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
                .withMilestones(singletonList(newMilestone()
                        .withType(MilestoneType.ASSESSMENT_PANEL)
                        .withDate(panelDate)
                        .build())
                )
                .build();

        List<ReviewParticipant> reviewParticipants =
                newReviewParticipant()
                        .withUser(assessor)
                        .build(1);
        List<Application> applications = newApplication()
                .withCompetition(competition)
                .build(1);

        List<ProcessRole> processRoles = newProcessRole()
                .withUser(assessor)
                .withApplication(applications.toArray(new Application[1]))
                .build(1);

        Review review = new Review(applications.get(0), reviewParticipants.get(0));
        review.setProcessState(CREATED);

        when(reviewParticipantRepositoryMock
                .getPanelAssessorsByCompetitionAndStatusContains(competitionId, singletonList(ParticipantStatus.ACCEPTED)))
                .thenReturn(reviewParticipants);
        when(applicationRepositoryMock
                .findByCompetitionIdAndInAssessmentReviewPanelTrueAndApplicationProcessActivityState(competitionId, ApplicationState.SUBMITTED.SUBMITTED))
                .thenReturn(applications);

        when(reviewRepositoryMock.existsByParticipantUserAndTargetAndActivityStateNot(assessor, application, ReviewState.WITHDRAWN))
                .thenReturn(true);

        when(processRoleRepositoryMock.save(isA(ProcessRole.class))).thenReturn(processRoles.get(0));

        when(reviewRepositoryMock
                .findByTargetCompetitionIdAndActivityState(competitionId, CREATED))
                .thenReturn(asList(review));

        Notification expectedNotification = createLambdaMatcher(n -> {
            Map<String, Object> globalArguments = n.getGlobalArguments();
            assertEquals(assessor.getEmail(), n.getTo().get(0).getEmailAddress());
            assertEquals(globalArguments.get("subject"), "Applications ready for review");
            assertEquals(globalArguments.get("name"), "Tom Baldwin");
            assertEquals(globalArguments.get("competitionName"),  competitionName);
            assertEquals(globalArguments.get("panelDate"), panelDate.format(INVITE_DATE_FORMAT));
            assertEquals(globalArguments.get("ifsUrl"), webBaseUrl);
        });

        when(notificationSenderMock.sendNotification(expectedNotification)).thenReturn(ServiceResult.serviceSuccess(expectedNotification));


        service.createAndNotifyReviews(competitionId).getSuccess();


        InOrder inOrder = inOrder(reviewParticipantRepositoryMock, applicationRepositoryMock,
                reviewRepositoryMock, reviewRepositoryMock,
                reviewRepositoryMock, reviewWorkflowHandlerMock, notificationSenderMock, processRoleRepositoryMock);
        inOrder.verify(reviewParticipantRepositoryMock)
                .getPanelAssessorsByCompetitionAndStatusContains(competitionId, singletonList(ParticipantStatus.ACCEPTED));
        inOrder.verify(applicationRepositoryMock)
                .findByCompetitionIdAndInAssessmentReviewPanelTrueAndApplicationProcessActivityState(competitionId, ApplicationState.SUBMITTED);
        inOrder.verify(reviewRepositoryMock)
                .existsByParticipantUserAndTargetAndActivityStateNot(assessor, applications.get(0), ReviewState.WITHDRAWN);
        inOrder.verify(reviewRepositoryMock)
                .save(review);
        inOrder.verify(reviewRepositoryMock)
                .findByTargetCompetitionIdAndActivityState(competitionId, CREATED);
        inOrder.verify(reviewWorkflowHandlerMock)
                .notifyInvitation(review);
        inOrder.verify(notificationSenderMock)
                .sendNotification(isA(Notification.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void isPendingReviewNotifications() {
        final boolean expectedPendingReviewNotifications = true;

        when(reviewRepositoryMock.notifiable(competitionId)).thenReturn(expectedPendingReviewNotifications);

        assertEquals(expectedPendingReviewNotifications, service.isPendingReviewNotifications(competitionId).getSuccess());

        verify(reviewRepositoryMock, only()).notifiable(competitionId);
    }

    @Test
    public void isPendingReviewNotifications_none() {
        final boolean expectedPendingReviewNotifications = false;

        when(reviewRepositoryMock.notifiable(competitionId)).thenReturn(expectedPendingReviewNotifications);

        assertEquals(expectedPendingReviewNotifications, service.isPendingReviewNotifications(competitionId).getSuccess());

        verify(reviewRepositoryMock, only()).notifiable(competitionId);
    }

    @Test
    public void getAssessmentReviews() {
        List<Review> reviews = newReview().build(2);

        List<ReviewResource> reviewResources = newReviewResource().build(2);

        when(reviewRepositoryMock.findByParticipantUserIdAndTargetCompetitionIdOrderByActivityStateAscIdAsc(userId, competitionId)).thenReturn(reviews);
        when(reviewMapperMock.mapToResource(same(reviews.get(0)))).thenReturn(reviewResources.get(0));
        when(reviewMapperMock.mapToResource(same(reviews.get(1)))).thenReturn(reviewResources.get(1));

        assertEquals(reviewResources, service.getReviews(userId, competitionId).getSuccess());

        InOrder inOrder = inOrder(reviewRepositoryMock, reviewMapperMock);
        inOrder.verify(reviewRepositoryMock).findByParticipantUserIdAndTargetCompetitionIdOrderByActivityStateAscIdAsc(userId, competitionId);
        inOrder.verify(reviewMapperMock).mapToResource(same(reviews.get(0)));
        inOrder.verify(reviewMapperMock).mapToResource(same(reviews.get(1)));
    }

    @Test
    public void acceptAssessmentReview() {
        Review review = newReview().build();

        when(reviewRepositoryMock.findOne(review.getId())).thenReturn(review);
        when(reviewWorkflowHandlerMock.acceptInvitation(review)).thenReturn(true);

        service.acceptReview(review.getId()).getSuccess();

        InOrder inOrder = inOrder(reviewRepositoryMock, reviewWorkflowHandlerMock);
        inOrder.verify(reviewRepositoryMock).findOne(review.getId());
        inOrder.verify(reviewWorkflowHandlerMock).acceptInvitation(review);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptAssessmentReview_notFound() {
        Review review = newReview().build();

        ServiceResult<Void> serviceResult = service.acceptReview(review.getId());

        assertTrue(serviceResult.isFailure());
        assertEquals(GENERAL_NOT_FOUND.getErrorKey(), serviceResult.getErrors().get(0).getErrorKey());

        InOrder inOrder = inOrder(reviewRepositoryMock, reviewWorkflowHandlerMock);
        inOrder.verify(reviewRepositoryMock).findOne(review.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptAssessmentReview_invalidState() {
        Review review = newReview().build();

        when(reviewRepositoryMock.findOne(review.getId())).thenReturn(review);
        when(reviewWorkflowHandlerMock.acceptInvitation(review)).thenReturn(false);

        ServiceResult<Void> serviceResult = service.acceptReview(review.getId());
        assertTrue(serviceResult.isFailure());
        assertEquals(ASSESSMENT_REVIEW_ACCEPT_FAILED.getErrorKey(), serviceResult.getErrors().get(0).getErrorKey());

        InOrder inOrder = inOrder(reviewRepositoryMock, reviewWorkflowHandlerMock);
        inOrder.verify(reviewRepositoryMock).findOne(review.getId());
        inOrder.verify(reviewWorkflowHandlerMock).acceptInvitation(review);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectAssessmentReview() {
        ReviewRejectOutcomeResource rejectOutcomeResource =
                newReviewRejectOutcomeResource().build();
        Review review = newReview().build();
        ReviewRejectOutcome reviewRejectOutcome = newReviewRejectOutcome().build();

        when(reviewRepositoryMock.findOne(review.getId())).thenReturn(review);
        when(reviewWorkflowHandlerMock.rejectInvitation(review, reviewRejectOutcome)).thenReturn(true);
        when(reviewRejectOutcomeMapperMock.mapToDomain(rejectOutcomeResource)).thenReturn(reviewRejectOutcome);

        service.rejectReview(review.getId(), rejectOutcomeResource).getSuccess();

        InOrder inOrder = inOrder(reviewRepositoryMock, reviewWorkflowHandlerMock, reviewRejectOutcomeMapperMock);
        inOrder.verify(reviewRepositoryMock).findOne(review.getId());
        inOrder.verify(reviewRejectOutcomeMapperMock).mapToDomain(rejectOutcomeResource);
        inOrder.verify(reviewWorkflowHandlerMock).rejectInvitation(review, reviewRejectOutcome);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectAssessmentReview_invalidState() {
        ReviewRejectOutcomeResource rejectOutcomeResource =
                newReviewRejectOutcomeResource().build();
        Review review = newReview().build();
        ReviewRejectOutcome reviewRejectOutcome = newReviewRejectOutcome().build();

        when(reviewRepositoryMock.findOne(review.getId())).thenReturn(review);
        when(reviewWorkflowHandlerMock.rejectInvitation(review, reviewRejectOutcome)).thenReturn(false);
        when(reviewRejectOutcomeMapperMock.mapToDomain(rejectOutcomeResource)).thenReturn(reviewRejectOutcome);

        ServiceResult<Void> serviceResult = service.rejectReview(review.getId(), rejectOutcomeResource);
        assertTrue(serviceResult.isFailure());
        assertEquals(ASSESSMENT_REVIEW_REJECT_FAILED.getErrorKey(), serviceResult.getErrors().get(0).getErrorKey());

        InOrder inOrder = inOrder(reviewRepositoryMock, reviewWorkflowHandlerMock, reviewRejectOutcomeMapperMock);
        inOrder.verify(reviewRepositoryMock).findOne(review.getId());
        inOrder.verify(reviewRejectOutcomeMapperMock).mapToDomain(rejectOutcomeResource);
        inOrder.verify(reviewWorkflowHandlerMock).rejectInvitation(review, reviewRejectOutcome);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getAssessmentReview() {
        ReviewResource reviewResource = newReviewResource().build();
        Review review = newReview().build();

        when(reviewRepositoryMock.findOne(reviewResource.getId())).thenReturn(review);
        when(reviewMapperMock.mapToResource(review)).thenReturn(reviewResource);

        ReviewResource result = service.getReview(reviewResource.getId())
                .getSuccess();

        assertEquals(reviewResource, result);

        InOrder inOrder = inOrder(reviewRepositoryMock, reviewMapperMock);
        inOrder.verify(reviewRepositoryMock).findOne(reviewResource.getId());
        inOrder.verify(reviewMapperMock).mapToResource(review);
        inOrder.verifyNoMoreInteractions();
    }
}