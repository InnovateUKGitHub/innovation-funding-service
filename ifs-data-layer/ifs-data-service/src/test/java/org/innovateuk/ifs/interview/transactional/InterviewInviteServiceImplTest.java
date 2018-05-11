package org.innovateuk.ifs.interview.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.assessment.domain.AssessmentParticipant;
import org.innovateuk.ifs.assessment.repository.AssessmentParticipantRepository;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.interview.domain.InterviewInvite;
import org.innovateuk.ifs.interview.domain.InterviewParticipant;
import org.innovateuk.ifs.assessment.mapper.AssessorCreatedInviteMapper;
import org.innovateuk.ifs.assessment.mapper.AssessorInviteOverviewMapper;
import org.innovateuk.ifs.assessment.mapper.AvailableAssessorMapper;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.interview.mapper.InterviewInviteMapper;
import org.innovateuk.ifs.interview.repository.InterviewInviteRepository;
import org.innovateuk.ifs.interview.repository.InterviewParticipantRepository;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.Invite;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.domain.RejectionReason;
import org.innovateuk.ifs.invite.repository.RejectionReasonRepository;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer;
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
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.TRUE;
import static java.time.ZonedDateTime.now;
import static java.time.ZonedDateTime.of;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.assessment.builder.AssessmentParticipantBuilder.newAssessmentParticipant;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.MilestoneBuilder.newMilestone;
import static org.innovateuk.ifs.competition.resource.MilestoneType.*;
import static org.innovateuk.ifs.interview.builder.InterviewInviteBuilder.newInterviewInvite;
import static org.innovateuk.ifs.interview.builder.InterviewInviteResourceBuilder.newInterviewInviteResource;
import static org.innovateuk.ifs.interview.builder.InterviewParticipantBuilder.newInterviewParticipant;
import static org.innovateuk.ifs.invite.builder.AssessorCreatedInviteResourceBuilder.newAssessorCreatedInviteResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewResourceBuilder.newAssessorInviteOverviewResource;
import static org.innovateuk.ifs.invite.builder.AssessorInvitesToSendResourceBuilder.newAssessorInvitesToSendResource;
import static org.innovateuk.ifs.invite.builder.AvailableAssessorPageResourceBuilder.newAvailableAssessorPageResource;
import static org.innovateuk.ifs.invite.builder.AvailableAssessorResourceBuilder.newAvailableAssessorResource;
import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteResourceBuilder.newExistingUserStagedInviteResource;
import static org.innovateuk.ifs.invite.builder.RejectionReasonBuilder.newRejectionReason;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.PENDING;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
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

public class InterviewInviteServiceImplTest extends BaseServiceUnitTest<InterviewInviteServiceImpl> {

    private static final String UID = "5cc0ac0d-b969-40f5-9cc5-b9bdd98c86de";
    private static final String INVITE_HASH = "inviteHash";
    private Role assessorRole;

    @Mock
    private AssessorCreatedInviteMapper assessorCreatedInviteMapperMock;
    @Mock
    private AvailableAssessorMapper availableAssessorMapper;
    @Mock
    private AssessorInviteOverviewMapper assessorInviteOverviewMapperMock;
    @Mock
    private InterviewInviteRepository interviewInviteRepositoryMock;
    @Mock
    private UserMapper userMapperMock;
    @Mock
    private InterviewInviteMapper interviewInviteMapperMock;
    @Mock
    private InterviewParticipantRepository interviewParticipantRepositoryMock;
    @Mock
    private RejectionReasonRepository rejectionReasonRepositoryMock;
    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private ProfileRepository profileRepositoryMock;
    @Mock
    private LoggedInUserSupplier loggedInUserSupplierMock;
    @Mock
    private AssessmentParticipantRepository assessmentParticipantRepositoryMock;
    @Mock
    private CompetitionRepository competitionRepositoryMock;
    @Mock
    private SystemNotificationSource systemNotificationSourceMock;
    @Mock
    private NotificationTemplateRenderer notificationTemplateRendererMock;


    @Override
    protected InterviewInviteServiceImpl supplyServiceUnderTest() {
        return new InterviewInviteServiceImpl();
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

        InterviewInvite interviewInvite = setUpAssessmentInterviewPanelInvite(competition, SENT);
        InterviewParticipant interviewParticipant = new InterviewParticipant(interviewInvite);

        InterviewInviteResource expected = newInterviewInviteResource().withCompetitionName("my competition").build();
        RejectionReason rejectionReason = newRejectionReason().withId(1L).withReason("not available").build();
        Profile profile = newProfile().withId(profileId).build();
        User user = newUser().withId(userId).withProfileId(profile.getId()).build();

        UserResource senderResource = newUserResource().withId(-1L).withUID(UID).build();
        User sender = newUser().withId(-1L).withUid(UID).build();
        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(senderResource));
        when(userMapperMock.mapToDomain(senderResource)).thenReturn(sender);

