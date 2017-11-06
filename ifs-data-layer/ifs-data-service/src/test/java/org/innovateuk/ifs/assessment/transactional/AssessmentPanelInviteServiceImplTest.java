package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.*;
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
import static org.innovateuk.ifs.assessment.builder.AssessmentPanelInviteResourceBuilder.newAssessmentPanelInviteResource;
import static org.innovateuk.ifs.assessment.builder.CompetitionAssessmentParticipantBuilder.newCompetitionAssessmentParticipant;
import static org.innovateuk.ifs.assessment.panel.builder.AssessmentPanelInviteBuilder.newAssessmentPanelInvite;
import static org.innovateuk.ifs.assessment.panel.builder.AssessmentPanelParticipantBuilder.newAssessmentPanelParticipant;
import static org.innovateuk.ifs.assessment.transactional.AssessmentPanelInviteServiceImpl.Notifications.INVITE_ASSESSOR_GROUP_TO_PANEL;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.ASSESSMENT_PANEL_INVITE_EXPIRED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.MilestoneBuilder.newMilestone;
import static org.innovateuk.ifs.competition.resource.MilestoneType.*;
import static org.innovateuk.ifs.invite.builder.AssessmentPanelParticipantResourceBuilder.newAssessmentPanelParticipantResource;
import static org.innovateuk.ifs.invite.builder.AssessorCreatedInviteResourceBuilder.newAssessorCreatedInviteResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteSendResourceBuilder.newAssessorInviteSendResource;
import static org.innovateuk.ifs.invite.builder.AssessorInvitesToSendResourceBuilder.newAssessorInvitesToSendResource;
import static org.innovateuk.ifs.invite.builder.AvailableAssessorPageResourceBuilder.newAvailableAssessorPageResource;
import static org.innovateuk.ifs.invite.builder.AvailableAssessorResourceBuilder.newAvailableAssessorResource;
import static org.innovateuk.ifs.invite.builder.CompetitionInviteBuilder.newCompetitionInvite;
import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteResourceBuilder.newExistingUserStagedInviteResource;
import static org.innovateuk.ifs.invite.builder.RejectionReasonBuilder.newRejectionReason;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.invite.domain.CompetitionParticipantRole.PANEL_ASSESSOR;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.PENDING;
import static org.innovateuk.ifs.notifications.builders.NotificationBuilder.newNotification;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.builder.AffiliationBuilder.newAffiliation;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
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

public class AssessmentPanelInviteServiceImplTest extends BaseServiceUnitTest<AssessmentPanelInviteServiceImpl> {
    private static final String UID = "5cc0ac0d-b969-40f5-9cc5-b9bdd98c86de";
    private static final String INVITE_HASH = "inviteHash";
    private Role assessorRole;

    @Override
    protected AssessmentPanelInviteServiceImpl supplyServiceUnderTest() {
        return new AssessmentPanelInviteServiceImpl();
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

        AssessmentPanelInvite assessmentPanelInvite = setUpAssessmentPanelInvite(competition, SENT);
        AssessmentPanelParticipant assessmentPanelParticipant = new AssessmentPanelParticipant(assessmentPanelInvite);

        AssessmentPanelInviteResource expected = newAssessmentPanelInviteResource().withCompetitionName("my competition").build();
        RejectionReason rejectionReason = newRejectionReason().withId(1L).withReason("not available").build();
        Profile profile = newProfile().withId(profileId).build();
        User user = newUser().withId(userId).withProfileId(profile.getId()).build();

        assessorRole = newRole().withName(UserRoleType.ASSESSOR.getName()).build();

        UserResource senderResource = newUserResource().withId(-1L).withUID(UID).build();
        User sender = newUser().withId(-1L).withUid(UID).build();
        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(senderResource));
        when(userMapperMock.mapToDomain(senderResource)).thenReturn(sender);

        when(assessmentPanelInviteRepositoryMock.getByHash(INVITE_HASH)).thenReturn(assessmentPanelInvite);
        when(assessmentPanelInviteRepositoryMock.save(isA(AssessmentPanelInvite.class))).thenReturn(assessmentPanelInvite);
        when(assessmentPanelInviteMapperMock.mapToResource(same(assessmentPanelInvite))).thenReturn(expected);
        when(assessmentPanelParticipantRepositoryMock.getByInviteHash(INVITE_HASH)).thenReturn(assessmentPanelParticipant);
        when(rejectionReasonRepositoryMock.findOne(rejectionReason.getId())).thenReturn(rejectionReason);
        when(userRepositoryMock.findOne(userId)).thenReturn(user);
        when(profileRepositoryMock.findOne(user.getProfileId())).thenReturn(profile);
        when(loggedInUserSupplierMock.get()).thenReturn(newUser().build());

