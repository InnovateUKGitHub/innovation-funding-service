package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.assessment.mapper.AssessorCreatedInviteMapper;
import org.innovateuk.ifs.assessment.mapper.AssessorInviteOverviewMapper;
import org.innovateuk.ifs.assessment.repository.AssessmentInviteRepository;
import org.innovateuk.ifs.assessment.repository.AssessmentParticipantRepository;
import org.innovateuk.ifs.category.domain.Category;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.competition.mapper.CompetitionInviteMapper;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.invite.builder.RejectionReasonResourceBuilder;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.Invite;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.assessment.domain.AssessmentInvite;
import org.innovateuk.ifs.assessment.domain.AssessmentParticipant;
import org.innovateuk.ifs.competition.domain.CompetitionParticipant;
import org.innovateuk.ifs.invite.domain.RejectionReason;
import org.innovateuk.ifs.invite.repository.RejectionReasonRepository;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer;
import org.innovateuk.ifs.notifications.service.senders.NotificationSender;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static java.time.ZonedDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.assessment.builder.AssessmentParticipantBuilder.newAssessmentParticipant;
import static org.innovateuk.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static org.innovateuk.ifs.assessment.transactional.AssessmentInviteServiceImpl.Notifications.INVITE_ASSESSOR_GROUP;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.MilestoneBuilder.newMilestone;
import static org.innovateuk.ifs.competition.resource.MilestoneType.*;
import static org.innovateuk.ifs.assessment.builder.AssessmentInviteBuilder.newAssessmentInvite;
import static org.innovateuk.ifs.invite.builder.AssessorCreatedInviteResourceBuilder.newAssessorCreatedInviteResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewResourceBuilder.newAssessorInviteOverviewResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteSendResourceBuilder.newAssessorInviteSendResource;
import static org.innovateuk.ifs.invite.builder.AssessorInvitesToSendResourceBuilder.newAssessorInvitesToSendResource;
import static org.innovateuk.ifs.invite.builder.AvailableAssessorPageResourceBuilder.newAvailableAssessorPageResource;
import static org.innovateuk.ifs.invite.builder.AvailableAssessorResourceBuilder.newAvailableAssessorResource;
import static org.innovateuk.ifs.invite.builder.CompetitionInviteStatisticsResourceBuilder.newCompetitionInviteStatisticsResource;
import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteResourceBuilder.newExistingUserStagedInviteResource;
import static org.innovateuk.ifs.invite.builder.NewUserStagedInviteResourceBuilder.newNewUserStagedInviteResource;
import static org.innovateuk.ifs.invite.builder.RejectionReasonBuilder.newRejectionReason;
import static org.innovateuk.ifs.invite.constant.InviteStatus.*;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.*;
import static org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.ASSESSOR;
import static org.innovateuk.ifs.notifications.builders.NotificationBuilder.newNotification;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.builder.AffiliationBuilder.newAffiliation;
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

public class AssessmentInviteServiceImplTest extends BaseServiceUnitTest<AssessmentInviteServiceImpl> {
    private static final String UID = "5cc0ac0d-b969-40f5-9cc5-b9bdd98c86de";
    private static final String INVITE_HASH = "inviteHash";
    private static final DateTimeFormatter inviteFormatter = ofPattern("d MMMM yyyy");

    @Mock
    private AssessorCreatedInviteMapper assessorCreatedInviteMapperMock;

    @Mock
    private AssessorInviteOverviewMapper assessorInviteOverviewMapperMock;

    @Mock
    private UserMapper userMapperMock;

    @Mock
    private AssessmentInviteRepository assessmentInviteRepositoryMock;

    @Mock
    private CompetitionInviteMapper competitionInviteMapperMock;

    @Mock
    private AssessmentParticipantRepository assessmentParticipantRepositoryMock;

    @Mock
    private RejectionReasonRepository rejectionReasonRepositoryMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private LoggedInUserSupplier loggedInUserSupplierMock;

    @Mock
    private ProfileRepository profileRepositoryMock;

    @Mock
    private CompetitionRepository competitionRepositoryMock;

    @Mock
    private NotificationTemplateRenderer notificationTemplateRendererMock;

    @Mock
    private SystemNotificationSource systemNotificationSourceMock;

    @Mock
    private NotificationSender notificationSenderMock;

    @Mock
    private InnovationAreaRepository innovationAreaRepositoryMock;

    @Mock
    private InnovationAreaMapper innovationAreaMapperMock;


    private AssessmentParticipant competitionParticipant;

    private UserResource userResource;
    private User user;
    private Profile profile;
    private InnovationArea innovationArea;


    @Override
    protected AssessmentInviteServiceImpl supplyServiceUnderTest() {
        return new AssessmentInviteServiceImpl();
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
        AssessmentInvite assessmentInvite = setUpCompetitionInvite(competition, SENT, innovationArea);

        competitionParticipant = new AssessmentParticipant(assessmentInvite);
        CompetitionInviteResource expected = newCompetitionInviteResource().withCompetitionName("my competition").build();
        RejectionReason rejectionReason = newRejectionReason().withId(1L).withReason("not available").build();
        userResource = newUserResource().withId(userId).build();
        profile = newProfile().withId(profileId).build();
        user = newUser().withId(userId).withProfileId(profile.getId()).build();

        UserResource senderResource = newUserResource().withId(-1L).withUID(UID).build();
        User sender = newUser().withId(-1L).withUid(UID).build();
        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(senderResource));
        when(userMapperMock.mapToDomain(senderResource)).thenReturn(sender);

        when(assessmentInviteRepositoryMock.getByHash(INVITE_HASH)).thenReturn(assessmentInvite);

        when(assessmentInviteRepositoryMock.save(same(assessmentInvite))).thenReturn(assessmentInvite);
        when(competitionInviteMapperMock.mapToResource(same(assessmentInvite))).thenReturn(expected);

        when(assessmentParticipantRepositoryMock.getByInviteHash(INVITE_HASH)).thenReturn(competitionParticipant);

        when(rejectionReasonRepositoryMock.findOne(rejectionReason.getId())).thenReturn(rejectionReason);

        when(userRepositoryMock.findOne(userId)).thenReturn(user);
        when(profileRepositoryMock.findOne(user.getProfileId())).thenReturn(profile);

        when(loggedInUserSupplierMock.get()).thenReturn(newUser().build());

