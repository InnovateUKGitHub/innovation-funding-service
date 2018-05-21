package org.innovateuk.ifs.review.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.assessment.domain.AssessmentParticipant;
import org.innovateuk.ifs.assessment.repository.AssessmentParticipantRepository;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.invite.mapper.ParticipantStatusMapper;
import org.innovateuk.ifs.invite.repository.RejectionReasonRepository;
import org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer;
import org.innovateuk.ifs.notifications.service.senders.NotificationSender;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.review.domain.ReviewInvite;
import org.innovateuk.ifs.review.domain.ReviewParticipant;
import org.innovateuk.ifs.assessment.mapper.AssessorCreatedInviteMapper;
import org.innovateuk.ifs.assessment.mapper.AssessorInviteOverviewMapper;
import org.innovateuk.ifs.assessment.mapper.AvailableAssessorMapper;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.Invite;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.domain.RejectionReason;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.review.domain.Review;
import org.innovateuk.ifs.review.mapper.ReviewInviteMapper;
import org.innovateuk.ifs.review.mapper.ReviewParticipantMapper;
import org.innovateuk.ifs.review.repository.ReviewInviteRepository;
import org.innovateuk.ifs.review.repository.ReviewParticipantRepository;
import org.innovateuk.ifs.review.repository.ReviewRepository;
import org.innovateuk.ifs.review.resource.ReviewState;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.Boolean.TRUE;
import static java.time.ZonedDateTime.now;
import static java.time.ZonedDateTime.of;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.builder.AssessmentParticipantBuilder.newAssessmentParticipant;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.ASSESSMENT_PANEL_INVITE_EXPIRED;
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
import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteResourceBuilder.newExistingUserStagedInviteResource;
import static org.innovateuk.ifs.invite.builder.RejectionReasonBuilder.newRejectionReason;
import static org.innovateuk.ifs.invite.constant.InviteStatus.*;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.PENDING;
import static org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.PANEL_ASSESSOR;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.REJECTED;
import static org.innovateuk.ifs.notifications.builders.NotificationBuilder.newNotification;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.review.builder.ReviewBuilder.newReview;
import static org.innovateuk.ifs.review.builder.ReviewInviteBuilder.newReviewInvite;
import static org.innovateuk.ifs.review.builder.ReviewInviteResourceBuilder.newReviewInviteResource;
import static org.innovateuk.ifs.review.builder.ReviewParticipantBuilder.newReviewParticipant;
import static org.innovateuk.ifs.review.builder.ReviewParticipantResourceBuilder.newReviewParticipantResource;
import static org.innovateuk.ifs.review.transactional.ReviewInviteServiceImpl.Notifications.INVITE_ASSESSOR_GROUP_TO_PANEL;
import static org.innovateuk.ifs.user.builder.AffiliationBuilder.newAffiliation;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.AffiliationType.EMPLOYER;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.Direction.ASC;

public class ReviewInviteServiceImplTest extends BaseServiceUnitTest<ReviewInviteServiceImpl> {
    private static final String UID = "5cc0ac0d-b969-40f5-9cc5-b9bdd98c86de";
    private static final String INVITE_HASH = "inviteHash";

    @Mock
    private AvailableAssessorMapper availableAssessorMapperMock;
    @Mock
    private AssessorInviteOverviewMapper assessorInviteOverviewMapperMock;
    @Mock
    private AssessorCreatedInviteMapper assessorCreatedInviteMapperMock;
    @Mock
    private UserMapper userMapperMock;
    @Mock
    private ReviewInviteRepository reviewInviteRepositoryMock;
    @Mock
    private ReviewInviteMapper reviewInviteMapperMock;
    @Mock
    private ReviewParticipantRepository reviewParticipantRepositoryMock;
    @Mock
    private AssessmentParticipantRepository assessmentParticipantRepositoryMock;
    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private RejectionReasonRepository rejectionReasonRepositoryMock;
    @Mock
    private ProfileRepository profileRepositoryMock;
    @Mock
    private SystemNotificationSource systemNotificationSourceMock;
    @Mock
    private CompetitionRepository competitionRepositoryMock;
    @Mock
    private NotificationTemplateRenderer notificationTemplateRendererMock;
    @Mock
    private NotificationSender notificationSenderMock;
    @Mock
    private ReviewParticipantMapper reviewParticipantMapperMock;
    @Mock
    private ParticipantStatusMapper participantStatusMapperMock;
    @Mock
    private ApplicationRepository applicationRepositoryMock;
    @Mock
    private ReviewRepository reviewRepositoryMock;
    @Mock
    private LoggedInUserSupplier loggedInUserSupplierMock;