        ReflectionTestUtils.setField(service, "webBaseUrl", "https://ifs-local-dev");
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

        List<CompetitionAssessmentParticipant> participants = newCompetitionAssessmentParticipant()
                .withUser(assessors.get(0), assessors.get(1))
                .build(2);

        Pageable pageable = new PageRequest(page, pageSize, new Sort(ASC, "firstName"));

        Page<CompetitionAssessmentParticipant> expectedPage = new PageImpl<>(participants, pageable, 2L);

        when(competitionParticipantRepositoryMock.findParticipantsNotOnPanel(competitionId, pageable))
                .thenReturn(expectedPage);
        when(profileRepositoryMock.findOne(assessors.get(0).getProfileId())).thenReturn(profile.get(0));
        when(profileRepositoryMock.findOne(assessors.get(1).getProfileId())).thenReturn(profile.get(1));
        when(innovationAreaMapperMock.mapToResource(innovationArea)).thenReturn(innovationAreaResources.get(0));

        AvailableAssessorPageResource actual = service.getAvailableAssessors(competitionId, pageable)
                .getSuccessObjectOrThrowException();

        verify(competitionParticipantRepositoryMock).findParticipantsNotOnPanel(competitionId, pageable);
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

        Pageable pageable = new PageRequest(page, pageSize, new Sort(ASC, "firstName"));

        Page<CompetitionAssessmentParticipant> assessorPage = new PageImpl<>(emptyList(), pageable, 0);

        when(competitionParticipantRepositoryMock.findParticipantsNotOnPanel(competitionId, pageable))
                .thenReturn(assessorPage);

        AvailableAssessorPageResource result = service.getAvailableAssessors(competitionId, pageable)
                .getSuccessObjectOrThrowException();

        verify(competitionParticipantRepositoryMock).findParticipantsNotOnPanel(competitionId, pageable);

