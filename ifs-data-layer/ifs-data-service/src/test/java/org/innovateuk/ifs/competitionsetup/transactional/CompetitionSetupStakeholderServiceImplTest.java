package org.innovateuk.ifs.competitionsetup.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.builder.CompetitionBuilder;
import org.innovateuk.ifs.competition.builder.StakeholderBuilder;
import org.innovateuk.ifs.competition.builder.StakeholderInviteBuilder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.competition.domain.Stakeholder;
import org.innovateuk.ifs.competition.domain.StakeholderInvite;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.StakeholderInviteRepository;
import org.innovateuk.ifs.competition.repository.StakeholderRepository;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.builder.UserBuilder;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.time.ZonedDateTime.now;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.invite.domain.Invite.generateInviteHash;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests the CompetitionSetupStakeholderServiceImpl with mocked repository.
 */
public class CompetitionSetupStakeholderServiceImplTest extends BaseServiceUnitTest<CompetitionSetupStakeholderServiceImpl> {

    private UserResource invitedUser = null;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private StakeholderInviteRepository stakeholderInviteRepositoryMock;

    @Mock
    private CompetitionRepository competitionRepositoryMock;

    @Mock
    private NotificationService notificationServiceMock;

    @Mock
    private LoggedInUserSupplier loggedInUserSupplierMock;

    @Mock
    private StakeholderRepository stakeholderRepositoryMock;

    @Mock
    private UserMapper userMapperMock;

    @Override
    protected CompetitionSetupStakeholderServiceImpl supplyServiceUnderTest() {
        return new CompetitionSetupStakeholderServiceImpl();
    }

    @Before
    public void setUp() {

        invitedUser = UserResourceBuilder.newUserResource()
                .withFirstName("Rayon")
                .withLastName("Kevin")
                .withEmail("Rayon.Kevin@gmail.com")
                .build();
    }

