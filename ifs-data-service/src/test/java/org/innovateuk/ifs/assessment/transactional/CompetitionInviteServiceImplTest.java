package org.innovateuk.ifs.assessment.transactional;

import org.assertj.core.util.Lists;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.category.domain.Category;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.email.resource.EmailAddress;
import org.innovateuk.ifs.email.resource.EmailContent;
import org.innovateuk.ifs.invite.builder.RejectionReasonResourceBuilder;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.*;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.assessment.builder.CompetitionInviteBuilder.newCompetitionInvite;
import static org.innovateuk.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static org.innovateuk.ifs.assessment.builder.CompetitionParticipantBuilder.newCompetitionParticipant;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.MilestoneBuilder.newMilestone;
import static org.innovateuk.ifs.competition.resource.MilestoneType.*;
import static org.innovateuk.ifs.email.builders.EmailContentResourceBuilder.newEmailContentResource;
import static org.innovateuk.ifs.invite.builder.AssessorCreatedInviteResourceBuilder.newAssessorCreatedInviteResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewResourceBuilder.newAssessorInviteOverviewResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteToSendResourceBuilder.newAssessorInviteToSendResource;
import static org.innovateuk.ifs.invite.builder.AvailableAssessorResourceBuilder.newAvailableAssessorResource;
import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteResourceBuilder.newExistingUserStagedInviteResource;
import static org.innovateuk.ifs.invite.builder.NewUserStagedInviteResourceBuilder.newNewUserStagedInviteResource;
import static org.innovateuk.ifs.invite.builder.RejectionReasonBuilder.newRejectionReason;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.invite.domain.CompetitionParticipantRole.ASSESSOR;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.*;
import static org.innovateuk.ifs.user.builder.AffiliationBuilder.newAffiliation;
import static org.innovateuk.ifs.user.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.AffiliationType.EMPLOYER;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class CompetitionInviteServiceImplTest extends BaseServiceUnitTest<CompetitionInviteServiceImpl> {
    private static final String UID = "5cc0ac0d-b969-40f5-9cc5-b9bdd98c86de";
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

        UserResource senderResource = newUserResource().withId(-1L).withUID(UID).build();
        User sender = newUser().withId(-1L).withUid(UID).build();
        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(senderResource));
        when(userMapperMock.mapToDomain(senderResource)).thenReturn(sender);

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

        LocalDateTime acceptsDate = LocalDateTime.of(2016, 12, 20, 12, 0, 0);
        LocalDateTime deadlineDate = LocalDateTime.of(2017, 1, 17, 12, 0, 0);

        Competition competition = newCompetition()
                .withName("my competition")
                .withAssessorAcceptsDate(acceptsDate)
                .withAssessorDeadlineDate(deadlineDate)
                .build();

        InnovationArea innovationArea = newInnovationArea().withName("innovation area").build();

        CompetitionInvite invite = setUpCompetitionInvite(competition, email, name, CREATED, innovationArea, null);

        Map<String, Object> expectedNotificationArguments = asMap("name", name,
                "competitionName", "my competition",
                "innovationArea", innovationArea,
                "acceptsDate", acceptsDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
                "deadlineDate", deadlineDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
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
        inOrder.verify(competitionInviteMapperMock).mapToResource(isA(CompetitionInvite.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInvite_hashNotExists() throws Exception {
        when(competitionInviteRepositoryMock.getByHash(isA(String.class))).thenReturn(null);

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
        inOrder.verify(competitionInviteRepositoryMock).save(isA(CompetitionInvite.class));
        inOrder.verify(competitionInviteMapperMock).mapToResource(isA(CompetitionInvite.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void openInvite_hashNotExists() throws Exception {
        when(competitionInviteRepositoryMock.getByHash(isA(String.class))).thenReturn(null);

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

        when(competitionInviteRepositoryMock.getByHash(isA(String.class))).thenReturn(competitionInvite);

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

    @Test
    public void acceptInvite() {
        service.openInvite("inviteHash");

        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());
        assertNull(competitionParticipant.getUser());

        ServiceResult<Void> serviceResult = service.acceptInvite("inviteHash", userResource);

        assertTrue(serviceResult.isSuccess());
        assertEquals(ACCEPTED, competitionParticipant.getStatus());
        assertEquals(user, competitionParticipant.getUser());

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock, userRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(7L);
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash("inviteHash");
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

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, userRepositoryMock, competitionParticipantRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(userResource.getId());
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
        assertEquals(ACCEPTED, competitionParticipant.getStatus());

        // accept a second time
        serviceResult = service.acceptInvite("inviteHash", userResource);

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(new Error(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_ALREADY_ACCEPTED_INVITE, "my competition")));

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, userRepositoryMock, competitionParticipantRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash("inviteHash");
        inOrder.verify(userRepositoryMock).findOne(userResource.getId());
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash("inviteHash");
        inOrder.verify(userRepositoryMock).findOne(userResource.getId());
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash("inviteHash");
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

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, rejectionReasonRepositoryMock, userRepositoryMock, competitionParticipantRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash("inviteHash");
        inOrder.verify(rejectionReasonRepositoryMock).findOne(1L);
        inOrder.verify(competitionParticipantRepositoryMock, times(2)).getByInviteHash("inviteHash");
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

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionParticipantRepositoryMock, rejectionReasonRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash("inviteHash");
        inOrder.verify(rejectionReasonRepositoryMock).findOne(1L);
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash("inviteHash");

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

        InOrder inOrder = inOrder(rejectionReasonRepositoryMock, competitionParticipantRepositoryMock);
        inOrder.verify(rejectionReasonRepositoryMock).findOne(1L);
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
        assertEquals(ACCEPTED, competitionParticipant.getStatus());

        // reject
        RejectionReasonResource rejectionReasonResource = RejectionReasonResourceBuilder
                .newRejectionReasonResource()
                .withId(1L)
                .build();

        serviceResult = service.rejectInvite("inviteHash", rejectionReasonResource, Optional.of("too busy"));

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(new Error(COMPETITION_PARTICIPANT_CANNOT_REJECT_ALREADY_ACCEPTED_INVITE, "my competition")));

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, userRepositoryMock, competitionParticipantRepositoryMock, rejectionReasonRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash("inviteHash");
        inOrder.verify(userRepositoryMock).findOne(7L);
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash("inviteHash");
        inOrder.verify(rejectionReasonRepositoryMock).findOne(1L);
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash("inviteHash");
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

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionParticipantRepositoryMock, rejectionReasonRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash("inviteHash");
        inOrder.verify(rejectionReasonRepositoryMock).findOne(1L);
        inOrder.verify(competitionParticipantRepositoryMock, times(2)).getByInviteHash("inviteHash");
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

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionParticipantRepositoryMock, rejectionReasonRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash("inviteHash");
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

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionParticipantRepositoryMock, rejectionReasonRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash("inviteHash");
        inOrder.verify(rejectionReasonRepositoryMock).findOne(1L);
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash("inviteHash");

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

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, userRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash("hash");
        inOrder.verify(userRepositoryMock, never()).findByEmail(isA(String.class));
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

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, userRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash("hash");
        inOrder.verify(userRepositoryMock, never()).findByEmail(isA(String.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void checkExistingUser_userExistsForEmail() throws Exception {
        User user = newUser().build();

        CompetitionInvite competitionInvite = newCompetitionInvite()
                .withEmail("test@test.com")
                .build();

        when(competitionInviteRepositoryMock.getByHash("hash")).thenReturn(competitionInvite);
        when(userRepositoryMock.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        assertTrue(service.checkExistingUser("hash").getSuccessObjectOrThrowException());

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, userRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash("hash");
        inOrder.verify(userRepositoryMock).findByEmail("test@test.com");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void checkExistingUser_userDoesNotExist() throws Exception {
        CompetitionInvite competitionInvite = newCompetitionInvite()
                .withEmail("test@test.com")
                .build();

        when(competitionInviteRepositoryMock.getByHash("hash")).thenReturn(competitionInvite);
        when(userRepositoryMock.findByEmail("test@test.com")).thenReturn(Optional.empty());

        assertFalse(service.checkExistingUser("hash").getSuccessObjectOrThrowException());

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, userRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash("hash");
        inOrder.verify(userRepositoryMock).findByEmail("test@test.com");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getAvailableAssessors() throws Exception {
        long competitionId = 1L;

        List<InnovationAreaResource> innovationAreas = newInnovationAreaResource()
                .withName("Emerging Tech and Industries")
                .build(1);

        List<AvailableAssessorResource> expected = newAvailableAssessorResource()
                .withName("Jeremy Alufson")
                .withCompliant(TRUE)
                .withEmail("worth.email.test+assessor1@gmail.com")
                .withBusinessType(BUSINESS)
                .withAdded(FALSE)
                .withInnovationAreas(innovationAreas)
                .build(1);

        InnovationArea innovationArea = newInnovationArea()
                .withName("Emerging Tech and Industries")
                .build();

        Profile profile = newProfile()
                .withSkillsAreas("Java")
                .withInnovationArea(innovationArea)
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
        when(innovationAreaMapperMock.mapToResource(innovationArea)).thenReturn(innovationAreas.get(0));

        List<AvailableAssessorResource> actual = service.getAvailableAssessors(competitionId).getSuccessObjectOrThrowException();
        assertEquals(expected, actual);

        verify(userRepositoryMock, only()).findAllAvailableAssessorsByCompetition(competitionId);
        verify(profileRepositoryMock, times(2)).findOne(profile.getId());
        verify(innovationAreaMapperMock).mapToResource(innovationArea);
    }

    @Test
    public void getCreatedInvites() throws Exception {
        long competitionId = 1L;

        InnovationArea innovationArea = newInnovationArea().build();
        InnovationAreaResource innovationAreaResource = newInnovationAreaResource()
                .withId(2L)
                .withName("Earth Observation")
                .build();
        List<InnovationAreaResource> innovationAreaList = asList(innovationAreaResource);

        Profile profile1 = newProfile()
                .withSkillsAreas("Java")
                .withContractSignedDate(now())
                .withInnovationArea(innovationArea)
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
                .withInnovationArea(innovationArea)
                .build();

        List<AssessorCreatedInviteResource> expected = newAssessorCreatedInviteResource()
                .withInviteId(1L, 2L, 3L, 4L, 5L)
                .withName("John Barnes", "Dave Smith", "Richard Turner", "Oliver Romero", "Christopher Soames")
                .withInnovationAreas(innovationAreaList, emptyList(), emptyList(), emptyList(), innovationAreaList)
                .withCompliant(true, false, false, false, false)
                .withEmail("john@example.com", "dave@example.com", "richard@example.com", "oliver@example.com", "christopher@example.com")
                .build(5);

        when(competitionInviteRepositoryMock.getByCompetitionIdAndStatus(competitionId, CREATED)).thenReturn(combineLists(existingUserInvites, newUserInvite));
        when(innovationAreaMapperMock.mapToResource(innovationArea)).thenReturn(innovationAreaResource);
        when(profileRepositoryMock.findOne(profile1.getId())).thenReturn(profile1);
        when(profileRepositoryMock.findOne(profile2.getId())).thenReturn(profile2);
        when(profileRepositoryMock.findOne(profile3.getId())).thenReturn(profile3);
        when(profileRepositoryMock.findOne(profile4.getId())).thenReturn(profile4);

        List<AssessorCreatedInviteResource> actual = service.getCreatedInvites(competitionId).getSuccessObjectOrThrowException();
        assertEquals(expected, actual);

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, innovationAreaMapperMock);
        inOrder.verify(competitionInviteRepositoryMock).getByCompetitionIdAndStatus(competitionId, CREATED);
        inOrder.verify(innovationAreaMapperMock, times(2)).mapToResource(innovationArea);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInvitationOverview() throws Exception {
        long competitionId = 1L;

        List<InnovationArea> innovationAreas = newInnovationArea()
                .withName("Earth Observation", "Internet of Things", "Data")
                .build(3);
        List<InnovationAreaResource> innovationAreaResources = newInnovationAreaResource()
                .withName("Earth Observation", "Internet of Things", "Data")
                .build(3);

        Profile[] profiles = newProfile()
                .withBusinessType(BUSINESS, ACADEMIC, BUSINESS)
                .withInnovationArea(innovationAreas.get(0), innovationAreas.get(1), innovationAreas.get(2))
                .buildArray(3, Profile.class);
        List<User> users = newUser()
                .withProfile(profiles)
                .build(3);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        LocalDateTime[] sentOn = { LocalDateTime.now(), LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusHours(1)};

        List<CompetitionInvite> invites = newCompetitionInvite()
                .withName("John Barnes", "Dave Smith", "Richard Turner")
                .withSentOn(sentOn[0], sentOn[1], sentOn[2])
                .build(3);

        List<CompetitionParticipant> competitionParticipants = newCompetitionParticipant()
                .withCompetition(newCompetition().build())
                .withUser(users.get(0), users.get(1), users.get(2))
                .withInvite(invites.get(0), invites.get(1), invites.get(2))
                .withStatus(ACCEPTED, REJECTED, PENDING)
                .withRejectionReason(null, newRejectionReason().withReason("Not available").build(), null)
                .build(3);

        List<AssessorInviteOverviewResource> expected = newAssessorInviteOverviewResource()
                .withName("John Barnes", "Dave Smith", "Richard Turner")
                .withBusinessType(BUSINESS, ACADEMIC, BUSINESS)
                .withInnovationAreas(asList(innovationAreaResources.get(0)), asList(innovationAreaResources.get(1)), asList(innovationAreaResources.get(2)))
                .withCompliant(false, false, false)
                .withStatus(ParticipantStatusResource.ACCEPTED, ParticipantStatusResource.REJECTED, ParticipantStatusResource.PENDING)
                .withDetails(null, "Invite declined as not available", "Invite sent: " + sentOn[2].format(formatter))
                .build(3);

        when(profileRepositoryMock.findOne(profiles[0].getId())).thenReturn(profiles[0]);
        when(profileRepositoryMock.findOne(profiles[1].getId())).thenReturn(profiles[1]);
        when(profileRepositoryMock.findOne(profiles[2].getId())).thenReturn(profiles[2]);
        when(competitionParticipantRepositoryMock.getByCompetitionIdAndRole(competitionId, ASSESSOR)).thenReturn(competitionParticipants);
        when(participantStatusMapperMock.mapToResource(ACCEPTED)).thenReturn(ParticipantStatusResource.ACCEPTED);
        when(participantStatusMapperMock.mapToResource(REJECTED)).thenReturn(ParticipantStatusResource.REJECTED);
        when(participantStatusMapperMock.mapToResource(PENDING)).thenReturn(ParticipantStatusResource.PENDING);
        when(innovationAreaMapperMock.mapToResource(innovationAreas.get(0))).thenReturn(innovationAreaResources.get(0));
        when(innovationAreaMapperMock.mapToResource(innovationAreas.get(1))).thenReturn(innovationAreaResources.get(1));
        when(innovationAreaMapperMock.mapToResource(innovationAreas.get(2))).thenReturn(innovationAreaResources.get(2));

        List<AssessorInviteOverviewResource> actual = service.getInvitationOverview(competitionId).getSuccessObjectOrThrowException();
        assertEquals(expected, actual);

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock, participantStatusMapperMock, profileRepositoryMock, innovationAreaMapperMock);
        inOrder.verify(competitionParticipantRepositoryMock).getByCompetitionIdAndRole(competitionId, ASSESSOR);
        inOrder.verify(participantStatusMapperMock, calls(3)).mapToResource(isA(ParticipantStatus.class));
        inOrder.verify(profileRepositoryMock, calls(2)).findOne(isA(Long.class));
        inOrder.verify(innovationAreaMapperMock).mapToResource(isA(InnovationArea.class));

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInvitationOverview_notExistingUsers() throws Exception {
        long competitionId = 1L;

        List<InnovationArea> innovationAreas = newInnovationArea()
                .withName("Earth Observation", "Internet of Things", "Data")
                .build(3);
        List<InnovationAreaResource> innovationAreaResources = newInnovationAreaResource()
                .withName("Earth Observation", "Internet of Things", "Data")
                .build(3);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        LocalDateTime[] sentOn = { LocalDateTime.now(), LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusHours(1)};

        CompetitionInvite[] invites = newCompetitionInvite()
                .withName("John Barnes", "Dave Smith", "Richard Turner")
                .withSentOn(sentOn[0], sentOn[1], sentOn[2])
                .withInnovationArea(innovationAreas.get(0), innovationAreas.get(1), innovationAreas.get(2))
                .buildArray(3, CompetitionInvite.class);

        List<CompetitionParticipant> competitionParticipants = newCompetitionParticipant()
                .withCompetition(newCompetition().build())
                .withInvite(invites)
                .withStatus(ACCEPTED, REJECTED, PENDING)
                .withRejectionReason(null, newRejectionReason().withReason("Not available").build(), null)
                .build(3);

        List<AssessorInviteOverviewResource> expected = newAssessorInviteOverviewResource()
                .withName("John Barnes", "Dave Smith", "Richard Turner")
                .withInnovationAreas(asList(innovationAreaResources.get(0)), asList(innovationAreaResources.get(1)), asList(innovationAreaResources.get(2)))
                .withCompliant(false, false, false)
                .withStatus(ParticipantStatusResource.ACCEPTED, ParticipantStatusResource.REJECTED, ParticipantStatusResource.PENDING)
                .withDetails(null, "Invite declined as not available", "Invite sent: " + sentOn[2].format(formatter))
                .build(3);

        when(competitionParticipantRepositoryMock.getByCompetitionIdAndRole(competitionId, ASSESSOR)).thenReturn(competitionParticipants);
        when(participantStatusMapperMock.mapToResource(ACCEPTED)).thenReturn(ParticipantStatusResource.ACCEPTED);
        when(participantStatusMapperMock.mapToResource(REJECTED)).thenReturn(ParticipantStatusResource.REJECTED);
        when(participantStatusMapperMock.mapToResource(PENDING)).thenReturn(ParticipantStatusResource.PENDING);
        when(innovationAreaMapperMock.mapToResource(innovationAreas.get(0))).thenReturn(innovationAreaResources.get(0));
        when(innovationAreaMapperMock.mapToResource(innovationAreas.get(1))).thenReturn(innovationAreaResources.get(1));
        when(innovationAreaMapperMock.mapToResource(innovationAreas.get(2))).thenReturn(innovationAreaResources.get(2));

        List<AssessorInviteOverviewResource> actual = service.getInvitationOverview(competitionId).getSuccessObjectOrThrowException();
        assertEquals(expected, actual);

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock, participantStatusMapperMock, innovationAreaMapperMock);
        inOrder.verify(competitionParticipantRepositoryMock).getByCompetitionIdAndRole(competitionId, ASSESSOR);
        inOrder.verify(participantStatusMapperMock, calls(3)).mapToResource(isA(ParticipantStatus.class));
        inOrder.verify(innovationAreaMapperMock).mapToResource(isA(InnovationArea.class));

        inOrder.verifyNoMoreInteractions();
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

        CompetitionInvite inviteExpectation = createInviteExpectations(newUser.getName(), newUser.getEmail(), CREATED, competition, null);

        when(competitionInviteRepositoryMock.save(inviteExpectation)).thenReturn(competitionInvite);
        when(competitionInviteMapperMock.mapToResource(competitionInvite)).thenReturn(expectedInviteResource);

        CompetitionInviteResource invite = service.inviteUser(existingAssessor).getSuccessObjectOrThrowException();

        assertEquals(expectedInviteResource, invite);

        InOrder inOrder = inOrder(userRepositoryMock, competitionRepositoryMock, competitionInviteRepositoryMock, competitionInviteMapperMock);
        inOrder.verify(userRepositoryMock).findByEmail(newUser.getEmail());
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(competitionInviteRepositoryMock).save(createInviteExpectations(newUser.getName(), newUser.getEmail(), CREATED, competition, null));
        inOrder.verify(competitionInviteMapperMock).mapToResource(competitionInvite);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteUser_new() {
        String newAssessorName = "tom baldwin";
        String newAssessorEmail = "tom@poly.io";

        Competition competition = newCompetition().build();

        InnovationArea innovationArea = newInnovationArea()
                .withName("machine learning")
                .build();

        NewUserStagedInviteResource newAssessor = newNewUserStagedInviteResource()
                .withName(newAssessorName)
                .withEmail(newAssessorEmail)
                .withCompetitionId(competition.getId())
                .withInnovationCategoryId(innovationArea.getId())
                .build();

        CompetitionInvite competitionInvite = newCompetitionInvite()
                .withCompetition(competition)
                .withHash(Invite.generateInviteHash())
                .withName(newAssessorName)
                .withEmail(newAssessorEmail)
                .withInnovationArea(innovationArea)
                .build();

        CompetitionInviteResource expectedInviteResource = newCompetitionInviteResource().build();

        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(innovationAreaRepositoryMock.findOne(innovationArea.getId())).thenReturn(innovationArea);
        when(competitionInviteRepositoryMock.getByEmailAndCompetitionId(newAssessorEmail, competition.getId())).thenReturn(null);

        CompetitionInvite inviteExpectation = createInviteExpectations(newAssessorName, newAssessorEmail, CREATED, competition, innovationArea);
        when(competitionInviteRepositoryMock.save(inviteExpectation)).thenReturn(competitionInvite);
        when(competitionInviteMapperMock.mapToResource(competitionInvite)).thenReturn(expectedInviteResource);

        ServiceResult<CompetitionInviteResource> serviceResult = service.inviteUser(newAssessor);
        assertTrue(serviceResult.isSuccess());

        CompetitionInviteResource invite = serviceResult.getSuccessObjectOrThrowException();
        assertEquals(expectedInviteResource, invite);

        InOrder inOrder = inOrder(innovationAreaRepositoryMock, competitionRepositoryMock, competitionInviteRepositoryMock, competitionInviteMapperMock, userRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByEmailAndCompetitionId(newAssessorEmail, competition.getId());
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(innovationAreaRepositoryMock).findOne(innovationArea.getId());
        inOrder.verify(competitionInviteRepositoryMock).save(createInviteExpectations(newAssessorName, newAssessorEmail, CREATED, competition, innovationArea));
        inOrder.verify(competitionInviteMapperMock).mapToResource(competitionInvite);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteUser_new_categoryNotFound() {
        String newAssessorName = "tom baldwin";
        String newAssessorEmail = "tom@poly.io";
        long innovationArea = 100L;

        Competition competition = newCompetition().build();

        NewUserStagedInviteResource newAssessor = newNewUserStagedInviteResource()
                .withEmail(newAssessorEmail)
                .withName(newAssessorName)
                .withCompetitionId(competition.getId())
                .withInnovationCategoryId(innovationArea)
                .build();

        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(innovationAreaRepositoryMock.findOne(innovationArea)).thenReturn(null);
        when(competitionInviteRepositoryMock.getByEmailAndCompetitionId(newAssessorEmail, competition.getId())).thenReturn(null);

        ServiceResult<CompetitionInviteResource> serviceResult = service.inviteUser(newAssessor);

        assertFalse(serviceResult.isSuccess());

        InOrder inOrder = inOrder(innovationAreaRepositoryMock, competitionRepositoryMock, competitionInviteRepositoryMock, competitionInviteMapperMock, userRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByEmailAndCompetitionId(newAssessorEmail, competition.getId());
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(innovationAreaRepositoryMock).findOne(innovationArea);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteUser_new_alreadyExists() {
        String newAssessorName = "tom baldwin";
        String newAssessorEmail = "tom@poly.io";

        Competition competition = newCompetition().build();

        InnovationArea innovationArea = newInnovationArea()
                .withName("machine learning")
                .build();

        NewUserStagedInviteResource newAssessor = newNewUserStagedInviteResource()
                .withEmail(newAssessorEmail)
                .withName(newAssessorName)
                .withCompetitionId(competition.getId())
                .withInnovationCategoryId(innovationArea.getId())
                .build();

        when(competitionInviteRepositoryMock.getByEmailAndCompetitionId(newAssessorEmail, competition.getId())).thenReturn(new CompetitionInvite());

        ServiceResult<CompetitionInviteResource> serviceResult = service.inviteUser(newAssessor);

        assertFalse(serviceResult.isSuccess());

        InOrder inOrder = inOrder(competitionRepositoryMock, competitionInviteRepositoryMock, competitionInviteMapperMock, userRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByEmailAndCompetitionId(newAssessorEmail, competition.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteUser_new_competitionNotFound() {
        String newAssessorName = "tom baldwin";
        String newAssessorEmail = "tom@poly.io";

        long competitionId = 10L;

        InnovationArea innovationArea = newInnovationArea()
                .withName("machine learning")
                .build();

        NewUserStagedInviteResource newAssessor = newNewUserStagedInviteResource()
                .withEmail(newAssessorEmail)
                .withName(newAssessorName)
                .withCompetitionId(competitionId)
                .withInnovationCategoryId(innovationArea.getId())
                .build();

        when(competitionInviteRepositoryMock.getByEmailAndCompetitionId(newAssessorEmail, competitionId)).thenReturn(null);
        when(competitionRepositoryMock.findOne(competitionId)).thenReturn(null);

        ServiceResult<CompetitionInviteResource> serviceResult = service.inviteUser(newAssessor);

        assertFalse(serviceResult.isSuccess());

        InOrder inOrder = inOrder(competitionRepositoryMock, competitionInviteRepositoryMock, competitionInviteMapperMock, userRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByEmailAndCompetitionId(newAssessorEmail, competitionId);
        inOrder.verify(competitionRepositoryMock).findOne(competitionId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteNewUsers() throws Exception {
        Competition competition = newCompetition().build();

        InnovationArea innovationArea = newInnovationArea()
                .withName("machine learning")
                .build();

        String testName1 = "Test Name A";
        String testName2 = "Test Name B";
        String testEmail1 = "test1@test.com";
        String testEmail2 = "test2@test.com";

        List<NewUserStagedInviteResource> newUserInvites = newNewUserStagedInviteResource()
                .withName(testName1, testName2)
                .withEmail(testEmail1, testEmail2)
                .withInnovationCategoryId(innovationArea.getId())
                .withCompetitionId(competition.getId())
                .build(2);

        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(competitionInviteRepositoryMock.getByEmailAndCompetitionId(isA(String.class), isA(Long.class))).thenReturn(null);
        when(innovationAreaRepositoryMock.findOne(innovationArea.getId())).thenReturn(innovationArea);
        when(competitionInviteRepositoryMock.save(isA(CompetitionInvite.class))).thenReturn(new CompetitionInvite());

        ServiceResult<Void> serviceResult = service.inviteNewUsers(newUserInvites, competition.getId());

        assertTrue(serviceResult.isSuccess());

        InOrder inOrder = inOrder(competitionRepositoryMock, competitionInviteRepositoryMock, innovationAreaRepositoryMock);
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(competitionInviteRepositoryMock).getByEmailAndCompetitionId(testEmail1, competition.getId());
        inOrder.verify(innovationAreaRepositoryMock).findOne(innovationArea.getId());
        inOrder.verify(competitionInviteRepositoryMock).save(createInviteExpectations(testName1, testEmail1, CREATED, competition, innovationArea));
        inOrder.verify(competitionInviteRepositoryMock).getByEmailAndCompetitionId(testEmail2, competition.getId());
        inOrder.verify(innovationAreaRepositoryMock).findOne(innovationArea.getId());
        inOrder.verify(competitionInviteRepositoryMock).save(createInviteExpectations(testName2, testEmail2, CREATED, competition, innovationArea));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteNewUsers_alreadyExists() throws Exception {
        Competition competition = newCompetition().build();

        InnovationArea innovationArea = newInnovationArea()
                .withName("machine learning")
                .build();

        String testName1 = "Test Name A";
        String testName2 = "Test Name B";
        String testEmail1 = "test1@test.com";
        String testEmail2 = "test2@test.com";

        List<NewUserStagedInviteResource> newUserInvites = newNewUserStagedInviteResource()
                .withName(testName1, testName2)
                .withEmail(testEmail1, testEmail2)
                .withInnovationCategoryId(innovationArea.getId())
                .withCompetitionId(competition.getId())
                .build(2);

        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(competitionInviteRepositoryMock.getByEmailAndCompetitionId(testEmail1, competition.getId()))
                .thenReturn(new CompetitionInvite());
        when(competitionInviteRepositoryMock.getByEmailAndCompetitionId(testEmail2, competition.getId())).thenReturn(null);

        when(innovationAreaRepositoryMock.findOne(innovationArea.getId())).thenReturn(innovationArea);
        when(competitionInviteRepositoryMock.save(isA(CompetitionInvite.class))).thenReturn(new CompetitionInvite());

        ServiceResult<Void> serviceResult = service.inviteNewUsers(newUserInvites, competition.getId());

        assertFalse(serviceResult.isSuccess());
        assertEquals(1, serviceResult.getErrors().size());
        assertEquals("test1@test.com", serviceResult.getErrors().get(0).getFieldRejectedValue());

        InOrder inOrder = inOrder(competitionRepositoryMock, competitionInviteRepositoryMock, innovationAreaRepositoryMock);
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(competitionInviteRepositoryMock).getByEmailAndCompetitionId(testEmail1, competition.getId());
        inOrder.verify(competitionInviteRepositoryMock).getByEmailAndCompetitionId(testEmail2, competition.getId());
        inOrder.verify(innovationAreaRepositoryMock).findOne(innovationArea.getId());
        inOrder.verify(competitionInviteRepositoryMock).save(createInviteExpectations(testName2, testEmail2, CREATED, competition, innovationArea));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteNewUsers_competitionNotFound() throws Exception {
        long competitionId = 5L;

        InnovationArea innovationArea = newInnovationArea()
                .withName("machine learning")
                .build();

        String testName1 = "Test Name A";
        String testName2 = "Test Name B";
        String testEmail1 = "test1@test.com";
        String testEmail2 = "test2@test.com";

        List<NewUserStagedInviteResource> newUserInvites = newNewUserStagedInviteResource()
                .withName(testName1, testName2)
                .withEmail(testEmail1, testEmail2)
                .withInnovationCategoryId(innovationArea.getId())
                .withCompetitionId(competitionId)
                .build(2);

        when(competitionRepositoryMock.findOne(competitionId)).thenReturn(null);
        when(competitionInviteRepositoryMock.getByEmailAndCompetitionId(isA(String.class), isA(Long.class))).thenReturn(null);

        ServiceResult<Void> serviceResult = service.inviteNewUsers(newUserInvites, competitionId);

        assertFalse(serviceResult.isSuccess());

        InOrder inOrder = inOrder(competitionRepositoryMock, competitionInviteRepositoryMock);
        inOrder.verify(competitionRepositoryMock).findOne(competitionId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteNewUsers_categoryNotFound() throws Exception {
        Competition competition = newCompetition().build();
        long categoryId = 2L;

        String testName1 = "Test Name A";
        String testName2 = "Test Name B";
        String testEmail1 = "test1@test.com";
        String testEmail2 = "test2@test.com";

        List<NewUserStagedInviteResource> newUserInvites = newNewUserStagedInviteResource()
                .withName(testName1, testName2)
                .withEmail(testEmail1, testEmail2)
                .withInnovationCategoryId(categoryId)
                .withCompetitionId(competition.getId())
                .build(2);

        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(competitionInviteRepositoryMock.getByEmailAndCompetitionId(isA(String.class), isA(Long.class))).thenReturn(null);
        when(innovationAreaRepositoryMock.findOne(categoryId)).thenReturn(null);
        when(competitionInviteRepositoryMock.save(isA(CompetitionInvite.class))).thenReturn(new CompetitionInvite());

        ServiceResult<Void> serviceResult = service.inviteNewUsers(newUserInvites, competition.getId());

        assertFalse(serviceResult.isSuccess());

        InOrder inOrder = inOrder(competitionRepositoryMock, competitionInviteRepositoryMock, innovationAreaRepositoryMock);
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(competitionInviteRepositoryMock).getByEmailAndCompetitionId(testEmail1, competition.getId());
        inOrder.verify(innovationAreaRepositoryMock).findOne(categoryId);
        inOrder.verify(competitionInviteRepositoryMock).getByEmailAndCompetitionId(testEmail2, competition.getId());
        inOrder.verify(innovationAreaRepositoryMock).findOne(categoryId);

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

    private CompetitionInvite setUpCompetitionInvite(Competition competition, String email, String name, InviteStatus status, InnovationArea innovationArea, User user) {
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
                    assertEquals(name, invite.getName());
                    assertEquals(email, invite.getEmail());
                    assertEquals(status, invite.getStatus());
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