        when(interviewInviteRepositoryMock.getByHash(INVITE_HASH)).thenReturn(interviewInvite);
        when(interviewInviteRepositoryMock.save(isA(InterviewInvite.class))).thenReturn(interviewInvite);
        when(interviewInviteMapperMock.mapToResource(same(interviewInvite))).thenReturn(expected);
        when(interviewParticipantRepositoryMock.getByInviteHash(INVITE_HASH)).thenReturn(interviewParticipant);
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

        when(assessmentParticipantRepositoryMock.findParticipantsNotOnInterviewPanel(competitionId, pageable))
                .thenReturn(expectedPage);
        when(availableAssessorMapper.mapToResource(participants.get(0)))
                .thenReturn(assessorItems.get(0));
        when(availableAssessorMapper.mapToResource(participants.get(1)))
                .thenReturn(assessorItems.get(1));

        AvailableAssessorPageResource actual = service.getAvailableAssessors(competitionId, pageable).getSuccess();

        verify(assessmentParticipantRepositoryMock).findParticipantsNotOnInterviewPanel(competitionId, pageable);
        verify(availableAssessorMapper).mapToResource(participants.get(0));
        verify(availableAssessorMapper).mapToResource(participants.get(1));

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

        when(assessmentParticipantRepositoryMock.findParticipantsNotOnInterviewPanel(competitionId, pageable))
                .thenReturn(assessorPage);

        AvailableAssessorPageResource result = service.getAvailableAssessors(competitionId, pageable).getSuccess();

        verify(assessmentParticipantRepositoryMock).findParticipantsNotOnInterviewPanel(competitionId, pageable);

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

        when(assessmentParticipantRepositoryMock.findParticipantsNotOnInterviewPanel(competitionId))
                .thenReturn(participants);

        List<Long> actualAssessorIds = service.getAvailableAssessorIds(competitionId).getSuccess();

        verify(assessmentParticipantRepositoryMock).findParticipantsNotOnInterviewPanel(competitionId);

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

        List<InterviewInvite> existingUserInvites = newInterviewInvite()
                .withId(1L, 2L, 3L, 4L)
                .withName("John Barnes", "Dave Smith", "Richard Turner", "Oliver Romero")
                .withEmail("john@example.com", "dave@example.com", "richard@example.com", "oliver@example.com")
                .withUser(compliantUser, nonCompliantUserNoSkills, nonCompliantUserNoAffiliations, nonCompliantUserNoAgreement)
                .build(4);

        List<AssessorCreatedInviteResource> expectedInvites = newAssessorCreatedInviteResource()
                .withId(
                        compliantUser.getId(),
                        nonCompliantUserNoSkills.getId(),
                        nonCompliantUserNoAffiliations.getId(),
                        nonCompliantUserNoAgreement.getId()
                )
                .withInviteId(1L, 2L, 3L, 4L)
                .withName("John Barnes", "Dave Smith", "Richard Turner", "Oliver Romero")
                .withInnovationAreas(innovationAreaList, emptyList(), emptyList(), emptyList())
                .withCompliant(true, false, false, false)
                .withEmail("john@example.com", "dave@example.com", "richard@example.com", "oliver@example.com")
                .build(4);

        long totalElements = 100L;

        Pageable pageable = new PageRequest(0, 20);
        Page<InterviewInvite> page = new PageImpl<>(existingUserInvites, pageable, totalElements);

