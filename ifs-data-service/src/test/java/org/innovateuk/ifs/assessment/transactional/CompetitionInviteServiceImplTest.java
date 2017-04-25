package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.category.domain.Category;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.invite.builder.RejectionReasonResourceBuilder;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.*;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.notifications.resource.ExternalUserNotificationTarget;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static java.time.ZonedDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.Matchers.containsString;
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
import static org.innovateuk.ifs.invite.builder.AssessorCreatedInviteResourceBuilder.newAssessorCreatedInviteResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteSendResourceBuilder.newAssessorInviteSendResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteToSendResourceBuilder.newAssessorInviteToSendResource;
import static org.innovateuk.ifs.invite.builder.AvailableAssessorPageResourceBuilder.newAvailableAssessorPageResource;
import static org.innovateuk.ifs.invite.builder.AvailableAssessorResourceBuilder.newAvailableAssessorResource;
import static org.innovateuk.ifs.invite.builder.CompetitionInviteStatisticsResourceBuilder.newCompetitionInviteStatisticsResource;
import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteResourceBuilder.newExistingUserStagedInviteResource;
import static org.innovateuk.ifs.invite.builder.NewUserStagedInviteResourceBuilder.newNewUserStagedInviteResource;
import static org.innovateuk.ifs.invite.builder.RejectionReasonBuilder.newRejectionReason;
import static org.innovateuk.ifs.invite.constant.InviteStatus.*;
import static org.innovateuk.ifs.invite.domain.CompetitionParticipantRole.ASSESSOR;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.*;
import static org.innovateuk.ifs.user.builder.AffiliationBuilder.newAffiliation;
import static org.innovateuk.ifs.user.builder.AgreementBuilder.newAgreement;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.AffiliationType.EMPLOYER;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.Direction.ASC;

public class CompetitionInviteServiceImplTest extends BaseServiceUnitTest<CompetitionInviteServiceImpl> {
    private static final String UID = "5cc0ac0d-b969-40f5-9cc5-b9bdd98c86de";
    private static final String INVITE_HASH = "inviteHash";

    private CompetitionParticipant competitionParticipant;
    private UserResource userResource;
    private User user;
    private Profile profile;
    private InnovationArea innovationArea;
    private Role assessorRole;
    private Role applicantRole;

    @Override
    protected CompetitionInviteServiceImpl supplyServiceUnderTest() {
        return new CompetitionInviteServiceImpl();
    }

    @Before
    public void setUp() {
        long userId = 7L;
        long profileId = 11L;

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

        innovationArea = newInnovationArea().build();
        CompetitionInvite competitionInvite = setUpCompetitionInvite(competition, SENT, innovationArea);

        competitionParticipant = new CompetitionParticipant(competitionInvite);
        CompetitionInviteResource expected = newCompetitionInviteResource().withCompetitionName("my competition").build();
        RejectionReason rejectionReason = newRejectionReason().withId(1L).withReason("not available").build();
        userResource = newUserResource().withId(userId).build();
        profile = newProfile().withId(profileId).build();
        user = newUser().withId(userId).withProfileId(profile.getId()).build();

        assessorRole = newRole().withName(UserRoleType.ASSESSOR.getName()).build();
        applicantRole = newRole().withName(UserRoleType.APPLICANT.getName()).build();

        UserResource senderResource = newUserResource().withId(-1L).withUID(UID).build();
        User sender = newUser().withId(-1L).withUid(UID).build();
        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(senderResource));
        when(userMapperMock.mapToDomain(senderResource)).thenReturn(sender);

        when(competitionInviteRepositoryMock.getByHash(INVITE_HASH)).thenReturn(competitionInvite);

        when(competitionInviteRepositoryMock.save(same(competitionInvite))).thenReturn(competitionInvite);
        when(competitionInviteMapperMock.mapToResource(same(competitionInvite))).thenReturn(expected);

        when(competitionParticipantRepositoryMock.getByInviteHash(INVITE_HASH)).thenReturn(competitionParticipant);

        when(rejectionReasonRepositoryMock.findOne(rejectionReason.getId())).thenReturn(rejectionReason);

        when(userRepositoryMock.findOne(userId)).thenReturn(user);
        when(profileRepositoryMock.findOne(user.getProfileId())).thenReturn(profile);

        when(loggedInUserSupplierMock.get()).thenReturn(newUser().build());

