 package org.innovateuk.ifs.assessment.transactional;

 import org.innovateuk.ifs.BaseServiceUnitTest;
 import org.innovateuk.ifs.assessment.domain.AssessmentInvite;
 import org.innovateuk.ifs.assessment.domain.AssessmentParticipant;
 import org.innovateuk.ifs.assessment.mapper.AssessmentInviteMapper;
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
 import org.innovateuk.ifs.competition.domain.CompetitionParticipant;
 import org.innovateuk.ifs.competition.domain.Milestone;
 import org.innovateuk.ifs.competition.repository.CompetitionRepository;
 import org.innovateuk.ifs.invite.builder.RejectionReasonResourceBuilder;
 import org.innovateuk.ifs.invite.constant.InviteStatus;
 import org.innovateuk.ifs.invite.domain.Invite;
 import org.innovateuk.ifs.invite.domain.ParticipantStatus;
 import org.innovateuk.ifs.invite.domain.RejectionReason;
 import org.innovateuk.ifs.invite.repository.RejectionReasonRepository;
 import org.innovateuk.ifs.invite.repository.RoleInviteRepository;
 import org.innovateuk.ifs.invite.resource.*;
 import org.innovateuk.ifs.notifications.resource.*;
 import org.innovateuk.ifs.notifications.service.NotificationService;
 import org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer;
 import org.innovateuk.ifs.profile.domain.Profile;
 import org.innovateuk.ifs.profile.repository.ProfileRepository;
 import org.innovateuk.ifs.security.LoggedInUserSupplier;
 import org.innovateuk.ifs.user.domain.Agreement;
 import org.innovateuk.ifs.user.domain.User;
 import org.innovateuk.ifs.user.mapper.UserMapper;
 import org.innovateuk.ifs.user.repository.RoleProfileStatusRepository;
 import org.innovateuk.ifs.user.repository.UserRepository;
 import org.innovateuk.ifs.user.resource.Role;
 import org.innovateuk.ifs.user.resource.UserResource;
 import org.innovateuk.ifs.user.transactional.UserService;
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

 import static java.lang.String.format;
 import static java.time.ZonedDateTime.now;
 import static java.time.format.DateTimeFormatter.ofPattern;
 import static java.util.Arrays.asList;
 import static java.util.Collections.*;
 import static java.util.Optional.empty;
 import static java.util.Optional.of;
 import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
 import static org.innovateuk.ifs.assessment.builder.AssessmentInviteBuilder.newAssessmentInvite;
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
 import static org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.ASSESSOR;
 import static org.innovateuk.ifs.competition.resource.MilestoneType.*;
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
 import static org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED;
 import static org.innovateuk.ifs.invite.domain.ParticipantStatus.*;
 import static org.innovateuk.ifs.notifications.builders.NotificationBuilder.newNotification;
 import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
 import static org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer.PREVIEW_TEMPLATES_PATH;
 import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
 import static org.innovateuk.ifs.user.builder.AffiliationBuilder.newAffiliation;
 import static org.innovateuk.ifs.user.builder.AgreementBuilder.newAgreement;
 import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
 import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
 import static org.innovateuk.ifs.user.resource.AffiliationType.EMPLOYER;
 import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
 import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
 import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
 import static org.innovateuk.ifs.util.MapFunctions.asMap;
 import static org.junit.Assert.*;
 import static org.mockito.ArgumentMatchers.*;
 import static org.mockito.Mockito.any;
 import static org.mockito.Mockito.*;
 import static org.springframework.data.domain.Sort.Direction.ASC;

public class AssessmentInviteServiceImplTest extends BaseServiceUnitTest<AssessmentInviteServiceImpl> {
    private static final String UID = "5cc0ac0d-b969-40f5-9cc5-b9bdd98c86de";
    private static final String INVITE_HASH = "inviteHash";
    private static final DateTimeFormatter inviteFormatter = ofPattern("d MMMM yyyy");

    @Mock
    private AssessorCreatedInviteMapper assessorCreatedInviteMapper;

    @Mock
    private AssessorInviteOverviewMapper assessorInviteOverviewMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AssessmentInviteRepository assessmentInviteRepository;

    @Mock
    private AssessmentInviteMapper assessmentInviteMapper;

    @Mock
    private AssessmentParticipantRepository assessmentParticipantRepository;

    @Mock
    private RejectionReasonRepository rejectionReasonRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoggedInUserSupplier loggedInUserSupplier;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private NotificationTemplateRenderer notificationTemplateRenderer;

    @Mock
    private SystemNotificationSource systemNotificationSource;

    @Mock
    private NotificationService notificationService;

    @Mock
    private InnovationAreaRepository innovationAreaRepository;

    @Mock
    private InnovationAreaMapper innovationAreaMapper;

    @Mock
    private UserService userService;

    @Mock
    private RoleProfileStatusRepository roleProfileStatusRepository;

    @Mock
    private RoleInviteRepository roleInviteRepository;

    private AssessmentParticipant competitionParticipant;

    private UserResource userResource;
    private User user;
    private Profile profile;
    private InnovationArea innovationArea;
    private final long inviteId = 1L;


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
                .withAlwaysOpen(false)
                .build();

        innovationArea = newInnovationArea().build();
        AssessmentInvite assessmentInvite = setUpCompetitionInvite(competition, SENT, innovationArea);

        competitionParticipant = new AssessmentParticipant(assessmentInvite);
        CompetitionInviteResource expected = newCompetitionInviteResource().withCompetitionName("my competition").build();
        RejectionReason rejectionReason = newRejectionReason().withId(1L).withReason("not available").build();
        userResource = newUserResource().withId(userId).build();
        profile = newProfile().withId(profileId).build();
        user = newUser().withId(userId).withProfileId(profile.getId()).build();

