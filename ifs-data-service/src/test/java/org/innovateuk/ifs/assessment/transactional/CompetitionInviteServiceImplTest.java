package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.category.domain.Category;
import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.email.resource.EmailAddress;
import org.innovateuk.ifs.email.resource.EmailContent;
import org.innovateuk.ifs.invite.builder.RejectionReasonResourceBuilder;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.*;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.notifications.resource.ExternalUserNotificationTarget;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.user.domain.Profile;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.assessment.builder.CompetitionInviteBuilder.newCompetitionInvite;
import static org.innovateuk.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static org.innovateuk.ifs.category.builder.CategoryBuilder.newCategory;
import static org.innovateuk.ifs.category.builder.CategoryResourceBuilder.newCategoryResource;
import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_AREA;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.MilestoneBuilder.newMilestone;
import static org.innovateuk.ifs.competition.resource.MilestoneType.*;
import static org.innovateuk.ifs.email.builders.EmailContentResourceBuilder.newEmailContentResource;
import static org.innovateuk.ifs.invite.builder.AssessorCreatedInviteResourceBuilder.newAssessorCreatedInviteResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteToSendResourceBuilder.newAssessorInviteToSendResource;
import static org.innovateuk.ifs.invite.builder.AvailableAssessorResourceBuilder.newAvailableAssessorResource;
import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteResourceBuilder.newExistingUserStagedInviteResource;
import static org.innovateuk.ifs.invite.builder.NewUserStagedInviteResourceBuilder.newNewUserStagedInviteResource;
import static org.innovateuk.ifs.invite.builder.RejectionReasonBuilder.newRejectionReason;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.invite.domain.CompetitionParticipantRole.ASSESSOR;
import static org.innovateuk.ifs.user.builder.AffiliationBuilder.newAffiliation;
import static org.innovateuk.ifs.user.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.AffiliationType.EMPLOYER;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class CompetitionInviteServiceImplTest extends BaseServiceUnitTest<CompetitionInviteServiceImpl> {

    private CompetitionParticipant competitionParticipant;
    private UserResource userResource;
    private User user;

    @Override
    protected CompetitionInviteServiceImpl supplyServiceUnderTest() {
        return new CompetitionInviteServiceImpl();
    }

    @Before
    public void setUp() {
        List<Milestone> milestones = newMilestone()
                .withDate(now().minusDays(1))
                .withType(OPEN_DATE, SUBMISSION_DATE, ASSESSORS_NOTIFIED, ASSESSOR_ACCEPTS).build(4);
        milestones.addAll(newMilestone()
                .withDate(now().plusDays(1))
                .withType(NOTIFICATIONS, ASSESSOR_DEADLINE)
                .build(2));

        Competition competition = newCompetition().withName("my competition")
                .withMilestones(milestones)
                .withSetupComplete(true)
                .build();

        CompetitionInvite competitionInvite = setUpCompetitionInvite(competition, SENT);

        competitionParticipant = new CompetitionParticipant(competitionInvite);
        CompetitionInviteResource expected = newCompetitionInviteResource().withCompetitionName("my competition").build();
        RejectionReason rejectionReason = newRejectionReason().withId(1L).withReason("not available").build();
        userResource = newUserResource().withId(7L).build();
        user = newUser().withId(7L).build();

        when(competitionInviteRepositoryMock.getByHash("inviteHash")).thenReturn(competitionInvite);

        when(competitionInviteRepositoryMock.save(same(competitionInvite))).thenReturn(competitionInvite);
        when(competitionInviteMapperMock.mapToResource(same(competitionInvite))).thenReturn(expected);

        when(competitionParticipantRepositoryMock.getByInviteHash("inviteHash")).thenReturn(competitionParticipant);

        when(rejectionReasonRepositoryMock.findOne(1L)).thenReturn(rejectionReason);

        when(userRepositoryMock.findOne(7L)).thenReturn(user);

        ReflectionTestUtils.setField(service, "webBaseUrl", "https://ifs-local-dev");
    }

    @Test
    public void getCreatedInvite() throws Exception {
        String email = "john@email.com";
        String name = "John Barnes";

        Competition competition = newCompetition()
                .withName("my competition")
                .withAssessorAcceptsDate(LocalDateTime.of(2016, 12, 20, 12, 0, 0))
                .withAssessorDeadlineDate(LocalDateTime.of(2017, 1, 17, 12, 0, 0))
                .build();

        Category innovationArea = newCategory().withName("innovation area").build();

        CompetitionInvite invite = setUpCompetitionInvite(competition, email, name, CREATED, innovationArea, null);

        Map<String, Object> expectedNotificationArguments = asMap("name", name,
                "competitionName", "my competition",
                "innovationArea", innovationArea,
                "acceptsDate", "20 December 2016",
                "deadlineDate", "17 January 2017",
                "inviteUrl", format("%s/invite/competition/%s", "https://ifs-local-dev/assessment", invite.getHash()));

        AssessorInviteToSendResource expectedAssessorInviteToSendResource = newAssessorInviteToSendResource()
                .withCompetitionName("my competition")
                .build();

        SystemNotificationSource from = systemNotificationSourceMock;
        NotificationTarget to = new ExternalUserNotificationTarget(name, email);
        Notification notification = new Notification(from, singletonList(to), CompetitionInviteServiceImpl.Notifications.INVITE_ASSESSOR, expectedNotificationArguments);

        when(competitionInviteRepositoryMock.findOne(invite.getId())).thenReturn(invite);
        when(notificationSender.renderTemplates(notification)).thenReturn(serviceSuccess(asMap(to, setUpEmailContent())));
        when(assessorInviteToSendMapperMock.mapToResource(invite)).thenReturn(expectedAssessorInviteToSendResource);

        AssessorInviteToSendResource result = service.getCreatedInvite(invite.getId()).getSuccessObjectOrThrowException();
        assertEquals(expectedAssessorInviteToSendResource, result);

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, notificationSender, assessorInviteToSendMapperMock);
        inOrder.verify(competitionInviteRepositoryMock).findOne(invite.getId());
        inOrder.verify(notificationSender).renderTemplates(notification);
        inOrder.verify(assessorInviteToSendMapperMock).mapToResource(invite);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getCreatedInvite_notCreated() throws Exception {
        CompetitionInvite competitionInvite = setUpCompetitionInvite(newCompetition().withName("my competition").build(), SENT);

        when(competitionInviteRepositoryMock.findOne(competitionInvite.getId())).thenReturn(competitionInvite);

        ServiceResult<AssessorInviteToSendResource> inviteServiceResult = service.getCreatedInvite(competitionInvite.getId());

        assertTrue(inviteServiceResult.isFailure());
        assertTrue(inviteServiceResult.getFailure().is(new Error(COMPETITION_INVITE_ALREADY_SENT, "my competition")));

        verify(competitionInviteRepositoryMock, only()).findOne(competitionInvite.getId());
    }

    @Test
    public void getInvite() throws Exception {
        ServiceResult<CompetitionInviteResource> inviteServiceResult = service.getInvite("inviteHash");

        assertTrue(inviteServiceResult.isSuccess());

        CompetitionInviteResource competitionInviteResource = inviteServiceResult.getSuccessObjectOrThrowException();
        assertEquals("my competition", competitionInviteResource.getCompetitionName());

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionInviteMapperMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash("inviteHash");
        inOrder.verify(competitionInviteMapperMock).mapToResource(any(CompetitionInvite.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInvite_hashNotExists() throws Exception {
        when(competitionInviteRepositoryMock.getByHash(anyString())).thenReturn(null);

        ServiceResult<CompetitionInviteResource> inviteServiceResult = service.getInvite("inviteHashNotExists");

        assertTrue(inviteServiceResult.isFailure());
        assertTrue(inviteServiceResult.getFailure().is(notFoundError(CompetitionInvite.class, "inviteHashNotExists")));

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionInviteMapperMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash("inviteHashNotExists");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInvite_afterAccepted() throws Exception {
        service.openInvite("inviteHash");
        ServiceResult<Void> acceptResult = service.acceptInvite("inviteHash", userResource);
        assertTrue(acceptResult.isSuccess());

        ServiceResult<CompetitionInviteResource> getResult = service.getInvite("inviteHash");
        assertTrue(getResult.isFailure());
        assertTrue(getResult.getFailure().is(new Error(COMPETITION_INVITE_CLOSED, "my competition")));

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionParticipantRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash("inviteHash");
        inOrder.verify(competitionInviteRepositoryMock).getByHash("inviteHash");
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash("inviteHash");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInvite_afterRejected() throws Exception {
        RejectionReasonResource rejectionReason = RejectionReasonResourceBuilder.newRejectionReasonResource()
                .withId(1L)
                .build();

        service.openInvite("inviteHash");
        ServiceResult<Void> rejectResult = service.rejectInvite("inviteHash", rejectionReason, Optional.of("no time"));
        assertTrue(rejectResult.isSuccess());

        ServiceResult<CompetitionInviteResource> getResult = service.getInvite("inviteHash");
        assertTrue(getResult.isFailure());
        assertTrue(getResult.getFailure().is(new Error(COMPETITION_INVITE_CLOSED, "my competition")));

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionParticipantRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash("inviteHash");
        inOrder.verify(competitionInviteRepositoryMock).getByHash("inviteHash");
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash("inviteHash");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void openInvite() throws Exception {
        ServiceResult<CompetitionInviteResource> inviteServiceResult = service.openInvite("inviteHash");

        assertTrue(inviteServiceResult.isSuccess());

        CompetitionInviteResource competitionInviteResource = inviteServiceResult.getSuccessObjectOrThrowException();
        assertEquals("my competition", competitionInviteResource.getCompetitionName());

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionInviteMapperMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash("inviteHash");
        inOrder.verify(competitionInviteRepositoryMock).save(any(CompetitionInvite.class));
        inOrder.verify(competitionInviteMapperMock).mapToResource(any(CompetitionInvite.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void openInvite_hashNotExists() throws Exception {
        when(competitionInviteRepositoryMock.getByHash(anyString())).thenReturn(null);

        ServiceResult<CompetitionInviteResource> inviteServiceResult = service.openInvite("inviteHashNotExists");

        assertTrue(inviteServiceResult.isFailure());
        assertTrue(inviteServiceResult.getFailure().is(notFoundError(CompetitionInvite.class, "inviteHashNotExists")));

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionInviteMapperMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash("inviteHashNotExists");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void openInvite_inviteExpired() throws Exception {
        CompetitionInvite competitionInvite = setUpCompetitionInvite(newCompetition()
                .withName("my competition")
                .withAssessorAcceptsDate(now().minusDays(1))
                .build(), SENT);

        when(competitionInviteRepositoryMock.getByHash(anyString())).thenReturn(competitionInvite);

        ServiceResult<CompetitionInviteResource> inviteServiceResult = service.openInvite("inviteHashExpired");

        assertTrue(inviteServiceResult.isFailure());
        assertTrue(inviteServiceResult.getFailure().is(new Error(COMPETITION_INVITE_EXPIRED, "my competition")));

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionInviteMapperMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash("inviteHashExpired");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void openInvite_afterAccepted() throws Exception {
        service.openInvite("inviteHash");
        ServiceResult<Void> acceptResult = service.acceptInvite("inviteHash", userResource);
        assertTrue(acceptResult.isSuccess());

        ServiceResult<CompetitionInviteResource> getResult = service.openInvite("inviteHash");
        assertTrue(getResult.isFailure());
        assertTrue(getResult.getFailure().is(new Error(COMPETITION_INVITE_CLOSED, "my competition")));

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionParticipantRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash("inviteHash");
        inOrder.verify(competitionInviteRepositoryMock).getByHash("inviteHash");
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash("inviteHash");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void openInvite_afterRejected() throws Exception {
        RejectionReasonResource rejectionReason = RejectionReasonResourceBuilder.newRejectionReasonResource()
                .withId(1L)
                .build();

        service.openInvite("inviteHash");
        ServiceResult<Void> rejectResult = service.rejectInvite("inviteHash", rejectionReason, Optional.of("no time"));
        assertTrue(rejectResult.isSuccess());

        ServiceResult<CompetitionInviteResource> getResult = service.openInvite("inviteHash");
        assertTrue(getResult.isFailure());
        assertTrue(getResult.getFailure().is(new Error(COMPETITION_INVITE_CLOSED, "my competition")));

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionParticipantRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash("inviteHash");
        inOrder.verify(competitionInviteRepositoryMock).getByHash("inviteHash");
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash("inviteHash");
        inOrder.verifyNoMoreInteractions();
    }

    // accept

    @Test
    public void acceptInvite() {
        service.openInvite("inviteHash");

        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());
        assertNull(competitionParticipant.getUser());

        ServiceResult<Void> serviceResult = service.acceptInvite("inviteHash", userResource);

        assertTrue(serviceResult.isSuccess());
        assertEquals(ParticipantStatus.ACCEPTED, competitionParticipant.getStatus());
        assertEquals(user, competitionParticipant.getUser());

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock, userRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(7L);
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash("inviteHash");
        inOrder.verify(competitionParticipantRepositoryMock).save(competitionParticipant);

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptInvite_hashNotExists() {
        ServiceResult<Void> serviceResult = service.acceptInvite("inviteHashNotExists", userResource);

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(notFoundError(CompetitionParticipant.class, "inviteHashNotExists")));

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock);
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash("inviteHashNotExists");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptInvite_notOpened() {
        assertEquals(SENT, competitionParticipant.getInvite().getStatus());
        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());

        ServiceResult<Void> serviceResult = service.acceptInvite("inviteHash", userResource);

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(new Error(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_UNOPENED_INVITE, "my competition")));

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock);
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash("inviteHash");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptInvite_alreadyAccepted() {
        service.openInvite("inviteHash");

        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());

        // accept the invite
        ServiceResult<Void> serviceResult = service.acceptInvite("inviteHash", userResource);
        assertTrue(serviceResult.isSuccess());
        assertEquals(ParticipantStatus.ACCEPTED, competitionParticipant.getStatus());

        // accept a second time
        serviceResult = service.acceptInvite("inviteHash", userResource);

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(new Error(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_ALREADY_ACCEPTED_INVITE, "my competition")));

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock);
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByInviteHash("inviteHash");
        inOrder.verify(competitionParticipantRepositoryMock).save(competitionParticipant);
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByInviteHash("inviteHash");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptInvite_alreadyRejected() {
        service.openInvite("inviteHash");

        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());

        // reject the invite
        RejectionReasonResource rejectionReasonResource = RejectionReasonResourceBuilder
                .newRejectionReasonResource()
                .withId(1L)
                .build();

        ServiceResult<Void> serviceResult = service.rejectInvite("inviteHash", rejectionReasonResource, Optional.of("too busy"));
        assertTrue(serviceResult.isSuccess());
        assertEquals(ParticipantStatus.REJECTED, competitionParticipant.getStatus());

        // accept the invite
        serviceResult = service.acceptInvite("inviteHash", userResource);

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(new Error(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_ALREADY_REJECTED_INVITE, "my competition")));

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock, rejectionReasonRepositoryMock);
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash("inviteHash");
        inOrder.verify(competitionParticipantRepositoryMock).save(competitionParticipant);
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash("inviteHash");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvite() {
        service.openInvite("inviteHash");

        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());

        RejectionReasonResource rejectionReasonResource = RejectionReasonResourceBuilder
                .newRejectionReasonResource()
                .withId(1L)
                .build();

        ServiceResult<Void> serviceResult = service.rejectInvite("inviteHash", rejectionReasonResource, Optional.of("too busy"));

        assertTrue(serviceResult.isSuccess());
        assertEquals(ParticipantStatus.REJECTED, competitionParticipant.getStatus());
        assertEquals("too busy", competitionParticipant.getRejectionReasonComment());

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock, rejectionReasonRepositoryMock);
        inOrder.verify(rejectionReasonRepositoryMock).findOne(1L);
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash("inviteHash");
        inOrder.verify(competitionParticipantRepositoryMock).save(competitionParticipant);

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvite_hashNotExists() {
        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());
        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());

        RejectionReasonResource rejectionReasonResource = RejectionReasonResourceBuilder
                .newRejectionReasonResource()
                .withId(1L)
                .build();

        ServiceResult<Void> serviceResult = service.rejectInvite("inviteHashNotExists", rejectionReasonResource, Optional.of("too busy"));

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(notFoundError(CompetitionParticipant.class, "inviteHashNotExists")));

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock);
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash("inviteHashNotExists");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvite_notOpened() {
        assertEquals(SENT, competitionParticipant.getInvite().getStatus());
        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());

        RejectionReasonResource rejectionReasonResource = RejectionReasonResourceBuilder
                .newRejectionReasonResource()
                .withId(1L)
                .build();

        ServiceResult<Void> serviceResult = service.rejectInvite("inviteHash", rejectionReasonResource, Optional.of("too busy"));

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(new Error(COMPETITION_PARTICIPANT_CANNOT_REJECT_UNOPENED_INVITE, "my competition")));

        InOrder inOrder = inOrder(rejectionReasonRepositoryMock, competitionParticipantRepositoryMock);
        inOrder.verify(rejectionReasonRepositoryMock).findOne(1L);
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash("inviteHash");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvite_alreadyAccepted() {
        service.openInvite("inviteHash");

        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());

        // accept the invite
        ServiceResult<Void> serviceResult = service.acceptInvite("inviteHash", userResource);
        assertTrue(serviceResult.isSuccess());
        assertEquals(ParticipantStatus.ACCEPTED, competitionParticipant.getStatus());

        // reject
        RejectionReasonResource rejectionReasonResource = RejectionReasonResourceBuilder
                .newRejectionReasonResource()
                .withId(1L)
                .build();

        serviceResult = service.rejectInvite("inviteHash", rejectionReasonResource, Optional.of("too busy"));

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(new Error(COMPETITION_PARTICIPANT_CANNOT_REJECT_ALREADY_ACCEPTED_INVITE, "my competition")));

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock, rejectionReasonRepositoryMock);
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByInviteHash("inviteHash");
        inOrder.verify(competitionParticipantRepositoryMock).save(competitionParticipant);
        inOrder.verify(rejectionReasonRepositoryMock).findOne(1L);
        inOrder.verify(competitionParticipantRepositoryMock, calls(1)).getByInviteHash("inviteHash");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvite_alreadyRejected() {
        service.openInvite("inviteHash");

        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());

        // reject the invite
        RejectionReasonResource rejectionReasonResource = RejectionReasonResourceBuilder
                .newRejectionReasonResource()
                .withId(1L)
                .build();
        ServiceResult<Void> serviceResult = service.rejectInvite("inviteHash", rejectionReasonResource, Optional.of("too busy"));
        assertTrue(serviceResult.isSuccess());
        assertEquals(ParticipantStatus.REJECTED, competitionParticipant.getStatus());

        // reject again

        serviceResult = service.rejectInvite("inviteHash", rejectionReasonResource, Optional.of("still too busy"));

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(new Error(COMPETITION_PARTICIPANT_CANNOT_REJECT_ALREADY_REJECTED_INVITE, "my competition")));

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock, rejectionReasonRepositoryMock);

        inOrder.verify(rejectionReasonRepositoryMock).findOne(1L);
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash("inviteHash");
        inOrder.verify(competitionParticipantRepositoryMock).save(competitionParticipant);
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash("inviteHash");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvite_unknownRejectionReason() {
        service.openInvite("inviteHash");

        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());


        RejectionReasonResource rejectionReasonResource = RejectionReasonResourceBuilder
                .newRejectionReasonResource()
                .withId(2L)
                .build();

        ServiceResult<Void> serviceResult = service.rejectInvite("inviteHash", rejectionReasonResource, Optional.of("too busy"));

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(notFoundError(RejectionReason.class, 2L)));

        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock, rejectionReasonRepositoryMock);
        inOrder.verify(rejectionReasonRepositoryMock).findOne(2L);

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvite_emptyComment() {
        service.openInvite("inviteHash");

        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());


        RejectionReasonResource rejectionReasonResource = RejectionReasonResourceBuilder
                .newRejectionReasonResource()
                .withId(1L)
                .build();

        ServiceResult<Void> serviceResult = service.rejectInvite("inviteHash", rejectionReasonResource, Optional.of(""));


        assertTrue(serviceResult.isSuccess());

        assertEquals(ParticipantStatus.REJECTED, competitionParticipant.getStatus());
        assertEquals("", competitionParticipant.getRejectionReasonComment());

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock, rejectionReasonRepositoryMock);
        inOrder.verify(rejectionReasonRepositoryMock).findOne(1L);
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash("inviteHash");
        inOrder.verify(competitionParticipantRepositoryMock).save(competitionParticipant);

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void sendInvite() throws Exception {
        String email = "john@email.com";
        String name = "John Barnes";

        CompetitionInvite invite = setUpCompetitionInvite(newCompetition().withName("my competition").build(), email, name, CREATED, null, newUser()
                .withFirstName("Paul")
                .build());

        EmailContent content = setUpEmailContent();

        Map<String, Object> expectedNotificationArguments = emptyMap();
        SystemNotificationSource from = systemNotificationSourceMock;
        NotificationTarget to = new ExternalUserNotificationTarget(name, email);
        Notification notification = new Notification(from, singletonList(to), CompetitionInviteServiceImpl.Notifications.INVITE_ASSESSOR, expectedNotificationArguments);

        when(competitionInviteRepositoryMock.findOne(invite.getId())).thenReturn(invite);
        when(notificationSender.sendEmailWithContent(notification, to, content)).thenReturn(serviceSuccess(asList(new EmailAddress(email, name))));

        ServiceResult<AssessorInviteToSendResource> serviceResult = service.sendInvite(invite.getId(), content);

        assertTrue(serviceResult.isSuccess());
        assertEquals(SENT, invite.getStatus());

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionParticipantRepositoryMock, notificationSender, assessorInviteToSendMapperMock);
        inOrder.verify(competitionInviteRepositoryMock).findOne(invite.getId());
        inOrder.verify(competitionParticipantRepositoryMock).save(createCompetitionParticipantExpectations(invite));
        inOrder.verify(notificationSender).sendEmailWithContent(notification, to, content);
        inOrder.verify(assessorInviteToSendMapperMock).mapToResource(invite);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void sendInvite_withoutUser() throws Exception {
        String email = "john@email.com";
        String name = "John Barnes";

        CompetitionInvite invite = setUpCompetitionInvite(newCompetition().withName("my competition").build(), email, name, CREATED, null, null);

        EmailContent content = setUpEmailContent();

        Map<String, Object> expectedNotificationArguments = emptyMap();
        SystemNotificationSource from = systemNotificationSourceMock;
        NotificationTarget to = new ExternalUserNotificationTarget(name, email);
        Notification notification = new Notification(from, singletonList(to), CompetitionInviteServiceImpl.Notifications.INVITE_ASSESSOR, expectedNotificationArguments);

        when(competitionInviteRepositoryMock.findOne(invite.getId())).thenReturn(invite);
        when(notificationSender.sendEmailWithContent(notification, to, content)).thenReturn(serviceSuccess(asList(new EmailAddress(email, name))));

        ServiceResult<AssessorInviteToSendResource> serviceResult = service.sendInvite(invite.getId(), content);

        assertTrue(serviceResult.isSuccess());
        assertEquals(SENT, invite.getStatus());

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionParticipantRepositoryMock, notificationSender, assessorInviteToSendMapperMock);
        inOrder.verify(competitionInviteRepositoryMock).findOne(invite.getId());
        inOrder.verify(competitionParticipantRepositoryMock).save(createCompetitionParticipantExpectations(invite));
        inOrder.verify(notificationSender).sendEmailWithContent(notification, to, content);
        inOrder.verify(assessorInviteToSendMapperMock).mapToResource(invite);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void sendInvite_alreadySent() throws Exception {
        long inviteId = 1L;
        CompetitionInvite invite = setUpCompetitionInvite(newCompetition().withName("my competition").build(), SENT);

        when(competitionInviteRepositoryMock.findOne(inviteId)).thenReturn(invite);

        try {
            service.sendInvite(inviteId, setUpEmailContent());
            fail();
        } catch (RuntimeException e) {
            assertSame(IllegalStateException.class, e.getCause().getClass());

            verify(competitionInviteRepositoryMock).findOne(inviteId);
            verifyNoMoreInteractions(competitionInviteRepositoryMock);
        }
    }

    @Test
    public void checkExistingUser_hashNotExists() throws Exception {
        when(competitionInviteRepositoryMock.getByHash(isA(String.class))).thenReturn(null);

        ServiceResult<Boolean> result = service.checkExistingUser("hash");
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(CompetitionInvite.class, "hash")));

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, userServiceMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash("hash");
        inOrder.verify(userServiceMock, never()).findByEmail(isA(String.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void checkExistingUser_userExistsOnInvite() throws Exception {
        User user = newUser().build();

        CompetitionInvite competitionInvite = newCompetitionInvite()
                .withUser(user)
                .withEmail("test@test.com")
                .build();

        when(competitionInviteRepositoryMock.getByHash("hash")).thenReturn(competitionInvite);

        assertTrue(service.checkExistingUser("hash").getSuccessObjectOrThrowException());

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, userServiceMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash("hash");
        inOrder.verify(userServiceMock, never()).findByEmail(isA(String.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void checkExistingUser_userExistsForEmail() throws Exception {
        UserResource user = newUserResource().build();

        CompetitionInvite competitionInvite = newCompetitionInvite()
                .withEmail("test@test.com")
                .build();

        when(competitionInviteRepositoryMock.getByHash("hash")).thenReturn(competitionInvite);
        when(userServiceMock.findByEmail("test@test.com")).thenReturn(serviceSuccess(user));

        assertTrue(service.checkExistingUser("hash").getSuccessObjectOrThrowException());

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, userServiceMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash("hash");
        inOrder.verify(userServiceMock).findByEmail("test@test.com");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void checkExistingUser_userDoesNotExist() throws Exception {
        CompetitionInvite competitionInvite = newCompetitionInvite()
                .withEmail("test@test.com")
                .build();

        when(competitionInviteRepositoryMock.getByHash("hash")).thenReturn(competitionInvite);
        when(userServiceMock.findByEmail("test@test.com")).thenReturn(serviceFailure(notFoundError(UserResource.class, "hash")));

        assertFalse(service.checkExistingUser("hash").getSuccessObjectOrThrowException());

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, userServiceMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash("hash");
        inOrder.verify(userServiceMock).findByEmail("test@test.com");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getAvailableAssessors() throws Exception {
        long competitionId = 1L;

        List<AvailableAssessorResource> expected = newAvailableAssessorResource()
                .withName("Jeremy Alufson")
                .withCompliant(TRUE)
                .withEmail("worth.email.test+assessor1@gmail.com")
                .withBusinessType(BUSINESS)
                .withAdded(FALSE)
                // TODO INFUND-6865 Users should have innovation areas
                .withInnovationArea()
                .build(1);

        Profile profile = newProfile()
                .withSkillsAreas("Java")
                .withBusinessType(BUSINESS)
                .withContractSignedDate(now())
                .build();
        User assessor = newUser()
                .withFirstName("Jeremy")
                .withLastName("Alufson")
                .withEmailAddress("worth.email.test+assessor1@gmail.com")
                .withAffiliations(newAffiliation()
                        .withAffiliationType(EMPLOYER)
                        .withOrganisation("Hive IT")
                        .withPosition("Software Developer")
                        .withExists(true)
                        .build(1))
                .withProfile(profile)
                .build();
        when(userRepositoryMock.findAllAvailableAssessorsByCompetition(competitionId)).thenReturn(singletonList(assessor));
        when(profileRepositoryMock.findOne(profile.getId())).thenReturn(profile);

        List<AvailableAssessorResource> actual = service.getAvailableAssessors(competitionId).getSuccessObjectOrThrowException();
        assertEquals(expected, actual);

        verify(userRepositoryMock, only()).findAllAvailableAssessorsByCompetition(competitionId);
    }

    @Test
    public void getCreatedInvites() throws Exception {
        long competitionId = 1L;

        Category innovationAreaCategory = newCategory().build();
        CategoryResource innovationAreaCategoryResource = newCategoryResource()
                .withName("Earth Observation")
                .build();

        // TODO INFUND-6865 Users should have innovation areas
        Profile profile1 = newProfile()
                .withSkillsAreas("Java")
                .withContractSignedDate(now())
                .build();
        User compliantUser = newUser()
                .withAffiliations(newAffiliation()
                    .withAffiliationType(EMPLOYER)
                    .withOrganisation("Hive IT")
                    .withPosition("Software Developer")
                    .withExists(true)
                    .build(1))
                .withProfile(profile1)
                .build();

        Profile profile2 = newProfile()
                .withSkillsAreas()
                .withContractSignedDate(now())
                .build();
        User nonCompliantUserNoSkills = newUser()
                .withAffiliations(newAffiliation()
                        .withAffiliationType(EMPLOYER)
                        .withOrganisation("Hive IT")
                        .withPosition("Software Developer")
                        .withExists(true)
                        .build(1))
                .withProfile(profile2)
                .build();

        Profile profile3 = newProfile()
                .withSkillsAreas("Java")
                .withContractSignedDate(now())
                .build();
        User nonCompliantUserNoAffiliations = newUser()
                .withAffiliations()
                .withProfile(profile3)
                .build();

        Profile profile4 = newProfile()
                .withSkillsAreas("Java")
                .withContractSignedDate()
                .build();
        User nonCompliantUserNoContract = newUser()
                .withAffiliations(newAffiliation()
                        .withAffiliationType(EMPLOYER)
                        .withOrganisation("Hive IT")
                        .withPosition("Software Developer")
                        .withExists(true)
                        .build(1))
                .withProfile(profile4)
                .build();

        List<CompetitionInvite> existingUserInvites = newCompetitionInvite()
                .withId(1L, 2L, 3L, 4L)
                .withName("John Barnes", "Dave Smith", "Richard Turner", "Oliver Romero")
                .withEmail("john@example.com", "dave@example.com", "richard@example.com", "oliver@example.com")
                .withUser(compliantUser, nonCompliantUserNoSkills, nonCompliantUserNoAffiliations, nonCompliantUserNoContract)
                .withInnovationArea()
                .build(4);

        CompetitionInvite newUserInvite = newCompetitionInvite()
                .withId(5L)
                .withName("Christopher Soames")
                .withEmail("christopher@example.com")
                .withUser()
                .withInnovationArea(innovationAreaCategory)
                .build();

        List<AssessorCreatedInviteResource> expected = newAssessorCreatedInviteResource()
                .withInviteId(1L, 2L, 3L, 4L, 5L)
                .withName("John Barnes", "Dave Smith", "Richard Turner", "Oliver Romero", "Christopher Soames")
                .withInnovationArea(null, null, null, null, innovationAreaCategoryResource)
                .withCompliant(true, false, false, false, false)
                .withEmail("john@example.com", "dave@example.com", "richard@example.com", "oliver@example.com", "christopher@example.com")
                .build(5);

        when(competitionInviteRepositoryMock.getByCompetitionIdAndStatus(competitionId, CREATED)).thenReturn(combineLists(existingUserInvites, newUserInvite));
        when(categoryMapperMock.mapToResource(innovationAreaCategory)).thenReturn(innovationAreaCategoryResource);

        when(profileRepositoryMock.findOne(profile1.getId())).thenReturn(profile1);
        when(profileRepositoryMock.findOne(profile2.getId())).thenReturn(profile2);
        when(profileRepositoryMock.findOne(profile3.getId())).thenReturn(profile3);
        when(profileRepositoryMock.findOne(profile4.getId())).thenReturn(profile4);

        List<AssessorCreatedInviteResource> actual = service.getCreatedInvites(competitionId).getSuccessObjectOrThrowException();
        assertEquals(expected, actual);

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, categoryMapperMock);
        inOrder.verify(competitionInviteRepositoryMock).getByCompetitionIdAndStatus(competitionId, CREATED);
        inOrder.verify(categoryMapperMock).mapToResource(innovationAreaCategory);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInvitationOverview() throws Exception {
        long competitionId = 1L;

        // TODO INFUND-6450

        List<AssessorInviteOverviewResource> actual = service.getInvitationOverview(competitionId).getSuccessObjectOrThrowException();
        assertTrue(actual.isEmpty());
    }

    @Test
    public void inviteUser_existing() {
        User newUser = newUser()
                .withEmailAddress("tom@poly.io")
                .withFirstName("tom")
                .withLastName("baldwin")
                .build();

        Competition competition = newCompetition()
                .withName("competition name")
                .build();

        ExistingUserStagedInviteResource existingAssessor = newExistingUserStagedInviteResource()
                .withCompetitionId(competition.getId())
                .withEmail(newUser.getEmail())
                .build();

        CompetitionInvite competitionInvite = newCompetitionInvite()
                .withCompetition(competition)
                .withHash(Invite.generateInviteHash())
                .withEmail(newUser.getEmail())
                .withName(newUser.getName())
                .build();

        CompetitionInviteResource expectedInviteResource = newCompetitionInviteResource().build();

        when(userRepositoryMock.findByEmail(newUser.getEmail())).thenReturn(Optional.of(newUser));
        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);

        CompetitionInvite inviteExpectation = createInviteExpectations(newUser.getEmail(), newUser.getName(), CREATED, competition, null);

        when(competitionInviteRepositoryMock.save(inviteExpectation)).thenReturn(competitionInvite);
        when(competitionInviteMapperMock.mapToResource(competitionInvite)).thenReturn(expectedInviteResource);

        CompetitionInviteResource invite = service.inviteUser(existingAssessor).getSuccessObjectOrThrowException();

        assertEquals(expectedInviteResource, invite);

        InOrder inOrder = inOrder(userRepositoryMock, competitionRepositoryMock, competitionInviteRepositoryMock, competitionInviteMapperMock);
        inOrder.verify(userRepositoryMock).findByEmail(newUser.getEmail());
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(competitionInviteRepositoryMock).save(createInviteExpectations(newUser.getEmail(), newUser.getName(), CREATED, competition, null));
        inOrder.verify(competitionInviteMapperMock).mapToResource(competitionInvite);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteUser_new() {
        String newAssessorName = "tom baldwin";
        String newAssessorEmail = "tom@poly.io";

        Competition competition = newCompetition().build();

        Category innovationArea = newCategory()
                .withName("machine learning")
                .withType(INNOVATION_AREA)
                .build();

        NewUserStagedInviteResource newAssessor = newNewUserStagedInviteResource()
                .withEmail(newAssessorName)
                .withName(newAssessorEmail)
                .withCompetitionId(competition.getId())
                .withInnovationCategoryId(innovationArea.getId())
                .build();

        CompetitionInvite competitionInvite = newCompetitionInvite()
                .withCompetition(competition)
                .withHash(Invite.generateInviteHash())
                .withEmail(newAssessorName)
                .withName(newAssessorEmail)
                .withInnovationArea(innovationArea)
                .build();

        CompetitionInviteResource expectedInviteResource = newCompetitionInviteResource().build();

        when(categoryRepositoryMock.findByIdAndType(innovationArea.getId(), innovationArea.getType())).thenReturn(innovationArea);
        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);

        CompetitionInvite inviteExpectation = createInviteExpectations(newAssessorName, newAssessorEmail, CREATED, competition, innovationArea);

        when(competitionInviteRepositoryMock.save(inviteExpectation)).thenReturn(competitionInvite);
        when(competitionInviteMapperMock.mapToResource(competitionInvite)).thenReturn(expectedInviteResource);

        CompetitionInviteResource invite = service.inviteUser(newAssessor).getSuccessObjectOrThrowException();

        assertEquals(expectedInviteResource, invite);

        InOrder inOrder = inOrder(categoryRepositoryMock, competitionRepositoryMock, competitionInviteRepositoryMock, competitionInviteMapperMock, userRepositoryMock);
        inOrder.verify(categoryRepositoryMock).findByIdAndType(innovationArea.getId(), innovationArea.getType());
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(competitionInviteRepositoryMock).save(createInviteExpectations(newAssessorName, newAssessorEmail, CREATED, competition, innovationArea));
        inOrder.verify(competitionInviteMapperMock).mapToResource(competitionInvite);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void deleteInvite() {
        String email = "tom@poly.io";
        long competitionId = 11L;

        CompetitionInvite competitionInvite = newCompetitionInvite()
                .withStatus(CREATED)
                .build();

        when(competitionInviteRepositoryMock.getByEmailAndCompetitionId(email, competitionId)).thenReturn(competitionInvite);

        service.deleteInvite(email, competitionId).getSuccessObjectOrThrowException();

        InOrder inOrder = inOrder(competitionInviteRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByEmailAndCompetitionId(email, competitionId);
        inOrder.verify(competitionInviteRepositoryMock).delete(competitionInvite);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void deleteInvite_sent() {
        String email = "tom@poly.io";
        long competitionId = 11L;
        CompetitionInvite competitionInvite = newCompetitionInvite()
                .withStatus(SENT)
                .build();

        when(competitionInviteRepositoryMock.getByEmailAndCompetitionId(email, competitionId)).thenReturn(competitionInvite);

        ServiceResult<Void> serviceResult = service.deleteInvite(email, competitionId);

        assertTrue(serviceResult.isFailure());

        verify(competitionInviteRepositoryMock).getByEmailAndCompetitionId(email, competitionId);
        verifyNoMoreInteractions(competitionInviteRepositoryMock);
    }

    private EmailContent setUpEmailContent() {
        return newEmailContentResource()
                .withSubject("subject")
                .withPlainText("plain")
                .withHtmlText("html")
                .build();
    }

    private CompetitionInvite setUpCompetitionInvite(Competition competition, InviteStatus status) {
        return newCompetitionInvite()
                .withCompetition(competition)
                .withHash(Invite.generateInviteHash())
                .withStatus(status)
                .build();
    }

    private CompetitionInvite setUpCompetitionInvite(Competition competition, String email, String name, InviteStatus status, Category innovationArea, User user) {
        return newCompetitionInvite()
                .withCompetition(competition)
                .withEmail(email)
                .withHash(Invite.generateInviteHash())
                .withInnovationArea(innovationArea)
                .withName(name)
                .withStatus(status)
                .withUser(user)
                .build();
    }

    private CompetitionInvite createInviteExpectations(String name, String email, InviteStatus status, Competition competition, Category innovationArea) {
        return createLambdaMatcher(invite -> {
                    assertEquals(name, invite.getEmail());
                    assertEquals(email, invite.getName());
                    assertEquals(CREATED, invite.getStatus());
                    assertEquals(competition, invite.getTarget());
                    assertFalse(invite.getHash().isEmpty());
                    assertEquals(innovationArea, invite.getInnovationArea());
                }
        );
    }

    private CompetitionParticipant createCompetitionParticipantExpectations(CompetitionInvite competitionInvite) {
        return createLambdaMatcher(competitionParticipant -> {
            assertNull(competitionParticipant.getId());
            assertEquals(competitionInvite.getTarget(), competitionParticipant.getProcess());
            assertEquals(competitionInvite, competitionParticipant.getInvite());
            assertEquals(ASSESSOR, competitionParticipant.getRole());
            assertEquals(competitionInvite.getUser(), competitionParticipant.getUser());
        });
    }
}