        ReflectionTestUtils.setField(service, "webBaseUrl", "https://ifs-local-dev");
    }

    @Test
    public void getCreatedInvite() throws Exception {
        String email = "john@email.com";
        String name = "John Barnes";

        ZonedDateTime acceptsDate = ZonedDateTime.of(2016, 12, 20, 12, 0,0,0, ZoneId.systemDefault());
        ZonedDateTime deadlineDate = ZonedDateTime.of(2017, 1, 17, 12, 0,0,0, ZoneId.systemDefault());

        Competition competition = newCompetition()
                .withName("my competition")
                .withAssessorAcceptsDate(acceptsDate)
                .withAssessorDeadlineDate(deadlineDate)
                .build();

        CompetitionInvite invite = setUpCompetitionInvite(competition, email, name, CREATED, innovationArea, null);

        Map<String, Object> expectedNotificationArguments = asMap("name", name,
                "competitionName", "my competition",
                "acceptsDate", acceptsDate.format(ofPattern("dd MMMM yyyy")),
                "deadlineDate", deadlineDate.format(ofPattern("dd MMMM yyyy")),
                "inviteUrl", format("%s/invite/competition/%s", "https://ifs-local-dev/assessment", invite.getHash()));

        NotificationTarget notificationTarget = new ExternalUserNotificationTarget(name, email);

        String templatePath = "invite_assessor_editable_text.txt";

        when(competitionInviteRepositoryMock.findOne(invite.getId())).thenReturn(invite);
        when(assessorInviteToSendMapperMock.mapToResource(invite)).thenReturn(newAssessorInviteToSendResource().build());
        when(notificationTemplateRendererMock.renderTemplate(systemNotificationSourceMock, notificationTarget, templatePath,
                expectedNotificationArguments)).thenReturn(serviceSuccess("content"));

        AssessorInviteToSendResource expectedAssessorInviteToSendResource = newAssessorInviteToSendResource()
                .withContent("content")
                .build();

        AssessorInviteToSendResource result = service.getCreatedInvite(invite.getId()).getSuccessObjectOrThrowException();
        assertEquals(expectedAssessorInviteToSendResource, result);

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, notificationTemplateRendererMock, assessorInviteToSendMapperMock);
        inOrder.verify(competitionInviteRepositoryMock).findOne(invite.getId());
        inOrder.verify(assessorInviteToSendMapperMock).mapToResource(invite);
        inOrder.verify(notificationTemplateRendererMock).renderTemplate(systemNotificationSourceMock, notificationTarget,
                templatePath, expectedNotificationArguments);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getCreatedInvite_notCreated() throws Exception {
        CompetitionInvite competitionInvite = setUpCompetitionInvite(newCompetition().withName("my competition").build(), SENT, innovationArea);

        when(competitionInviteRepositoryMock.findOne(competitionInvite.getId())).thenReturn(competitionInvite);

        ServiceResult<AssessorInviteToSendResource> inviteServiceResult = service.getCreatedInvite(competitionInvite.getId());

        assertTrue(inviteServiceResult.isFailure());
        assertTrue(inviteServiceResult.getFailure().is(new Error(COMPETITION_INVITE_ALREADY_SENT, "my competition")));

        verify(competitionInviteRepositoryMock, only()).findOne(competitionInvite.getId());
    }

    @Test
    public void getInvite() throws Exception {
        ServiceResult<CompetitionInviteResource> inviteServiceResult = service.getInvite(INVITE_HASH);

        assertTrue(inviteServiceResult.isSuccess());

        CompetitionInviteResource competitionInviteResource = inviteServiceResult.getSuccessObjectOrThrowException();
        assertEquals("my competition", competitionInviteResource.getCompetitionName());

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionInviteMapperMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash(INVITE_HASH);
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
        service.openInvite(INVITE_HASH);
        ServiceResult<Void> acceptResult = service.acceptInvite(INVITE_HASH, userResource);
        assertTrue(acceptResult.isSuccess());

        ServiceResult<CompetitionInviteResource> getResult = service.getInvite(INVITE_HASH);
        assertTrue(getResult.isFailure());
        assertTrue(getResult.getFailure().is(new Error(COMPETITION_INVITE_CLOSED, "my competition")));

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionParticipantRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(competitionInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash(INVITE_HASH);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInvite_afterRejected() throws Exception {
        RejectionReasonResource rejectionReason = RejectionReasonResourceBuilder.newRejectionReasonResource()
                .withId(1L)
                .build();

        service.openInvite(INVITE_HASH);
        ServiceResult<Void> rejectResult = service.rejectInvite(INVITE_HASH, rejectionReason, of("no time"));
        assertTrue(rejectResult.isSuccess());

        ServiceResult<CompetitionInviteResource> getResult = service.getInvite(INVITE_HASH);
        assertTrue(getResult.isFailure());
        assertTrue(getResult.getFailure().is(new Error(COMPETITION_INVITE_CLOSED, "my competition")));

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionParticipantRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(competitionInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash(INVITE_HASH);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void openInvite() throws Exception {
        ServiceResult<CompetitionInviteResource> inviteServiceResult = service.openInvite(INVITE_HASH);

        assertTrue(inviteServiceResult.isSuccess());

        CompetitionInviteResource competitionInviteResource = inviteServiceResult.getSuccessObjectOrThrowException();
        assertEquals("my competition", competitionInviteResource.getCompetitionName());

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionInviteMapperMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash(INVITE_HASH);
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
                .build(), SENT, innovationArea);

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
        service.openInvite(INVITE_HASH);
        ServiceResult<Void> acceptResult = service.acceptInvite(INVITE_HASH, userResource);
        assertTrue(acceptResult.isSuccess());

        ServiceResult<CompetitionInviteResource> getResult = service.openInvite(INVITE_HASH);
        assertTrue(getResult.isFailure());
        assertTrue(getResult.getFailure().is(new Error(COMPETITION_INVITE_CLOSED, "my competition")));

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionParticipantRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(competitionInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash(INVITE_HASH);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void openInvite_afterRejected() throws Exception {
        RejectionReasonResource rejectionReason = RejectionReasonResourceBuilder.newRejectionReasonResource()
                .withId(1L)
                .build();

        service.openInvite(INVITE_HASH);
        ServiceResult<Void> rejectResult = service.rejectInvite(INVITE_HASH, rejectionReason, of("no time"));
        assertTrue(rejectResult.isSuccess());

        ServiceResult<CompetitionInviteResource> getResult = service.openInvite(INVITE_HASH);
        assertTrue(getResult.isFailure());
        assertTrue(getResult.getFailure().is(new Error(COMPETITION_INVITE_CLOSED, "my competition")));

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionParticipantRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(competitionInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash(INVITE_HASH);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptInvite() {
        service.openInvite(INVITE_HASH);

        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());
        assertNull(competitionParticipant.getUser());

        service.acceptInvite(INVITE_HASH, userResource).getSuccessObjectOrThrowException();

        assertEquals(ACCEPTED, competitionParticipant.getStatus());
        assertEquals(user, competitionParticipant.getUser());

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock, userRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(7L);
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash(INVITE_HASH);
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

        ServiceResult<Void> serviceResult = service.acceptInvite(INVITE_HASH, userResource);

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(new Error(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_UNOPENED_INVITE, "my competition")));

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, userRepositoryMock, competitionParticipantRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(userResource.getId());
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash(INVITE_HASH);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptInvite_alreadyAccepted() {
        service.openInvite(INVITE_HASH);

        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());

        // accept the invite
        ServiceResult<Void> serviceResult = service.acceptInvite(INVITE_HASH, userResource);
        assertTrue(serviceResult.isSuccess());
        assertEquals(ACCEPTED, competitionParticipant.getStatus());

        // accept a second time
        serviceResult = service.acceptInvite(INVITE_HASH, userResource);

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(new Error(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_ALREADY_ACCEPTED_INVITE, "my competition")));

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, userRepositoryMock, competitionParticipantRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(userRepositoryMock).findOne(userResource.getId());
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash(INVITE_HASH);
        inOrder.verify(userRepositoryMock).findOne(userResource.getId());
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash(INVITE_HASH);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptInvite_alreadyRejected() {
        service.openInvite(INVITE_HASH);

        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());

        // reject the invite
        RejectionReasonResource rejectionReasonResource = RejectionReasonResourceBuilder
                .newRejectionReasonResource()
                .withId(1L)
                .build();

        ServiceResult<Void> serviceResult = service.rejectInvite(INVITE_HASH, rejectionReasonResource, of("too busy"));
        assertTrue(serviceResult.isSuccess());
        assertEquals(ParticipantStatus.REJECTED, competitionParticipant.getStatus());

        // accept the invite
        serviceResult = service.acceptInvite(INVITE_HASH, userResource);

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(new Error(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_ALREADY_REJECTED_INVITE, "my competition")));

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, rejectionReasonRepositoryMock, userRepositoryMock, competitionParticipantRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(rejectionReasonRepositoryMock).findOne(1L);
        inOrder.verify(competitionParticipantRepositoryMock, times(2)).getByInviteHash(INVITE_HASH);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvite() {
        service.openInvite(INVITE_HASH);

        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());

        RejectionReasonResource rejectionReasonResource = RejectionReasonResourceBuilder
                .newRejectionReasonResource()
                .withId(1L)
                .build();

        ServiceResult<Void> serviceResult = service.rejectInvite(INVITE_HASH, rejectionReasonResource, of("too busy"));

        assertTrue(serviceResult.isSuccess());
        assertEquals(ParticipantStatus.REJECTED, competitionParticipant.getStatus());
        assertEquals("too busy", competitionParticipant.getRejectionReasonComment());

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionParticipantRepositoryMock, rejectionReasonRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(rejectionReasonRepositoryMock).findOne(1L);
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash(INVITE_HASH);

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

        ServiceResult<Void> serviceResult = service.rejectInvite("inviteHashNotExists", rejectionReasonResource, of("too busy"));

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

        ServiceResult<Void> serviceResult = service.rejectInvite(INVITE_HASH, rejectionReasonResource, of("too busy"));

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(new Error(COMPETITION_PARTICIPANT_CANNOT_REJECT_UNOPENED_INVITE, "my competition")));

        InOrder inOrder = inOrder(rejectionReasonRepositoryMock, competitionParticipantRepositoryMock);
        inOrder.verify(rejectionReasonRepositoryMock).findOne(1L);
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash(INVITE_HASH);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvite_alreadyAccepted() {
        service.openInvite(INVITE_HASH);

        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());

        // accept the invite
        ServiceResult<Void> serviceResult = service.acceptInvite(INVITE_HASH, userResource);
        assertTrue(serviceResult.isSuccess());
        assertEquals(ACCEPTED, competitionParticipant.getStatus());

        // reject
        RejectionReasonResource rejectionReasonResource = RejectionReasonResourceBuilder
                .newRejectionReasonResource()
                .withId(1L)
                .build();

        serviceResult = service.rejectInvite(INVITE_HASH, rejectionReasonResource, of("too busy"));

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(new Error(COMPETITION_PARTICIPANT_CANNOT_REJECT_ALREADY_ACCEPTED_INVITE, "my competition")));

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, userRepositoryMock, competitionParticipantRepositoryMock, rejectionReasonRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(userRepositoryMock).findOne(7L);
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash(INVITE_HASH);
        inOrder.verify(rejectionReasonRepositoryMock).findOne(1L);
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash(INVITE_HASH);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvite_alreadyRejected() {
        service.openInvite(INVITE_HASH);

        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());

        // reject the invite
        RejectionReasonResource rejectionReasonResource = RejectionReasonResourceBuilder
                .newRejectionReasonResource()
                .withId(1L)
                .build();
        ServiceResult<Void> serviceResult = service.rejectInvite(INVITE_HASH, rejectionReasonResource, of("too busy"));
        assertTrue(serviceResult.isSuccess());
        assertEquals(ParticipantStatus.REJECTED, competitionParticipant.getStatus());

        // reject again

        serviceResult = service.rejectInvite(INVITE_HASH, rejectionReasonResource, of("still too busy"));

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(new Error(COMPETITION_PARTICIPANT_CANNOT_REJECT_ALREADY_REJECTED_INVITE, "my competition")));

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionParticipantRepositoryMock, rejectionReasonRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(rejectionReasonRepositoryMock).findOne(1L);
        inOrder.verify(competitionParticipantRepositoryMock, times(2)).getByInviteHash(INVITE_HASH);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvite_unknownRejectionReason() {
        service.openInvite(INVITE_HASH);

        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());


        RejectionReasonResource rejectionReasonResource = RejectionReasonResourceBuilder
                .newRejectionReasonResource()
                .withId(2L)
                .build();

        ServiceResult<Void> serviceResult = service.rejectInvite(INVITE_HASH, rejectionReasonResource, of("too busy"));

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(notFoundError(RejectionReason.class, 2L)));

        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionParticipantRepositoryMock, rejectionReasonRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(rejectionReasonRepositoryMock).findOne(2L);

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvite_emptyComment() {
        service.openInvite(INVITE_HASH);

        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());


        RejectionReasonResource rejectionReasonResource = RejectionReasonResourceBuilder
                .newRejectionReasonResource()
                .withId(1L)
                .build();

        ServiceResult<Void> serviceResult = service.rejectInvite(INVITE_HASH, rejectionReasonResource, of(""));


        assertTrue(serviceResult.isSuccess());

        assertEquals(ParticipantStatus.REJECTED, competitionParticipant.getStatus());
        assertEquals("", competitionParticipant.getRejectionReasonComment());

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionParticipantRepositoryMock, rejectionReasonRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(rejectionReasonRepositoryMock).findOne(1L);
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash(INVITE_HASH);

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void sendInvite() throws Exception {
        String email = "john@email.com";
        String name = "John Barnes";

        CompetitionInvite invite = setUpCompetitionInvite(newCompetition().withName("my competition").build(), email, name, CREATED, null, newUser()
                .withFirstName("Paul")
                .build());

        AssessorInviteSendResource assessorInviteSendResource = setUpAssessorInviteSendResource();

        Map<String, Object> expectedNotificationArguments = asMap(
                "subject", assessorInviteSendResource.getSubject(),
                "bodyPlain", "content",
                "bodyHtml", "content"
        );

        SystemNotificationSource from = systemNotificationSourceMock;
        NotificationTarget to = new ExternalUserNotificationTarget(name, email);
        Notification notification = new Notification(from, singletonList(to), CompetitionInviteServiceImpl.Notifications.INVITE_ASSESSOR, expectedNotificationArguments);

        when(competitionInviteRepositoryMock.findOne(invite.getId())).thenReturn(invite);
        when(notificationSender.sendNotification(notification)).thenReturn(serviceSuccess(notification));

        ServiceResult<Void> serviceResult = service.sendInvite(invite.getId(), assessorInviteSendResource);
        assertTrue(serviceResult.isSuccess());

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionParticipantRepositoryMock, notificationSender);
        inOrder.verify(competitionInviteRepositoryMock).findOne(invite.getId());
        inOrder.verify(competitionParticipantRepositoryMock).save(createCompetitionParticipantExpectations(invite));
        inOrder.verify(notificationSender).sendNotification(notification);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void sendInvite_toExistingApplicant() throws Exception {
        String email = "john@email.com";
        String name = "John Barnes";

        CompetitionInvite invite = setUpCompetitionInvite(newCompetition().withName("my competition").build(), email, name, CREATED, newInnovationArea().build(), newUser()
                .withFirstName("Paul")
                .build());

        User applicant = newUser()
                .withRoles(new HashSet(asList(applicantRole)))
                .build();

        AssessorInviteSendResource assessorInviteSendResource = setUpAssessorInviteSendResource();

        Map<String, Object> expectedNotificationArguments = asMap(
                "subject", assessorInviteSendResource.getSubject(),
                "bodyPlain", "content",
                "bodyHtml", "content"
        );

        SystemNotificationSource from = systemNotificationSourceMock;
        NotificationTarget to = new ExternalUserNotificationTarget(name, email);
        Notification notification = new Notification(from, singletonList(to), CompetitionInviteServiceImpl.Notifications.INVITE_ASSESSOR, expectedNotificationArguments);

        when(competitionInviteRepositoryMock.findOne(invite.getId())).thenReturn(invite);
        when(notificationSender.sendNotification(notification)).thenReturn(serviceSuccess(notification));

        when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(applicant));
        when(roleRepositoryMock.findOneByName(UserRoleType.ASSESSOR.getName())).thenReturn(assessorRole);

        service.sendInvite(invite.getId(), assessorInviteSendResource).getSuccessObjectOrThrowException();

        assertEquals(SENT, invite.getStatus());
        assertTrue(applicant.getRoles().containsAll(asList(applicantRole, assessorRole)));


        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionParticipantRepositoryMock, userRepositoryMock, roleRepositoryMock,  notificationSender);
        inOrder.verify(competitionInviteRepositoryMock).findOne(invite.getId());
        inOrder.verify(competitionParticipantRepositoryMock).save(createCompetitionParticipantExpectations(invite));
        inOrder.verify(userRepositoryMock).findByEmail(email);
        inOrder.verify(roleRepositoryMock).findOneByName(UserRoleType.ASSESSOR.getName());
        inOrder.verify(notificationSender).sendNotification(notification);
        inOrder.verifyNoMoreInteractions();
    }


    @Test
    public void sendInvite_withoutUser() throws Exception {
        String email = "john@email.com";
        String name = "John Barnes";

        CompetitionInvite invite = setUpCompetitionInvite(newCompetition().withName("my competition").build(), email, name, CREATED, null, null);

        AssessorInviteSendResource assessorInviteSendResource = setUpAssessorInviteSendResource();

        Map<String, Object> expectedNotificationArguments = asMap(
                "subject", assessorInviteSendResource.getSubject(),
                "bodyPlain", "content",
                "bodyHtml", "content"
        );

        SystemNotificationSource from = systemNotificationSourceMock;
        NotificationTarget to = new ExternalUserNotificationTarget(name, email);
        Notification notification = new Notification(from, singletonList(to), CompetitionInviteServiceImpl.Notifications.INVITE_ASSESSOR, expectedNotificationArguments);

        when(competitionInviteRepositoryMock.findOne(invite.getId())).thenReturn(invite);
        when(notificationSender.sendNotification(notification)).thenReturn(serviceSuccess(notification));

        ServiceResult<Void> serviceResult = service.sendInvite(invite.getId(), assessorInviteSendResource);
        assertTrue(serviceResult.isSuccess());

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionParticipantRepositoryMock, notificationSender);
        inOrder.verify(competitionInviteRepositoryMock).findOne(invite.getId());
        inOrder.verify(competitionParticipantRepositoryMock).save(createCompetitionParticipantExpectations(invite));
        inOrder.verify(notificationSender).sendNotification(notification);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void sendInvite_alreadySent() throws Exception {
        long inviteId = 1L;
        CompetitionInvite invite = setUpCompetitionInvite(newCompetition().withName("my competition").build(), SENT, innovationArea);

        when(competitionInviteRepositoryMock.findOne(inviteId)).thenReturn(invite);

        try {
            service.sendInvite(inviteId, setUpAssessorInviteSendResource());
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
        when(userRepositoryMock.findByEmail("test@test.com")).thenReturn(of(user));

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
        when(userRepositoryMock.findByEmail("test@test.com")).thenReturn(empty());

        assertFalse(service.checkExistingUser("hash").getSuccessObjectOrThrowException());

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, userRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).getByHash("hash");
        inOrder.verify(userRepositoryMock).findByEmail("test@test.com");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getAvailableAssessors() throws Exception {
        long competitionId = 1L;
        int page = 1;
        int pageSize = 1;

        List<InnovationAreaResource> innovationAreaResources = newInnovationAreaResource()
                .withName("Emerging Tech and Industries")
                .build(1);

        List<AvailableAssessorResource> assessorItems = newAvailableAssessorResource()
                .withId(4L, 8L)
                .withName("Jeremy Alufson", "Felix Wilson")
                .withCompliant(TRUE)
                .withEmail("worth.email.test+assessor1@gmail.com", "felix.wilson@gmail.com")
                .withBusinessType(BUSINESS, ACADEMIC)
                .withInnovationAreas(innovationAreaResources)
                .build(2);

        AvailableAssessorPageResource expected = newAvailableAssessorPageResource()
                .withContent(assessorItems)
                .withSize(pageSize)
                .withNumber(page)
                .withTotalPages(2)
                .withTotalElements(2L)
                .build();

        InnovationArea innovationArea = newInnovationArea()
                .withName("Emerging Tech and Industries")
                .build();

        List<Profile> profile = newProfile()
                .withSkillsAreas("Java", "Javascript")
                .withInnovationArea(innovationArea)
                .withBusinessType(BUSINESS, ACADEMIC)
                .withAgreementSignedDate(now())
                .build(2);
        List<User> assessors = newUser()
                .withId(4L, 8L)
                .withFirstName("Jeremy", "Felix")
                .withLastName("Alufson", "Wilson")
                .withEmailAddress("worth.email.test+assessor1@gmail.com", "felix.wilson@gmail.com")
                .withAffiliations(newAffiliation()
                        .withAffiliationType(EMPLOYER)
                        .withOrganisation("Hive IT")
                        .withPosition("Software Developer")
                        .withExists(true)
                        .build(1))
                .withProfileId(profile.get(0).getId(), profile.get(1).getId())
                .build(2);

        Optional<Long> innovationAreaId = of(innovationArea.getId());

        Pageable pageable = new PageRequest(page, pageSize, new Sort(ASC, "firstName"));

        Page<User> expectedPage = new PageImpl<>(assessors, pageable, 2L);

        when(userRepositoryMock.findAssessorsByCompetitionAndInnovationArea(competitionId, innovationArea.getId(), pageable))
                .thenReturn(expectedPage);
        when(profileRepositoryMock.findOne(assessors.get(0).getProfileId())).thenReturn(profile.get(0));
        when(profileRepositoryMock.findOne(assessors.get(1).getProfileId())).thenReturn(profile.get(1));
        when(innovationAreaMapperMock.mapToResource(innovationArea)).thenReturn(innovationAreaResources.get(0));

        AvailableAssessorPageResource actual = service.getAvailableAssessors(competitionId, pageable, innovationAreaId)
                .getSuccessObjectOrThrowException();

        verify(userRepositoryMock).findAssessorsByCompetitionAndInnovationArea(competitionId, innovationArea.getId(), pageable);
        verify(profileRepositoryMock).findOne(assessors.get(0).getProfileId());
        verify(profileRepositoryMock).findOne(assessors.get(1).getProfileId());
        verify(innovationAreaMapperMock, times(2)).mapToResource(innovationArea);

        assertEquals(expected.getNumber(), actual.getNumber());
        assertEquals(expected.getSize(), actual.getSize());
        assertEquals(expected.getTotalElements(), actual.getTotalElements());
        assertEquals(expected.getTotalPages(), actual.getTotalPages());
        assertEquals(expected.getContent(), actual.getContent());
    }

    @Test
    public void getAvailableAssessors_empty() throws Exception {
        long competitionId = 1L;
        int page = 0;
        int pageSize = 20;
        long innovationAreaId = 10L;

        Pageable pageable = new PageRequest(page, pageSize, new Sort(ASC, "firstName"));

        Page<User> assessorPage = new PageImpl<>(emptyList(), pageable, 0);

        when(userRepositoryMock.findAssessorsByCompetitionAndInnovationArea(competitionId, innovationAreaId, pageable))
                .thenReturn(assessorPage);

        AvailableAssessorPageResource result = service.getAvailableAssessors(competitionId, pageable, of(innovationAreaId))
                .getSuccessObjectOrThrowException();

        verify(userRepositoryMock).findAssessorsByCompetitionAndInnovationArea(competitionId, innovationAreaId, pageable);

        assertEquals(page, result.getNumber());
        assertEquals(pageSize, result.getSize());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
        assertEquals(emptyList(), result.getContent());
    }

    @Test
    public void getAvailableAssessors_noInnovationArea() throws Exception {
        long competitionId = 1L;
        int page = 0;
        int pageSize = 20;

        Pageable pageable = new PageRequest(page, pageSize, new Sort(ASC, "firstName"));

        Page<User> assessorPage = new PageImpl<>(emptyList(), pageable, 0);

        when(userRepositoryMock.findAssessorsByCompetition(competitionId, pageable)).thenReturn(assessorPage);

        AvailableAssessorPageResource result = service.getAvailableAssessors(competitionId, pageable, empty())
                .getSuccessObjectOrThrowException();

        verify(userRepositoryMock).findAssessorsByCompetition(competitionId, pageable);

        assertEquals(page, result.getNumber());
        assertEquals(pageSize, result.getSize());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
        assertEquals(emptyList(), result.getContent());
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
                .withAgreementSignedDate(now())
                .withInnovationArea(innovationArea)
                .build();
        User compliantUser = newUser()
                .withAffiliations(newAffiliation()
                        .withAffiliationType(EMPLOYER)
                        .withOrganisation("Hive IT")
                        .withPosition("Software Developer")
                        .withExists(true)
                        .build(1))
                .withProfileId(profile1.getId())
                .build();

        Profile profile2 = newProfile()
                .withSkillsAreas()
                .withAgreementSignedDate(now())
                .build();
        User nonCompliantUserNoSkills = newUser()
                .withAffiliations(newAffiliation()
                        .withAffiliationType(EMPLOYER)
                        .withOrganisation("Hive IT")
                        .withPosition("Software Developer")
                        .withExists(true)
                        .build(1))
                .withProfileId(profile2.getId())
                .build();

        Profile profile3 = newProfile()
                .withSkillsAreas("Java")
                .withAgreementSignedDate(now())
                .build();
        User nonCompliantUserNoAffiliations = newUser()
                .withAffiliations()
                .withProfileId(profile3.getId())
                .build();

        Profile profile4 = newProfile()
                .withSkillsAreas("Java")
                .withAgreementSignedDate()
                .build();
        User nonCompliantUserNoAgreement = newUser()
                .withAffiliations(newAffiliation()
                        .withAffiliationType(EMPLOYER)
                        .withOrganisation("Hive IT")
                        .withPosition("Software Developer")
                        .withExists(true)
                        .build(1))
                .withProfileId(profile4.getId())
                .build();

        List<CompetitionInvite> existingUserInvites = newCompetitionInvite()
                .withId(1L, 2L, 3L, 4L)
                .withName("John Barnes", "Dave Smith", "Richard Turner", "Oliver Romero")
                .withEmail("john@example.com", "dave@example.com", "richard@example.com", "oliver@example.com")
                .withUser(compliantUser, nonCompliantUserNoSkills, nonCompliantUserNoAffiliations, nonCompliantUserNoAgreement)
                .withInnovationArea()
                .build(4);

        CompetitionInvite newUserInvite = newCompetitionInvite()
                .withId(5L)
                .withName("Christopher Soames")
                .withEmail("christopher@example.com")
                .withUser()
                .withInnovationArea(innovationArea)
                .build();

        List<AssessorCreatedInviteResource> expectedInvites = newAssessorCreatedInviteResource()
                .withId(compliantUser.getId(), nonCompliantUserNoSkills.getId(), nonCompliantUserNoAffiliations.getId(), nonCompliantUserNoAgreement.getId(), null)
                .withInviteId(1L, 2L, 3L, 4L, 5L)
                .withName("John Barnes", "Dave Smith", "Richard Turner", "Oliver Romero", "Christopher Soames")
                .withInnovationAreas(innovationAreaList, emptyList(), emptyList(), emptyList(), innovationAreaList)
                .withCompliant(true, false, false, false, false)
                .withEmail("john@example.com", "dave@example.com", "richard@example.com", "oliver@example.com", "christopher@example.com")
                .build(5);

        long totalElements = 100L;

        Pageable pageable = new PageRequest(0, 20);
        Page<CompetitionInvite> page = new PageImpl<>(combineLists(existingUserInvites, newUserInvite), pageable, totalElements);

        when(competitionInviteRepositoryMock.getByCompetitionIdAndStatus(competitionId, CREATED, pageable)).thenReturn(page);
        when(innovationAreaMapperMock.mapToResource(innovationArea)).thenReturn(innovationAreaResource);
        when(profileRepositoryMock.findOne(profile1.getId())).thenReturn(profile1);
        when(profileRepositoryMock.findOne(profile2.getId())).thenReturn(profile2);
        when(profileRepositoryMock.findOne(profile3.getId())).thenReturn(profile3);
        when(profileRepositoryMock.findOne(profile4.getId())).thenReturn(profile4);

        AssessorCreatedInvitePageResource actual = service.getCreatedInvites(competitionId, pageable).getSuccessObjectOrThrowException();
        assertEquals(totalElements, actual.getTotalElements());
        assertEquals(5, actual.getTotalPages());
        assertEquals(expectedInvites, actual.getContent());
        assertEquals(0, actual.getNumber());
        assertEquals(20, actual.getSize());

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, innovationAreaMapperMock);
        inOrder.verify(competitionInviteRepositoryMock).getByCompetitionIdAndStatus(competitionId, CREATED, pageable);
        inOrder.verify(innovationAreaMapperMock, times(2)).mapToResource(innovationArea);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInviteStatistics() throws Exception {
        long competitionId = 1L;
        CompetitionInviteStatisticsResource expected = newCompetitionInviteStatisticsResource()
                .withAccepted(1)
                .withDeclined(2)
                .withInviteList(3)
                .withInvited(4)
                .build();

        when(competitionInviteRepositoryMock.countByCompetitionIdAndStatusIn(competitionId, EnumSet.of(OPENED, SENT))).thenReturn(expected.getInvited());
        when(competitionInviteRepositoryMock.countByCompetitionIdAndStatusIn(competitionId, EnumSet.of(CREATED))).thenReturn(expected.getInviteList());
        when(competitionParticipantRepositoryMock.countByCompetitionIdAndRoleAndStatus(competitionId, ASSESSOR, ACCEPTED)).thenReturn(expected.getAccepted());
        when(competitionParticipantRepositoryMock.countByCompetitionIdAndRoleAndStatus(competitionId, ASSESSOR, REJECTED)).thenReturn(expected.getDeclined());
        CompetitionInviteStatisticsResource actual = service.getInviteStatistics(competitionId).getSuccessObject();
        assertEquals(expected, actual);

        InOrder inOrder = inOrder(competitionInviteRepositoryMock, competitionParticipantRepositoryMock);
        inOrder.verify(competitionInviteRepositoryMock).countByCompetitionIdAndStatusIn(competitionId, EnumSet.of(OPENED, SENT));
        inOrder.verify(competitionInviteRepositoryMock).countByCompetitionIdAndStatusIn(competitionId, EnumSet.of(CREATED));
        inOrder.verify(competitionParticipantRepositoryMock).countByCompetitionIdAndRoleAndStatus(competitionId, ASSESSOR, ACCEPTED);
        inOrder.verify(competitionParticipantRepositoryMock).countByCompetitionIdAndRoleAndStatus(competitionId, ASSESSOR, REJECTED);
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
                .withInnovationArea(newInnovationArea().build())
                .build();

        CompetitionInviteResource expectedInviteResource = newCompetitionInviteResource().build();

        when(userRepositoryMock.findByEmail(newUser.getEmail())).thenReturn(of(newUser));
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
                .withInnovationAreaId(innovationArea.getId())
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
                .withInnovationAreaId(innovationArea)
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
                .withInnovationAreaId(innovationArea.getId())
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
                .withInnovationAreaId(innovationArea.getId())
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
                .withInnovationAreaId(innovationArea.getId())
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
                .withInnovationAreaId(innovationArea.getId())
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
                .withInnovationAreaId(innovationArea.getId())
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
                .withInnovationAreaId(categoryId)
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

    @Test
    public void acceptInvite_newAssessor() {
        InnovationArea innovationArea = newInnovationArea().build();
        CompetitionParticipant competitionParticipant = newCompetitionParticipant()
                .withInvite(newCompetitionInvite()
                        .withStatus(OPENED)
                        .withInnovationArea(innovationArea)
                )
                .build();

        when(competitionParticipantRepositoryMock.getByInviteHash(INVITE_HASH)).thenReturn(competitionParticipant);
        when(profileRepositoryMock.findOne(user.getProfileId())).thenReturn(profile); // move to setup?

        service.acceptInvite(INVITE_HASH, userResource).getSuccessObjectOrThrowException();

        assertEquals(ACCEPTED, competitionParticipant.getStatus());
        assertEquals(user, competitionParticipant.getUser());
        assertEquals(singleton(innovationArea), profile.getInnovationAreas());

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock, userRepositoryMock, profileRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(user.getId());
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash(INVITE_HASH);
        inOrder.verify(profileRepositoryMock).findOne(user.getProfileId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptInvite_newAssessorExistingInnovationArea() {
        InnovationArea innovationArea = newInnovationArea().build();
        CompetitionParticipant competitionParticipant = newCompetitionParticipant()
                .withInvite(newCompetitionInvite()
                        .withStatus(OPENED)
                        .withInnovationArea(innovationArea)
                        .withUser(newUser().build())
                )
                .build();

        // profile with the innovation area already in place
        profile = newProfile()
                .withInnovationArea(innovationArea)
                .build();

        when(competitionParticipantRepositoryMock.getByInviteHash(INVITE_HASH)).thenReturn(competitionParticipant);
        when(profileRepositoryMock.findOne(user.getProfileId())).thenReturn(profile); // move to setup?

        service.acceptInvite(INVITE_HASH, userResource).getSuccessObjectOrThrowException();

        assertEquals(ACCEPTED, competitionParticipant.getStatus());
        assertEquals(user, competitionParticipant.getUser());
        assertEquals(singleton(innovationArea), profile.getInnovationAreas());

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock, userRepositoryMock, profileRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(user.getId());
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash(INVITE_HASH);
        inOrder.verify(profileRepositoryMock).findOne(user.getProfileId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptInvite_newAssessorDifferentInnovationArea() {
        InnovationArea innovationArea = newInnovationArea().build();
        CompetitionParticipant competitionParticipant = newCompetitionParticipant()
                .withInvite(newCompetitionInvite()
                        .withStatus(OPENED)
                        .withInnovationArea(innovationArea)
                        .withUser(newUser().build())
                )
                .build();

        // profile with the innovation area already in place
        profile = newProfile()
                .withInnovationArea(newInnovationArea().build())
                .build();

        when(competitionParticipantRepositoryMock.getByInviteHash(INVITE_HASH)).thenReturn(competitionParticipant);
        when(profileRepositoryMock.findOne(user.getProfileId())).thenReturn(profile); // move to setup?

        service.acceptInvite(INVITE_HASH, userResource).getSuccessObjectOrThrowException();

        assertEquals(ACCEPTED, competitionParticipant.getStatus());
        assertEquals(user, competitionParticipant.getUser());
        assertEquals(singleton(innovationArea), profile.getInnovationAreas());

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock, userRepositoryMock, profileRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(user.getId());
        inOrder.verify(competitionParticipantRepositoryMock).getByInviteHash(INVITE_HASH);
        inOrder.verify(profileRepositoryMock).findOne(user.getProfileId());
        inOrder.verifyNoMoreInteractions();
    }

    private AssessorInviteSendResource setUpAssessorInviteSendResource() {
        return newAssessorInviteSendResource()
                .withSubject("subject")
                .withContent("content")
                .build();
    }

    private CompetitionInvite setUpCompetitionInvite(Competition competition, InviteStatus status, InnovationArea innovationArea) {
        return newCompetitionInvite()
                .withCompetition(competition)
                .withHash(Invite.generateInviteHash())
                .withStatus(status)
                .withInnovationArea(innovationArea)
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
                    assertEquals(innovationArea, invite.getInnovationAreaOrNull());
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

    @Test
    public void getInvitationOverview_allFilters() throws Exception {
        long competitionId = 1L;
        Pageable pageable = new PageRequest(0, 5);
        Long innovationArea = 2L;
        ParticipantStatus status = ParticipantStatus.PENDING;
        Boolean compliant = true;

        List<CompetitionParticipant> expectedParticipants = newCompetitionParticipant()
                .withInvite(
                        newCompetitionInvite()
                                .withName("Name 1", "Name 2", "Name 3", "Name 4", "Name 5")
                                .withSentOn(now())
                                .withStatus(SENT)
                                .withInnovationArea(newInnovationArea().build())
                                .buildArray(5, CompetitionInvite.class)
                )
                .withStatus(PENDING)
                .build(5);

        Page<CompetitionParticipant> pageResult = new PageImpl<>(expectedParticipants, pageable, 10);

        when(competitionParticipantRepositoryMock.getAssessorsByCompetitionAndInnovationAreaAndStatusAndCompliant(
                competitionId,
                innovationArea,
                status,
                compliant,
                pageable
        ))
                .thenReturn(pageResult);
        when(participantStatusMapperMock.mapToResource(PENDING)).thenReturn(ParticipantStatusResource.PENDING);

        ServiceResult<AssessorInviteOverviewPageResource> result = service.getInvitationOverview(
                competitionId,
                pageable,
                of(innovationArea),
                of(status),
                of(compliant)
        );

        verify(competitionParticipantRepositoryMock).getAssessorsByCompetitionAndInnovationAreaAndStatusAndCompliant(
                competitionId,
                innovationArea,
                status,
                compliant,
                pageable
        );
        verify(participantStatusMapperMock, times(5)).mapToResource(PENDING);

        assertTrue(result.isSuccess());

        AssessorInviteOverviewPageResource pageResource = result.getSuccessObjectOrThrowException();

        assertEquals(0, pageResource.getNumber());
        assertEquals(5, pageResource.getSize());
        assertEquals(2, pageResource.getTotalPages());
        assertEquals(10, pageResource.getTotalElements());

        List<AssessorInviteOverviewResource> content = pageResource.getContent();
        assertEquals(5, content.size());
        assertEquals("Name 1", content.get(0).getName());
        assertEquals("Name 2", content.get(1).getName());
        assertEquals("Name 3", content.get(2).getName());
        assertEquals("Name 4", content.get(3).getName());
        assertEquals("Name 5", content.get(4).getName());

        content.forEach(this::assertNotExistingAssessorUser);
    }

    @Test
    public void getInvitationOverview_withExistingUser() throws Exception {
        long competitionId = 1L;
        Pageable pageable = new PageRequest(0, 2);
        Long innovationArea = 2L;
        ParticipantStatus status = ParticipantStatus.PENDING;
        Boolean compliant = true;
        ZonedDateTime agreementSignedDate = now().minusDays(5);

        List<InnovationArea> innovationAreas = newInnovationArea()
                .withName("Innovation 1", "Innovation 2")
                .build(2);

        Profile profile = newProfile()
                .withId(1L)
                .withBusinessType(ACADEMIC)
                .withInnovationAreas(innovationAreas)
                .withSkillsAreas("Some skill")
                .withAgreement(newAgreement().build())
                .withAgreementSignedDate(agreementSignedDate)
                .build();

        User user = newUser()
                .withProfileId(1L)
                .withAffiliations(newAffiliation().build(2))
                .build();

        List<CompetitionParticipant> expectedParticipants = newCompetitionParticipant()
                .withInvite(
                        newCompetitionInvite()
                                .withName("Existing user", "New user")
                                .withSentOn(now().minusDays(1))
                                .withStatus(SENT)
                                .withInnovationArea(null, innovationAreas.get(1))
                                .buildArray(2, CompetitionInvite.class)
                )
                .withUser(user, null)
                .withStatus(ACCEPTED, PENDING)
                .build(2);

        List<InnovationAreaResource> expectedInnovationAreaResources = newInnovationAreaResource()
                .withName("Innovation 1", "Innovation 2")
                .build(2);

        Page<CompetitionParticipant> pageResult = new PageImpl<>(expectedParticipants, pageable, 6);

        when(competitionParticipantRepositoryMock.getAssessorsByCompetitionAndInnovationAreaAndStatusAndCompliant(
                competitionId,
                innovationArea,
                status,
                compliant,
                pageable
        ))
                .thenReturn(pageResult);
        when(profileRepositoryMock.findOne(user.getProfileId())).thenReturn(profile);
        when(participantStatusMapperMock.mapToResource(PENDING)).thenReturn(ParticipantStatusResource.PENDING);
        when(participantStatusMapperMock.mapToResource(ACCEPTED)).thenReturn(ParticipantStatusResource.ACCEPTED);
        when(innovationAreaMapperMock.mapToResource(innovationAreas.get(0))).thenReturn(expectedInnovationAreaResources.get(0));
        when(innovationAreaMapperMock.mapToResource(innovationAreas.get(1))).thenReturn(expectedInnovationAreaResources.get(1));

        ServiceResult<AssessorInviteOverviewPageResource> result = service.getInvitationOverview(
                competitionId,
                pageable,
                of(innovationArea),
                of(status),
                of(compliant)
        );

        verify(competitionParticipantRepositoryMock).getAssessorsByCompetitionAndInnovationAreaAndStatusAndCompliant(
                competitionId,
                innovationArea,
                status,
                compliant,
                pageable
        );
        verify(profileRepositoryMock, only()).findOne(user.getProfileId());
        verify(participantStatusMapperMock).mapToResource(PENDING);
        verify(participantStatusMapperMock).mapToResource(ACCEPTED);
        verify(innovationAreaMapperMock).mapToResource(innovationAreas.get(0));
        verify(innovationAreaMapperMock, times(2)).mapToResource(innovationAreas.get(1));

        assertTrue(result.isSuccess());

        AssessorInviteOverviewPageResource pageResource = result.getSuccessObjectOrThrowException();

        assertEquals(0, pageResource.getNumber());
        assertEquals(2, pageResource.getSize());
        assertEquals(3, pageResource.getTotalPages());
        assertEquals(6, pageResource.getTotalElements());

        List<AssessorInviteOverviewResource> content = pageResource.getContent();
        assertEquals(2, content.size());

        assertEquals("Existing user", content.get(0).getName());
        assertEquals(ParticipantStatusResource.ACCEPTED, content.get(0).getStatus());
        assertNull(content.get(0).getDetails());
        assertEquals(profile.getBusinessType(), content.get(0).getBusinessType());
        assertTrue(content.get(0).isCompliant());
        assertEquals(expectedInnovationAreaResources, content.get(0).getInnovationAreas());

        assertEquals("New user", content.get(1).getName());
        assertEquals(ParticipantStatusResource.PENDING, content.get(1).getStatus());
        assertThat(content.get(1).getDetails(), containsString("Invite sent"));
        assertEquals(singletonList(expectedInnovationAreaResources.get(1)), content.get(1).getInnovationAreas());
        assertNotExistingAssessorUser(content.get(1));
    }

    @Test
    public void getInvitationOverview_noFilters() throws Exception {
        long competitionId = 1L;
        Pageable pageable = new PageRequest(0, 5);
        List<CompetitionParticipant> expectedParticipants = newCompetitionParticipant()
                .withInvite(
                        newCompetitionInvite()
                                .withName("Name 1", "Name 2", "Name 3", "Name 4", "Name 5")
                                .withSentOn(now())
                                .withStatus(SENT)
                                .withInnovationArea(newInnovationArea().build())
                                .buildArray(5, CompetitionInvite.class)
                )
                .withStatus(PENDING)
                .build(5);

        Page<CompetitionParticipant> pageResult = new PageImpl<>(expectedParticipants, pageable, 10);

        when(competitionParticipantRepositoryMock.getAssessorsByCompetitionAndStatus(competitionId, null, pageable))
                .thenReturn(pageResult);
        when(participantStatusMapperMock.mapToResource(PENDING)).thenReturn(ParticipantStatusResource.PENDING);

        ServiceResult<AssessorInviteOverviewPageResource> result = service.getInvitationOverview(competitionId, pageable, empty(), empty(), empty());

        verify(competitionParticipantRepositoryMock).getAssessorsByCompetitionAndStatus(competitionId, null, pageable);
        verify(participantStatusMapperMock, times(5)).mapToResource(PENDING);

        assertTrue(result.isSuccess());

        AssessorInviteOverviewPageResource pageResource = result.getSuccessObjectOrThrowException();

        assertEquals(0, pageResource.getNumber());
        assertEquals(5, pageResource.getSize());
        assertEquals(2, pageResource.getTotalPages());
        assertEquals(10, pageResource.getTotalElements());

        List<AssessorInviteOverviewResource> content = pageResource.getContent();
        assertEquals("Name 1", content.get(0).getName());
        assertEquals("Name 2", content.get(1).getName());
        assertEquals("Name 3", content.get(2).getName());
        assertEquals("Name 4", content.get(3).getName());
        assertEquals("Name 5", content.get(4).getName());

        content.forEach(this::assertNotExistingAssessorUser);
    }

    private void assertNotExistingAssessorUser(AssessorInviteOverviewResource assessorInviteOverviewResource) {
        assertNull(assessorInviteOverviewResource.getId());
        assertNull(assessorInviteOverviewResource.getBusinessType());
        assertFalse(assessorInviteOverviewResource.isCompliant());
    }
}