    @Test
    public void inviteStakeholderWhenUserDetailsMissing() throws Exception {

        UserResource invitedUser = UserResourceBuilder.newUserResource().build();

        ServiceResult<Void> result = service.inviteStakeholder(invitedUser, 1L);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(STAKEHOLDER_INVITE_INVALID));
        verify(stakeholderInviteRepositoryMock, never()).save(any(StakeholderInvite.class));
    }

    @Test
    public void inviteStakeholderWhenEmailDomainIsIncorrect() throws Exception {

        invitedUser.setEmail("Rayon.Kevin@innovateuk.ukri.org");

        ServiceResult<Void> result = service.inviteStakeholder(invitedUser, 1L);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(STAKEHOLDERS_CANNOT_BE_INTERNAL_USERS));
        verify(stakeholderInviteRepositoryMock, never()).save(any(StakeholderInvite.class));
    }

    @Test
    public void inviteAlreadyInvitedStakeholderFailure() throws Exception {

        long competitionId = 1L;

        String user1Name = "Rayon Kevin";
        String user2Name = "Sonal Dsilva";
        String user1Email = "Rayon.Kevin@gmail.com";
        String user2Email = "Sonal.Dsilva@gmail.com";

        when(stakeholderInviteRepositoryMock.existsByCompetitionIdAndStatusAndEmail(competitionId, SENT, user1Email)).thenReturn(true);

        ServiceResult<Void> result = service.inviteStakeholder(invitedUser, 1L);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(STAKEHOLDER_INVITE_TARGET_USER_ALREADY_INVITED));
        verify(stakeholderInviteRepositoryMock, never()).save(any(StakeholderInvite.class));
        verify(stakeholderInviteRepositoryMock).existsByCompetitionIdAndStatusAndEmail(competitionId, SENT, user1Email);
    }

    @Test
    public void inviteUserAlreadyStakeholderOnCompetitionFailure() {

        long competitionId = 1L;
        long stakeholderUser1 = 12L;
        long stakeholderUser2 = 14L;
        String email1 = "user1@gmail.com";
        String email2 = "Rayon.Kevin@gmail.com";

        List<User> stakeholderUsers = UserBuilder.newUser()
                .withId(stakeholderUser1, stakeholderUser2)
                .withEmailAddress(email1, email2)
                .build(2);

        List<Stakeholder> stakeholders = StakeholderBuilder.newStakeholder()
                .withUser(stakeholderUsers.get(0), stakeholderUsers.get(1))
                .build(2);

        when(stakeholderInviteRepositoryMock.existsByCompetitionIdAndStatusAndEmail(competitionId, SENT, email1)).thenReturn(false);
        when(stakeholderRepositoryMock.existsByCompetitionIdAndStakeholderEmail(competitionId, email2)).thenReturn(true);

        ServiceResult<Void> result = service.inviteStakeholder(invitedUser, 1L);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(STAKEHOLDER_HAS_ACCEPTED_INVITE));
    }

    @Test
    public void addExistingUserAsStakeholder() {

        long competitionId = 1L;
        long stakeholderUserId = 2L;
        String competitionName = "competition1";
        Competition competition = CompetitionBuilder.newCompetition()
                .withId(competitionId)
                .withName(competitionName)
                .build();

        User stakeholderUser = UserBuilder.newUser()
                .withId(stakeholderUserId)
                .withFirstName("Rayon")
                .withLastName("Kevin")
                .withEmailAddress("Rayon.Kevin@gmail.com")
                .build();

        Stakeholder savedStakeholderInDB = new Stakeholder(competition, stakeholderUser);
        User user = newUser().withId(stakeholderUserId).withEmailAddress("Rayon.Kevin@gmail.com").build();
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);

        when(stakeholderInviteRepositoryMock.existsByCompetitionIdAndStatusAndEmail(competitionId, SENT, stakeholderUser.getEmail())).thenReturn(false);
        when(stakeholderRepositoryMock.existsByCompetitionIdAndStakeholderEmail(competitionId, stakeholderUser.getEmail())).thenReturn(false);
        when(userRepositoryMock.findByEmail(invitedUser.getEmail())).thenReturn(Optional.of(user));
        when(userRepositoryMock.save(any(User.class))).thenReturn(stakeholderUser);
        when(userRepositoryMock.findOne(stakeholderUserId)).thenReturn(stakeholderUser);
        when(stakeholderRepositoryMock.save(any(Stakeholder.class))).thenReturn(savedStakeholderInDB);
        when(notificationServiceMock.sendNotificationWithFlush(any(Notification.class), eq(EMAIL))).thenReturn(serviceSuccess());
        when(competitionRepositoryMock.findOne(competitionId)).thenReturn(competition);

        ServiceResult<Void> result = service.inviteStakeholder(invitedUser, 1L);

        assertTrue(result.isSuccess());

        verify(stakeholderRepositoryMock).save(savedStakeholderInDB);
        verify(notificationServiceMock).sendNotificationWithFlush(notificationCaptor.capture(), eq(EMAIL));

    }

    @Test
    public void inviteStakeholderSuccess() throws Exception {

        long competitionId = 1L;
        String competitionName = "competition1";
        Competition competition = CompetitionBuilder.newCompetition()
                .withId(competitionId)
                .withName(competitionName)
                .build();

        when(userRepositoryMock.findByEmail(invitedUser.getEmail())).thenReturn(Optional.empty());
        when(stakeholderInviteRepositoryMock.findByEmail(invitedUser.getEmail())).thenReturn(Collections.emptyList());
        when(competitionRepositoryMock.findById(competitionId)).thenReturn(Optional.of(competition));

        StakeholderInvite savedStakeholderInvite = new StakeholderInvite(competition,
                invitedUser.getFirstName() + " " + invitedUser.getLastName(),
                invitedUser.getEmail(),
                generateInviteHash(),
                CREATED);

        when(stakeholderInviteRepositoryMock.save(any(StakeholderInvite.class))).thenReturn(savedStakeholderInvite);
        when(notificationServiceMock.sendNotificationWithFlush(any(Notification.class), eq(EMAIL))).thenReturn(serviceSuccess());

        User loggedInUser = newUser().build();
        when(loggedInUserSupplierMock.get()).thenReturn(loggedInUser);

        ServiceResult<Void> result = service.inviteStakeholder(invitedUser, competitionId);
        assertTrue(result.isSuccess());
        verify(stakeholderInviteRepositoryMock, times(2)).save(any(StakeholderInvite.class));

        // Create a captor and verify that the correct and expected StakeholderInvite was saved
        ArgumentCaptor<StakeholderInvite> captor = ArgumentCaptor.forClass(StakeholderInvite.class);
        verify(stakeholderInviteRepositoryMock, times(2)).save(captor.capture());
        String expectedName = "Rayon Kevin";
        String expectedEmail = "Rayon.Kevin@gmail.com";

        StakeholderInvite savedStakeholderInvite1 = captor.getAllValues().get(0);
        assertEquals(competition, savedStakeholderInvite1.getTarget());
        assertEquals(expectedName, savedStakeholderInvite1.getName());
        assertEquals(expectedEmail, savedStakeholderInvite1.getEmail());
        assertNotNull(savedStakeholderInvite1.getHash());
        assertEquals(CREATED, savedStakeholderInvite1.getStatus());
        assertNull(savedStakeholderInvite1.getSentBy());
        assertNull(savedStakeholderInvite1.getSentOn());

        //Create a captor for the sent notification
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationServiceMock).sendNotificationWithFlush(notificationCaptor.capture(), eq(EMAIL));

        Notification sentNotification = notificationCaptor.getValue();
        assertEquals("competition1", sentNotification.getGlobalArguments().get("competitionName"));
        assertEquals("null/management/stakeholder/" + savedStakeholderInvite.getHash() + "/register",
                sentNotification.getGlobalArguments().get("inviteUrl"));
        assertEquals(expectedName, sentNotification.getTo().get(0).getName());
        assertEquals(expectedEmail, sentNotification.getTo().get(0).getEmailAddress());
        assertEquals(CompetitionSetupStakeholderServiceImpl.Notifications.STAKEHOLDER_INVITE, sentNotification.getMessageKey());

        //Assert that correct StakeholderInvite was saved after the notification was sent
        StakeholderInvite savedStakeholderInvite2 = captor.getAllValues().get(1);
        assertEquals(competition, savedStakeholderInvite2.getTarget());
        assertEquals(expectedName, savedStakeholderInvite2.getName());
        assertEquals(expectedEmail, savedStakeholderInvite2.getEmail());
        assertNotNull(savedStakeholderInvite2.getHash());
        assertEquals(SENT, savedStakeholderInvite2.getStatus());
        assertEquals(loggedInUser, savedStakeholderInvite2.getSentBy());
        assertFalse(now().isBefore(savedStakeholderInvite2.getSentOn()));
    }

    @Test
    public void findStakeholders() throws Exception {

        long competitionId = 1L;

        long stakeholderUser1 = 12L;
        long stakeholderUser2 = 14L;

        List<User> stakeholderUsers = UserBuilder.newUser()
                .withId(stakeholderUser1, stakeholderUser2)
                .build(2);
        List<UserResource> stakeholderUserResources = UserResourceBuilder.newUserResource()
                .withId(stakeholderUser1, stakeholderUser2)
                .build(2);
        List<Stakeholder> stakeholders = StakeholderBuilder.newStakeholder()
                .withUser(stakeholderUsers.get(0), stakeholderUsers.get(1))
                .build(2);

        when(stakeholderRepositoryMock.findStakeholders(competitionId)).thenReturn(stakeholders);
        when(userMapperMock.mapToResource(stakeholderUsers.get(0))).thenReturn(stakeholderUserResources.get(0));
        when(userMapperMock.mapToResource(stakeholderUsers.get(1))).thenReturn(stakeholderUserResources.get(1));

        ServiceResult<List<UserResource>> result = service.findStakeholders(competitionId);
        assertTrue(result.isSuccess());
        assertEquals(stakeholderUserResources, result.getSuccess());

        verify(stakeholderRepositoryMock).findStakeholders(competitionId);
    }

    @Test
    public void addStakeholder() throws Exception {

        long competitionId = 1L;
        long stakeholderUserId = 2L;

        String competitionName = "competition1";
        Competition competition = CompetitionBuilder.newCompetition()
                .withId(competitionId)
                .withName(competitionName)
                .build();

        String stakeholderFirstName = "Rayon";
        String stakeholderLastName = "Kevin";
        String stakeholderUserEmail = "Rayon.Kevin@gmail.com";
        User stakeholderUser = UserBuilder.newUser()
                .withId(stakeholderUserId)
                .withFirstName(stakeholderFirstName)
                .withLastName(stakeholderLastName)
                .withEmailAddress(stakeholderUserEmail)
                .build();

        Stakeholder savedStakeholderInDB = new Stakeholder(competition, stakeholderUser);
        when(competitionRepositoryMock.findById(competitionId)).thenReturn(Optional.of(competition));
        when(userRepositoryMock.findById(stakeholderUserId)).thenReturn(Optional.of(stakeholderUser));
        when(stakeholderRepositoryMock.save(any(Stakeholder.class))).thenReturn(savedStakeholderInDB);
        when(notificationServiceMock.sendNotificationWithFlush(any(Notification.class), eq(EMAIL))).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.addStakeholder(competitionId, stakeholderUserId);
        assertTrue(result.isSuccess());

        verify(stakeholderRepositoryMock).save(any(Stakeholder.class));

        // Create a captor and verify that the correct and expected Stakeholder was saved
        ArgumentCaptor<Stakeholder> captor = ArgumentCaptor.forClass(Stakeholder.class);
        verify(stakeholderRepositoryMock).save(captor.capture());
        Stakeholder savedStakeholder = captor.getValue();

        assertEquals(competition, savedStakeholder.getProcess());
        assertEquals(stakeholderUser, savedStakeholder.getUser());
        assertEquals(CompetitionParticipantRole.STAKEHOLDER, savedStakeholder.getRole());
        assertEquals(ParticipantStatus.ACCEPTED, savedStakeholder.getStatus());


        verify(notificationServiceMock).sendNotificationWithFlush(any(Notification.class), eq(EMAIL));

        //Create a captor for the sent notification
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationServiceMock).sendNotificationWithFlush(notificationCaptor.capture(), eq(EMAIL));

        Notification sentNotification = notificationCaptor.getValue();
        assertEquals(competitionName, sentNotification.getGlobalArguments().get("competitionName"));
        assertEquals("null/management/dashboard/live", sentNotification.getGlobalArguments().get("dashboardUrl"));
        assertEquals(stakeholderFirstName + " " + stakeholderLastName, sentNotification.getTo().get(0).getName());
        assertEquals(stakeholderUserEmail, sentNotification.getTo().get(0).getEmailAddress());
        assertEquals(CompetitionSetupStakeholderServiceImpl.Notifications.ADD_STAKEHOLDER, sentNotification.getMessageKey());
    }

    @Test
    public void removeStakeholder() throws Exception {

        long competitionId = 1L;
        long stakeholderUserId = 2L;

        ServiceResult<Void> result = service.removeStakeholder(competitionId, stakeholderUserId);
        assertTrue(result.isSuccess());

        verify(stakeholderRepositoryMock).deleteStakeholder(competitionId, stakeholderUserId);
    }

    @Test
    public void findPendingStakeholderInvites() throws Exception {

        long competitionId = 1L;

        String user1Name = "Rayon Kevin";
        String user2Name = "Sonal Dsilva";
        String user1Email = "Rayon.Kevin@gmail.com";
        String user2Email = "Sonal.Dsilva@gmail.com";
        List<StakeholderInvite> pendingStakeholderInvites = StakeholderInviteBuilder.newStakeholderInvite()
                .withName(user1Name, user2Name)
                .withEmail(user1Email, user2Email)
                .build(2);

        when(stakeholderInviteRepositoryMock.findByCompetitionIdAndStatus(competitionId, SENT)).thenReturn(pendingStakeholderInvites);

        ServiceResult<List<UserResource>> result = service.findPendingStakeholderInvites(competitionId);
        assertTrue(result.isSuccess());

        verify(stakeholderInviteRepositoryMock).findByCompetitionIdAndStatus(competitionId, SENT);
        assertEquals(user1Name, result.getSuccess().get(0).getFirstName());
        assertEquals(user2Name, result.getSuccess().get(1).getFirstName());
        assertEquals(user1Email, result.getSuccess().get(0).getEmail());
        assertEquals(user2Email, result.getSuccess().get(1).getEmail());
    }
}