        when(interviewInviteRepositoryMock.getByCompetitionIdAndStatus(
                competitionId,
                CREATED,
                pageable
        ))
                .thenReturn(page);
        when(assessorCreatedInviteMapperMock.mapToResource(isA(InterviewInvite.class))).thenReturn(
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

        InOrder inOrder = inOrder(interviewInviteRepositoryMock, assessorCreatedInviteMapperMock);
        inOrder.verify(interviewInviteRepositoryMock)
                .getByCompetitionIdAndStatus(competitionId, CREATED, pageable);
        inOrder.verify(assessorCreatedInviteMapperMock, times(4))
                .mapToResource(isA(InterviewInvite.class));
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
        when(interviewInviteRepositoryMock.save(isA(InterviewInvite.class))).thenReturn(new InterviewInvite());

        ServiceResult<Void> serviceResult = service.inviteUsers(existingAssessors);
        assertTrue(serviceResult.isSuccess());

        InOrder inOrder = inOrder(userRepositoryMock, competitionRepositoryMock, interviewInviteRepositoryMock);
        inOrder.verify(userRepositoryMock).findOne(existingAssessors.get(0).getUserId());
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(interviewInviteRepositoryMock).save(createInviteExpectations(existingUsers.get(0).getName(), existingUsers.get(0).getEmail(), CREATED, competition));
        inOrder.verify(userRepositoryMock).findOne(existingAssessors.get(1).getUserId());
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(interviewInviteRepositoryMock).save(createInviteExpectations(existingUsers.get(1).getName(), existingUsers.get(1).getEmail(), CREATED, competition));
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

        List<InterviewInvite> invites = newInterviewInvite()
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

        String templatePath = "invite_assessors_to_interview_panel_text.txt";

        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(interviewInviteRepositoryMock.getByCompetitionIdAndStatus(competition.getId(), CREATED)).thenReturn(invites);
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

        InOrder inOrder = inOrder(competitionRepositoryMock, interviewInviteRepositoryMock, notificationTemplateRendererMock);
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(interviewInviteRepositoryMock).getByCompetitionIdAndStatus(competition.getId(), CREATED);
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

        List<InterviewInvite> invites = newInterviewInvite()
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

        String templatePath = "invite_assessors_to_interview_panel_text.txt";

        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);
        when(interviewInviteRepositoryMock.getByIdIn(inviteIds)).thenReturn(invites);
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