    @Override
    protected ReviewInviteServiceImpl supplyServiceUnderTest() {
        return new ReviewInviteServiceImpl();
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
                .withType(NOTIFICATIONS, ASSESSOR_DEADLINE, ASSESSMENT_PANEL)
                .build(2));

        Competition competition = newCompetition().withName("my competition")
                .withMilestones(milestones)
                .withSetupComplete(true)
                .build();

        ReviewInvite reviewInvite = setUpAssessmentPanelInvite(competition, SENT);
        ReviewParticipant reviewParticipant = new ReviewParticipant(reviewInvite);

        ReviewInviteResource expected = newReviewInviteResource().withCompetitionName("my competition").build();
        RejectionReason rejectionReason = newRejectionReason().withId(1L).withReason("not available").build();
        Profile profile = newProfile().withId(profileId).build();
        User user = newUser().withId(userId).withProfileId(profile.getId()).build();

        UserResource senderResource = newUserResource().withId(-1L).withUID(UID).build();
        User sender = newUser().withId(-1L).withUid(UID).build();
        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(senderResource));
        when(userMapperMock.mapToDomain(senderResource)).thenReturn(sender);

        when(reviewInviteRepositoryMock.getByHash(INVITE_HASH)).thenReturn(reviewInvite);
        when(reviewInviteRepositoryMock.save(isA(ReviewInvite.class))).thenReturn(reviewInvite);
        when(reviewInviteMapperMock.mapToResource(same(reviewInvite))).thenReturn(expected);
        when(reviewParticipantRepositoryMock.getByInviteHash(INVITE_HASH)).thenReturn(reviewParticipant);
        when(rejectionReasonRepositoryMock.findOne(rejectionReason.getId())).thenReturn(rejectionReason);
        when(userRepositoryMock.findOne(userId)).thenReturn(user);
        when(profileRepositoryMock.findOne(user.getProfileId())).thenReturn(profile);
        when(loggedInUserSupplierMock.get()).thenReturn(newUser().build());

        ReflectionTestUtils.setField(service, "webBaseUrl", "https://ifs-local-dev");
    }

    @Test
    public void getAvailableAssessors() {
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

        List<AssessmentParticipant> participants = newAssessmentParticipant()
                .withUser(assessors.get(0), assessors.get(1))
                .build(2);

        Pageable pageable = new PageRequest(page, pageSize, new Sort(ASC, "firstName"));

        Page<AssessmentParticipant> expectedPage = new PageImpl<>(participants, pageable, 2L);

        when(assessmentParticipantRepositoryMock.findParticipantsNotOnAssessmentPanel(competitionId, pageable))
                .thenReturn(expectedPage);
        when(availableAssessorMapperMock.mapToResource(participants.get(0))).thenReturn(assessorItems.get(0));
        when(availableAssessorMapperMock.mapToResource(participants.get(1))).thenReturn(assessorItems.get(1));

        AvailableAssessorPageResource actual = service.getAvailableAssessors(competitionId, pageable)
                .getSuccess();

        verify(assessmentParticipantRepositoryMock).findParticipantsNotOnAssessmentPanel(competitionId, pageable);
        verify(availableAssessorMapperMock).mapToResource(participants.get(0));
        verify(availableAssessorMapperMock).mapToResource(participants.get(1));

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

        Pageable pageable = new PageRequest(page, pageSize, new Sort(ASC, "firstName"));

        Page<AssessmentParticipant> assessorPage = new PageImpl<>(emptyList(), pageable, 0);

        when(assessmentParticipantRepositoryMock.findParticipantsNotOnAssessmentPanel(competitionId, pageable))
                .thenReturn(assessorPage);

        AvailableAssessorPageResource result = service.getAvailableAssessors(competitionId, pageable)
                .getSuccess();

        verify(assessmentParticipantRepositoryMock).findParticipantsNotOnAssessmentPanel(competitionId, pageable);

        assertEquals(page, result.getNumber());
        assertEquals(pageSize, result.getSize());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
        assertEquals(emptyList(), result.getContent());
    }

    @Test
    public void getAvailableAssessorIds() {
        long competitionId = 1L;

        InnovationArea innovationArea = newInnovationArea()
                .withName("Emerging Tech and Industries")
                .build();

        List<Long> expectedAssessorIds = asList(4L, 8L);

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

        List<AssessmentParticipant> participants = newAssessmentParticipant()
                .withUser(assessorUsers.get(0), assessorUsers.get(1))
                .build(2);

        when(assessmentParticipantRepositoryMock.findParticipantsNotOnAssessmentPanel(competitionId))
                .thenReturn(participants);

        List<Long> actualAssessorIds = service.getAvailableAssessorIds(competitionId)
                .getSuccess();

        verify(assessmentParticipantRepositoryMock).findParticipantsNotOnAssessmentPanel(competitionId);

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

        List<ReviewInvite> existingUserInvites = newReviewInvite()
                .withId(1L, 2L, 3L, 4L)
                .withName("John Barnes", "Dave Smith", "Richard Turner", "Oliver Romero")
                .withEmail("john@example.com", "dave@example.com", "richard@example.com", "oliver@example.com")
                .withUser(compliantUser, nonCompliantUserNoSkills, nonCompliantUserNoAffiliations, nonCompliantUserNoAgreement)
                .build(4);

        List<AssessorCreatedInviteResource> expectedInvites = newAssessorCreatedInviteResource()
                .withId(compliantUser.getId(), nonCompliantUserNoSkills.getId(), nonCompliantUserNoAffiliations.getId(), nonCompliantUserNoAgreement.getId())
                .withInviteId(1L, 2L, 3L, 4L)
                .withName("John Barnes", "Dave Smith", "Richard Turner", "Oliver Romero")
                .withInnovationAreas(innovationAreaList, emptyList(), emptyList(), emptyList())
                .withCompliant(true, false, false, false)
                .withEmail("john@example.com", "dave@example.com", "richard@example.com", "oliver@example.com")
                .build(4);

        long totalElements = 100L;

        Pageable pageable = new PageRequest(0, 20);
        Page<ReviewInvite> page = new PageImpl<>(existingUserInvites, pageable, totalElements);

        when(reviewInviteRepositoryMock.getByCompetitionIdAndStatus(competitionId, CREATED, pageable)).thenReturn(page);
        when(assessorCreatedInviteMapperMock.mapToResource(isA(ReviewInvite.class))).thenReturn(
                expectedInvites.get(0),
                expectedInvites.get(1),
                expectedInvites.get(2),
                expectedInvites.get(3)
        );

        AssessorCreatedInvitePageResource actual = service.getCreatedInvites(competitionId, pageable).getSuccess();
        assertEquals(totalElements, actual.getTotalElements());
        assertEquals(5, actual.getTotalPages());
        assertEquals(expectedInvites, actual.getContent());
        assertEquals(0, actual.getNumber());
        assertEquals(20, actual.getSize());

        InOrder inOrder = inOrder(reviewInviteRepositoryMock, assessorCreatedInviteMapperMock);
        inOrder.verify(reviewInviteRepositoryMock).getByCompetitionIdAndStatus(competitionId, CREATED, pageable);
        inOrder.verify(assessorCreatedInviteMapperMock, times(4))
                .mapToResource(isA(ReviewInvite.class));
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
                .build();

        List<ExistingUserStagedInviteResource> existingAssessors = newExistingUserStagedInviteResource()
                .withUserId(existingUsers.get(0).getId(), existingUsers.get(1).getId())
                .withCompetitionId(competition.getId())
                .build(2);

        when(userRepositoryMock.findOne(existingUsers.get(0).getId())).thenReturn(existingUsers.get(0));
        when(userRepositoryMock.findOne(existingUsers.get(1).getId())).thenReturn(existingUsers.get(1));
        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(reviewInviteRepositoryMock.save(isA(ReviewInvite.class))).thenReturn(new ReviewInvite());

        ServiceResult<Void> serviceResult = service.inviteUsers(existingAssessors);
        assertTrue(serviceResult.isSuccess());

        InOrder inOrder = inOrder(userRepositoryMock, competitionRepositoryMock, reviewInviteRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(existingAssessors.get(0).getUserId());
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(reviewInviteRepositoryMock).save(createInviteExpectations(existingUsers.get(0).getName(), existingUsers.get(0).getEmail(), CREATED, competition));
        inOrder.verify(userRepositoryMock).findOne(existingAssessors.get(1).getUserId());
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(reviewInviteRepositoryMock).save(createInviteExpectations(existingUsers.get(1).getName(), existingUsers.get(1).getEmail(), CREATED, competition));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void sendAllInvites() {
        List<String> emails = asList("john@email.com", "peter@email.com");
        List<String> names = asList("John Barnes", "Peter Jones");

        Competition competition = newCompetition()
                .withName("my competition")
                .withAssessorAcceptsDate(ZonedDateTime.parse("2017-08-24T12:00:00+01:00"))
                .withAssessorDeadlineDate(ZonedDateTime.parse("2017-08-30T12:00:00+01:00"))
                .build();

        List<ReviewInvite> invites = newReviewInvite()
                .withCompetition(competition)
                .withEmail(emails.get(0), emails.get(1))
                .withHash(Invite.generateInviteHash())
                .withName(names.get(0), names.get(1))
                .withStatus(CREATED)
                .withUser(newUser().withFirstName("Paul").build())
                .build(2);

        AssessorInviteSendResource assessorInviteSendResource = setUpAssessorInviteSendResource();

        Map<String, Object> expectedNotificationArguments1 = asMap(
                "subject", assessorInviteSendResource.getSubject(),
                "name", invites.get(0).getName(),
                "competitionName", invites.get(0).getTarget().getName(),
                "inviteUrl", "https://ifs-local-dev/assessment/invite/panel/" + invites.get(0).getHash(),
                "customTextPlain", "content",
                "customTextHtml", "content"
        );
        Map<String, Object> expectedNotificationArguments2 = asMap(
                "subject", assessorInviteSendResource.getSubject(),
                "name", invites.get(1).getName(),
                "competitionName", invites.get(1).getTarget().getName(),
                "inviteUrl", "https://ifs-local-dev/assessment/invite/panel/" + invites.get(1).getHash(),
                "customTextPlain", "content",
                "customTextHtml", "content"
        );

        SystemNotificationSource from = systemNotificationSourceMock;
        NotificationTarget to1 = new UserNotificationTarget(names.get(0), emails.get(0));
        NotificationTarget to2 = new UserNotificationTarget(names.get(1), emails.get(1));

        List<Notification> notifications = newNotification()
                .withSource(from, from)
                .withMessageKey(INVITE_ASSESSOR_GROUP_TO_PANEL, INVITE_ASSESSOR_GROUP_TO_PANEL)
                .withTargets(singletonList(to1), singletonList(to2))
                .withGlobalArguments(expectedNotificationArguments1, expectedNotificationArguments2)
                .build(2);

        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(reviewInviteRepositoryMock.getByCompetitionIdAndStatus(competition.getId(), CREATED)).thenReturn(invites);
        when(userRepositoryMock.findByEmail(emails.get(0))).thenReturn(Optional.empty());
        when(userRepositoryMock.findByEmail(emails.get(1))).thenReturn(Optional.empty());
        when(notificationSenderMock.sendNotification(notifications.get(0))).thenReturn(serviceSuccess(notifications.get(0)));
        when(notificationSenderMock.sendNotification(notifications.get(1))).thenReturn(serviceSuccess(notifications.get(1)));

        ServiceResult<Void> serviceResult = service.sendAllInvites(competition.getId(), assessorInviteSendResource);
        assertTrue(serviceResult.isSuccess());

        InOrder inOrder = inOrder(competitionRepositoryMock, reviewInviteRepositoryMock, userRepositoryMock, reviewParticipantRepositoryMock, notificationSenderMock);
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(reviewInviteRepositoryMock).getByCompetitionIdAndStatus(competition.getId(), CREATED);
        inOrder.verify(reviewParticipantRepositoryMock).save(createAssessmentPanelParticipantExpectations(invites.get(0)));
        inOrder.verify(notificationSenderMock).sendNotification(notifications.get(0));
        inOrder.verify(reviewParticipantRepositoryMock).save(createAssessmentPanelParticipantExpectations(invites.get(1)));
        inOrder.verify(notificationSenderMock).sendNotification(notifications.get(1));

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getAllInvitesToSend() {
        List<String> emails = asList("john@email.com", "peter@email.com");
        List<String> names = asList("John Barnes", "Peter Jones");

        ZonedDateTime acceptsDate = of(2016, 12, 20, 12, 0,0,0, ZoneId.systemDefault());
        ZonedDateTime deadlineDate = of(2017, 1, 17, 12, 0,0,0, ZoneId.systemDefault());

        Competition competition = newCompetition()
                .withName("Competition in Assessor Panel")
                .withAssessorAcceptsDate(acceptsDate)
                .withAssessorDeadlineDate(deadlineDate)
                .build();

        List<ReviewInvite> invites = newReviewInvite()
                .withCompetition(competition)
                .withEmail(emails.get(0), emails.get(1))
                .withHash(Invite.generateInviteHash())
                .withName(names.get(0), names.get(1))
                .withStatus(CREATED)
                .withUser(new User())
                .build(2);

        Map<String, Object> expectedNotificationArguments = asMap(
                "competitionName", competition.getName()
                );

        NotificationTarget notificationTarget = new UserNotificationTarget("", "");

        String templatePath = "invite_assessors_to_assessors_panel_text.txt";

        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(reviewInviteRepositoryMock.getByCompetitionIdAndStatus(competition.getId(), CREATED)).thenReturn(invites);
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

        InOrder inOrder = inOrder(competitionRepositoryMock, reviewInviteRepositoryMock, notificationTemplateRendererMock);
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(reviewInviteRepositoryMock).getByCompetitionIdAndStatus(competition.getId(), CREATED);
        inOrder.verify(notificationTemplateRendererMock).renderTemplate(systemNotificationSourceMock, notificationTarget,
                templatePath, expectedNotificationArguments);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getAllInvitesToResend() {
        List<String> emails = asList("john@email.com", "peter@email.com");
        List<String> names = asList("John Barnes", "Peter Jones");
        List<Long> inviteIds = asList(1L, 2L);

        ZonedDateTime acceptsDate = of(2016, 12, 20, 12, 0,0,0, ZoneId.systemDefault());
        ZonedDateTime deadlineDate = of(2017, 1, 17, 12, 0,0,0, ZoneId.systemDefault());

        Competition competition = newCompetition()
                .withName("my competition")
                .withAssessorAcceptsDate(acceptsDate)
                .withAssessorDeadlineDate(deadlineDate)
                .build();

        List<ReviewInvite> invites = newReviewInvite()
                .withCompetition(competition)
                .withEmail(emails.get(0), emails.get(1))
                .withHash(Invite.generateInviteHash())
                .withName(names.get(0), names.get(1))
                .withStatus(SENT)
                .withUser(newUser())
                .build(2);

        Map<String, Object> expectedNotificationArguments = asMap(
                "competitionName", competition.getName()
        );

        NotificationTarget notificationTarget = new UserNotificationTarget("", "");

        String templatePath = "invite_assessors_to_assessors_panel_text.txt";

        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(reviewInviteRepositoryMock.getByIdIn(inviteIds)).thenReturn(invites);
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

        InOrder inOrder = inOrder(competitionRepositoryMock, reviewInviteRepositoryMock, notificationTemplateRendererMock);
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(reviewInviteRepositoryMock).getByIdIn(inviteIds);
        inOrder.verify(notificationTemplateRendererMock).renderTemplate(systemNotificationSourceMock, notificationTarget,
                templatePath, expectedNotificationArguments);
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
                .build();

        List<ReviewInvite> invites = newReviewInvite()
                .withCompetition(competition)
                .withEmail(emails.get(0), emails.get(1))
                .withHash(Invite.generateInviteHash())
                .withName(names.get(0), names.get(1))
                .withStatus(SENT)
                .withUser(newUser().build())
                .build(2);

        List<ReviewParticipant> reviewParticipants = newReviewParticipant()
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
                "inviteUrl", "https://ifs-local-dev/assessment/invite/panel/" + invites.get(0).getHash(),
                "customTextPlain", "content",
                "customTextHtml", "content"
        );
        Map<String, Object> expectedNotificationArguments2 = asMap(
                "subject", assessorInviteSendResource.getSubject(),
                "name", invites.get(1).getName(),
                "competitionName", invites.get(1).getTarget().getName(),
                "inviteUrl", "https://ifs-local-dev/assessment/invite/panel/" + invites.get(1).getHash(),
                "customTextPlain", "content",
                "customTextHtml", "content"
        );

        SystemNotificationSource from = systemNotificationSourceMock;
        NotificationTarget to1 = new UserNotificationTarget(names.get(0), emails.get(0));
        NotificationTarget to2 = new UserNotificationTarget(names.get(1), emails.get(1));

        List<Notification> notifications = newNotification()
                .withSource(from, from)
                .withMessageKey(INVITE_ASSESSOR_GROUP_TO_PANEL, INVITE_ASSESSOR_GROUP_TO_PANEL)
                .withTargets(singletonList(to1), singletonList(to2))
                .withGlobalArguments(expectedNotificationArguments1, expectedNotificationArguments2)
                .build(2);

        when(reviewInviteRepositoryMock.getByIdIn(inviteIds)).thenReturn(invites);
        when(reviewParticipantRepositoryMock.getByInviteHash(invites.get(0).getHash())).thenReturn(reviewParticipants.get(0));
        when(reviewParticipantRepositoryMock.getByInviteHash(invites.get(1).getHash())).thenReturn(reviewParticipants.get(1));
        when(notificationSenderMock.sendNotification(notifications.get(0))).thenReturn(serviceSuccess(notifications.get(0)));
        when(notificationSenderMock.sendNotification(notifications.get(1))).thenReturn(serviceSuccess(notifications.get(1)));

        ServiceResult<Void> serviceResult = service.resendInvites(inviteIds, assessorInviteSendResource);
        assertTrue(serviceResult.isSuccess());

        InOrder inOrder = inOrder(reviewInviteRepositoryMock, reviewParticipantRepositoryMock, notificationSenderMock);
        inOrder.verify(reviewInviteRepositoryMock).getByIdIn(inviteIds);
        inOrder.verify(reviewParticipantRepositoryMock).getByInviteHash(invites.get(0).getHash());
        inOrder.verify(notificationSenderMock).sendNotification(notifications.get(0));
        inOrder.verify(reviewParticipantRepositoryMock).getByInviteHash(invites.get(1).getHash());
        inOrder.verify(notificationSenderMock).sendNotification(notifications.get(1));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInvitationOverview() {
        long competitionId = 1L;
        Pageable pageable = new PageRequest(0, 5);
        List<ReviewParticipant> expectedParticipants = newReviewParticipant()
                .withInvite(
                        newReviewInvite()
                                .withName("Name 1", "Name 2", "Name 3", "Name 4", "Name 5")
                                .withSentOn(now())
                                .withStatus(SENT)
                                .buildArray(5, ReviewInvite.class)
                )
                .withStatus(PENDING)
                .build(5);

        Page<ReviewParticipant> pageResult = new PageImpl<>(expectedParticipants, pageable, 10);

        when(reviewParticipantRepositoryMock.getPanelAssessorsByCompetitionAndStatusContains(competitionId, singletonList(PENDING), pageable))
                .thenReturn(pageResult);

        List<AssessorInviteOverviewResource> overviewResources = newAssessorInviteOverviewResource()
                .withName("Name 1", "Name 2", "Name 3", "Name 4", "Name 5")
                .build(5);

        when(assessorInviteOverviewMapperMock.mapToResource(isA(ReviewParticipant.class)))
                .thenReturn(
                        overviewResources.get(0),
                        overviewResources.get(1),
                        overviewResources.get(2),
                        overviewResources.get(3),
                        overviewResources.get(4)
                );
        when(participantStatusMapperMock.mapToResource(PENDING)).thenReturn(ParticipantStatusResource.PENDING);

        ServiceResult<AssessorInviteOverviewPageResource> result = service.getInvitationOverview(competitionId, pageable, singletonList(PENDING));

        verify(reviewParticipantRepositoryMock)
                .getPanelAssessorsByCompetitionAndStatusContains(competitionId, singletonList(PENDING), pageable);
        verify(assessorInviteOverviewMapperMock, times(5))
                .mapToResource(isA(ReviewParticipant.class));

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

    @Test
    public void getAllInvitesByUser() {
        User user = newUser()
                .withId(1L)
                .build();

        Milestone milestone = newMilestone()
                .withType(ASSESSMENT_PANEL)
                .withDate(now().plusDays(1))
                .build();
        Competition competition = newCompetition()
                .withId(2L)
                .withName("Competition in Assessor Panel")
                .withMilestones(singletonList(milestone))
                .build();

        List<ReviewInvite> invites = newReviewInvite()
                .withEmail("paulplum@gmail.com")
                .withHash("")
                .withCompetition(competition)
                .withUser(user)
                .build(2);

        List<ReviewParticipant> reviewParticipants = newReviewParticipant()
                .withInvite(invites.get(0), invites.get(1))
                .withStatus(PENDING)
                .withCompetition(competition)
                .withUser(user)
                .build(2);

        List<ReviewParticipantResource> expected = newReviewParticipantResource()
                .withCompetition(2L)
                .withCompetitionName("Competition in Assessor Panel")
                .withUser(1L)
                .build(2);

        List<Review> reviews = newReview()
                .withState(ReviewState.PENDING)
                .build(2);

        when(reviewParticipantRepositoryMock.findByUserIdAndRole(1L, PANEL_ASSESSOR)).thenReturn(reviewParticipants);
        when(reviewParticipantMapperMock.mapToResource(reviewParticipants.get(0))).thenReturn(expected.get(0));
        when(reviewParticipantMapperMock.mapToResource(reviewParticipants.get(1))).thenReturn(expected.get(1));
        when(reviewRepositoryMock.findByParticipantUserIdAndTargetCompetitionIdOrderByActivityStateAscIdAsc(1L, competition.getId())).thenReturn(reviews);

        List<ReviewParticipantResource> actual = service.getAllInvitesByUser(1L).getSuccess();
        assertEquals(actual.get(0), expected.get(0));
        assertEquals(actual.get(1), expected.get(1));
        InOrder inOrder = inOrder(reviewParticipantRepositoryMock);
        inOrder.verify(reviewParticipantRepositoryMock).findByUserIdAndRole(1L, PANEL_ASSESSOR);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getAllInvitesByUser_invitesExpired() {
        User user = newUser()
                .withId(1L)
                .build();
        Milestone milestone = newMilestone()
                .withType(ASSESSMENT_PANEL)
                .withDate(now().minusDays(2))
                .build();
        Competition competition = newCompetition()
                .withId(2L)
                .withName("Competition in Assessor Panel")
                .withMilestones(singletonList(milestone))
                .build();
        List<ReviewInvite> invites = newReviewInvite()
                .withEmail("paulplum@gmail.com")
                .withHash("")
                .withCompetition(competition)
                .withUser(user)
                .build(2);
        List<ReviewParticipant> reviewParticipants = newReviewParticipant()
                .withInvite(invites.get(0), invites.get(1))
                .withStatus(PENDING)
                .withCompetition(competition)
                .withUser(user)
                .build(2);
        List<ReviewParticipantResource> expected = newReviewParticipantResource()
                .withCompetition(2L)
                .withCompetitionName("Competition in Assessor Panel")
                .withUser(1L)
                .build(2);

        when(reviewParticipantRepositoryMock.findByUserIdAndRole(1L, PANEL_ASSESSOR)).thenReturn(reviewParticipants);
        when(reviewParticipantMapperMock.mapToResource(reviewParticipants.get(0))).thenReturn(expected.get(0));
        when(reviewParticipantMapperMock.mapToResource(reviewParticipants.get(1))).thenReturn(expected.get(1));

        List<ReviewParticipantResource> actual = service.getAllInvitesByUser(1L).getSuccess();
        assertTrue(actual.isEmpty());
        InOrder inOrder = inOrder(reviewParticipantRepositoryMock);
        inOrder.verify(reviewParticipantRepositoryMock).findByUserIdAndRole(1L, PANEL_ASSESSOR);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void deleteInvite() {
        String email = "tom@poly.io";
        long competitionId = 11L;

        ReviewInvite reviewInvite = newReviewInvite()
                .withStatus(CREATED)
                .build();

        when(reviewInviteRepositoryMock.getByEmailAndCompetitionId(email, competitionId)).thenReturn(reviewInvite);

        service.deleteInvite(email, competitionId).getSuccess();

        InOrder inOrder = inOrder(reviewInviteRepositoryMock);
        inOrder.verify(reviewInviteRepositoryMock).getByEmailAndCompetitionId(email, competitionId);
        inOrder.verify(reviewInviteRepositoryMock).delete(reviewInvite);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void deleteInvite_sent() {
        String email = "tom@poly.io";
        long competitionId = 11L;
        ReviewInvite reviewInvite = newReviewInvite()
                .withStatus(SENT)
                .build();

        when(reviewInviteRepositoryMock.getByEmailAndCompetitionId(email, competitionId)).thenReturn(reviewInvite);

        ServiceResult<Void> serviceResult = service.deleteInvite(email, competitionId);

        assertTrue(serviceResult.isFailure());

        verify(reviewInviteRepositoryMock).getByEmailAndCompetitionId(email, competitionId);
        verifyNoMoreInteractions(reviewInviteRepositoryMock);
    }

    @Test
    public void deleteAllInvites() {
        long competitionId = 1L;

        when(competitionRepositoryMock.findOne(competitionId)).thenReturn(newCompetition().build());

        assertTrue(service.deleteAllInvites(competitionId).isSuccess());

        verify(competitionRepositoryMock).findOne(competitionId);
    }

    @Test
    public void deleteAllInvites_noCompetition() {
        long competitionId = 1L;

        when(competitionRepositoryMock.findOne(competitionId)).thenReturn(null);

        assertFalse(service.deleteAllInvites(competitionId).isSuccess());

        verify(competitionRepositoryMock).findOne(competitionId);
    }

    @Test
    public void openInvite() {
        Milestone milestone = newMilestone()
                .withType(ASSESSMENT_PANEL)
                .withDate(now().plusDays(1))
                .build();
        ReviewInvite reviewInvite = setUpAssessmentPanelInvite(newCompetition()
                .withName("my competition")
                .withMilestones(singletonList(milestone))
                .build(), SENT);
        when(reviewInviteRepositoryMock.getByHash(isA(String.class))).thenReturn(reviewInvite);
        ServiceResult<ReviewInviteResource> inviteServiceResult = service.openInvite(INVITE_HASH);

        assertTrue(inviteServiceResult.isSuccess());
        ReviewInviteResource reviewInviteResource = inviteServiceResult.getSuccess();
        assertEquals("my competition", reviewInviteResource.getCompetitionName());

        InOrder inOrder = inOrder(reviewInviteRepositoryMock, reviewInviteMapperMock);
        inOrder.verify(reviewInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(reviewInviteRepositoryMock).save(isA(ReviewInvite.class));
        inOrder.verify(reviewInviteMapperMock).mapToResource(isA(ReviewInvite.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void openInvite_inviteExpired() {
        Milestone milestone = newMilestone()
                .withType(ASSESSMENT_PANEL)
                .withDate(now().minusDays(1))
                .build();
        ReviewInvite reviewInvite = setUpAssessmentPanelInvite(newCompetition()
                .withName("my competition")
                .withMilestones(singletonList(milestone))
                .build(), SENT);
        when(reviewInviteRepositoryMock.getByHash(isA(String.class))).thenReturn(reviewInvite);
        ServiceResult<ReviewInviteResource> inviteServiceResult = service.openInvite("inviteHashExpired");

        assertTrue(inviteServiceResult.isFailure());
        assertTrue(inviteServiceResult.getFailure().is(new Error(ASSESSMENT_PANEL_INVITE_EXPIRED, "my competition")));

        InOrder inOrder = inOrder(reviewInviteRepositoryMock);
        inOrder.verify(reviewInviteRepositoryMock).getByHash("inviteHashExpired");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptInvite() {
        String openedInviteHash = "openedInviteHash";
        Competition competition = newCompetition().build();
        ReviewParticipant reviewParticipant = newReviewParticipant()
                .withInvite(newReviewInvite().withStatus(OPENED))
                .withUser(newUser())
                .withCompetition(competition)
                .build();

        when(reviewParticipantRepositoryMock.getByInviteHash(openedInviteHash)).thenReturn(reviewParticipant);
        when(applicationRepositoryMock.findByCompetitionAndInAssessmentReviewPanelTrueAndApplicationProcessActivityState(competition, ApplicationState.SUBMITTED)).thenReturn(emptyList());

        service.acceptInvite(openedInviteHash).getSuccess();

        assertEquals(ParticipantStatus.ACCEPTED, reviewParticipant.getStatus());

        InOrder inOrder = inOrder(reviewParticipantRepositoryMock, applicationRepositoryMock);
        inOrder.verify(reviewParticipantRepositoryMock).getByInviteHash(openedInviteHash);
        inOrder.verify(applicationRepositoryMock).findByCompetitionAndInAssessmentReviewPanelTrueAndApplicationProcessActivityState(competition, ApplicationState.SUBMITTED);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptInvite_existingApplicationsOnPanel() {
        String openedInviteHash = "openedInviteHash";
        Competition competition = newCompetition().build();
        ReviewParticipant reviewParticipant = newReviewParticipant()
                .withInvite(newReviewInvite().withStatus(OPENED))
                .withUser(newUser())
                .withCompetition(competition)
                .build();
        List<Application> applicationsOnPanel = newApplication().build(2);

        when(reviewParticipantRepositoryMock.getByInviteHash(openedInviteHash)).thenReturn(reviewParticipant);
        when(applicationRepositoryMock.findByCompetitionAndInAssessmentReviewPanelTrueAndApplicationProcessActivityState(competition, ApplicationState.SUBMITTED)).thenReturn(applicationsOnPanel);

        service.acceptInvite(openedInviteHash).getSuccess();

        assertEquals(ParticipantStatus.ACCEPTED, reviewParticipant.getStatus());

        InOrder inOrder = inOrder(reviewParticipantRepositoryMock, applicationRepositoryMock, reviewRepositoryMock);
        inOrder.verify(reviewParticipantRepositoryMock).getByInviteHash(openedInviteHash);
        inOrder.verify(applicationRepositoryMock).findByCompetitionAndInAssessmentReviewPanelTrueAndApplicationProcessActivityState(competition, ApplicationState.SUBMITTED);
        inOrder.verify(reviewRepositoryMock, times(2)).save(any(Review.class));

        inOrder.verifyNoMoreInteractions();
    }

    private void assertNotExistingAssessorUser(AssessorInviteOverviewResource assessorInviteOverviewResource) {
        assertNull(assessorInviteOverviewResource.getId());
        assertNull(assessorInviteOverviewResource.getBusinessType());
        assertFalse(assessorInviteOverviewResource.isCompliant());
    }

    private ReviewInvite setUpAssessmentPanelInvite(Competition competition, InviteStatus status) {
        return newReviewInvite()
                .withCompetition(competition)
                .withHash(Invite.generateInviteHash())
                .withStatus(status)
                .build();
    }

    private ReviewInvite createInviteExpectations(String name, String email, InviteStatus status, Competition competition) {
        return createLambdaMatcher(invite -> {
                    assertEquals(name, invite.getName());
                    assertEquals(email, invite.getEmail());
                    assertEquals(status, invite.getStatus());
                    assertEquals(competition, invite.getTarget());
                    assertFalse(invite.getHash().isEmpty());
                }
        );
    }

    private static ReviewParticipant createAssessmentPanelParticipantExpectations(ReviewInvite reviewInvite) {
        return createLambdaMatcher(assessmentPanelParticipant -> {
            assertNull(assessmentPanelParticipant.getId());
            assertEquals(reviewInvite.getTarget(), assessmentPanelParticipant.getProcess());
            assertEquals(reviewInvite, assessmentPanelParticipant.getInvite());
            assertEquals(PANEL_ASSESSOR, assessmentPanelParticipant.getRole());
            assertEquals(reviewInvite.getUser(), assessmentPanelParticipant.getUser());
        });
    }

    private AssessorInviteSendResource setUpAssessorInviteSendResource() {
        return newAssessorInviteSendResource()
                .withSubject("subject")
                .withContent("content")
                .build();
    }
}