        ReflectionTestUtils.setField(service, "webBaseUrl", "https://ifs-local-dev");
    }

    @Test
    public void getAllInvitesToSend() throws Exception {
        List<String> emails = asList("john@email.com", "peter@email.com");
        List<String> names = asList("John Barnes", "Peter Jones");

        ZonedDateTime acceptsDate = ZonedDateTime.of(2016, 12, 20, 12, 0,0,0, ZoneId.systemDefault());
        ZonedDateTime deadlineDate = ZonedDateTime.of(2017, 1, 17, 12, 0,0,0, ZoneId.systemDefault());

        Competition competition = newCompetition()
                .withName("my competition")
                .withAssessorAcceptsDate(acceptsDate)
                .withAssessorDeadlineDate(deadlineDate)
                .build();

        List<AssessmentInvite> invites = newAssessmentInvite()
                .withCompetition(competition)
                .withEmail(emails.get(0), emails.get(1))
                .withHash(Invite.generateInviteHash())
                .withInnovationArea(innovationArea)
                .withName(names.get(0), names.get(1))
                .withStatus(CREATED)
                .withUser(user)
                .build(2);

        Map<String, Object> expectedNotificationArguments = asMap(
                "competitionName", competition.getName(),
                "acceptsDate", acceptsDate.format(inviteFormatter),
                "deadlineDate", deadlineDate.format(inviteFormatter)
        );

        NotificationTarget notificationTarget = new UserNotificationTarget("", "");

        String templatePath = "invite_assessor_preview_text.txt";

        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(assessmentInviteRepositoryMock.getByCompetitionIdAndStatus(competition.getId(), CREATED)).thenReturn(invites);
        when(notificationTemplateRendererMock.renderTemplate(systemNotificationSourceMock, notificationTarget, templatePath,
                expectedNotificationArguments)).thenReturn(serviceSuccess("content"));

        AssessorInvitesToSendResource expectedAssessorInviteToSendResource = newAssessorInvitesToSendResource()
                .withContent("content")
                .withCompetitionId(competition.getId())
                .withCompetitionName(competition.getName())
                .withRecipients(names)
                .build();

        AssessorInvitesToSendResource result = service.getAllInvitesToSend(competition.getId()).getSuccess();
        assertEquals(expectedAssessorInviteToSendResource, result);

        InOrder inOrder = inOrder(competitionRepositoryMock,
                                  assessmentInviteRepositoryMock, notificationTemplateRendererMock);
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(assessmentInviteRepositoryMock).getByCompetitionIdAndStatus(competition.getId(), CREATED);
        inOrder.verify(notificationTemplateRendererMock).renderTemplate(systemNotificationSourceMock, notificationTarget,
                templatePath, expectedNotificationArguments);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getAllInvitesToResend() throws Exception {
        List<String> emails = asList("john@email.com", "peter@email.com");
        List<String> names = asList("John Barnes", "Peter Jones");
        List<Long> inviteIds = asList(1L, 2L);

        ZonedDateTime acceptsDate = ZonedDateTime.of(2016, 12, 20, 12, 0,0,0, ZoneId.systemDefault());
        ZonedDateTime deadlineDate = ZonedDateTime.of(2017, 1, 17, 12, 0,0,0, ZoneId.systemDefault());

        Competition competition = newCompetition()
                .withName("my competition")
                .withAssessorAcceptsDate(acceptsDate)
                .withAssessorDeadlineDate(deadlineDate)
                .build();

        List<AssessmentInvite> invites = newAssessmentInvite()
                .withCompetition(competition)
                .withEmail(emails.get(0), emails.get(1))
                .withHash(Invite.generateInviteHash())
                .withInnovationArea(innovationArea)
                .withName(names.get(0), names.get(1))
                .withStatus(SENT)
                .withUser(user)
                .build(2);

        Map<String, Object> expectedNotificationArguments = asMap(
                "competitionName", competition.getName(),
                "acceptsDate", acceptsDate.format(inviteFormatter),
                "deadlineDate", deadlineDate.format(inviteFormatter)
        );

        NotificationTarget notificationTarget = new UserNotificationTarget("", "");

        String templatePath = "invite_assessor_preview_text.txt";

        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(assessmentInviteRepositoryMock.getByIdIn(inviteIds)).thenReturn(invites);
        when(notificationTemplateRendererMock.renderTemplate(systemNotificationSourceMock, notificationTarget, templatePath,
                expectedNotificationArguments)).thenReturn(serviceSuccess("content"));

        AssessorInvitesToSendResource expectedAssessorInviteToSendResource = newAssessorInvitesToSendResource()
                .withContent("content")
                .withCompetitionId(competition.getId())
                .withCompetitionName(competition.getName())
                .withRecipients(names)
                .build();

        AssessorInvitesToSendResource result = service.getAllInvitesToResend(competition.getId(), inviteIds).getSuccess();
        assertEquals(expectedAssessorInviteToSendResource, result);

        InOrder inOrder = inOrder(competitionRepositoryMock,
                                  assessmentInviteRepositoryMock, notificationTemplateRendererMock);
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(assessmentInviteRepositoryMock).getByIdIn(inviteIds);
        inOrder.verify(notificationTemplateRendererMock).renderTemplate(systemNotificationSourceMock, notificationTarget,
                templatePath, expectedNotificationArguments);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInviteToSend() throws Exception {
        String email = "john@email.com";
        String name = "John Barnes";

        ZonedDateTime acceptsDate = ZonedDateTime.of(2016, 12, 20, 12, 0,0,0, ZoneId.systemDefault());
        ZonedDateTime deadlineDate = ZonedDateTime.of(2017, 1, 17, 12, 0,0,0, ZoneId.systemDefault());

        Competition competition = newCompetition()
                .withName("my competition")
                .withAssessorAcceptsDate(acceptsDate)
                .withAssessorDeadlineDate(deadlineDate)
                .build();

        AssessmentInvite invite = setUpCompetitionInvite(competition, email, name, CREATED, innovationArea, null);

        Map<String, Object> expectedNotificationArguments = asMap(
                "name", name,
                "competitionName", "my competition",
                "acceptsDate", acceptsDate.format(inviteFormatter),
                "deadlineDate", deadlineDate.format(inviteFormatter),
                "inviteUrl", format("%s/invite/competition/%s", "https://ifs-local-dev/assessment", invite.getHash()));

        NotificationTarget notificationTarget = new UserNotificationTarget("", "");

        String templatePath = "invite_assessor_editable_text.txt";

        when(assessmentInviteRepositoryMock.findOne(invite.getId())).thenReturn(invite);
        when(notificationTemplateRendererMock.renderTemplate(systemNotificationSourceMock, notificationTarget, templatePath,
                expectedNotificationArguments)).thenReturn(serviceSuccess("content"));

        AssessorInvitesToSendResource expectedAssessorInviteToSendResource = newAssessorInvitesToSendResource()
                .withContent("content")
                .withCompetitionId(competition.getId())
                .withCompetitionName(competition.getName())
                .withRecipients(singletonList(name))
                .build();

        AssessorInvitesToSendResource result = service.getInviteToSend(invite.getId()).getSuccess();
        assertEquals(expectedAssessorInviteToSendResource, result);

        InOrder inOrder = inOrder(assessmentInviteRepositoryMock, notificationTemplateRendererMock);
        inOrder.verify(assessmentInviteRepositoryMock).findOne(invite.getId());
        inOrder.verify(notificationTemplateRendererMock)
                .renderTemplate(systemNotificationSourceMock, notificationTarget, templatePath, expectedNotificationArguments);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInviteToSend_notCreated() throws Exception {
        String email = "john@email.com";
        String name = "John Barnes";

        ZonedDateTime acceptsDate = ZonedDateTime.of(2016, 12, 20, 12, 0,0,0, ZoneId.systemDefault());
        ZonedDateTime deadlineDate = ZonedDateTime.of(2017, 1, 17, 12, 0,0,0, ZoneId.systemDefault());

        Competition competition = newCompetition()
                .withName("my competition")
                .withAssessorAcceptsDate(acceptsDate)
                .withAssessorDeadlineDate(deadlineDate)
                .build();

        AssessmentInvite invite = setUpCompetitionInvite(competition, email, name, SENT, innovationArea, null);

        Map<String, Object> expectedNotificationArguments = asMap(
                "name", name,
                "competitionName", "my competition",
                "acceptsDate", acceptsDate.format(inviteFormatter),
                "deadlineDate", deadlineDate.format(inviteFormatter),
                "inviteUrl", format("%s/invite/competition/%s", "https://ifs-local-dev/assessment", invite.getHash()));

        NotificationTarget notificationTarget = new UserNotificationTarget("", "");

        String templatePath = "invite_assessor_editable_text.txt";

        when(assessmentInviteRepositoryMock.findOne(invite.getId())).thenReturn(invite);
        when(notificationTemplateRendererMock.renderTemplate(systemNotificationSourceMock, notificationTarget, templatePath,
                expectedNotificationArguments)).thenReturn(serviceSuccess("content"));

        AssessorInvitesToSendResource expectedAssessorInviteToSendResource = newAssessorInvitesToSendResource()
                .withCompetitionId(competition.getId())
                .withCompetitionName(competition.getName())
                .withContent("content")
                .withRecipients(singletonList(name))
                .build();

        AssessorInvitesToSendResource result = service.getInviteToSend(invite.getId()).getSuccess();
        assertEquals(expectedAssessorInviteToSendResource, result);

        InOrder inOrder = inOrder(assessmentInviteRepositoryMock, notificationTemplateRendererMock);
        inOrder.verify(assessmentInviteRepositoryMock).findOne(invite.getId());
        inOrder.verify(notificationTemplateRendererMock).renderTemplate(systemNotificationSourceMock, notificationTarget,
                templatePath, expectedNotificationArguments);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInvite() throws Exception {
        ServiceResult<CompetitionInviteResource> inviteServiceResult = service.getInvite(INVITE_HASH);

        assertTrue(inviteServiceResult.isSuccess());

        CompetitionInviteResource competitionInviteResource = inviteServiceResult.getSuccess();
        assertEquals("my competition", competitionInviteResource.getCompetitionName());

        InOrder inOrder = inOrder(assessmentInviteRepositoryMock, competitionInviteMapperMock);
        inOrder.verify(assessmentInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(competitionInviteMapperMock).mapToResource(isA(AssessmentInvite.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInvite_hashNotExists() throws Exception {
        when(assessmentInviteRepositoryMock.getByHash(isA(String.class))).thenReturn(null);

        ServiceResult<CompetitionInviteResource> inviteServiceResult = service.getInvite("inviteHashNotExists");

        assertTrue(inviteServiceResult.isFailure());
        assertTrue(inviteServiceResult.getFailure().is(notFoundError(AssessmentInvite.class, "inviteHashNotExists")));

        InOrder inOrder = inOrder(assessmentInviteRepositoryMock, competitionInviteMapperMock);
        inOrder.verify(assessmentInviteRepositoryMock).getByHash("inviteHashNotExists");
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

        InOrder inOrder = inOrder(assessmentInviteRepositoryMock, assessmentParticipantRepositoryMock);
        inOrder.verify(assessmentInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(assessmentInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(assessmentParticipantRepositoryMock).getByInviteHash(INVITE_HASH);
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

        InOrder inOrder = inOrder(assessmentInviteRepositoryMock, assessmentParticipantRepositoryMock);
        inOrder.verify(assessmentInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(assessmentInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(assessmentParticipantRepositoryMock).getByInviteHash(INVITE_HASH);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void openInvite() throws Exception {
        ServiceResult<CompetitionInviteResource> inviteServiceResult = service.openInvite(INVITE_HASH);

        assertTrue(inviteServiceResult.isSuccess());

        CompetitionInviteResource competitionInviteResource = inviteServiceResult.getSuccess();
        assertEquals("my competition", competitionInviteResource.getCompetitionName());

        InOrder inOrder = inOrder(assessmentInviteRepositoryMock, competitionInviteMapperMock);
        inOrder.verify(assessmentInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(assessmentInviteRepositoryMock).save(isA(AssessmentInvite.class));
        inOrder.verify(competitionInviteMapperMock).mapToResource(isA(AssessmentInvite.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void openInvite_hashNotExists() throws Exception {
        when(assessmentInviteRepositoryMock.getByHash(isA(String.class))).thenReturn(null);

        ServiceResult<CompetitionInviteResource> inviteServiceResult = service.openInvite("inviteHashNotExists");

        assertTrue(inviteServiceResult.isFailure());
        assertTrue(inviteServiceResult.getFailure().is(notFoundError(AssessmentInvite.class, "inviteHashNotExists")));

        InOrder inOrder = inOrder(assessmentInviteRepositoryMock, competitionInviteMapperMock);
        inOrder.verify(assessmentInviteRepositoryMock).getByHash("inviteHashNotExists");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void openInvite_inviteExpired() throws Exception {
        AssessmentInvite assessmentInvite = setUpCompetitionInvite(newCompetition()
                .withName("my competition")
                .withAssessorAcceptsDate(now().minusDays(1))
                .build(), SENT, innovationArea);

        when(assessmentInviteRepositoryMock.getByHash(isA(String.class))).thenReturn(assessmentInvite);

        ServiceResult<CompetitionInviteResource> inviteServiceResult = service.openInvite("inviteHashExpired");

        assertTrue(inviteServiceResult.isFailure());
        assertTrue(inviteServiceResult.getFailure().is(new Error(COMPETITION_INVITE_EXPIRED, "my competition")));

        InOrder inOrder = inOrder(assessmentInviteRepositoryMock, competitionInviteMapperMock);
        inOrder.verify(assessmentInviteRepositoryMock).getByHash("inviteHashExpired");
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

        InOrder inOrder = inOrder(assessmentInviteRepositoryMock, assessmentParticipantRepositoryMock);
        inOrder.verify(assessmentInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(assessmentInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(assessmentParticipantRepositoryMock).getByInviteHash(INVITE_HASH);
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

        InOrder inOrder = inOrder(assessmentInviteRepositoryMock, assessmentParticipantRepositoryMock);
        inOrder.verify(assessmentInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(assessmentInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(assessmentParticipantRepositoryMock).getByInviteHash(INVITE_HASH);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptInvite() {
        service.openInvite(INVITE_HASH);

        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());
        assertNull(competitionParticipant.getUser());

        service.acceptInvite(INVITE_HASH, userResource).getSuccess();

        assertEquals(ACCEPTED, competitionParticipant.getStatus());
        assertEquals(user, competitionParticipant.getUser());

        InOrder inOrder = inOrder(assessmentParticipantRepositoryMock, userRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(7L);
        inOrder.verify(assessmentParticipantRepositoryMock).getByInviteHash(INVITE_HASH);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptInvite_hashNotExists() {
        ServiceResult<Void> serviceResult = service.acceptInvite("inviteHashNotExists", userResource);

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(notFoundError(CompetitionParticipant.class, "inviteHashNotExists")));

        InOrder inOrder = inOrder(assessmentParticipantRepositoryMock);
        inOrder.verify(assessmentParticipantRepositoryMock).getByInviteHash("inviteHashNotExists");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptInvite_notOpened() {
        assertEquals(SENT, competitionParticipant.getInvite().getStatus());
        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());

        ServiceResult<Void> serviceResult = service.acceptInvite(INVITE_HASH, userResource);

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(new Error(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_UNOPENED_INVITE, "my competition")));

        InOrder inOrder = inOrder(assessmentInviteRepositoryMock, userRepositoryMock, assessmentParticipantRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(userResource.getId());
        inOrder.verify(assessmentParticipantRepositoryMock).getByInviteHash(INVITE_HASH);
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

        InOrder inOrder = inOrder(assessmentInviteRepositoryMock, userRepositoryMock, assessmentParticipantRepositoryMock);
        inOrder.verify(assessmentInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(userRepositoryMock).findOne(userResource.getId());
        inOrder.verify(assessmentParticipantRepositoryMock).getByInviteHash(INVITE_HASH);
        inOrder.verify(userRepositoryMock).findOne(userResource.getId());
        inOrder.verify(assessmentParticipantRepositoryMock).getByInviteHash(INVITE_HASH);
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

        InOrder inOrder = inOrder(assessmentInviteRepositoryMock, rejectionReasonRepositoryMock, userRepositoryMock, assessmentParticipantRepositoryMock);
        inOrder.verify(assessmentInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(rejectionReasonRepositoryMock).findOne(1L);
        inOrder.verify(assessmentParticipantRepositoryMock, times(2)).getByInviteHash(INVITE_HASH);
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

        InOrder inOrder = inOrder(assessmentInviteRepositoryMock, assessmentParticipantRepositoryMock, rejectionReasonRepositoryMock);
        inOrder.verify(assessmentInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(rejectionReasonRepositoryMock).findOne(1L);
        inOrder.verify(assessmentParticipantRepositoryMock).getByInviteHash(INVITE_HASH);

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

        InOrder inOrder = inOrder(rejectionReasonRepositoryMock, assessmentParticipantRepositoryMock);
        inOrder.verify(rejectionReasonRepositoryMock).findOne(1L);
        inOrder.verify(assessmentParticipantRepositoryMock).getByInviteHash("inviteHashNotExists");
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

        InOrder inOrder = inOrder(rejectionReasonRepositoryMock, assessmentParticipantRepositoryMock);
        inOrder.verify(rejectionReasonRepositoryMock).findOne(1L);
        inOrder.verify(assessmentParticipantRepositoryMock).getByInviteHash(INVITE_HASH);
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

        InOrder inOrder = inOrder(assessmentInviteRepositoryMock, userRepositoryMock, assessmentParticipantRepositoryMock, rejectionReasonRepositoryMock);
        inOrder.verify(assessmentInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(userRepositoryMock).findOne(7L);
        inOrder.verify(assessmentParticipantRepositoryMock).getByInviteHash(INVITE_HASH);
        inOrder.verify(rejectionReasonRepositoryMock).findOne(1L);
        inOrder.verify(assessmentParticipantRepositoryMock).getByInviteHash(INVITE_HASH);
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

        InOrder inOrder = inOrder(assessmentInviteRepositoryMock, assessmentParticipantRepositoryMock, rejectionReasonRepositoryMock);
        inOrder.verify(assessmentInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(rejectionReasonRepositoryMock).findOne(1L);
        inOrder.verify(assessmentParticipantRepositoryMock, times(2)).getByInviteHash(INVITE_HASH);
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

        InOrder inOrder = inOrder(assessmentInviteRepositoryMock, assessmentParticipantRepositoryMock, rejectionReasonRepositoryMock);
        inOrder.verify(assessmentInviteRepositoryMock).getByHash(INVITE_HASH);
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

        InOrder inOrder = inOrder(assessmentInviteRepositoryMock, assessmentParticipantRepositoryMock, rejectionReasonRepositoryMock);
        inOrder.verify(assessmentInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(rejectionReasonRepositoryMock).findOne(1L);
        inOrder.verify(assessmentParticipantRepositoryMock).getByInviteHash(INVITE_HASH);

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void sendAllInvites() throws Exception {
        List<String> emails = asList("john@email.com", "peter@email.com");
        List<String> names = asList("John Barnes", "Peter Jones");

        Competition competition = newCompetition()
                .withName("my competition")
                .withAssessorAcceptsDate(ZonedDateTime.parse("2017-05-24T12:00:00+01:00"))
                .withAssessorDeadlineDate(ZonedDateTime.parse("2017-05-30T12:00:00+01:00"))
                .build();

        List<AssessmentInvite> invites = newAssessmentInvite()
                .withCompetition(competition)
                .withEmail(emails.get(0), emails.get(1))
                .withHash(Invite.generateInviteHash())
                .withInnovationArea(innovationArea)
                .withName(names.get(0), names.get(1))
                .withStatus(CREATED)
                .withUser(newUser().withFirstName("Paul").build())
                .build(2);

        AssessorInviteSendResource assessorInviteSendResource = setUpAssessorInviteSendResource();

        Map<String, Object> expectedNotificationArguments1 = asMap(
                "subject", assessorInviteSendResource.getSubject(),
                "name", invites.get(0).getName(),
                "competitionName", invites.get(0).getTarget().getName(),
                "acceptsDate", "24 May 2017",
                "deadlineDate", "30 May 2017",
                "inviteUrl", "https://ifs-local-dev/assessment/invite/competition/" + invites.get(0).getHash(),
                "customTextPlain", "content",
                "customTextHtml", "content"
        );
        Map<String, Object> expectedNotificationArguments2 = asMap(
                "subject", assessorInviteSendResource.getSubject(),
                "name", invites.get(1).getName(),
                "competitionName", invites.get(1).getTarget().getName(),
                "acceptsDate", "24 May 2017",
                "deadlineDate", "30 May 2017",
                "inviteUrl", "https://ifs-local-dev/assessment/invite/competition/" + invites.get(1).getHash(),
                "customTextPlain", "content",
                "customTextHtml", "content"
        );

        SystemNotificationSource from = systemNotificationSourceMock;
        NotificationTarget to1 = new UserNotificationTarget(names.get(0), emails.get(0));
        NotificationTarget to2 = new UserNotificationTarget(names.get(1), emails.get(1));

        List<Notification> notifications = newNotification()
                .withSource(from, from)
                .withMessageKey(INVITE_ASSESSOR_GROUP, INVITE_ASSESSOR_GROUP)
                .withTargets(singletonList(to1), singletonList(to2))
                .withGlobalArguments(expectedNotificationArguments1, expectedNotificationArguments2)
                .build(2);

        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(assessmentInviteRepositoryMock.getByCompetitionIdAndStatus(competition.getId(), CREATED)).thenReturn(invites);
        when(userRepositoryMock.findByEmail(emails.get(0))).thenReturn(Optional.empty());
        when(userRepositoryMock.findByEmail(emails.get(1))).thenReturn(Optional.empty());
        when(notificationSenderMock.sendNotification(notifications.get(0))).thenReturn(serviceSuccess(notifications.get(0)));
        when(notificationSenderMock.sendNotification(notifications.get(1))).thenReturn(serviceSuccess(notifications.get(1)));

        ServiceResult<Void> serviceResult = service.sendAllInvites(competition.getId(), assessorInviteSendResource);
        assertTrue(serviceResult.isSuccess());

        InOrder inOrder = inOrder(competitionRepositoryMock,
                                  assessmentInviteRepositoryMock, userRepositoryMock, assessmentParticipantRepositoryMock, notificationSenderMock);
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(assessmentInviteRepositoryMock).getByCompetitionIdAndStatus(competition.getId(), CREATED);

        inOrder.verify(assessmentParticipantRepositoryMock).save(createCompetitionParticipantExpectations(invites.get(0)));
        inOrder.verify(userRepositoryMock).findByEmail(emails.get(0));
        inOrder.verify(notificationSenderMock).sendNotification(notifications.get(0));

        inOrder.verify(assessmentParticipantRepositoryMock).save(createCompetitionParticipantExpectations(invites.get(1)));
        inOrder.verify(userRepositoryMock).findByEmail(emails.get(1));
        inOrder.verify(notificationSenderMock).sendNotification(notifications.get(1));

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void resendInvites() throws Exception {
        List<String> emails = asList("john@email.com", "peter@email.com");
        List<String> names = asList("John Barnes", "Peter Jones");
        List<Long> inviteIds = asList(1L, 2L);

        Competition competition = newCompetition()
                .withName("my competition")
                .withAssessorAcceptsDate(ZonedDateTime.parse("2017-05-24T12:00:00+01:00"))
                .withAssessorDeadlineDate(ZonedDateTime.parse("2017-05-30T12:00:00+01:00"))
                .build();

        List<AssessmentInvite> invites = newAssessmentInvite()
                .withCompetition(competition)
                .withEmail(emails.get(0), emails.get(1))
                .withHash(Invite.generateInviteHash())
                .withInnovationArea(innovationArea)
                .withName(names.get(0), names.get(1))
                .withStatus(SENT)
                .withUser(newUser().withFirstName("Paul").build())
                .build(2);

        List<AssessmentParticipant> assessmentParticipants = newAssessmentParticipant()
                .with(id(null))
                .withStatus(PENDING, REJECTED)
                .withRole(ASSESSOR, ASSESSOR)
                .withCompetition(competition, competition)
                .withInvite(invites.get(0), invites.get(1))
                .withUser()
                .build(2);

        AssessorInviteSendResource assessorInviteSendResource = setUpAssessorInviteSendResource();

        Map<String, Object> expectedNotificationArguments1 = asMap(
                "subject", assessorInviteSendResource.getSubject(),
                "name", invites.get(0).getName(),
                "competitionName", invites.get(0).getTarget().getName(),
                "acceptsDate", "24 May 2017",
                "deadlineDate", "30 May 2017",
                "inviteUrl", "https://ifs-local-dev/assessment/invite/competition/" + invites.get(0).getHash(),
                "customTextPlain", "content",
                "customTextHtml", "content"
        );
        Map<String, Object> expectedNotificationArguments2 = asMap(
                "subject", assessorInviteSendResource.getSubject(),
                "name", invites.get(1).getName(),
                "competitionName", invites.get(1).getTarget().getName(),
                "acceptsDate", "24 May 2017",
                "deadlineDate", "30 May 2017",
                "inviteUrl", "https://ifs-local-dev/assessment/invite/competition/" + invites.get(1).getHash(),
                "customTextPlain", "content",
                "customTextHtml", "content"
        );

        SystemNotificationSource from = systemNotificationSourceMock;
        NotificationTarget to1 = new UserNotificationTarget(names.get(0), emails.get(0));
        NotificationTarget to2 = new UserNotificationTarget(names.get(1), emails.get(1));

        List<Notification> notifications = newNotification()
                .withSource(from, from)
                .withMessageKey(INVITE_ASSESSOR_GROUP, INVITE_ASSESSOR_GROUP)
                .withTargets(singletonList(to1), singletonList(to2))
                .withGlobalArguments(expectedNotificationArguments1, expectedNotificationArguments2)
                .build(2);

        when(assessmentInviteRepositoryMock.getByIdIn(inviteIds)).thenReturn(invites);
        when(assessmentParticipantRepositoryMock.getByInviteHash(invites.get(0).getHash())).thenReturn(assessmentParticipants.get(0));
        when(assessmentParticipantRepositoryMock.getByInviteHash(invites.get(1).getHash())).thenReturn(assessmentParticipants.get(1));
        when(notificationSenderMock.sendNotification(notifications.get(0))).thenReturn(serviceSuccess(notifications.get(0)));
        when(notificationSenderMock.sendNotification(notifications.get(1))).thenReturn(serviceSuccess(notifications.get(1)));

        ServiceResult<Void> serviceResult = service.resendInvites(inviteIds, assessorInviteSendResource);
        assertTrue(serviceResult.isSuccess());

        InOrder inOrder = inOrder(assessmentInviteRepositoryMock, assessmentParticipantRepositoryMock, notificationSenderMock);
        inOrder.verify(assessmentInviteRepositoryMock).getByIdIn(inviteIds);
        inOrder.verify(assessmentParticipantRepositoryMock).getByInviteHash(invites.get(0).getHash());
        inOrder.verify(notificationSenderMock).sendNotification(notifications.get(0));
        inOrder.verify(assessmentParticipantRepositoryMock).getByInviteHash(invites.get(1).getHash());
        inOrder.verify(notificationSenderMock).sendNotification(notifications.get(1));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void sendAllInvites_existingUsersGetAssessorRole() throws Exception {
        List<String> emails = asList("john@email.com", "peter@email.com");
        List<String> names = asList("John Barnes", "Peter Jones");

        Competition competition = newCompetition()
                .withName("my competition")
                .withAssessorAcceptsDate(ZonedDateTime.parse("2017-05-24T12:00:00+01:00"))
                .withAssessorDeadlineDate(ZonedDateTime.parse("2017-05-30T12:00:00+01:00"))
                .build();

        List<User> existingUsers = newUser()
                .withFirstName("John", "Peter")
                .withLastName("Barnes", "Jones")
                .withRoles(new HashSet(singleton(Role.APPLICANT)), new HashSet(singleton(Role.ASSESSOR)))
                .build(2);

        List<AssessmentInvite> invites = newAssessmentInvite()
                .withCompetition(competition)
                .withEmail(emails.get(0), emails.get(1))
                .withHash(Invite.generateInviteHash())
                .withName(names.get(0), names.get(1))
                .withStatus(CREATED)
                .withInnovationArea(innovationArea)
                .withUser(newUser().withFirstName("Paul").build())
                .build(2);

        AssessorInviteSendResource assessorInviteSendResource = setUpAssessorInviteSendResource();

        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(assessmentInviteRepositoryMock.getByCompetitionIdAndStatus(competition.getId(), CREATED)).thenReturn(invites);
        when(userRepositoryMock.findByEmail(emails.get(0))).thenReturn(Optional.of(existingUsers.get(0)));
        when(userRepositoryMock.findByEmail(emails.get(1))).thenReturn(Optional.of(existingUsers.get(1)));
        when(notificationSenderMock.sendNotification(isA(Notification.class))).thenAnswer(ServiceResult::serviceSuccess);

        ServiceResult<Void> serviceResult = service.sendAllInvites(competition.getId(), assessorInviteSendResource);
        assertTrue(serviceResult.isSuccess());

        assertTrue(existingUsers.get(0).hasRole(Role.ASSESSOR));
        assertTrue(existingUsers.get(1).hasRole(Role.ASSESSOR));

        InOrder inOrder = inOrder(competitionRepositoryMock,
                                  assessmentInviteRepositoryMock, userRepositoryMock, assessmentParticipantRepositoryMock, notificationSenderMock);
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(assessmentInviteRepositoryMock).getByCompetitionIdAndStatus(competition.getId(), CREATED);

        inOrder.verify(assessmentParticipantRepositoryMock).save(createCompetitionParticipantExpectations(invites.get(0)));
        inOrder.verify(userRepositoryMock).findByEmail(emails.get(0));
        inOrder.verify(notificationSenderMock).sendNotification(isA(Notification.class));

        inOrder.verify(assessmentParticipantRepositoryMock).save(createCompetitionParticipantExpectations(invites.get(1)));
        inOrder.verify(userRepositoryMock).findByEmail(emails.get(1));
        inOrder.verify(notificationSenderMock).sendNotification(isA(Notification.class));

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void resendInvite() throws Exception {
        String email = "john@email.com";
        String name = "John Barnes";

        AssessmentInvite invite = setUpCompetitionInvite(newCompetition().withName("my competition").build(), email, name, SENT, null, newUser()
                .build());

        competitionParticipant = newAssessmentParticipant().withInvite(invite).build();

        AssessorInviteSendResource assessorInviteSendResource = setUpAssessorInviteSendResource();

        Map<String, Object> expectedNotificationArguments = asMap(
                "subject", assessorInviteSendResource.getSubject(),
                "bodyPlain", "content",
                "bodyHtml", "content"
        );

        SystemNotificationSource from = systemNotificationSourceMock;
        NotificationTarget to = new UserNotificationTarget(name, email);
        Notification notification = new Notification(from, singletonList(to), AssessmentInviteServiceImpl.Notifications.INVITE_ASSESSOR, expectedNotificationArguments);

        when(assessmentParticipantRepositoryMock.getByInviteId(invite.getId())).thenReturn(competitionParticipant);
        when(assessmentInviteRepositoryMock.findOne(invite.getId())).thenReturn(invite);
        when(notificationSenderMock.sendNotification(notification)).thenReturn(serviceSuccess(notification));

        ServiceResult<Void> serviceResult = service.resendInvite(invite.getId(), assessorInviteSendResource);
        assertTrue(serviceResult.isSuccess());

        InOrder inOrder = inOrder(assessmentInviteRepositoryMock, assessmentParticipantRepositoryMock, notificationSenderMock);
        inOrder.verify(notificationSenderMock).sendNotification(notification);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void checkExistingUser_hashNotExists() throws Exception {
        when(assessmentInviteRepositoryMock.getByHash(isA(String.class))).thenReturn(null);

        ServiceResult<Boolean> result = service.checkUserExistsForInvite("hash");
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(AssessmentInvite.class, "hash")));

        InOrder inOrder = inOrder(assessmentInviteRepositoryMock, userRepositoryMock);
        inOrder.verify(assessmentInviteRepositoryMock).getByHash("hash");
        inOrder.verify(userRepositoryMock, never()).findByEmail(isA(String.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void checkExistingUser_userExistsOnInvite() throws Exception {
        User user = newUser().build();

        AssessmentInvite assessmentInvite = newAssessmentInvite()
                .withUser(user)
                .withEmail("test@test.com")
                .build();

        when(assessmentInviteRepositoryMock.getByHash("hash")).thenReturn(assessmentInvite);

        assertTrue(service.checkUserExistsForInvite("hash").getSuccess());

        InOrder inOrder = inOrder(assessmentInviteRepositoryMock, userRepositoryMock);
        inOrder.verify(assessmentInviteRepositoryMock).getByHash("hash");
        inOrder.verify(userRepositoryMock, never()).findByEmail(isA(String.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void checkExistingUser_userExistsForEmail() throws Exception {
        User user = newUser().build();

        AssessmentInvite assessmentInvite = newAssessmentInvite()
                .withEmail("test@test.com")
                .build();

        when(assessmentInviteRepositoryMock.getByHash("hash")).thenReturn(assessmentInvite);
        when(userRepositoryMock.findByEmail("test@test.com")).thenReturn(of(user));

        assertTrue(service.checkUserExistsForInvite("hash").getSuccess());

        InOrder inOrder = inOrder(assessmentInviteRepositoryMock, userRepositoryMock);
        inOrder.verify(assessmentInviteRepositoryMock).getByHash("hash");
        inOrder.verify(userRepositoryMock).findByEmail("test@test.com");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void checkExistingUser_userDoesNotExist() throws Exception {
        AssessmentInvite assessmentInvite = newAssessmentInvite()
                .withEmail("test@test.com")
                .build();

        when(assessmentInviteRepositoryMock.getByHash("hash")).thenReturn(assessmentInvite);
        when(userRepositoryMock.findByEmail("test@test.com")).thenReturn(empty());

        assertFalse(service.checkUserExistsForInvite("hash").getSuccess());

        InOrder inOrder = inOrder(assessmentInviteRepositoryMock, userRepositoryMock);
        inOrder.verify(assessmentInviteRepositoryMock).getByHash("hash");
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

        when(assessmentInviteRepositoryMock.findAssessorsByCompetitionAndInnovationArea(competitionId, innovationArea.getId(), pageable))
                .thenReturn(expectedPage);
        when(profileRepositoryMock.findOne(assessors.get(0).getProfileId())).thenReturn(profile.get(0));
        when(profileRepositoryMock.findOne(assessors.get(1).getProfileId())).thenReturn(profile.get(1));
        when(innovationAreaMapperMock.mapToResource(innovationArea)).thenReturn(innovationAreaResources.get(0));

        AvailableAssessorPageResource actual = service.getAvailableAssessors(competitionId, pageable, innovationAreaId)
                .getSuccess();

        verify(assessmentInviteRepositoryMock).findAssessorsByCompetitionAndInnovationArea(competitionId, innovationArea.getId(), pageable);
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

        when(assessmentInviteRepositoryMock.findAssessorsByCompetitionAndInnovationArea(competitionId, innovationAreaId, pageable))
                .thenReturn(assessorPage);

        AvailableAssessorPageResource result = service.getAvailableAssessors(competitionId, pageable, of(innovationAreaId))
                .getSuccess();

        verify(assessmentInviteRepositoryMock).findAssessorsByCompetitionAndInnovationArea(competitionId, innovationAreaId, pageable);

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

        when(assessmentInviteRepositoryMock.findAssessorsByCompetition(competitionId, pageable)).thenReturn(assessorPage);

        AvailableAssessorPageResource result = service.getAvailableAssessors(competitionId, pageable, empty())
                .getSuccess();

        verify(assessmentInviteRepositoryMock).findAssessorsByCompetition(competitionId, pageable);

        assertEquals(page, result.getNumber());
        assertEquals(pageSize, result.getSize());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
        assertEquals(emptyList(), result.getContent());
    }

    @Test
    public void getAvailableAssessors_all() throws Exception {
        long competitionId = 1L;

        List<InnovationAreaResource> innovationAreaResources = newInnovationAreaResource()
                .withName("Emerging Tech and Industries")
                .build(1);

        InnovationArea innovationArea = newInnovationArea()
                .withName("Emerging Tech and Industries")
                .build();

        List<Long> expectedAssessorIds = asList(4L, 8L);
        List<AvailableAssessorResource> expectedAssessors = newAvailableAssessorResource()
                .withId(expectedAssessorIds.get(0), expectedAssessorIds.get(1))
                .withName("Jeremy Alufson", "Felix Wilson")
                .withCompliant(TRUE)
                .withEmail("worth.email.test+assessor1@gmail.com", "felix.wilson@gmail.com")
                .withBusinessType(BUSINESS, ACADEMIC)
                .withInnovationAreas(innovationAreaResources)
                .build(2);

        List<Profile> profiles = newProfile()
                .withSkillsAreas("Java", "Javascript")
                .withInnovationArea(innovationArea)
                .withBusinessType(BUSINESS, ACADEMIC)
                .withAgreementSignedDate(now())
                .build(2);

        List<User> assessorUsers = newUser()
                .withId(expectedAssessorIds.get(0), expectedAssessorIds.get(1))
                .withFirstName("Jeremy", "Felix")
                .withLastName("Alufson", "Wilson")
                .withEmailAddress("worth.email.test+assessor1@gmail.com", "felix.wilson@gmail.com")
                .withAffiliations(newAffiliation()
                        .withAffiliationType(EMPLOYER)
                        .withOrganisation("Hive IT")
                        .withPosition("Software Developer")
                        .withExists(true)
                        .build(1))
                .withProfileId(profiles.get(0).getId(), profiles.get(1).getId())
                .build(2);

        Optional<Long> innovationAreaId = of(innovationArea.getId());


        when(assessmentInviteRepositoryMock.findAssessorsByCompetitionAndInnovationArea(competitionId, innovationArea.getId()))
                .thenReturn(assessorUsers);

        List<Long> actualAssessorIds = service.getAvailableAssessorIds(competitionId, innovationAreaId)
                .getSuccess();

        verify(assessmentInviteRepositoryMock).findAssessorsByCompetitionAndInnovationArea(competitionId, innovationArea.getId());

        assertEquals(expectedAssessorIds, actualAssessorIds);
    }

    @Test
    public void getCreatedInvites() throws Exception {
        long competitionId = 1L;

        InnovationArea innovationArea = newInnovationArea().build();
        InnovationAreaResource innovationAreaResource = newInnovationAreaResource()
                .withId(2L)
                .withName("Earth Observation")
                .build();
        List<InnovationAreaResource> innovationAreaList = singletonList(innovationAreaResource);

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

        List<AssessmentInvite> existingUserInvites = newAssessmentInvite()
                .withId(1L, 2L, 3L, 4L)
                .withName("John Barnes", "Dave Smith", "Richard Turner", "Oliver Romero")
                .withEmail("john@example.com", "dave@example.com", "richard@example.com", "oliver@example.com")
                .withUser(compliantUser, nonCompliantUserNoSkills, nonCompliantUserNoAffiliations, nonCompliantUserNoAgreement)
                .withInnovationArea()
                .build(4);

        AssessmentInvite newUserInvite = newAssessmentInvite()
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
        Page<AssessmentInvite> page = new PageImpl<>(combineLists(existingUserInvites, newUserInvite), pageable, totalElements);

        when(assessmentInviteRepositoryMock.getByCompetitionIdAndStatus(competitionId, CREATED, pageable)).thenReturn(page);
        when(innovationAreaMapperMock.mapToResource(innovationArea)).thenReturn(innovationAreaResource);
        when(assessorCreatedInviteMapperMock.mapToResource(isA(AssessmentInvite.class))).thenReturn(
                expectedInvites.get(0),
                expectedInvites.get(1),
                expectedInvites.get(2),
                expectedInvites.get(3),
                expectedInvites.get(4)
        );
        when(profileRepositoryMock.findOne(profile1.getId())).thenReturn(profile1);
        when(profileRepositoryMock.findOne(profile2.getId())).thenReturn(profile2);
        when(profileRepositoryMock.findOne(profile3.getId())).thenReturn(profile3);
        when(profileRepositoryMock.findOne(profile4.getId())).thenReturn(profile4);

        AssessorCreatedInvitePageResource actual = service.getCreatedInvites(competitionId, pageable).getSuccess();
        assertEquals(totalElements, actual.getTotalElements());
        assertEquals(5, actual.getTotalPages());
        assertEquals(expectedInvites, actual.getContent());
        assertEquals(0, actual.getNumber());
        assertEquals(20, actual.getSize());

        InOrder inOrder = inOrder(assessmentInviteRepositoryMock, assessorCreatedInviteMapperMock);
        inOrder.verify(assessmentInviteRepositoryMock).getByCompetitionIdAndStatus(competitionId, CREATED, pageable);
        inOrder.verify(assessorCreatedInviteMapperMock, times(5)).mapToResource(isA(AssessmentInvite.class));
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

        when(assessmentInviteRepositoryMock.countByCompetitionIdAndStatusIn(competitionId, EnumSet.of(OPENED, SENT))).thenReturn(expected.getInvited());
        when(assessmentInviteRepositoryMock.countByCompetitionIdAndStatusIn(competitionId, EnumSet.of(CREATED))).thenReturn(expected.getInviteList());
        when(assessmentParticipantRepositoryMock.countByCompetitionIdAndRoleAndStatus(competitionId, ASSESSOR, ACCEPTED)).thenReturn(expected.getAccepted());
        when(assessmentParticipantRepositoryMock.countByCompetitionIdAndRoleAndStatus(competitionId, ASSESSOR, REJECTED)).thenReturn(expected.getDeclined());
        CompetitionInviteStatisticsResource actual = service.getInviteStatistics(competitionId).getSuccess();
        assertEquals(expected, actual);

        InOrder inOrder = inOrder(assessmentInviteRepositoryMock, assessmentParticipantRepositoryMock);
        inOrder.verify(assessmentInviteRepositoryMock).countByCompetitionIdAndStatusIn(competitionId, EnumSet.of(OPENED, SENT));
        inOrder.verify(assessmentInviteRepositoryMock).countByCompetitionIdAndStatusIn(competitionId, EnumSet.of(CREATED));
        inOrder.verify(assessmentParticipantRepositoryMock).countByCompetitionIdAndRoleAndStatus(competitionId, ASSESSOR, ACCEPTED);
        inOrder.verify(assessmentParticipantRepositoryMock).countByCompetitionIdAndRoleAndStatus(competitionId, ASSESSOR, REJECTED);
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
                .withUserId(newUser.getId())
                .withCompetitionId(competition.getId())
                .build();

        AssessmentInvite assessmentInvite = newAssessmentInvite()
                .withCompetition(competition)
                .withHash(Invite.generateInviteHash())
                .withEmail(newUser.getEmail())
                .withName(newUser.getName())
                .withInnovationArea(newInnovationArea().build())
                .build();

        CompetitionInviteResource expectedInviteResource = newCompetitionInviteResource().build();

        when(userRepositoryMock.findOne(newUser.getId())).thenReturn(newUser);
        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);

        AssessmentInvite inviteExpectation = createInviteExpectations(newUser.getName(), newUser.getEmail(), CREATED, competition, null);

        when(assessmentInviteRepositoryMock.save(inviteExpectation)).thenReturn(assessmentInvite);
        when(competitionInviteMapperMock.mapToResource(assessmentInvite)).thenReturn(expectedInviteResource);

        CompetitionInviteResource invite = service.inviteUser(existingAssessor).getSuccess();

        assertEquals(expectedInviteResource, invite);

        InOrder inOrder = inOrder(userRepositoryMock, competitionRepositoryMock,
                                  assessmentInviteRepositoryMock, competitionInviteMapperMock);
        inOrder.verify(userRepositoryMock).findOne(newUser.getId());
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(assessmentInviteRepositoryMock).save(createInviteExpectations(newUser.getName(), newUser.getEmail(), CREATED, competition, null));
        inOrder.verify(competitionInviteMapperMock).mapToResource(assessmentInvite);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteUsers_existing() throws Exception {
        List<User> existingUsers = newUser()
                .withEmailAddress("fred.smith@abc.com", "joe.brown@abc.com")
                .withFirstName("fred", "joe")
                .withLastName("smith", "brown")
                .build(2);

        Competition competition = newCompetition()
                .withName("competition name")
                .build();

        List<ExistingUserStagedInviteResource> existingAssessors = newExistingUserStagedInviteResource()
                .withUserId(existingUsers.get(0).getId(), existingUsers.get(1).getId())
                .withCompetitionId(competition.getId())
                .build(2);

        when(userRepositoryMock.findOne(existingUsers.get(0).getId())).thenReturn(existingUsers.get(0));
        when(userRepositoryMock.findOne(existingUsers.get(1).getId())).thenReturn(existingUsers.get(1));
        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(assessmentInviteRepositoryMock.save(isA(AssessmentInvite.class))).thenReturn(new AssessmentInvite());

        ServiceResult<Void> serviceResult = service.inviteUsers(existingAssessors);
        assertTrue(serviceResult.isSuccess());

        InOrder inOrder = inOrder(userRepositoryMock, competitionRepositoryMock, assessmentInviteRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(existingAssessors.get(0).getUserId());
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(assessmentInviteRepositoryMock).save(createInviteExpectations(existingUsers.get(0).getName(), existingUsers.get(0).getEmail(), CREATED, competition, null));
        inOrder.verify(userRepositoryMock).findOne(existingAssessors.get(1).getUserId());
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(assessmentInviteRepositoryMock).save(createInviteExpectations(existingUsers.get(1).getName(), existingUsers.get(1).getEmail(), CREATED, competition, null));
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

        AssessmentInvite assessmentInvite = newAssessmentInvite()
                .withCompetition(competition)
                .withHash(Invite.generateInviteHash())
                .withName(newAssessorName)
                .withEmail(newAssessorEmail)
                .withInnovationArea(innovationArea)
                .build();

        CompetitionInviteResource expectedInviteResource = newCompetitionInviteResource().build();

        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(innovationAreaRepositoryMock.findOne(innovationArea.getId())).thenReturn(innovationArea);
        when(assessmentInviteRepositoryMock.getByEmailAndCompetitionId(newAssessorEmail, competition.getId())).thenReturn(null);

        AssessmentInvite inviteExpectation = createInviteExpectations(newAssessorName, newAssessorEmail, CREATED, competition, innovationArea);
        when(assessmentInviteRepositoryMock.save(inviteExpectation)).thenReturn(assessmentInvite);
        when(competitionInviteMapperMock.mapToResource(assessmentInvite)).thenReturn(expectedInviteResource);

        ServiceResult<CompetitionInviteResource> serviceResult = service.inviteUser(newAssessor);
        assertTrue(serviceResult.isSuccess());

        CompetitionInviteResource invite = serviceResult.getSuccess();
        assertEquals(expectedInviteResource, invite);

        InOrder inOrder = inOrder(innovationAreaRepositoryMock, competitionRepositoryMock,
                                  assessmentInviteRepositoryMock, competitionInviteMapperMock, userRepositoryMock);
        inOrder.verify(assessmentInviteRepositoryMock).getByEmailAndCompetitionId(newAssessorEmail, competition.getId());
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(innovationAreaRepositoryMock).findOne(innovationArea.getId());
        inOrder.verify(assessmentInviteRepositoryMock).save(createInviteExpectations(newAssessorName, newAssessorEmail, CREATED, competition, innovationArea));
        inOrder.verify(competitionInviteMapperMock).mapToResource(assessmentInvite);
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
        when(assessmentInviteRepositoryMock.getByEmailAndCompetitionId(newAssessorEmail, competition.getId())).thenReturn(null);

        ServiceResult<CompetitionInviteResource> serviceResult = service.inviteUser(newAssessor);

        assertFalse(serviceResult.isSuccess());

        InOrder inOrder = inOrder(innovationAreaRepositoryMock, competitionRepositoryMock,
                                  assessmentInviteRepositoryMock, competitionInviteMapperMock, userRepositoryMock);
        inOrder.verify(assessmentInviteRepositoryMock).getByEmailAndCompetitionId(newAssessorEmail, competition.getId());
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

        when(assessmentInviteRepositoryMock.getByEmailAndCompetitionId(newAssessorEmail, competition.getId())).thenReturn(new AssessmentInvite());

        ServiceResult<CompetitionInviteResource> serviceResult = service.inviteUser(newAssessor);

        assertFalse(serviceResult.isSuccess());

        InOrder inOrder = inOrder(competitionRepositoryMock,
                                  assessmentInviteRepositoryMock, competitionInviteMapperMock, userRepositoryMock);
        inOrder.verify(assessmentInviteRepositoryMock).getByEmailAndCompetitionId(newAssessorEmail, competition.getId());
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

        when(assessmentInviteRepositoryMock.getByEmailAndCompetitionId(newAssessorEmail, competitionId)).thenReturn(null);
        when(competitionRepositoryMock.findOne(competitionId)).thenReturn(null);

        ServiceResult<CompetitionInviteResource> serviceResult = service.inviteUser(newAssessor);

        assertFalse(serviceResult.isSuccess());

        InOrder inOrder = inOrder(competitionRepositoryMock,
                                  assessmentInviteRepositoryMock, competitionInviteMapperMock, userRepositoryMock);
        inOrder.verify(assessmentInviteRepositoryMock).getByEmailAndCompetitionId(newAssessorEmail, competitionId);
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

        List<AssessmentInvite> pagedResult = newAssessmentInvite()
                .withId(1L,2L)
                .withCompetition(competition, competition)
                .withEmail(testEmail1,testEmail2)
                .withHash("1dc914e2-d076-4b15-9fa6-99ee5b711613", "bddd15e6-9e9d-42e8-88b0-42f3abcbb26e")
                .withName(testName1,testName2)
                .withStatus(CREATED, CREATED)
                .withInnovationArea(innovationArea, innovationArea)
                .build(2);

        List<AssessorCreatedInviteResource> createdInviteResources = newAssessorCreatedInviteResource()
                .withEmail(testEmail1, testEmail2)
                .build(2);

        Pageable pageable = new PageRequest(0, 20, new Sort(ASC, "name"));

        Page<AssessmentInvite> pageResult = new PageImpl<>(pagedResult, pageable, 10);

        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(assessmentInviteRepositoryMock.getByEmailAndCompetitionId(isA(String.class), isA(Long.class))).thenReturn(null);
        when(innovationAreaRepositoryMock.findOne(innovationArea.getId())).thenReturn(innovationArea);
        when(assessmentInviteRepositoryMock.save(isA(AssessmentInvite.class))).thenReturn(new AssessmentInvite());
        when(assessmentInviteRepositoryMock.getByCompetitionIdAndStatus(competition.getId(), CREATED, pageable)).thenReturn(pageResult);
        when(assessorCreatedInviteMapperMock.mapToResource(pagedResult.get(0))).thenReturn(createdInviteResources.get(0));
        when(assessorCreatedInviteMapperMock.mapToResource(pagedResult.get(1))).thenReturn(createdInviteResources.get(1));
        when(userRepositoryMock.findByEmail(testEmail1)).thenReturn(null);

        ServiceResult<Void> serviceResult = service.inviteNewUsers(newUserInvites, competition.getId());

        assertFalse(serviceResult.isSuccess());

        InOrder inOrder = inOrder(
                competitionRepositoryMock,
                assessmentInviteRepositoryMock,
                userRepositoryMock,
                innovationAreaMapperMock,
                assessorCreatedInviteMapperMock
        );
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(assessmentInviteRepositoryMock).getByCompetitionIdAndStatus(competition.getId(), CREATED, pageable);
        inOrder.verify(userRepositoryMock).findByEmail(testEmail1);
        inOrder.verify(assessmentInviteRepositoryMock).getByEmailAndCompetitionId(testEmail1, competition.getId());
        inOrder.verify(assessmentInviteRepositoryMock).getByCompetitionIdAndStatus(competition.getId(), CREATED, pageable);
        inOrder.verify(assessorCreatedInviteMapperMock, times(2)).mapToResource(isA(AssessmentInvite.class));
        inOrder.verify(userRepositoryMock).findByEmail(testEmail2);
        inOrder.verify(assessmentInviteRepositoryMock).getByEmailAndCompetitionId(testEmail2, competition.getId());
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

        Pageable pageable = new PageRequest(0, 20, new Sort(ASC, "name"));

        List<AssessmentInvite> pagedResult = newAssessmentInvite()
                .withId(1L,2L)
                .withCompetition(competition, competition)
                .withEmail(testEmail1,testEmail2)
                .withHash("1dc914e2-d076-4b15-9fa6-99ee5b711613", "bddd15e6-9e9d-42e8-88b0-42f3abcbb26e")
                .withName(testName1,testName2)
                .withStatus(CREATED, CREATED)
                .withInnovationArea(innovationArea, innovationArea)
                .build(2);

        List<AssessorCreatedInviteResource> createdInviteResources = newAssessorCreatedInviteResource()
                .withEmail(testEmail1, testEmail2)
                .build(2);

        Page<AssessmentInvite> pageResult = new PageImpl<>(pagedResult, pageable, 10);

        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(assessmentInviteRepositoryMock.getByEmailAndCompetitionId(testEmail1, competition.getId()))
                .thenReturn(new AssessmentInvite());
        when(assessmentInviteRepositoryMock.getByEmailAndCompetitionId(testEmail2, competition.getId())).thenReturn(null);
        when(innovationAreaRepositoryMock.findOne(innovationArea.getId())).thenReturn(innovationArea);
        when(assessmentInviteRepositoryMock.save(isA(AssessmentInvite.class))).thenReturn(new AssessmentInvite());
        when(userRepositoryMock.findByEmail(testEmail1)).thenReturn(null);
        when(assessmentInviteRepositoryMock.getByCompetitionIdAndStatus(competition.getId(), CREATED, pageable)).thenReturn(pageResult);
        when(assessorCreatedInviteMapperMock.mapToResource(pagedResult.get(0))).thenReturn(createdInviteResources.get(0));
        when(assessorCreatedInviteMapperMock.mapToResource(pagedResult.get(1))).thenReturn(createdInviteResources.get(1));

        ServiceResult<Void> serviceResult = service.inviteNewUsers(newUserInvites, competition.getId());

        assertFalse(serviceResult.isSuccess());
        assertEquals(2, serviceResult.getErrors().size());
        assertEquals("test1@test.com", serviceResult.getErrors().get(0).getFieldRejectedValue());

        InOrder inOrder = inOrder(
                competitionRepositoryMock,
                assessmentInviteRepositoryMock,
                userRepositoryMock,
                innovationAreaMapperMock,
                assessorCreatedInviteMapperMock
        );
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(assessmentInviteRepositoryMock).getByCompetitionIdAndStatus(competition.getId(), CREATED, pageable);
        inOrder.verify(userRepositoryMock).findByEmail(testEmail1);
        inOrder.verify(assessmentInviteRepositoryMock).getByEmailAndCompetitionId(testEmail1, competition.getId());
        inOrder.verify(assessmentInviteRepositoryMock).getByCompetitionIdAndStatus(competition.getId(), CREATED, pageable);
        inOrder.verify(assessorCreatedInviteMapperMock, times(2)).mapToResource(isA(AssessmentInvite.class));
        inOrder.verify(userRepositoryMock).findByEmail(testEmail2);
        inOrder.verify(assessmentInviteRepositoryMock).getByEmailAndCompetitionId(testEmail2, competition.getId());
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
        when(assessmentInviteRepositoryMock.getByEmailAndCompetitionId(isA(String.class), isA(Long.class))).thenReturn(null);

        ServiceResult<Void> serviceResult = service.inviteNewUsers(newUserInvites, competitionId);

        assertFalse(serviceResult.isSuccess());

        InOrder inOrder = inOrder(competitionRepositoryMock, assessmentInviteRepositoryMock);
        inOrder.verify(competitionRepositoryMock).findOne(competitionId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteNewUsers_categoryNotFound() throws Exception {
        Competition competition = newCompetition()
                .withId(1L)
                .build();

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

        InnovationArea innovationArea_ = new InnovationArea();

        List<AssessmentInvite> pagedResult = newAssessmentInvite()
                .withId(1L,2L)
                .withCompetition(competition, competition)
                .withEmail(testEmail1,testEmail2)
                .withHash("1dc914e2-d076-4b15-9fa6-99ee5b711613", "bddd15e6-9e9d-42e8-88b0-42f3abcbb26e")
                .withName(testName1,testName2)
                .withStatus(CREATED, CREATED)
                .withInnovationArea(innovationArea_, innovationArea_)
                .build(2);

        List<AssessorCreatedInviteResource> createdInviteResources = newAssessorCreatedInviteResource()
                .withEmail(testEmail1, testEmail2)
                .build(2);

        Pageable pageable = new PageRequest(0, 20, new Sort(ASC, "name"));

        Page<AssessmentInvite> pageResult = new PageImpl<>(pagedResult, pageable, 10);

        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(assessmentInviteRepositoryMock.getByEmailAndCompetitionId(isA(String.class), isA(Long.class))).thenReturn(null);
        when(innovationAreaRepositoryMock.findOne(categoryId)).thenReturn(null);
        when(assessmentInviteRepositoryMock.save(isA(AssessmentInvite.class))).thenReturn(new AssessmentInvite());
        when(assessmentInviteRepositoryMock.getByCompetitionIdAndStatus(competition.getId(), CREATED, pageable)).thenReturn(pageResult);
        when(assessorCreatedInviteMapperMock.mapToResource(pagedResult.get(0))).thenReturn(createdInviteResources.get(0));
        when(assessorCreatedInviteMapperMock.mapToResource(pagedResult.get(1))).thenReturn(createdInviteResources.get(1));
        when(userRepositoryMock.findByEmail(isA(String.class))).thenReturn(null);

        ServiceResult<Void> serviceResult = service.inviteNewUsers(newUserInvites, competition.getId());

        assertFalse(serviceResult.isSuccess());

        InOrder inOrder = inOrder(
                competitionRepositoryMock,
                userRepositoryMock,
                assessmentInviteRepositoryMock,
                innovationAreaRepositoryMock,
                assessorCreatedInviteMapperMock
        );
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(assessmentInviteRepositoryMock).getByCompetitionIdAndStatus(competition.getId(), CREATED, pageable);
        inOrder.verify(userRepositoryMock).findByEmail(testEmail1);
        inOrder.verify(assessmentInviteRepositoryMock).getByEmailAndCompetitionId(testEmail1, competition.getId());
        inOrder.verify(assessmentInviteRepositoryMock).getByCompetitionIdAndStatus(competition.getId(), CREATED, pageable);
        inOrder.verify(assessorCreatedInviteMapperMock, times(2)).mapToResource(isA(AssessmentInvite.class));
        inOrder.verify(userRepositoryMock).findByEmail(testEmail2);
        inOrder.verify(assessmentInviteRepositoryMock).getByEmailAndCompetitionId(testEmail2, competition.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void deleteInvite() {
        String email = "tom@poly.io";
        long competitionId = 11L;

        AssessmentInvite assessmentInvite = newAssessmentInvite()
                .withStatus(CREATED)
                .build();

        when(assessmentInviteRepositoryMock.getByEmailAndCompetitionId(email, competitionId)).thenReturn(assessmentInvite);

        service.deleteInvite(email, competitionId).getSuccess();

        InOrder inOrder = inOrder(assessmentInviteRepositoryMock);
        inOrder.verify(assessmentInviteRepositoryMock).getByEmailAndCompetitionId(email, competitionId);
        inOrder.verify(assessmentInviteRepositoryMock).delete(assessmentInvite);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void deleteInvite_sent() {
        String email = "tom@poly.io";
        long competitionId = 11L;
        AssessmentInvite assessmentInvite = newAssessmentInvite()
                .withStatus(SENT)
                .build();

        when(assessmentInviteRepositoryMock.getByEmailAndCompetitionId(email, competitionId)).thenReturn(assessmentInvite);

        ServiceResult<Void> serviceResult = service.deleteInvite(email, competitionId);

        assertTrue(serviceResult.isFailure());

        verify(assessmentInviteRepositoryMock).getByEmailAndCompetitionId(email, competitionId);
        verifyNoMoreInteractions(assessmentInviteRepositoryMock);
    }

    @Test
    public void deleteAllInvites() throws Exception {
        long competitionId = 1L;

        when(competitionRepositoryMock.findOne(competitionId)).thenReturn(newCompetition().build());

        assertTrue(service.deleteAllInvites(competitionId).isSuccess());

        verify(competitionRepositoryMock).findOne(competitionId);
    }

    @Test
    public void deleteAllInvites_noCompetition() throws Exception {
        long competitionId = 1L;

        when(competitionRepositoryMock.findOne(competitionId)).thenReturn(null);

        assertFalse(service.deleteAllInvites(competitionId).isSuccess());

        verify(competitionRepositoryMock).findOne(competitionId);
    }

    @Test
    public void acceptInvite_newAssessor() {
        InnovationArea innovationArea = newInnovationArea().build();
        AssessmentParticipant competitionParticipant = newAssessmentParticipant()
                .withInvite(newAssessmentInvite()
                        .withStatus(OPENED)
                        .withInnovationArea(innovationArea)
                )
                .build();

        when(assessmentParticipantRepositoryMock.getByInviteHash(INVITE_HASH)).thenReturn(competitionParticipant);
        when(profileRepositoryMock.findOne(user.getProfileId())).thenReturn(profile); // move to setup?

        service.acceptInvite(INVITE_HASH, userResource).getSuccess();

        assertEquals(ACCEPTED, competitionParticipant.getStatus());
        assertEquals(user, competitionParticipant.getUser());
        assertEquals(singleton(innovationArea), profile.getInnovationAreas());

        InOrder inOrder = inOrder(assessmentParticipantRepositoryMock, userRepositoryMock, profileRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(user.getId());
        inOrder.verify(assessmentParticipantRepositoryMock).getByInviteHash(INVITE_HASH);
        inOrder.verify(profileRepositoryMock).findOne(user.getProfileId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptInvite_newAssessorExistingInnovationArea() {
        InnovationArea innovationArea = newInnovationArea().build();
        AssessmentParticipant competitionParticipant = newAssessmentParticipant()
                .withInvite(newAssessmentInvite()
                        .withStatus(OPENED)
                        .withInnovationArea(innovationArea)
                        .withUser(newUser().build())
                )
                .build();

        // profile with the innovation area already in place
        profile = newProfile()
                .withInnovationArea(innovationArea)
                .build();

        when(assessmentParticipantRepositoryMock.getByInviteHash(INVITE_HASH)).thenReturn(competitionParticipant);
        when(profileRepositoryMock.findOne(user.getProfileId())).thenReturn(profile); // move to setup?

        service.acceptInvite(INVITE_HASH, userResource).getSuccess();

        assertEquals(ACCEPTED, competitionParticipant.getStatus());
        assertEquals(user, competitionParticipant.getUser());
        assertEquals(singleton(innovationArea), profile.getInnovationAreas());

        InOrder inOrder = inOrder(assessmentParticipantRepositoryMock, userRepositoryMock, profileRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(user.getId());
        inOrder.verify(assessmentParticipantRepositoryMock).getByInviteHash(INVITE_HASH);
        inOrder.verify(profileRepositoryMock).findOne(user.getProfileId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptInvite_newAssessorDifferentInnovationArea() {
        InnovationArea innovationArea = newInnovationArea().build();
        AssessmentParticipant competitionParticipant = newAssessmentParticipant()
                .withInvite(newAssessmentInvite()
                        .withStatus(OPENED)
                        .withInnovationArea(innovationArea)
                        .withUser(newUser().build())
                )
                .build();

        // profile with the innovation area already in place
        profile = newProfile()
                .withInnovationArea(newInnovationArea().build())
                .build();

        when(assessmentParticipantRepositoryMock.getByInviteHash(INVITE_HASH)).thenReturn(competitionParticipant);
        when(profileRepositoryMock.findOne(user.getProfileId())).thenReturn(profile); // move to setup?

        service.acceptInvite(INVITE_HASH, userResource).getSuccess();

        assertEquals(ACCEPTED, competitionParticipant.getStatus());
        assertEquals(user, competitionParticipant.getUser());
        assertEquals(singleton(innovationArea), profile.getInnovationAreas());

        InOrder inOrder = inOrder(assessmentParticipantRepositoryMock, userRepositoryMock, profileRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(user.getId());
        inOrder.verify(assessmentParticipantRepositoryMock).getByInviteHash(INVITE_HASH);
        inOrder.verify(profileRepositoryMock).findOne(user.getProfileId());
        inOrder.verifyNoMoreInteractions();
    }

    private AssessorInviteSendResource setUpAssessorInviteSendResource() {
        return newAssessorInviteSendResource()
                .withSubject("subject")
                .withContent("content")
                .build();
    }

    private AssessmentInvite setUpCompetitionInvite(Competition competition, InviteStatus status, InnovationArea innovationArea) {
        return newAssessmentInvite()
                .withCompetition(competition)
                .withHash(Invite.generateInviteHash())
                .withStatus(status)
                .withInnovationArea(innovationArea)
                .build();
    }

    private AssessmentInvite setUpCompetitionInvite(Competition competition, String email, String name, InviteStatus status, InnovationArea innovationArea, User user) {
        return newAssessmentInvite()
                .withCompetition(competition)
                .withEmail(email)
                .withHash(Invite.generateInviteHash())
                .withInnovationArea(innovationArea)
                .withName(name)
                .withStatus(status)
                .withUser(user)
                .build();
    }

    private AssessmentInvite createInviteExpectations(String name, String email, InviteStatus status, Competition competition, Category innovationArea) {
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

    private static AssessmentParticipant createCompetitionParticipantExpectations(AssessmentInvite assessmentInvite) {
        return createLambdaMatcher(competitionParticipant -> {
            assertNull(competitionParticipant.getId());
            assertEquals(assessmentInvite.getTarget(), competitionParticipant.getProcess());
            assertEquals(assessmentInvite, competitionParticipant.getInvite());
            assertEquals(ASSESSOR, competitionParticipant.getRole());
            assertEquals(assessmentInvite.getUser(), competitionParticipant.getUser());
        });
    }

    @Test
    public void getInvitationOverview_allFilters() throws Exception {
        long competitionId = 1L;
        Pageable pageable = new PageRequest(0, 5);
        Long innovationArea = 2L;
        ParticipantStatus status = ParticipantStatus.PENDING;
        Boolean compliant = true;

        List<AssessmentParticipant> expectedParticipants = newAssessmentParticipant()
                .withInvite(
                        newAssessmentInvite()
                                .withName("Name 1", "Name 2", "Name 3", "Name 4", "Name 5")
                                .withSentOn(now())
                                .withStatus(SENT)
                                .withInnovationArea(newInnovationArea().build())
                                .buildArray(5, AssessmentInvite.class)
                )
                .withStatus(PENDING)
                .build(5);

        Page<AssessmentParticipant> pageResult = new PageImpl<>(expectedParticipants, pageable, 10);

        when(assessmentParticipantRepositoryMock.getAssessorsByCompetitionAndInnovationAreaAndStatusContainsAndCompliant(
                competitionId,
                innovationArea,
                singletonList(status),
                compliant,
                pageable
        ))
                .thenReturn(pageResult);

        List<AssessorInviteOverviewResource> overviewResources = newAssessorInviteOverviewResource()
                .withName("Name 1", "Name 2", "Name 3", "Name 4", "Name 5")
                .build(5);

        when(assessorInviteOverviewMapperMock.mapToResource(isA(AssessmentParticipant.class)))
                .thenReturn(
                        overviewResources.get(0),
                        overviewResources.get(1),
                        overviewResources.get(2),
                        overviewResources.get(3),
                        overviewResources.get(4)
                );

        ServiceResult<AssessorInviteOverviewPageResource> result = service.getInvitationOverview(
                competitionId,
                pageable,
                of(innovationArea),
                singletonList(status),
                of(compliant)
        );

        verify(assessmentParticipantRepositoryMock).getAssessorsByCompetitionAndInnovationAreaAndStatusContainsAndCompliant(
                competitionId,
                innovationArea,
                singletonList(status),
                compliant,
                pageable
        );
        verify(assessorInviteOverviewMapperMock, times(5)).mapToResource(isA(AssessmentParticipant.class));

        assertTrue(result.isSuccess());

        AssessorInviteOverviewPageResource pageResource = result.getSuccess();

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
    public void getInvitationOverview_noFilters() throws Exception {
        long competitionId = 1L;
        Pageable pageable = new PageRequest(0, 5);
        List<AssessmentParticipant> expectedParticipants = newAssessmentParticipant()
                .withInvite(
                        newAssessmentInvite()
                                .withName("Name 1", "Name 2", "Name 3", "Name 4", "Name 5")
                                .withSentOn(now())
                                .withStatus(SENT)
                                .withInnovationArea(newInnovationArea().build())
                                .buildArray(5, AssessmentInvite.class)
                )
                .withStatus(PENDING)
                .build(5);

        Page<AssessmentParticipant> pageResult = new PageImpl<>(expectedParticipants, pageable, 10);

        when(assessmentParticipantRepositoryMock.getAssessorsByCompetitionAndStatusContains(competitionId, singletonList(PENDING), pageable))
                .thenReturn(pageResult);

        List<AssessorInviteOverviewResource> overviewResources = newAssessorInviteOverviewResource()
                .withName("Name 1", "Name 2", "Name 3", "Name 4", "Name 5")
                .build(5);

        when(assessorInviteOverviewMapperMock.mapToResource(isA(AssessmentParticipant.class)))
                .thenReturn(
                        overviewResources.get(0),
                        overviewResources.get(1),
                        overviewResources.get(2),
                        overviewResources.get(3),
                        overviewResources.get(4)
                );


        ServiceResult<AssessorInviteOverviewPageResource> result = service.getInvitationOverview(competitionId, pageable, empty(), singletonList(PENDING), empty());

        verify(assessmentParticipantRepositoryMock).getAssessorsByCompetitionAndStatusContains(competitionId, singletonList(PENDING), pageable);
        verify(assessorInviteOverviewMapperMock, times(5)).mapToResource(isA(AssessmentParticipant.class));

        assertTrue(result.isSuccess());

        AssessorInviteOverviewPageResource pageResource = result.getSuccess();

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

    @Test
    public void getAssessorsNotAcceptedInviteIds() throws Exception {
        long competitionId = 1L;

        List<AssessmentInvite> invites = newAssessmentInvite()
                .withName("Name 1", "Name 2", "Name 3", "Name 4", "Name 5")
                .withSentOn(now())
                .withStatus(SENT)
                .withInnovationArea(newInnovationArea().build())
                .build(5);

        List<AssessmentParticipant> expectedParticipants = newAssessmentParticipant()
                .withInvite(invites.get(0), invites.get(1), invites.get(2), invites.get(3),invites.get(4))
                .withStatus(PENDING, PENDING, PENDING, PENDING, REJECTED)
                .build(5);

        when(assessmentParticipantRepositoryMock.getAssessorsByCompetitionAndStatusContains(competitionId, asList(PENDING, REJECTED)))
                .thenReturn(expectedParticipants);

        ServiceResult<List<Long>> result = service.getAssessorsNotAcceptedInviteIds(competitionId, empty(), asList(PENDING, REJECTED), empty());

        verify(assessmentParticipantRepositoryMock).getAssessorsByCompetitionAndStatusContains(competitionId, asList(PENDING, REJECTED));

        assertTrue(result.isSuccess());
        List<Long> returnedInviteIds = result.getSuccess();

        assertEquals(5, returnedInviteIds.size());
        assertEquals(invites.get(0).getId(), returnedInviteIds.get(0));
        assertEquals(invites.get(1).getId(), returnedInviteIds.get(1));
        assertEquals(invites.get(2).getId(), returnedInviteIds.get(2));
        assertEquals(invites.get(3).getId(), returnedInviteIds.get(3));
        assertEquals(invites.get(4).getId(), returnedInviteIds.get(4));
    }
}