        assertEquals(page, result.getNumber());
        assertEquals(pageSize, result.getSize());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
        assertEquals(emptyList(), result.getContent());
    }

    @Test
    public void getAvailableAssessorIds() throws Exception {
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

        List<CompetitionAssessmentParticipant> participants = newCompetitionAssessmentParticipant()
                .withUser(assessorUsers.get(0), assessorUsers.get(1))
                .build(2);

        when(competitionParticipantRepositoryMock.findParticipantsNotOnPanel(competitionId))
                .thenReturn(participants);

        List<Long> actualAssessorIds = service.getAvailableAssessorIds(competitionId)
                .getSuccessObjectOrThrowException();

        verify(competitionParticipantRepositoryMock).findParticipantsNotOnPanel(competitionId);

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

        List<AssessmentPanelInvite> existingUserInvites = newAssessmentPanelInvite()
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
        Page<AssessmentPanelInvite> page = new PageImpl<>(existingUserInvites, pageable, totalElements);

        when(assessmentPanelInviteRepositoryMock.getByCompetitionIdAndStatus(competitionId, CREATED, pageable)).thenReturn(page);
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

        InOrder inOrder = inOrder(assessmentPanelInviteRepositoryMock, innovationAreaMapperMock);
        inOrder.verify(assessmentPanelInviteRepositoryMock).getByCompetitionIdAndStatus(competitionId, CREATED, pageable);
        inOrder.verify(innovationAreaMapperMock).mapToResource(innovationArea);
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
        when(assessmentPanelInviteRepositoryMock.save(isA(AssessmentPanelInvite.class))).thenReturn(new AssessmentPanelInvite());

        ServiceResult<Void> serviceResult = service.inviteUsers(existingAssessors);
        assertTrue(serviceResult.isSuccess());

        InOrder inOrder = inOrder(userRepositoryMock, competitionRepositoryMock, assessmentPanelInviteRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(existingAssessors.get(0).getUserId());
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(assessmentPanelInviteRepositoryMock).save(createInviteExpectations(existingUsers.get(0).getName(), existingUsers.get(0).getEmail(), CREATED, competition));
        inOrder.verify(userRepositoryMock).findOne(existingAssessors.get(1).getUserId());
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(assessmentPanelInviteRepositoryMock).save(createInviteExpectations(existingUsers.get(1).getName(), existingUsers.get(1).getEmail(), CREATED, competition));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void sendAllInvites() throws Exception {
        List<String> emails = asList("john@email.com", "peter@email.com");
        List<String> names = asList("John Barnes", "Peter Jones");

        Competition competition = newCompetition()
                .withName("my competition")
                .withAssessorAcceptsDate(ZonedDateTime.parse("2017-08-24T12:00:00+01:00"))
                .withAssessorDeadlineDate(ZonedDateTime.parse("2017-08-30T12:00:00+01:00"))
                .build();

        List<AssessmentPanelInvite> invites = newAssessmentPanelInvite()
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
        NotificationTarget to1 = new ExternalUserNotificationTarget(names.get(0), emails.get(0));
        NotificationTarget to2 = new ExternalUserNotificationTarget(names.get(1), emails.get(1));

        List<Notification> notifications = newNotification()
                .withSource(from, from)
                .withMessageKey(INVITE_ASSESSOR_GROUP_TO_PANEL, INVITE_ASSESSOR_GROUP_TO_PANEL)
                .withTargets(singletonList(to1), singletonList(to2))
                .withGlobalArguments(expectedNotificationArguments1, expectedNotificationArguments2)
                .build(2);

        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(assessmentPanelInviteRepositoryMock.getByCompetitionIdAndStatus(competition.getId(), CREATED)).thenReturn(invites);
        when(userRepositoryMock.findByEmail(emails.get(0))).thenReturn(Optional.empty());
        when(userRepositoryMock.findByEmail(emails.get(1))).thenReturn(Optional.empty());
        when(roleRepositoryMock.findOneByName(UserRoleType.ASSESSOR.getName())).thenReturn(assessorRole);
        when(notificationSenderMock.sendNotification(notifications.get(0))).thenReturn(serviceSuccess(notifications.get(0)));
        when(notificationSenderMock.sendNotification(notifications.get(1))).thenReturn(serviceSuccess(notifications.get(1)));

        ServiceResult<Void> serviceResult = service.sendAllInvites(competition.getId(), assessorInviteSendResource);
        assertTrue(serviceResult.isSuccess());

        InOrder inOrder = inOrder(competitionRepositoryMock, assessmentPanelInviteRepositoryMock, userRepositoryMock, roleRepositoryMock, assessmentPanelParticipantRepositoryMock, notificationSenderMock);
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(assessmentPanelInviteRepositoryMock).getByCompetitionIdAndStatus(competition.getId(), CREATED);
        inOrder.verify(assessmentPanelParticipantRepositoryMock).save(createAssessmentPanelParticipantExpectations(invites.get(0)));
        inOrder.verify(notificationSenderMock).sendNotification(notifications.get(0));
        inOrder.verify(assessmentPanelParticipantRepositoryMock).save(createAssessmentPanelParticipantExpectations(invites.get(1)));
        inOrder.verify(notificationSenderMock).sendNotification(notifications.get(1));

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getAllInvitesToSend() throws Exception {
        List<String> emails = asList("john@email.com", "peter@email.com");
        List<String> names = asList("John Barnes", "Peter Jones");

        ZonedDateTime acceptsDate = of(2016, 12, 20, 12, 0,0,0, ZoneId.systemDefault());
        ZonedDateTime deadlineDate = of(2017, 1, 17, 12, 0,0,0, ZoneId.systemDefault());

        Competition competition = newCompetition()
                .withName("Competition in Assessor Panel")
                .withAssessorAcceptsDate(acceptsDate)
                .withAssessorDeadlineDate(deadlineDate)
                .build();

        List<AssessmentPanelInvite> invites = newAssessmentPanelInvite()
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

        NotificationTarget notificationTarget = new ExternalUserNotificationTarget("", "");

        String templatePath = "invite_assessors_to_assessors_panel_text.txt";

        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(assessmentPanelInviteRepositoryMock.getByCompetitionIdAndStatus(competition.getId(), CREATED)).thenReturn(invites);
        when(notificationTemplateRendererMock.renderTemplate(systemNotificationSourceMock, notificationTarget, templatePath,
                expectedNotificationArguments)).thenReturn(serviceSuccess("content"));

        AssessorInvitesToSendResource expectedAssessorInviteToSendResource = newAssessorInvitesToSendResource()
                .withContent("content")
                .withCompetitionId(competition.getId())
                .withCompetitionName(competition.getName())
                .withRecipients(names)
                .build();

        AssessorInvitesToSendResource result = service.getAllInvitesToSend(competition.getId()).getSuccessObjectOrThrowException();
        assertEquals(expectedAssessorInviteToSendResource, result);

        InOrder inOrder = inOrder(competitionRepositoryMock, assessmentPanelInviteRepositoryMock, notificationTemplateRendererMock);
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(assessmentPanelInviteRepositoryMock).getByCompetitionIdAndStatus(competition.getId(), CREATED);
        inOrder.verify(notificationTemplateRendererMock).renderTemplate(systemNotificationSourceMock, notificationTarget,
                templatePath, expectedNotificationArguments);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getAllInvitesToResend() throws Exception {
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

        List<AssessmentPanelInvite> invites = newAssessmentPanelInvite()
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

        NotificationTarget notificationTarget = new ExternalUserNotificationTarget("", "");

        String templatePath = "invite_assessors_to_assessors_panel_text.txt";

        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(assessmentPanelInviteRepositoryMock.getByIdIn(inviteIds)).thenReturn(invites);
        when(notificationTemplateRendererMock.renderTemplate(systemNotificationSourceMock, notificationTarget, templatePath,
                expectedNotificationArguments)).thenReturn(serviceSuccess("content"));

        AssessorInvitesToSendResource expectedAssessorInviteToSendResource = newAssessorInvitesToSendResource()
                .withContent("content")
                .withCompetitionId(competition.getId())
                .withCompetitionName(competition.getName())
                .withRecipients(names)
                .build();

        AssessorInvitesToSendResource result = service.getAllInvitesToResend(competition.getId(), inviteIds).getSuccessObjectOrThrowException();
        assertEquals(expectedAssessorInviteToSendResource, result);

        InOrder inOrder = inOrder(competitionRepositoryMock, assessmentPanelInviteRepositoryMock, notificationTemplateRendererMock);
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(assessmentPanelInviteRepositoryMock).getByIdIn(inviteIds);
        inOrder.verify(notificationTemplateRendererMock).renderTemplate(systemNotificationSourceMock, notificationTarget,
                templatePath, expectedNotificationArguments);
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

        List<AssessmentPanelInvite> invites = newAssessmentPanelInvite()
                .withCompetition(competition)
                .withEmail(emails.get(0), emails.get(1))
                .withHash(Invite.generateInviteHash())
                .withName(names.get(0), names.get(1))
                .withStatus(SENT)
                .withUser(newUser().build())
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
        NotificationTarget to1 = new ExternalUserNotificationTarget(names.get(0), emails.get(0));
        NotificationTarget to2 = new ExternalUserNotificationTarget(names.get(1), emails.get(1));

        List<Notification> notifications = newNotification()
                .withSource(from, from)
                .withMessageKey(INVITE_ASSESSOR_GROUP_TO_PANEL, INVITE_ASSESSOR_GROUP_TO_PANEL)
                .withTargets(singletonList(to1), singletonList(to2))
                .withGlobalArguments(expectedNotificationArguments1, expectedNotificationArguments2)
                .build(2);

        when(assessmentPanelInviteRepositoryMock.getByIdIn(inviteIds)).thenReturn(invites);
        when(notificationSenderMock.sendNotification(notifications.get(0))).thenReturn(serviceSuccess(notifications.get(0)));
        when(notificationSenderMock.sendNotification(notifications.get(1))).thenReturn(serviceSuccess(notifications.get(1)));

        ServiceResult<Void> serviceResult = service.resendInvites(inviteIds, assessorInviteSendResource);
        assertTrue(serviceResult.isSuccess());

        InOrder inOrder = inOrder(assessmentPanelInviteRepositoryMock, notificationSenderMock);
        inOrder.verify(assessmentPanelInviteRepositoryMock).getByIdIn(inviteIds);
        inOrder.verify(notificationSenderMock).sendNotification(notifications.get(0));
        inOrder.verify(notificationSenderMock).sendNotification(notifications.get(1));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInvitationOverview() throws Exception {
        long competitionId = 1L;
        Pageable pageable = new PageRequest(0, 5);
        List<AssessmentPanelParticipant> expectedParticipants = newAssessmentPanelParticipant()
                .withInvite(
                        newAssessmentPanelInvite()
                                .withName("Name 1", "Name 2", "Name 3", "Name 4", "Name 5")
                                .withSentOn(now())
                                .withStatus(SENT)
                                .buildArray(5, AssessmentPanelInvite.class)
                )
                .withStatus(PENDING)
                .build(5);

        Page<AssessmentPanelParticipant> pageResult = new PageImpl<>(expectedParticipants, pageable, 10);

        when(assessmentPanelParticipantRepositoryMock.getPanelAssessorsByCompetitionAndStatusContains(competitionId, singletonList(PENDING), pageable))
                .thenReturn(pageResult);
        when(participantStatusMapperMock.mapToResource(PENDING)).thenReturn(ParticipantStatusResource.PENDING);

        ServiceResult<AssessorInviteOverviewPageResource> result = service.getInvitationOverview(competitionId, pageable, singletonList(PENDING));

        verify(assessmentPanelParticipantRepositoryMock).getPanelAssessorsByCompetitionAndStatusContains(competitionId, singletonList(PENDING), pageable);
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

    @Test
    public void getAllInvitesByUser() throws Exception {

        User user = newUser()
                .withId(1L)
                .build();

        Milestone milestone = newMilestone()
                .withType(ASSESSMENT_PANEL)
                .withDate(now().minusDays(1))
                .build();
        Competition competition = newCompetition()
                .withId(2L)
                .withName("Competition in Assessor Panel")
                .withMilestones(singletonList(milestone))
                .build();

        List<AssessmentPanelInvite> invites = newAssessmentPanelInvite()
                .withEmail("paulplum@gmail.com")
                .withHash("")
                .withCompetition(competition)
                .withUser(user)
                .build(2);

        List<AssessmentPanelParticipant> assessmentPanelParticipants = newAssessmentPanelParticipant()
                .withInvite(invites.get(0), invites.get(1))
                .withStatus(PENDING)
                .withCompetition(competition)
                .withUser(user)
                .build(2);

        List<AssessmentPanelParticipantResource> expected = newAssessmentPanelParticipantResource()
                .withCompetition(2L)
                .withCompetitionName("Competition in Assessor Panel")
                .withUser(1L)
                .build(2);

        when(assessmentPanelParticipantRepositoryMock.findByUserIdAndRole(1L, PANEL_ASSESSOR)).thenReturn(assessmentPanelParticipants);
        when(assessmentPanelParticipantMapperMock.mapToResource(assessmentPanelParticipants.get(0))).thenReturn(expected.get(0));
        when(assessmentPanelParticipantMapperMock.mapToResource(assessmentPanelParticipants.get(1))).thenReturn(expected.get(1));

        List<AssessmentPanelParticipantResource> actual = service.getAllInvitesByUser(1L).getSuccessObject();
        assertEquals(actual.get(0), expected.get(0));
        assertEquals(actual.get(1), expected.get(1));
        InOrder inOrder = inOrder(assessmentPanelParticipantRepositoryMock);
        inOrder.verify(assessmentPanelParticipantRepositoryMock).findByUserIdAndRole(1L, PANEL_ASSESSOR);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void deleteInvite() {
        String email = "tom@poly.io";
        long competitionId = 11L;

        AssessmentPanelInvite assessmentPanelInvite = newAssessmentPanelInvite()
                .withStatus(CREATED)
                .build();

        when(assessmentPanelInviteRepositoryMock.getByEmailAndCompetitionId(email, competitionId)).thenReturn(assessmentPanelInvite);

        service.deleteInvite(email, competitionId).getSuccessObjectOrThrowException();

        InOrder inOrder = inOrder(assessmentPanelInviteRepositoryMock);
        inOrder.verify(assessmentPanelInviteRepositoryMock).getByEmailAndCompetitionId(email, competitionId);
        inOrder.verify(assessmentPanelInviteRepositoryMock).delete(assessmentPanelInvite);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void deleteInvite_sent() {
        String email = "tom@poly.io";
        long competitionId = 11L;
        AssessmentPanelInvite assessmentPanelInvite = newAssessmentPanelInvite()
                .withStatus(SENT)
                .build();

        when(assessmentPanelInviteRepositoryMock.getByEmailAndCompetitionId(email, competitionId)).thenReturn(assessmentPanelInvite);

        ServiceResult<Void> serviceResult = service.deleteInvite(email, competitionId);

        assertTrue(serviceResult.isFailure());

        verify(assessmentPanelInviteRepositoryMock).getByEmailAndCompetitionId(email, competitionId);
        verifyNoMoreInteractions(assessmentPanelInviteRepositoryMock);
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
    public void openInvite() throws Exception {

        Milestone milestone = newMilestone()
                .withType(ASSESSMENT_PANEL)
                .withDate(now().plusDays(1))
                .build();
        AssessmentPanelInvite assessmentPanelInvite = setUpAssessmentPanelInvite(newCompetition()
                .withName("my competition")
                .withMilestones(singletonList(milestone))
                .build(), SENT);
        when(assessmentPanelInviteRepositoryMock.getByHash(isA(String.class))).thenReturn(assessmentPanelInvite);
        ServiceResult<AssessmentPanelInviteResource> inviteServiceResult = service.openInvite(INVITE_HASH);

        assertTrue(inviteServiceResult.isSuccess());
        AssessmentPanelInviteResource assessmentPanelInviteResource = inviteServiceResult.getSuccessObjectOrThrowException();
        assertEquals("my competition", assessmentPanelInviteResource.getCompetitionName());

        InOrder inOrder = inOrder(assessmentPanelInviteRepositoryMock, assessmentPanelInviteMapperMock);
        inOrder.verify(assessmentPanelInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(assessmentPanelInviteRepositoryMock).save(isA(AssessmentPanelInvite.class));
        inOrder.verify(assessmentPanelInviteMapperMock).mapToResource(isA(AssessmentPanelInvite.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void openInvite_inviteExpired() throws Exception {

        Milestone milestone = newMilestone()
                .withType(ASSESSMENT_PANEL)
                .withDate(now().minusDays(1))
                .build();
        AssessmentPanelInvite assessmentPanelInvite = setUpAssessmentPanelInvite(newCompetition()
                .withName("my competition")
                .withMilestones(singletonList(milestone))
                .build(), SENT);
        when(assessmentPanelInviteRepositoryMock.getByHash(isA(String.class))).thenReturn(assessmentPanelInvite);
        ServiceResult<AssessmentPanelInviteResource> inviteServiceResult = service.openInvite("inviteHashExpired");

        assertTrue(inviteServiceResult.isFailure());
        assertTrue(inviteServiceResult.getFailure().is(new Error(ASSESSMENT_PANEL_INVITE_EXPIRED, "my competition")));

        InOrder inOrder = inOrder(assessmentPanelInviteRepositoryMock);
        inOrder.verify(assessmentPanelInviteRepositoryMock).getByHash("inviteHashExpired");
        inOrder.verifyNoMoreInteractions();
    }

    private void assertNotExistingAssessorUser(AssessorInviteOverviewResource assessorInviteOverviewResource) {
        assertNull(assessorInviteOverviewResource.getId());
        assertNull(assessorInviteOverviewResource.getBusinessType());
        assertFalse(assessorInviteOverviewResource.isCompliant());
    }

    private CompetitionInvite setUpCompetitionInvite(Competition competition, InviteStatus status, InnovationArea innovationArea) {
        return newCompetitionInvite()
                .withCompetition(competition)
                .withHash(Invite.generateInviteHash())
                .withStatus(status)
                .withInnovationArea(innovationArea)
                .build();
    }

    private AssessmentPanelInvite setUpAssessmentPanelInvite(Competition competition, InviteStatus status) {
        return newAssessmentPanelInvite()
                .withCompetition(competition)
                .withHash(Invite.generateInviteHash())
                .withStatus(status)
                .build();
    }

    private AssessmentPanelInvite createInviteExpectations(String name, String email, InviteStatus status, Competition competition) {
        return createLambdaMatcher(invite -> {
                    assertEquals(name, invite.getName());
                    assertEquals(email, invite.getEmail());
                    assertEquals(status, invite.getStatus());
                    assertEquals(competition, invite.getTarget());
                    assertFalse(invite.getHash().isEmpty());
                }
        );
    }

    private AssessmentPanelParticipant createAssessmentPanelParticipantExpectations(AssessmentPanelInvite assessmentPanelInvite) {
        return createLambdaMatcher(assessmentPanelParticipant -> {
            assertNull(assessmentPanelParticipant.getId());
            assertEquals(assessmentPanelInvite.getTarget(), assessmentPanelParticipant.getProcess());
            assertEquals(assessmentPanelInvite, assessmentPanelParticipant.getInvite());
            assertEquals(PANEL_ASSESSOR, assessmentPanelParticipant.getRole());
            assertEquals(assessmentPanelInvite.getUser(), assessmentPanelParticipant.getUser());
        });
    }

    private AssessorInviteSendResource setUpAssessorInviteSendResource() {
        return newAssessorInviteSendResource()
                .withSubject("subject")
                .withContent("content")
                .build();
    }
}