        InOrder inOrder = inOrder(competitionRepositoryMock, interviewInviteRepositoryMock, notificationTemplateRendererMock);
        inOrder.verify(competitionRepositoryMock).findOne(competition.getId());
        inOrder.verify(interviewInviteRepositoryMock).getByIdIn(inviteIds);
        inOrder.verify(notificationTemplateRendererMock).renderTemplate(systemNotificationSourceMock, notificationTarget,
                templatePath, expectedNotificationArguments);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getInvitationOverview() throws Exception {
        long competitionId = 1L;
        Pageable pageable = new PageRequest(0, 5);
        List<InterviewParticipant> expectedParticipants = newInterviewParticipant()
                .withInvite(
                        newInterviewInvite()
                                .withName("Name 1", "Name 2", "Name 3", "Name 4", "Name 5")
                                .withSentOn(now())
                                .withStatus(SENT)
                                .buildArray(5, InterviewInvite.class)
                )
                .withStatus(PENDING)
                .build(5);

        Page<InterviewParticipant> pageResult = new PageImpl<>(expectedParticipants, pageable, 10);

        when(interviewParticipantRepositoryMock.getInterviewPanelAssessorsByCompetitionAndStatusContains(
                competitionId,
                singletonList(PENDING),
                pageable
        ))
                .thenReturn(pageResult);

        List<AssessorInviteOverviewResource> overviewResources = newAssessorInviteOverviewResource()
                .withName("Name 1", "Name 2", "Name 3", "Name 4", "Name 5")
                .build(5);

        when(assessorInviteOverviewMapperMock.mapToResource(isA(InterviewParticipant.class)))
                .thenReturn(
                        overviewResources.get(0),
                        overviewResources.get(1),
                        overviewResources.get(2),
                        overviewResources.get(3),
                        overviewResources.get(4)
                );

        ServiceResult<AssessorInviteOverviewPageResource> result =
                service.getInvitationOverview(competitionId, pageable, singletonList(PENDING));

        verify(interviewParticipantRepositoryMock)
                .getInterviewPanelAssessorsByCompetitionAndStatusContains(competitionId, singletonList(PENDING), pageable);
        verify(assessorInviteOverviewMapperMock, times(5))
                .mapToResource(isA(InterviewParticipant.class));

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
    public void deleteInvite() {
        String email = "tom@poly.io";
        long competitionId = 11L;

        InterviewInvite interviewInvite = newInterviewInvite()
                .withStatus(CREATED)
                .build();

        when(interviewInviteRepositoryMock.getByEmailAndCompetitionId(email, competitionId)).thenReturn(interviewInvite);

        service.deleteInvite(email, competitionId).getSuccess();

        InOrder inOrder = inOrder(interviewInviteRepositoryMock);
        inOrder.verify(interviewInviteRepositoryMock).getByEmailAndCompetitionId(email, competitionId);
        inOrder.verify(interviewInviteRepositoryMock).delete(interviewInvite);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void deleteInvite_sent() {
        String email = "tom@poly.io";
        long competitionId = 11L;
        InterviewInvite interviewInvite = newInterviewInvite()
                .withStatus(SENT)
                .build();

        when(interviewInviteRepositoryMock.getByEmailAndCompetitionId(email, competitionId)).thenReturn(interviewInvite);

        ServiceResult<Void> serviceResult = service.deleteInvite(email, competitionId);

        assertTrue(serviceResult.isFailure());

        verify(interviewInviteRepositoryMock).getByEmailAndCompetitionId(email, competitionId);
        verifyNoMoreInteractions(interviewInviteRepositoryMock);
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
                .withType(PANEL_DATE)
                .withDate(now().plusDays(1))
                .build();
        InterviewInvite interviewInvite = setUpAssessmentInterviewPanelInvite(newCompetition()
                .withName("my competition")
                .withMilestones(singletonList(milestone))
                .build(), SENT);
        when(interviewInviteRepositoryMock.getByHash(isA(String.class))).thenReturn(interviewInvite);
        ServiceResult<InterviewInviteResource> inviteServiceResult = service.openInvite(INVITE_HASH);

        assertTrue(inviteServiceResult.isSuccess());
        InterviewInviteResource interviewInviteResource = inviteServiceResult.getSuccess();
        assertEquals("my competition", interviewInviteResource.getCompetitionName());

        InOrder inOrder = inOrder(interviewInviteRepositoryMock, interviewInviteMapperMock);
        inOrder.verify(interviewInviteRepositoryMock).getByHash(INVITE_HASH);
        inOrder.verify(interviewInviteRepositoryMock).save(isA(InterviewInvite.class));
        inOrder.verify(interviewInviteMapperMock).mapToResource(isA(InterviewInvite.class));
        inOrder.verifyNoMoreInteractions();
    }
    
    @Test
    public void acceptInvite() {
        String openedInviteHash = "openedInviteHash";
        Competition competition = newCompetition().build();
        InterviewParticipant interviewParticipant = newInterviewParticipant()
                .withInvite(newInterviewInvite().withStatus(OPENED))
                .withUser(newUser())
                .withCompetition(competition)
                .build();

        when(interviewParticipantRepositoryMock.getByInviteHash(openedInviteHash)).thenReturn(interviewParticipant);

        service.acceptInvite(openedInviteHash).getSuccess();

        assertEquals(ParticipantStatus.ACCEPTED, interviewParticipant.getStatus());

        InOrder inOrder = inOrder(interviewParticipantRepositoryMock);
        inOrder.verify(interviewParticipantRepositoryMock).getByInviteHash(openedInviteHash);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptInvite_existingApplicationsOnPanel() {
        String openedInviteHash = "openedInviteHash";
        Competition competition = newCompetition().build();
        InterviewParticipant interviewParticipant = newInterviewParticipant()
                .withInvite(newInterviewInvite().withStatus(OPENED))
                .withUser(newUser())
                .withCompetition(competition)
                .build();

        when(interviewParticipantRepositoryMock.getByInviteHash(openedInviteHash)).thenReturn(interviewParticipant);

        service.acceptInvite(openedInviteHash).getSuccess();

        assertEquals(ParticipantStatus.ACCEPTED, interviewParticipant.getStatus());

        InOrder inOrder = inOrder(interviewParticipantRepositoryMock);
        inOrder.verify(interviewParticipantRepositoryMock).getByInviteHash(openedInviteHash);
        inOrder.verifyNoMoreInteractions();
    }

    private void assertNotExistingAssessorUser(AssessorInviteOverviewResource assessorInviteOverviewResource) {
        assertNull(assessorInviteOverviewResource.getId());
        assertNull(assessorInviteOverviewResource.getBusinessType());
        assertFalse(assessorInviteOverviewResource.isCompliant());
    }

    private InterviewInvite setUpAssessmentInterviewPanelInvite(Competition competition, InviteStatus status) {
        return newInterviewInvite()
                .withCompetition(competition)
                .withHash(Invite.generateInviteHash())
                .withStatus(status)
                .build();
    }

    private InterviewInvite createInviteExpectations(String name, String email, InviteStatus status, Competition competition) {
        return createLambdaMatcher(invite -> {
                    assertEquals(name, invite.getName());
                    assertEquals(email, invite.getEmail());
                    assertEquals(status, invite.getStatus());
                    assertEquals(competition, invite.getTarget());
                    assertFalse(invite.getHash().isEmpty());
                }
        );
    }
}