        UserResource senderResource = newUserResource().withId(-1L).withUid(UID).build();
        User sender = newUser().withId(-1L).withUid(UID).build();
        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(senderResource));
        when(userMapper.mapToDomain(senderResource)).thenReturn(sender);

        when(assessmentInviteRepository.getByHash(INVITE_HASH)).thenReturn(assessmentInvite);
        when(assessmentInviteRepository.findById(inviteId)).thenReturn(Optional.of(assessmentInvite));

        when(assessmentInviteRepository.save(same(assessmentInvite))).thenReturn(assessmentInvite);
        when(assessmentInviteMapper.mapToResource(same(assessmentInvite))).thenReturn(expected);

        when(assessmentParticipantRepository.getByInviteHash(INVITE_HASH)).thenReturn(competitionParticipant);

        when(rejectionReasonRepository.findById(rejectionReason.getId())).thenReturn(Optional.of(rejectionReason));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(profileRepository.findById(user.getProfileId())).thenReturn(Optional.of(profile));

        when(loggedInUserSupplier.get()).thenReturn(newUser().build());

        ReflectionTestUtils.setField(service, "webBaseUrl", "https://ifs-local-dev");
    }

    @Test
    public void getAlwaysOpenInvitesToSend(){
        List<String> emails = asList("john@email.com", "peter@email.com");
        List<String> names = asList("John Barnes", "Peter Jones");

        Competition competition = newCompetition()
                .withName("my competition")
                .withId(1L)
                .withAlwaysOpen(true)
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
                "competitionName", competition.getName()
        );

        NotificationTarget notificationTarget = new UserNotificationTarget("", "");

        String templatePath = PREVIEW_TEMPLATES_PATH + "invite_assessor_always_open_preview_text.txt";

        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));
        when(assessmentInviteRepository.getByCompetitionIdAndStatus(competition.getId(), CREATED)).thenReturn(invites);
        when(notificationTemplateRenderer.renderTemplate(systemNotificationSource, notificationTarget, templatePath,
                expectedNotificationArguments)).thenReturn(serviceSuccess("content"));

        AssessorInvitesToSendResource expectedAssessorInviteToSendResource = newAssessorInvitesToSendResource()
                .withContent("content")
                .withCompetitionId(competition.getId())
                .withCompetitionName(competition.getName())
                .withRecipients(names)
                .build();

        AssessorInvitesToSendResource result = service.getAllInvitesToSend(competition.getId()).getSuccess();
        assertEquals(expectedAssessorInviteToSendResource, result);

        InOrder inOrder = inOrder(competitionRepository,
                assessmentInviteRepository, notificationTemplateRenderer);
        inOrder.verify(competitionRepository).findById(competition.getId());
        inOrder.verify(assessmentInviteRepository).getByCompetitionIdAndStatus(competition.getId(), CREATED);
        inOrder.verify(notificationTemplateRenderer).renderTemplate(systemNotificationSource, notificationTarget,
                templatePath, expectedNotificationArguments);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getAllInvitesToSend() {
        List<String> emails = asList("john@email.com", "peter@email.com");
        List<String> names = asList("John Barnes", "Peter Jones");

        ZonedDateTime acceptsDate = ZonedDateTime.of(2016, 12, 20, 12, 0,0,0, ZoneId.systemDefault());
        ZonedDateTime deadlineDate = ZonedDateTime.of(2017, 1, 17, 12, 0,0,0, ZoneId.systemDefault());

        Competition competition = newCompetition()
                .withName("my competition")
                .withAssessorAcceptsDate(acceptsDate)
                .withAssessorDeadlineDate(deadlineDate)
                .withAlwaysOpen(false)
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

        String templatePath = PREVIEW_TEMPLATES_PATH + "invite_assessor_preview_text.txt";

        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));
        when(assessmentInviteRepository.getByCompetitionIdAndStatus(competition.getId(), CREATED)).thenReturn(invites);
        when(notificationTemplateRenderer.renderTemplate(systemNotificationSource, notificationTarget, templatePath,
                expectedNotificationArguments)).thenReturn(serviceSuccess("content"));

        AssessorInvitesToSendResource expectedAssessorInviteToSendResource = newAssessorInvitesToSendResource()
                .withContent("content")
                .withCompetitionId(competition.getId())
                .withCompetitionName(competition.getName())
                .withRecipients(names)
                .build();

        AssessorInvitesToSendResource result = service.getAllInvitesToSend(competition.getId()).getSuccess();
        assertEquals(expectedAssessorInviteToSendResource, result);

        InOrder inOrder = inOrder(competitionRepository,
                                  assessmentInviteRepository, notificationTemplateRenderer);
        inOrder.verify(competitionRepository).findById(competition.getId());
        inOrder.verify(assessmentInviteRepository).getByCompetitionIdAndStatus(competition.getId(), CREATED);
        inOrder.verify(notificationTemplateRenderer).renderTemplate(systemNotificationSource, notificationTarget,
                templatePath, expectedNotificationArguments);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getAllInvitesToResend() {
        List<String> emails = asList("john@email.com", "peter@email.com");
        List<String> names = asList("John Barnes", "Peter Jones");
        List<Long> inviteIds = asList(1L, 2L);

        ZonedDateTime acceptsDate = ZonedDateTime.of(2016, 12, 20, 12, 0,0,0, ZoneId.systemDefault());
        ZonedDateTime deadlineDate = ZonedDateTime.of(2017, 1, 17, 12, 0,0,0, ZoneId.systemDefault());

        Competition competition = newCompetition()
                .withName("my competition")
                .withAssessorAcceptsDate(acceptsDate)
                .withAssessorDeadlineDate(deadlineDate)
                .withAlwaysOpen(false)
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

        String templatePath = PREVIEW_TEMPLATES_PATH + "invite_assessor_preview_text.txt";

        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));
        when(assessmentInviteRepository.getByIdIn(inviteIds)).thenReturn(invites);
        when(notificationTemplateRenderer.renderTemplate(systemNotificationSource, notificationTarget, templatePath,
                expectedNotificationArguments)).thenReturn(serviceSuccess("content"));

        AssessorInvitesToSendResource expectedAssessorInviteToSendResource = newAssessorInvitesToSendResource()
                .withContent("content")
                .withCompetitionId(competition.getId())
                .withCompetitionName(competition.getName())
                .withRecipients(names)
                .build();

        AssessorInvitesToSendResource result = service.getAllInvitesToResend(competition.getId(), inviteIds).getSuccess();
        assertEquals(expectedAssessorInviteToSendResource, result);

        InOrder inOrder = inOrder(competitionRepository,
                                  assessmentInviteRepository, notificationTemplateRenderer);
        inOrder.verify(competitionRepository).findById(competition.getId());
        inOrder.verify(assessmentInviteRepository).getByIdIn(inviteIds);
        inOrder.verify(notificationTemplateRenderer).renderTemplate(systemNotificationSource, notificationTarget,
                templatePath, expectedNotificationArguments);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInviteToSend() {
        String email = "john@email.com";
        String name = "John Barnes";

        ZonedDateTime acceptsDate = ZonedDateTime.of(2016, 12, 20, 12, 0,0,0, ZoneId.systemDefault());
        ZonedDateTime deadlineDate = ZonedDateTime.of(2017, 1, 17, 12, 0,0,0, ZoneId.systemDefault());

        Competition competition = newCompetition()
                .withName("my competition")
                .withAssessorAcceptsDate(acceptsDate)
                .withAssessorDeadlineDate(deadlineDate)
                .withAlwaysOpen(false)
                .build();

        AssessmentInvite invite = setUpCompetitionInvite(competition, email, name, CREATED, innovationArea, null);

        Map<String, Object> expectedNotificationArguments = asMap(
                "name", name,
                "competitionName", "my competition",
                "competitionId", competition.getId(),
                "acceptsDate", acceptsDate.format(inviteFormatter),
                "deadlineDate", deadlineDate.format(inviteFormatter),
                "inviteUrl", format("%s/invite/competition/%s", "https://ifs-local-dev/assessment", invite.getHash()));

        NotificationTarget notificationTarget = new UserNotificationTarget("", "");

        String templatePath = PREVIEW_TEMPLATES_PATH + "invite_assessor_editable_text.txt";

        when(assessmentInviteRepository.findById(invite.getId())).thenReturn(Optional.of(invite));
        when(notificationTemplateRenderer.renderTemplate(systemNotificationSource, notificationTarget, templatePath,
                expectedNotificationArguments)).thenReturn(serviceSuccess("content"));

        AssessorInvitesToSendResource expectedAssessorInviteToSendResource = newAssessorInvitesToSendResource()
                .withContent("content")
                .withCompetitionId(competition.getId())
                .withCompetitionName(competition.getName())
                .withRecipients(singletonList(name))
                .build();

        AssessorInvitesToSendResource result = service.getInviteToSend(invite.getId()).getSuccess();
        assertEquals(expectedAssessorInviteToSendResource, result);

        InOrder inOrder = inOrder(assessmentInviteRepository, notificationTemplateRenderer);
        inOrder.verify(assessmentInviteRepository).findById(invite.getId());
        inOrder.verify(notificationTemplateRenderer)
                .renderTemplate(systemNotificationSource, notificationTarget, templatePath, expectedNotificationArguments);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInviteToSend_notCreated() {
        String email = "john@email.com";
        String name = "John Barnes";

        ZonedDateTime acceptsDate = ZonedDateTime.of(2016, 12, 20, 12, 0,0,0, ZoneId.systemDefault());
        ZonedDateTime deadlineDate = ZonedDateTime.of(2017, 1, 17, 12, 0,0,0, ZoneId.systemDefault());

        Competition competition = newCompetition()
                .withName("my competition")
                .withAssessorAcceptsDate(acceptsDate)
                .withAssessorDeadlineDate(deadlineDate)
                .withAlwaysOpen(false)
                .build();

        AssessmentInvite invite = setUpCompetitionInvite(competition, email, name, SENT, innovationArea, null);

        Map<String, Object> expectedNotificationArguments = asMap(
                "name", name,
                "competitionName", "my competition",
                "competitionId", competition.getId(),
                "acceptsDate", acceptsDate.format(inviteFormatter),
                "deadlineDate", deadlineDate.format(inviteFormatter),
                "inviteUrl", format("%s/invite/competition/%s", "https://ifs-local-dev/assessment", invite.getHash()));

        NotificationTarget notificationTarget = new UserNotificationTarget("", "");

        String templatePath = PREVIEW_TEMPLATES_PATH + "invite_assessor_editable_text.txt";

        when(assessmentInviteRepository.findById(invite.getId())).thenReturn(Optional.of(invite));
        when(notificationTemplateRenderer.renderTemplate(systemNotificationSource, notificationTarget, templatePath,
                expectedNotificationArguments)).thenReturn(serviceSuccess("content"));

        AssessorInvitesToSendResource expectedAssessorInviteToSendResource = newAssessorInvitesToSendResource()
                .withCompetitionId(competition.getId())
                .withCompetitionName(competition.getName())
                .withContent("content")
                .withRecipients(singletonList(name))
                .build();

        AssessorInvitesToSendResource result = service.getInviteToSend(invite.getId()).getSuccess();
        assertEquals(expectedAssessorInviteToSendResource, result);

        InOrder inOrder = inOrder(assessmentInviteRepository, notificationTemplateRenderer);
        inOrder.verify(assessmentInviteRepository).findById(invite.getId());
        inOrder.verify(notificationTemplateRenderer).renderTemplate(systemNotificationSource, notificationTarget,
                templatePath, expectedNotificationArguments);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInvite() {
        ServiceResult<CompetitionInviteResource> inviteServiceResult = service.getInvite(INVITE_HASH);

        assertTrue(inviteServiceResult.isSuccess());

        CompetitionInviteResource competitionInviteResource = inviteServiceResult.getSuccess();
        assertEquals("my competition", competitionInviteResource.getCompetitionName());

        InOrder inOrder = inOrder(assessmentInviteRepository, assessmentInviteMapper);
        inOrder.verify(assessmentInviteRepository).getByHash(INVITE_HASH);
        inOrder.verify(assessmentInviteMapper).mapToResource(isA(AssessmentInvite.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInvite_hashNotExists() {
        when(assessmentInviteRepository.getByHash(isA(String.class))).thenReturn(null);

        ServiceResult<CompetitionInviteResource> inviteServiceResult = service.getInvite("inviteHashNotExists");

        assertTrue(inviteServiceResult.isFailure());
        assertTrue(inviteServiceResult.getFailure().is(notFoundError(AssessmentInvite.class, "inviteHashNotExists")));

        InOrder inOrder = inOrder(assessmentInviteRepository, assessmentInviteMapper);
        inOrder.verify(assessmentInviteRepository).getByHash("inviteHashNotExists");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInvite_afterAccepted() {
        service.openInvite(INVITE_HASH);
        ServiceResult<Void> acceptResult = service.acceptInvite(INVITE_HASH, userResource);
        assertTrue(acceptResult.isSuccess());

        ServiceResult<CompetitionInviteResource> getResult = service.getInvite(INVITE_HASH);
        assertTrue(getResult.isFailure());
        assertTrue(getResult.getFailure().is(new Error(COMPETITION_INVITE_CLOSED, "my competition")));

        InOrder inOrder = inOrder(assessmentInviteRepository, assessmentParticipantRepository);
        inOrder.verify(assessmentInviteRepository).getByHash(INVITE_HASH);
        inOrder.verify(assessmentInviteRepository).getByHash(INVITE_HASH);
        inOrder.verify(assessmentParticipantRepository).getByInviteHash(INVITE_HASH);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInvite_afterRejected() {
        RejectionReasonResource rejectionReason = RejectionReasonResourceBuilder.newRejectionReasonResource()
                .withId(1L)
                .build();

        service.openInvite(INVITE_HASH);
        ServiceResult<Void> rejectResult = service.rejectInvite(INVITE_HASH, rejectionReason, of("no time"));
        assertTrue(rejectResult.isSuccess());

        ServiceResult<CompetitionInviteResource> getResult = service.getInvite(INVITE_HASH);
        assertTrue(getResult.isFailure());
        assertTrue(getResult.getFailure().is(new Error(COMPETITION_INVITE_CLOSED, "my competition")));

        InOrder inOrder = inOrder(assessmentInviteRepository, assessmentParticipantRepository);
        inOrder.verify(assessmentInviteRepository).getByHash(INVITE_HASH);
        inOrder.verify(assessmentInviteRepository).getByHash(INVITE_HASH);
        inOrder.verify(assessmentParticipantRepository).getByInviteHash(INVITE_HASH);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void openInvite() {
        ServiceResult<CompetitionInviteResource> inviteServiceResult = service.openInvite(INVITE_HASH);

        assertTrue(inviteServiceResult.isSuccess());

        CompetitionInviteResource competitionInviteResource = inviteServiceResult.getSuccess();
        assertEquals("my competition", competitionInviteResource.getCompetitionName());

        InOrder inOrder = inOrder(assessmentInviteRepository, assessmentInviteMapper);
        inOrder.verify(assessmentInviteRepository).getByHash(INVITE_HASH);
        inOrder.verify(assessmentInviteRepository).save(isA(AssessmentInvite.class));
        inOrder.verify(assessmentInviteMapper).mapToResource(isA(AssessmentInvite.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void openInvite_hashNotExists() {
        when(assessmentInviteRepository.getByHash(isA(String.class))).thenReturn(null);

        ServiceResult<CompetitionInviteResource> inviteServiceResult = service.openInvite("inviteHashNotExists");

        assertTrue(inviteServiceResult.isFailure());
        assertTrue(inviteServiceResult.getFailure().is(notFoundError(AssessmentInvite.class, "inviteHashNotExists")));

        InOrder inOrder = inOrder(assessmentInviteRepository, assessmentInviteMapper);
        inOrder.verify(assessmentInviteRepository).getByHash("inviteHashNotExists");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void openInvite_inviteExpired() {
        AssessmentInvite assessmentInvite = setUpCompetitionInvite(newCompetition()
                .withName("my competition")
                .withAssessorAcceptsDate(now().minusDays(1))
                .build(), SENT, innovationArea);

        when(assessmentInviteRepository.getByHash(isA(String.class))).thenReturn(assessmentInvite);

        ServiceResult<CompetitionInviteResource> inviteServiceResult = service.openInvite("inviteHashExpired");

        assertTrue(inviteServiceResult.isFailure());
        assertTrue(inviteServiceResult.getFailure().is(new Error(COMPETITION_INVITE_EXPIRED, "my competition")));

        InOrder inOrder = inOrder(assessmentInviteRepository, assessmentInviteMapper);
        inOrder.verify(assessmentInviteRepository).getByHash("inviteHashExpired");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void openInvite_afterAccepted() {
        service.openInvite(INVITE_HASH);
        ServiceResult<Void> acceptResult = service.acceptInvite(INVITE_HASH, userResource);
        assertTrue(acceptResult.isSuccess());

        ServiceResult<CompetitionInviteResource> getResult = service.openInvite(INVITE_HASH);
        assertTrue(getResult.isFailure());
        assertTrue(getResult.getFailure().is(new Error(COMPETITION_INVITE_CLOSED, "my competition")));

        InOrder inOrder = inOrder(assessmentInviteRepository, assessmentParticipantRepository);
        inOrder.verify(assessmentInviteRepository).getByHash(INVITE_HASH);
        inOrder.verify(assessmentInviteRepository).getByHash(INVITE_HASH);
        inOrder.verify(assessmentParticipantRepository).getByInviteHash(INVITE_HASH);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void openInvite_afterRejected() {
        RejectionReasonResource rejectionReason = RejectionReasonResourceBuilder.newRejectionReasonResource()
                .withId(1L)
                .build();

        service.openInvite(INVITE_HASH);
        ServiceResult<Void> rejectResult = service.rejectInvite(INVITE_HASH, rejectionReason, of("no time"));
        assertTrue(rejectResult.isSuccess());

        ServiceResult<CompetitionInviteResource> getResult = service.openInvite(INVITE_HASH);
        assertTrue(getResult.isFailure());
        assertTrue(getResult.getFailure().is(new Error(COMPETITION_INVITE_CLOSED, "my competition")));

        InOrder inOrder = inOrder(assessmentInviteRepository, assessmentParticipantRepository);
        inOrder.verify(assessmentInviteRepository).getByHash(INVITE_HASH);
        inOrder.verify(assessmentInviteRepository).getByHash(INVITE_HASH);
        inOrder.verify(assessmentParticipantRepository).getByInviteHash(INVITE_HASH);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInviteByInviteId() {
        ServiceResult<CompetitionInviteResource> inviteServiceResult = service.getInviteByInviteId(inviteId);

        assertTrue(inviteServiceResult.isSuccess());

        CompetitionInviteResource competitionInviteResource = inviteServiceResult.getSuccess();
        assertEquals("my competition", competitionInviteResource.getCompetitionName());

        InOrder inOrder = inOrder(assessmentInviteRepository, assessmentInviteMapper);
        inOrder.verify(assessmentInviteRepository).findById(inviteId);
        inOrder.verify(assessmentInviteMapper).mapToResource(isA(AssessmentInvite.class));
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

        InOrder inOrder = inOrder(assessmentParticipantRepository, userRepository);
        inOrder.verify(userRepository).findById(7L);
        inOrder.verify(assessmentParticipantRepository).getByInviteHash(INVITE_HASH);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptInvite_hashNotExists() {
        ServiceResult<Void> serviceResult = service.acceptInvite("inviteHashNotExists", userResource);

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(notFoundError(CompetitionParticipant.class, "inviteHashNotExists")));

        InOrder inOrder = inOrder(assessmentParticipantRepository);
        inOrder.verify(assessmentParticipantRepository).getByInviteHash("inviteHashNotExists");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptInvite_notOpened() {
        assertEquals(SENT, competitionParticipant.getInvite().getStatus());
        assertEquals(ParticipantStatus.PENDING, competitionParticipant.getStatus());

        ServiceResult<Void> serviceResult = service.acceptInvite(INVITE_HASH, userResource);

        assertTrue(serviceResult.isFailure());
        assertTrue(serviceResult.getFailure().is(new Error(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_UNOPENED_INVITE, "my competition")));

        InOrder inOrder = inOrder(assessmentInviteRepository, userRepository, assessmentParticipantRepository);
        inOrder.verify(userRepository).findById(userResource.getId());
        inOrder.verify(assessmentParticipantRepository).getByInviteHash(INVITE_HASH);
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

        InOrder inOrder = inOrder(assessmentInviteRepository, userRepository, assessmentParticipantRepository);
        inOrder.verify(assessmentInviteRepository).getByHash(INVITE_HASH);
        inOrder.verify(userRepository).findById(userResource.getId());
        inOrder.verify(assessmentParticipantRepository).getByInviteHash(INVITE_HASH);
        inOrder.verify(userRepository).findById(userResource.getId());
        inOrder.verify(assessmentParticipantRepository).getByInviteHash(INVITE_HASH);
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

        InOrder inOrder = inOrder(assessmentInviteRepository, rejectionReasonRepository, userRepository, assessmentParticipantRepository);
        inOrder.verify(assessmentInviteRepository).getByHash(INVITE_HASH);
        inOrder.verify(rejectionReasonRepository).findById(1L);
        inOrder.verify(assessmentParticipantRepository, times(2)).getByInviteHash(INVITE_HASH);
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

        InOrder inOrder = inOrder(assessmentInviteRepository, assessmentParticipantRepository, rejectionReasonRepository);
        inOrder.verify(assessmentInviteRepository).getByHash(INVITE_HASH);
        inOrder.verify(rejectionReasonRepository).findById(1L);
        inOrder.verify(assessmentParticipantRepository).getByInviteHash(INVITE_HASH);

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

        InOrder inOrder = inOrder(rejectionReasonRepository, assessmentParticipantRepository);
        inOrder.verify(rejectionReasonRepository).findById(1L);
        inOrder.verify(assessmentParticipantRepository).getByInviteHash("inviteHashNotExists");
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

        InOrder inOrder = inOrder(rejectionReasonRepository, assessmentParticipantRepository);
        inOrder.verify(rejectionReasonRepository).findById(1L);
        inOrder.verify(assessmentParticipantRepository).getByInviteHash(INVITE_HASH);
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

        InOrder inOrder = inOrder(assessmentInviteRepository, userRepository, assessmentParticipantRepository, rejectionReasonRepository);
        inOrder.verify(assessmentInviteRepository).getByHash(INVITE_HASH);
        inOrder.verify(userRepository).findById(7L);
        inOrder.verify(assessmentParticipantRepository).getByInviteHash(INVITE_HASH);
        inOrder.verify(rejectionReasonRepository).findById(1L);
        inOrder.verify(assessmentParticipantRepository).getByInviteHash(INVITE_HASH);
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

        InOrder inOrder = inOrder(assessmentInviteRepository, assessmentParticipantRepository, rejectionReasonRepository);
        inOrder.verify(assessmentInviteRepository).getByHash(INVITE_HASH);
        inOrder.verify(rejectionReasonRepository).findById(1L);
        inOrder.verify(assessmentParticipantRepository, times(2)).getByInviteHash(INVITE_HASH);
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

        InOrder inOrder = inOrder(assessmentInviteRepository, assessmentParticipantRepository, rejectionReasonRepository);
        inOrder.verify(assessmentInviteRepository).getByHash(INVITE_HASH);
        inOrder.verify(rejectionReasonRepository).findById(2L);

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

        InOrder inOrder = inOrder(assessmentInviteRepository, assessmentParticipantRepository, rejectionReasonRepository);
        inOrder.verify(assessmentInviteRepository).getByHash(INVITE_HASH);
        inOrder.verify(rejectionReasonRepository).findById(1L);
        inOrder.verify(assessmentParticipantRepository).getByInviteHash(INVITE_HASH);

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void sendAllInvites() {
        List<String> emails = asList("john@email.com", "peter@email.com");
        List<String> names = asList("John Barnes", "Peter Jones");

        Competition competition = newCompetition()
                .withName("my competition")
                .withAssessorAcceptsDate(ZonedDateTime.parse("2017-05-24T12:00:00+01:00"))
                .withAssessorDeadlineDate(ZonedDateTime.parse("2017-05-30T12:00:00+01:00"))
                .withAlwaysOpen(false)
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
                "competitionId", invites.get(0).getTarget().getId(),
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
                "competitionId", invites.get(1).getTarget().getId(),
                "acceptsDate", "24 May 2017",
                "deadlineDate", "30 May 2017",
                "inviteUrl", "https://ifs-local-dev/assessment/invite/competition/" + invites.get(1).getHash(),
                "customTextPlain", "content",
                "customTextHtml", "content"
        );

        SystemNotificationSource from = systemNotificationSource;
        NotificationTarget to1 = new UserNotificationTarget(names.get(0), emails.get(0));
        NotificationTarget to2 = new UserNotificationTarget(names.get(1), emails.get(1));

        List<Notification> notifications = newNotification()
                .withSource(from, from)
                .withMessageKey(INVITE_ASSESSOR_GROUP, INVITE_ASSESSOR_GROUP)
                .withTargets(singletonList(new NotificationMessage(to1)), singletonList(new NotificationMessage(to2)))
                .withGlobalArguments(expectedNotificationArguments1, expectedNotificationArguments2)
                .build(2);

        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));
        when(assessmentInviteRepository.getByCompetitionIdAndStatus(competition.getId(), CREATED)).thenReturn(invites);
        when(userRepository.findByEmail(emails.get(0))).thenReturn(Optional.empty());
        when(userRepository.findByEmail(emails.get(1))).thenReturn(Optional.empty());
        when(notificationService.sendNotificationWithFlush(notifications.get(0), EMAIL)).thenReturn(serviceSuccess());
        when(notificationService.sendNotificationWithFlush(notifications.get(1), EMAIL)).thenReturn(serviceSuccess());

        ServiceResult<Void> serviceResult = service.sendAllInvites(competition.getId(), assessorInviteSendResource);
        assertTrue(serviceResult.isSuccess());

        InOrder inOrder = inOrder(competitionRepository,
                                  assessmentInviteRepository, userRepository, assessmentParticipantRepository, notificationService);
        inOrder.verify(competitionRepository).findById(competition.getId());
        inOrder.verify(assessmentInviteRepository).getByCompetitionIdAndStatus(competition.getId(), CREATED);

        inOrder.verify(assessmentParticipantRepository).save(createCompetitionParticipantExpectations(invites.get(0)));
        inOrder.verify(userRepository).findByEmail(emails.get(0));
        inOrder.verify(notificationService).sendNotificationWithFlush(notifications.get(0), EMAIL);

        inOrder.verify(assessmentParticipantRepository).save(createCompetitionParticipantExpectations(invites.get(1)));
        inOrder.verify(userRepository).findByEmail(emails.get(1));
        inOrder.verify(notificationService).sendNotificationWithFlush(notifications.get(1), EMAIL);

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void resendInvites() {
        List<String> emails = asList("john@email.com", "peter@email.com");
        List<String> names = asList("John Barnes", "Peter Jones");
        List<Long> inviteIds = asList(1L, 2L);

        Competition competition = newCompetition()
                .withName("my competition")
                .withAssessorAcceptsDate(ZonedDateTime.parse("2017-05-24T12:00:00+01:00"))
                .withAssessorDeadlineDate(ZonedDateTime.parse("2017-05-30T12:00:00+01:00"))
                .withAlwaysOpen(false)
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
                "competitionId", invites.get(0).getTarget().getId(),
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
                "competitionId", invites.get(1).getTarget().getId(),
                "acceptsDate", "24 May 2017",
                "deadlineDate", "30 May 2017",
                "inviteUrl", "https://ifs-local-dev/assessment/invite/competition/" + invites.get(1).getHash(),
                "customTextPlain", "content",
                "customTextHtml", "content"
        );

        SystemNotificationSource from = systemNotificationSource;
        NotificationTarget to1 = new UserNotificationTarget(names.get(0), emails.get(0));
        NotificationTarget to2 = new UserNotificationTarget(names.get(1), emails.get(1));

        List<Notification> notifications = newNotification()
                .withSource(from, from)
                .withMessageKey(INVITE_ASSESSOR_GROUP, INVITE_ASSESSOR_GROUP)
                .withTargets(singletonList(new NotificationMessage(to1)), singletonList(new NotificationMessage(to2)))
                .withGlobalArguments(expectedNotificationArguments1, expectedNotificationArguments2)
                .build(2);

        when(assessmentInviteRepository.getByIdIn(inviteIds)).thenReturn(invites);
        when(assessmentParticipantRepository.getByInviteHash(invites.get(0).getHash())).thenReturn(assessmentParticipants.get(0));
        when(assessmentParticipantRepository.getByInviteHash(invites.get(1).getHash())).thenReturn(assessmentParticipants.get(1));
        when(notificationService.sendNotificationWithFlush(notifications.get(0), EMAIL)).thenReturn(serviceSuccess());
        when(notificationService.sendNotificationWithFlush(notifications.get(1), EMAIL)).thenReturn(serviceSuccess());

        ServiceResult<Void> serviceResult = service.resendInvites(inviteIds, assessorInviteSendResource);
        assertTrue(serviceResult.isSuccess());

        InOrder inOrder = inOrder(assessmentInviteRepository, assessmentParticipantRepository, notificationService);
        inOrder.verify(assessmentInviteRepository).getByIdIn(inviteIds);
        inOrder.verify(assessmentParticipantRepository).getByInviteHash(invites.get(0).getHash());
        inOrder.verify(notificationService).sendNotificationWithFlush(notifications.get(0), EMAIL);
        inOrder.verify(assessmentParticipantRepository).getByInviteHash(invites.get(1).getHash());
        inOrder.verify(notificationService).sendNotificationWithFlush(notifications.get(1), EMAIL);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void sendAllInvites_existingUsersGetAssessorRole() {
        List<String> emails = asList("john@email.com", "peter@email.com");
        List<String> names = asList("John Barnes", "Peter Jones");

        Competition competition = newCompetition()
                .withName("my competition")
                .withAssessorAcceptsDate(ZonedDateTime.parse("2017-05-24T12:00:00+01:00"))
                .withAssessorDeadlineDate(ZonedDateTime.parse("2017-05-30T12:00:00+01:00"))
                .withAlwaysOpen(false)
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

        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));
        when(assessmentInviteRepository.getByCompetitionIdAndStatus(competition.getId(), CREATED)).thenReturn(invites);
        when(userRepository.findByEmail(emails.get(0))).thenReturn(Optional.of(existingUsers.get(0)));
        when(userRepository.findByEmail(emails.get(1))).thenReturn(Optional.of(existingUsers.get(1)));
        when(notificationService.sendNotificationWithFlush(isA(Notification.class), eq(EMAIL))).thenReturn(serviceSuccess());

        ServiceResult<Void> serviceResult = service.sendAllInvites(competition.getId(), assessorInviteSendResource);
        assertTrue(serviceResult.isSuccess());

        assertTrue(existingUsers.get(0).hasRole(Role.ASSESSOR));
        assertTrue(existingUsers.get(1).hasRole(Role.ASSESSOR));

        InOrder inOrder = inOrder(competitionRepository,
                                  assessmentInviteRepository, userRepository, assessmentParticipantRepository,
                                  notificationService, userService, roleProfileStatusRepository);
        inOrder.verify(competitionRepository).findById(competition.getId());
        inOrder.verify(assessmentInviteRepository).getByCompetitionIdAndStatus(competition.getId(), CREATED);

        inOrder.verify(assessmentParticipantRepository).save(createCompetitionParticipantExpectations(invites.get(0)));
        inOrder.verify(userRepository).findByEmail(emails.get(0));
        inOrder.verify(roleProfileStatusRepository).save(any());
        inOrder.verify(userService).evictUserCache(user.getUid());
        inOrder.verify(notificationService).sendNotificationWithFlush(isA(Notification.class), eq(EMAIL));

        inOrder.verify(assessmentParticipantRepository).save(createCompetitionParticipantExpectations(invites.get(1)));
        inOrder.verify(userRepository).findByEmail(emails.get(1));
        inOrder.verify(notificationService).sendNotificationWithFlush(isA(Notification.class), eq(EMAIL));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void resendInvite() {
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

        SystemNotificationSource from = systemNotificationSource;
        NotificationTarget to = new UserNotificationTarget(name, email);
        Notification notification = new Notification(from, to, AssessmentInviteServiceImpl.Notifications.INVITE_ASSESSOR, expectedNotificationArguments);

        when(assessmentParticipantRepository.getByInviteId(invite.getId())).thenReturn(competitionParticipant);
        when(assessmentInviteRepository.findById(invite.getId())).thenReturn(Optional.of(invite));
        when(notificationService.sendNotificationWithFlush(notification, EMAIL)).thenReturn(serviceSuccess());

        ServiceResult<Void> serviceResult = service.resendInvite(invite.getId(), assessorInviteSendResource);
        assertTrue(serviceResult.isSuccess());

        InOrder inOrder = inOrder(assessmentInviteRepository, assessmentParticipantRepository, notificationService);
        inOrder.verify(notificationService).sendNotificationWithFlush(notification, EMAIL);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void checkExistingUser_hashNotExists() {
        when(assessmentInviteRepository.getByHash(isA(String.class))).thenReturn(null);

        ServiceResult<Boolean> result = service.checkUserExistsForInvite("hash");
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(AssessmentInvite.class, "hash")));

        InOrder inOrder = inOrder(assessmentInviteRepository, userRepository);
        inOrder.verify(assessmentInviteRepository).getByHash("hash");
        inOrder.verify(userRepository, never()).findByEmail(isA(String.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void checkExistingUser_userExistsOnInvite() {
        User user = newUser().build();

        AssessmentInvite assessmentInvite = newAssessmentInvite()
                .withUser(user)
                .withEmail("test@test.com")
                .build();

        when(assessmentInviteRepository.getByHash("hash")).thenReturn(assessmentInvite);

        assertTrue(service.checkUserExistsForInvite("hash").getSuccess());

        InOrder inOrder = inOrder(assessmentInviteRepository, userRepository);
        inOrder.verify(assessmentInviteRepository).getByHash("hash");
        inOrder.verify(userRepository, never()).findByEmail(isA(String.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void checkExistingUser_userExistsForEmail() {
        User user = newUser().build();

        AssessmentInvite assessmentInvite = newAssessmentInvite()
                .withEmail("test@test.com")
                .build();

        when(assessmentInviteRepository.getByHash("hash")).thenReturn(assessmentInvite);
        when(userRepository.findByEmail("test@test.com")).thenReturn(of(user));

        assertTrue(service.checkUserExistsForInvite("hash").getSuccess());

        InOrder inOrder = inOrder(assessmentInviteRepository, userRepository);
        inOrder.verify(assessmentInviteRepository).getByHash("hash");
        inOrder.verify(userRepository).findByEmail("test@test.com");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void checkExistingUser_userDoesNotExist() {
        AssessmentInvite assessmentInvite = newAssessmentInvite()
                .withEmail("test@test.com")
                .build();

        when(assessmentInviteRepository.getByHash("hash")).thenReturn(assessmentInvite);
        when(userRepository.findByEmail("test@test.com")).thenReturn(empty());

        assertFalse(service.checkUserExistsForInvite("hash").getSuccess());

        InOrder inOrder = inOrder(assessmentInviteRepository, userRepository);
        inOrder.verify(assessmentInviteRepository).getByHash("hash");
        inOrder.verify(userRepository).findByEmail("test@test.com");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getAvailableAssessors() {
        long competitionId = 1L;
        int page = 1;
        int pageSize = 1;
        String assessorFilter = "";

        Agreement agreement = newAgreement()
                .withCurrent(true)
                .withModifiedOn(now().minusHours(1))
                .build();

        List<InnovationAreaResource> innovationAreaResources = newInnovationAreaResource()
                .withName("Emerging Tech and Industries")
                .build(1);

        List<AvailableAssessorResource> assessorItems = newAvailableAssessorResource()
                .withId(4L, 8L)
                .withName("Jeremy Alufson", "Felix Wilson")
                .withCompliant(true)
                .withValidAgreement(true)
                .withValidDoi(true)
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
                .withAgreement(agreement)
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
                        .withModifiedOn(now())
                        .build(1))
                .withProfileId(profile.get(0).getId(), profile.get(1).getId())
                .build(2);

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(ASC, "firstName"));

        Page<User> expectedPage = new PageImpl<>(assessors, pageable, 2L);

        when(assessmentInviteRepository.findAssessorsByCompetitionAndAssessorNameLike(competitionId, assessorFilter, pageable))
                .thenReturn(expectedPage);
        when(profileRepository.findById(assessors.get(0).getProfileId())).thenReturn(Optional.of(profile.get(0)));
        when(profileRepository.findById(assessors.get(1).getProfileId())).thenReturn(Optional.of(profile.get(1)));
        when(innovationAreaMapper.mapToResource(innovationArea)).thenReturn(innovationAreaResources.get(0));

        AvailableAssessorPageResource actual = service.getAvailableAssessors(competitionId, pageable, assessorFilter)
                .getSuccess();

        verify(assessmentInviteRepository).findAssessorsByCompetitionAndAssessorNameLike(competitionId, assessorFilter, pageable);
        verify(profileRepository).findById(assessors.get(0).getProfileId());
        verify(profileRepository).findById(assessors.get(1).getProfileId());
        verify(innovationAreaMapper, times(2)).mapToResource(innovationArea);

        assertEquals(expected.getNumber(), actual.getNumber());
        assertEquals(expected.getSize(), actual.getSize());
        assertEquals(expected.getTotalElements(), actual.getTotalElements());
        assertEquals(expected.getTotalPages(), actual.getTotalPages());
        assertEquals(expected.getContent(), actual.getContent());
    }

    @Test
    public void getAvailableAssessors_empty() {
        long competitionId = 1L;
        int page = 0;
        int pageSize = 20;
        long innovationAreaId = 10L;
        String assessorFilter = "";

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(ASC, "firstName"));

        Page<User> assessorPage = new PageImpl<>(emptyList(), pageable, 0);

        when(assessmentInviteRepository.findAssessorsByCompetitionAndAssessorNameLike(competitionId, assessorFilter, pageable))
                .thenReturn(assessorPage);

        AvailableAssessorPageResource result = service.getAvailableAssessors(competitionId, pageable, assessorFilter)
                .getSuccess();

        verify(assessmentInviteRepository).findAssessorsByCompetitionAndAssessorNameLike(competitionId, assessorFilter, pageable);

        assertEquals(page, result.getNumber());
        assertEquals(pageSize, result.getSize());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
        assertEquals(emptyList(), result.getContent());
    }

    @Test
    public void getAvailableAssessors_noAssessorFilter() {
        long competitionId = 1L;
        int page = 0;
        int pageSize = 20;
        String assessorFilter = "";

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(ASC, "firstName"));

        Page<User> assessorPage = new PageImpl<>(emptyList(), pageable, 0);

        when(assessmentInviteRepository.findAssessorsByCompetitionAndAssessorNameLike(competitionId, assessorFilter, pageable)).thenReturn(assessorPage);

        AvailableAssessorPageResource result = service.getAvailableAssessors(competitionId, pageable, assessorFilter)
                .getSuccess();

        verify(assessmentInviteRepository).findAssessorsByCompetitionAndAssessorNameLike(competitionId, assessorFilter, pageable);

        assertEquals(page, result.getNumber());
        assertEquals(pageSize, result.getSize());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
        assertEquals(emptyList(), result.getContent());
    }

    @Test
    public void getAvailableAssessors_all() {
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
                .withCompliant(true)
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

        Optional<Long> innovationAreaId = of(innovationArea.getId());
        String assessorFilter = "";


        when(assessmentInviteRepository.findAssessorsByCompetitionAndAssessorNameLike(competitionId, assessorFilter))
                .thenReturn(expectedAssessorIds);

        List<Long> actualAssessorIds = service.getAvailableAssessorIds(competitionId, assessorFilter)
                .getSuccess();

        verify(assessmentInviteRepository).findAssessorsByCompetitionAndAssessorNameLike(competitionId, assessorFilter);

        assertEquals(expectedAssessorIds, actualAssessorIds);
    }

    @Test
    public void getCreatedInvites() {
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

        Pageable pageable = PageRequest.of(0, 20);
        Page<AssessmentInvite> page = new PageImpl<>(combineLists(existingUserInvites, newUserInvite), pageable, totalElements);

        when(assessmentInviteRepository.getByCompetitionIdAndStatus(competitionId, CREATED, pageable)).thenReturn(page);
        when(innovationAreaMapper.mapToResource(innovationArea)).thenReturn(innovationAreaResource);
        when(assessorCreatedInviteMapper.mapToResource(isA(AssessmentInvite.class))).thenReturn(
                expectedInvites.get(0),
                expectedInvites.get(1),
                expectedInvites.get(2),
                expectedInvites.get(3),
                expectedInvites.get(4)
        );
        when(profileRepository.findById(profile1.getId())).thenReturn(Optional.of(profile1));
        when(profileRepository.findById(profile2.getId())).thenReturn(Optional.of(profile2));
        when(profileRepository.findById(profile3.getId())).thenReturn(Optional.of(profile3));
        when(profileRepository.findById(profile4.getId())).thenReturn(Optional.of(profile4));

        AssessorCreatedInvitePageResource actual = service.getCreatedInvites(competitionId, pageable).getSuccess();
        assertEquals(totalElements, actual.getTotalElements());
        assertEquals(5, actual.getTotalPages());
        assertEquals(expectedInvites, actual.getContent());
        assertEquals(0, actual.getNumber());
        assertEquals(20, actual.getSize());

        InOrder inOrder = inOrder(assessmentInviteRepository, assessorCreatedInviteMapper);
        inOrder.verify(assessmentInviteRepository).getByCompetitionIdAndStatus(competitionId, CREATED, pageable);
        inOrder.verify(assessorCreatedInviteMapper, times(5)).mapToResource(isA(AssessmentInvite.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInviteStatistics() {
        long competitionId = 1L;
        CompetitionInviteStatisticsResource expected = newCompetitionInviteStatisticsResource()
                .withAccepted(1)
                .withDeclined(2)
                .withInviteList(3)
                .withInvited(4)
                .build();

        when(assessmentInviteRepository.countByCompetitionIdAndStatusIn(competitionId, EnumSet.of(OPENED, SENT))).thenReturn(expected.getInvited());
        when(assessmentInviteRepository.countByCompetitionIdAndStatusIn(competitionId, EnumSet.of(CREATED))).thenReturn(expected.getInviteList());
        when(assessmentParticipantRepository.countByCompetitionIdAndRoleAndStatus(competitionId, ASSESSOR, ACCEPTED)).thenReturn(expected.getAccepted());
        when(assessmentParticipantRepository.countByCompetitionIdAndRoleAndStatus(competitionId, ASSESSOR, REJECTED)).thenReturn(expected.getDeclined());
        CompetitionInviteStatisticsResource actual = service.getInviteStatistics(competitionId).getSuccess();
        assertEquals(expected, actual);

        InOrder inOrder = inOrder(assessmentInviteRepository, assessmentParticipantRepository);
        inOrder.verify(assessmentInviteRepository).countByCompetitionIdAndStatusIn(competitionId, EnumSet.of(OPENED, SENT));
        inOrder.verify(assessmentInviteRepository).countByCompetitionIdAndStatusIn(competitionId, EnumSet.of(CREATED));
        inOrder.verify(assessmentParticipantRepository).countByCompetitionIdAndRoleAndStatus(competitionId, ASSESSOR, ACCEPTED);
        inOrder.verify(assessmentParticipantRepository).countByCompetitionIdAndRoleAndStatus(competitionId, ASSESSOR, REJECTED);
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
                .withAlwaysOpen(false)
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

        when(userRepository.findById(newUser.getId())).thenReturn(Optional.of(newUser));
        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));

        AssessmentInvite inviteExpectation = createInviteExpectations(newUser.getName(), newUser.getEmail(), CREATED, competition, null);

        when(assessmentInviteRepository.save(inviteExpectation)).thenReturn(assessmentInvite);
        when(assessmentInviteMapper.mapToResource(assessmentInvite)).thenReturn(expectedInviteResource);
        when(roleInviteRepository.findByEmail(newUser.getEmail())).thenReturn(emptyList());

        CompetitionInviteResource invite = service.inviteUser(existingAssessor).getSuccess();

        assertEquals(expectedInviteResource, invite);

        InOrder inOrder = inOrder(userRepository, competitionRepository,
                                  assessmentInviteRepository, assessmentInviteMapper);
        inOrder.verify(userRepository).findById(newUser.getId());
        inOrder.verify(competitionRepository).findById(competition.getId());
        inOrder.verify(assessmentInviteRepository).save(createInviteExpectations(newUser.getName(), newUser.getEmail(), CREATED, competition, null));
        inOrder.verify(assessmentInviteMapper).mapToResource(assessmentInvite);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteUsers_existing() {
        List<User> existingUsers = newUser()
                .withEmailAddress("fred.smith@abc.com", "joe.brown@abc.com")
                .withFirstName("fred", "joe")
                .withLastName("smith", "brown")
                .build(2);

        Competition competition = newCompetition()
                .withName("competition name")
                .withAlwaysOpen(false)
                .build();

        List<ExistingUserStagedInviteResource> existingAssessors = newExistingUserStagedInviteResource()
                .withUserId(existingUsers.get(0).getId(), existingUsers.get(1).getId())
                .withCompetitionId(competition.getId())
                .build(2);

        when(userRepository.findById(existingUsers.get(0).getId())).thenReturn(Optional.of(existingUsers.get(0)));
        when(userRepository.findById(existingUsers.get(1).getId())).thenReturn(Optional.of(existingUsers.get(1)));
        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));
        when(assessmentInviteRepository.save(isA(AssessmentInvite.class))).thenReturn(new AssessmentInvite());

        ServiceResult<Void> serviceResult = service.inviteUsers(existingAssessors);
        assertTrue(serviceResult.isSuccess());

        InOrder inOrder = inOrder(userRepository, competitionRepository, assessmentInviteRepository);
        inOrder.verify(userRepository).findById(existingAssessors.get(0).getUserId());
        inOrder.verify(competitionRepository).findById(competition.getId());
        inOrder.verify(assessmentInviteRepository).save(createInviteExpectations(existingUsers.get(0).getName(), existingUsers.get(0).getEmail(), CREATED, competition, null));
        inOrder.verify(userRepository).findById(existingAssessors.get(1).getUserId());
        inOrder.verify(competitionRepository).findById(competition.getId());
        inOrder.verify(assessmentInviteRepository).save(createInviteExpectations(existingUsers.get(1).getName(), existingUsers.get(1).getEmail(), CREATED, competition, null));
        inOrder.verifyNoMoreInteractions();
   }

    @Test
    public void inviteUser_new() {
        String newAssessorName = "tom baldwin";
        String newAssessorEmail = "tom@poly.io";

        Competition competition = newCompetition()
                .withAlwaysOpen(false)
                .build();

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

        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));
        when(innovationAreaRepository.findById(innovationArea.getId())).thenReturn(Optional.of(innovationArea));
        when(assessmentInviteRepository.getByEmailAndCompetitionId(newAssessorEmail, competition.getId())).thenReturn(null);

        AssessmentInvite inviteExpectation = createInviteExpectations(newAssessorName, newAssessorEmail, CREATED, competition, innovationArea);
        when(assessmentInviteRepository.save(inviteExpectation)).thenReturn(assessmentInvite);
        when(assessmentInviteMapper.mapToResource(assessmentInvite)).thenReturn(expectedInviteResource);

        ServiceResult<CompetitionInviteResource> serviceResult = service.inviteUser(newAssessor);
        assertTrue(serviceResult.isSuccess());

        CompetitionInviteResource invite = serviceResult.getSuccess();
        assertEquals(expectedInviteResource, invite);

        InOrder inOrder = inOrder(innovationAreaRepository, competitionRepository,
                                  assessmentInviteRepository, assessmentInviteMapper, userRepository);
        inOrder.verify(assessmentInviteRepository).getByEmailAndCompetitionId(newAssessorEmail, competition.getId());
        inOrder.verify(competitionRepository).findById(competition.getId());
        inOrder.verify(innovationAreaRepository).findById(innovationArea.getId());
        inOrder.verify(assessmentInviteRepository).save(createInviteExpectations(newAssessorName, newAssessorEmail, CREATED, competition, innovationArea));
        inOrder.verify(assessmentInviteMapper).mapToResource(assessmentInvite);
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

        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));
        when(innovationAreaRepository.findById(innovationArea)).thenReturn(Optional.empty());
        when(assessmentInviteRepository.getByEmailAndCompetitionId(newAssessorEmail, competition.getId())).thenReturn(null);

        ServiceResult<CompetitionInviteResource> serviceResult = service.inviteUser(newAssessor);

        assertFalse(serviceResult.isSuccess());

        InOrder inOrder = inOrder(innovationAreaRepository, competitionRepository,
                                  assessmentInviteRepository, assessmentInviteMapper, userRepository);
        inOrder.verify(assessmentInviteRepository).getByEmailAndCompetitionId(newAssessorEmail, competition.getId());
        inOrder.verify(competitionRepository).findById(competition.getId());
        inOrder.verify(innovationAreaRepository).findById(innovationArea);
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

        when(assessmentInviteRepository.getByEmailAndCompetitionId(newAssessorEmail, competition.getId())).thenReturn(new AssessmentInvite());

        ServiceResult<CompetitionInviteResource> serviceResult = service.inviteUser(newAssessor);

        assertFalse(serviceResult.isSuccess());

        InOrder inOrder = inOrder(competitionRepository,
                                  assessmentInviteRepository, assessmentInviteMapper, userRepository);
        inOrder.verify(assessmentInviteRepository).getByEmailAndCompetitionId(newAssessorEmail, competition.getId());
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

        when(assessmentInviteRepository.getByEmailAndCompetitionId(newAssessorEmail, competitionId)).thenReturn(null);
        when(competitionRepository.findById(competitionId)).thenReturn(Optional.empty());

        ServiceResult<CompetitionInviteResource> serviceResult = service.inviteUser(newAssessor);

        assertFalse(serviceResult.isSuccess());

        InOrder inOrder = inOrder(competitionRepository,
                                  assessmentInviteRepository, assessmentInviteMapper, userRepository);
        inOrder.verify(assessmentInviteRepository).getByEmailAndCompetitionId(newAssessorEmail, competitionId);
        inOrder.verify(competitionRepository).findById(competitionId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteNewUsers() {
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

        Pageable pageable = PageRequest.of(0, 20, Sort.by(ASC, "name"));

        Page<AssessmentInvite> pageResult = new PageImpl<>(pagedResult, pageable, 10);

        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));
        when(userRepository.findByEmailAndRolesNot(testEmail1, Role.APPLICANT)).thenReturn(empty());
        when(userRepository.findByEmailAndRolesNot(testEmail2, Role.APPLICANT)).thenReturn(empty());
        when(assessmentInviteRepository.getByEmailAndCompetitionId(isA(String.class), isA(Long.class))).thenReturn(null);
        when(innovationAreaRepository.findById(innovationArea.getId())).thenReturn(Optional.of(innovationArea));
        when(assessmentInviteRepository.save(isA(AssessmentInvite.class))).thenReturn(new AssessmentInvite());
        when(assessmentInviteRepository.getByCompetitionIdAndStatus(competition.getId(), CREATED, pageable)).thenReturn(pageResult);
        when(assessorCreatedInviteMapper.mapToResource(pagedResult.get(0))).thenReturn(createdInviteResources.get(0));
        when(assessorCreatedInviteMapper.mapToResource(pagedResult.get(1))).thenReturn(createdInviteResources.get(1));

        ServiceResult<Void> serviceResult = service.inviteNewUsers(newUserInvites, competition.getId());

        assertFalse(serviceResult.isSuccess());

        InOrder inOrder = inOrder(
                competitionRepository,
                userRepository,
                assessmentInviteRepository,
                innovationAreaMapper,
                assessorCreatedInviteMapper
        );
        inOrder.verify(competitionRepository).findById(competition.getId());
        inOrder.verify(userRepository).findByEmailAndRolesNot(testEmail1, Role.APPLICANT);
        inOrder.verify(assessmentInviteRepository).getByCompetitionIdAndStatus(competition.getId(), CREATED, pageable);
        inOrder.verify(assessmentInviteRepository).getByEmailAndCompetitionId(testEmail1, competition.getId());
        inOrder.verify(userRepository).findByEmailAndRolesNot(testEmail2, Role.APPLICANT);
        inOrder.verify(assessmentInviteRepository).getByCompetitionIdAndStatus(competition.getId(), CREATED, pageable);
        inOrder.verify(assessorCreatedInviteMapper, times(2)).mapToResource(isA(AssessmentInvite.class));
        inOrder.verify(assessmentInviteRepository).getByEmailAndCompetitionId(testEmail2, competition.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteNewUsers_alreadyExists() {
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

        Pageable pageable = PageRequest.of(0, 20, Sort.by(ASC, "name"));

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

        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));
        when(assessmentInviteRepository.getByEmailAndCompetitionId(testEmail1, competition.getId()))
                .thenReturn(new AssessmentInvite());
        when(assessmentInviteRepository.getByEmailAndCompetitionId(testEmail2, competition.getId())).thenReturn(null);
        when(innovationAreaRepository.findById(innovationArea.getId())).thenReturn(Optional.of(innovationArea));
        when(assessmentInviteRepository.save(isA(AssessmentInvite.class))).thenReturn(new AssessmentInvite());
        when(userRepository.findByEmailAndRolesNot(testEmail1, Role.APPLICANT)).thenReturn(empty());
        when(userRepository.findByEmailAndRolesNot(testEmail2, Role.APPLICANT)).thenReturn(empty());
        when(assessmentInviteRepository.getByCompetitionIdAndStatus(competition.getId(), CREATED, pageable)).thenReturn(pageResult);
        when(assessorCreatedInviteMapper.mapToResource(pagedResult.get(0))).thenReturn(createdInviteResources.get(0));
        when(assessorCreatedInviteMapper.mapToResource(pagedResult.get(1))).thenReturn(createdInviteResources.get(1));

        ServiceResult<Void> serviceResult = service.inviteNewUsers(newUserInvites, competition.getId());

        assertFalse(serviceResult.isSuccess());
        assertEquals(2, serviceResult.getErrors().size());
        assertEquals("test1@test.com", serviceResult.getErrors().get(0).getFieldRejectedValue());

        InOrder inOrder = inOrder(
                competitionRepository,
                assessmentInviteRepository,
                userRepository,
                innovationAreaMapper,
                assessorCreatedInviteMapper
        );
        inOrder.verify(competitionRepository).findById(competition.getId());
        inOrder.verify(userRepository).findByEmailAndRolesNot(testEmail1, Role.APPLICANT);
        inOrder.verify(assessmentInviteRepository).getByCompetitionIdAndStatus(competition.getId(), CREATED, pageable);
        inOrder.verify(assessmentInviteRepository).getByEmailAndCompetitionId(testEmail1, competition.getId());
        inOrder.verify(userRepository).findByEmailAndRolesNot(testEmail2, Role.APPLICANT);
        inOrder.verify(assessmentInviteRepository).getByCompetitionIdAndStatus(competition.getId(), CREATED, pageable);
        inOrder.verify(assessorCreatedInviteMapper, times(2)).mapToResource(isA(AssessmentInvite.class));
        inOrder.verify(assessmentInviteRepository).getByEmailAndCompetitionId(testEmail2, competition.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteNewUsers_competitionNotFound() {
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

        when(competitionRepository.findById(competitionId)).thenReturn(Optional.empty());
        when(assessmentInviteRepository.getByEmailAndCompetitionId(isA(String.class), isA(Long.class))).thenReturn(null);

        ServiceResult<Void> serviceResult = service.inviteNewUsers(newUserInvites, competitionId);

        assertFalse(serviceResult.isSuccess());

        InOrder inOrder = inOrder(competitionRepository, assessmentInviteRepository);
        inOrder.verify(competitionRepository).findById(competitionId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void inviteNewUsers_categoryNotFound() {
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

        Pageable pageable = PageRequest.of(0, 20, Sort.by(ASC, "name"));

        Page<AssessmentInvite> pageResult = new PageImpl<>(pagedResult, pageable, 10);

        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));
        when(assessmentInviteRepository.getByEmailAndCompetitionId(isA(String.class), isA(Long.class))).thenReturn(null);
        when(innovationAreaRepository.findById(categoryId)).thenReturn(Optional.empty());
        when(assessmentInviteRepository.save(isA(AssessmentInvite.class))).thenReturn(new AssessmentInvite());
        when(assessmentInviteRepository.getByCompetitionIdAndStatus(competition.getId(), CREATED, pageable)).thenReturn(pageResult);
        when(assessorCreatedInviteMapper.mapToResource(pagedResult.get(0))).thenReturn(createdInviteResources.get(0));
        when(assessorCreatedInviteMapper.mapToResource(pagedResult.get(1))).thenReturn(createdInviteResources.get(1));
        when(userRepository.findByEmailAndRolesNot(isA(String.class), isA(Role.class))).thenReturn(empty());

        ServiceResult<Void> serviceResult = service.inviteNewUsers(newUserInvites, competition.getId());

        assertFalse(serviceResult.isSuccess());

        InOrder inOrder = inOrder(
                competitionRepository,
                userRepository,
                assessmentInviteRepository,
                innovationAreaRepository,
                assessorCreatedInviteMapper
        );
        inOrder.verify(competitionRepository).findById(competition.getId());
        inOrder.verify(userRepository).findByEmailAndRolesNot(testEmail1, Role.APPLICANT);
        inOrder.verify(assessmentInviteRepository).getByCompetitionIdAndStatus(competition.getId(), CREATED, pageable);
        inOrder.verify(assessmentInviteRepository).getByEmailAndCompetitionId(testEmail1, competition.getId());
        inOrder.verify(userRepository).findByEmailAndRolesNot(testEmail2, Role.APPLICANT);
        inOrder.verify(assessmentInviteRepository).getByCompetitionIdAndStatus(competition.getId(), CREATED, pageable);
        inOrder.verify(assessorCreatedInviteMapper, times(2)).mapToResource(isA(AssessmentInvite.class));
        inOrder.verify(assessmentInviteRepository).getByEmailAndCompetitionId(testEmail2, competition.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void deleteInvite() {
        String email = "tom@poly.io";
        long competitionId = 11L;

        AssessmentInvite assessmentInvite = newAssessmentInvite()
                .withStatus(CREATED)
                .build();

        when(assessmentInviteRepository.getByEmailAndCompetitionId(email, competitionId)).thenReturn(assessmentInvite);

        service.deleteInvite(email, competitionId).getSuccess();

        InOrder inOrder = inOrder(assessmentInviteRepository);
        inOrder.verify(assessmentInviteRepository).getByEmailAndCompetitionId(email, competitionId);
        inOrder.verify(assessmentInviteRepository).delete(assessmentInvite);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void deleteInvite_sent() {
        String email = "tom@poly.io";
        long competitionId = 11L;
        AssessmentInvite assessmentInvite = newAssessmentInvite()
                .withStatus(SENT)
                .build();

        when(assessmentInviteRepository.getByEmailAndCompetitionId(email, competitionId)).thenReturn(assessmentInvite);

        ServiceResult<Void> serviceResult = service.deleteInvite(email, competitionId);

        assertTrue(serviceResult.isFailure());

        verify(assessmentInviteRepository).getByEmailAndCompetitionId(email, competitionId);
        verifyNoMoreInteractions(assessmentInviteRepository);
    }

    @Test
    public void deleteAllInvites() {
        long competitionId = 1L;

        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(newCompetition().build()));

        assertTrue(service.deleteAllInvites(competitionId).isSuccess());

        verify(competitionRepository).findById(competitionId);
    }

    @Test
    public void deleteAllInvites_noCompetition() {
        long competitionId = 1L;

        when(competitionRepository.findById(competitionId)).thenReturn(Optional.empty());

        assertFalse(service.deleteAllInvites(competitionId).isSuccess());

        verify(competitionRepository).findById(competitionId);
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

        when(assessmentParticipantRepository.getByInviteHash(INVITE_HASH)).thenReturn(competitionParticipant);
        when(profileRepository.findById(user.getProfileId())).thenReturn(Optional.of(profile)); // move to setup?

        service.acceptInvite(INVITE_HASH, userResource).getSuccess();

        assertEquals(ACCEPTED, competitionParticipant.getStatus());
        assertEquals(user, competitionParticipant.getUser());
        assertEquals(singleton(innovationArea), profile.getInnovationAreas());

        InOrder inOrder = inOrder(assessmentParticipantRepository, userRepository, profileRepository);
        inOrder.verify(userRepository).findById(user.getId());
        inOrder.verify(assessmentParticipantRepository).getByInviteHash(INVITE_HASH);
        inOrder.verify(profileRepository).findById(user.getProfileId());
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

        when(assessmentParticipantRepository.getByInviteHash(INVITE_HASH)).thenReturn(competitionParticipant);
        when(profileRepository.findById(user.getProfileId())).thenReturn(Optional.of(profile)); // move to setup?

        service.acceptInvite(INVITE_HASH, userResource).getSuccess();

        assertEquals(ACCEPTED, competitionParticipant.getStatus());
        assertEquals(user, competitionParticipant.getUser());
        assertEquals(singleton(innovationArea), profile.getInnovationAreas());

        InOrder inOrder = inOrder(assessmentParticipantRepository, userRepository, profileRepository);
        inOrder.verify(userRepository).findById(user.getId());
        inOrder.verify(assessmentParticipantRepository).getByInviteHash(INVITE_HASH);
        inOrder.verify(profileRepository).findById(user.getProfileId());
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

        when(assessmentParticipantRepository.getByInviteHash(INVITE_HASH)).thenReturn(competitionParticipant);
        when(profileRepository.findById(user.getProfileId())).thenReturn(Optional.of(profile)); // move to setup?

        service.acceptInvite(INVITE_HASH, userResource).getSuccess();

        assertEquals(ACCEPTED, competitionParticipant.getStatus());
        assertEquals(user, competitionParticipant.getUser());
        assertEquals(singleton(innovationArea), profile.getInnovationAreas());

        InOrder inOrder = inOrder(assessmentParticipantRepository, userRepository, profileRepository);
        inOrder.verify(userRepository).findById(user.getId());
        inOrder.verify(assessmentParticipantRepository).getByInviteHash(INVITE_HASH);
        inOrder.verify(profileRepository).findById(user.getProfileId());
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
    public void getInvitationOverview_allFilters() {
        long competitionId = 1L;
        Pageable pageable = PageRequest.of(0, 5);
        ParticipantStatus status = ParticipantStatus.PENDING;
        Boolean compliant = true;
        String assessorName = "";

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

        when(assessmentParticipantRepository.getAssessorsByCompetitionAndStatusContainsAndCompliantAndAssessorNameLike(
                eq(competitionId),
                eq(singletonList(status)),
                eq(compliant),
                eq(assessorName),
                any(ZonedDateTime.class),
                eq(pageable)
        ))
                .thenReturn(pageResult);

        List<AssessorInviteOverviewResource> overviewResources = newAssessorInviteOverviewResource()
                .withName("Name 1", "Name 2", "Name 3", "Name 4", "Name 5")
                .build(5);

        when(assessorInviteOverviewMapper.mapToResource(isA(AssessmentParticipant.class)))
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
                singletonList(status),
                of(compliant),
                of(assessorName)
        );

        verify(assessmentParticipantRepository).getAssessorsByCompetitionAndStatusContainsAndCompliantAndAssessorNameLike(
                eq(competitionId),
                eq(singletonList(status)),
                eq(compliant),
                eq(assessorName),
                any(ZonedDateTime.class),
                eq(pageable)
        );

        verify(assessorInviteOverviewMapper, times(5)).mapToResource(isA(AssessmentParticipant.class));

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
    public void getInvitationOverview_noFilters() {
        long competitionId = 1L;
        Pageable pageable = PageRequest.of(0, 5);
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

        when(assessmentParticipantRepository.getAssessorsByCompetitionAndStatusContainsAndAssessorNameLike(competitionId, singletonList(PENDING), "", pageable))
                .thenReturn(pageResult);

        List<AssessorInviteOverviewResource> overviewResources = newAssessorInviteOverviewResource()
                .withName("Name 1", "Name 2", "Name 3", "Name 4", "Name 5")
                .build(5);

        when(assessorInviteOverviewMapper.mapToResource(isA(AssessmentParticipant.class)))
                .thenReturn(
                        overviewResources.get(0),
                        overviewResources.get(1),
                        overviewResources.get(2),
                        overviewResources.get(3),
                        overviewResources.get(4)
                );


        ServiceResult<AssessorInviteOverviewPageResource> result = service.getInvitationOverview(competitionId, pageable, singletonList(PENDING), empty(), empty());

        verify(assessmentParticipantRepository).getAssessorsByCompetitionAndStatusContainsAndAssessorNameLike(competitionId, singletonList(PENDING), "", pageable);
        verify(assessorInviteOverviewMapper, times(5)).mapToResource(isA(AssessmentParticipant.class));

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
    public void getAssessorsNotAcceptedInviteIds() {
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

        when(assessmentParticipantRepository.getAssessorsByCompetitionAndStatusContainsAndAssessorNameLike(competitionId, asList(PENDING, REJECTED), ""))
                .thenReturn(expectedParticipants);

        ServiceResult<List<Long>> result = service.getAssessorsNotAcceptedInviteIds(competitionId, asList(PENDING, REJECTED), empty(), empty());

        verify(assessmentParticipantRepository).getAssessorsByCompetitionAndStatusContainsAndAssessorNameLike(competitionId, asList(PENDING, REJECTED), "");

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