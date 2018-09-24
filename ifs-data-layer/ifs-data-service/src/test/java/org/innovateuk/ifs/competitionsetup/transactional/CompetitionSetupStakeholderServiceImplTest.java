package org.innovateuk.ifs.competitionsetup.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.builder.CompetitionBuilder;
import org.innovateuk.ifs.competition.builder.StakeholderBuilder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Stakeholder;
import org.innovateuk.ifs.competition.domain.StakeholderInvite;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.StakeholderInviteRepository;
import org.innovateuk.ifs.competition.repository.StakeholderRepository;
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
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_UNEXPECTED_ERROR;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.STAKEHOLDER_INVITE_EMAIL_TAKEN;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.STAKEHOLDER_INVITE_INVALID;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.STAKEHOLDER_INVITE_INVALID_EMAIL;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.STAKEHOLDER_INVITE_TARGET_USER_ALREADY_INVITED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.invite.domain.Invite.generateInviteHash;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        invitedUser.setEmail("Rayon.Kevin@innovateuk.gov.uk");

        ServiceResult<Void> result = service.inviteStakeholder(invitedUser, 1L);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(STAKEHOLDER_INVITE_INVALID_EMAIL));
        verify(stakeholderInviteRepositoryMock, never()).save(any(StakeholderInvite.class));
    }

    @Test
    public void inviteStakeholderWhenEmailAlreadyTaken() throws Exception {

        when(userRepositoryMock.findByEmail(invitedUser.getEmail())).thenReturn(Optional.of(newUser().build()));

        ServiceResult<Void> result = service.inviteStakeholder(invitedUser, 1L);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(STAKEHOLDER_INVITE_EMAIL_TAKEN));
        verify(stakeholderInviteRepositoryMock, never()).save(any(StakeholderInvite.class));
    }

    @Test
    public void inviteStakeholderWhenUserAlreadyInvited() throws Exception {
        StakeholderInvite stakeholderInvite = new StakeholderInvite();

        when(userRepositoryMock.findByEmail(invitedUser.getEmail())).thenReturn(Optional.empty());
        when(stakeholderInviteRepositoryMock.findByEmail(invitedUser.getEmail())).thenReturn(Collections.singletonList(stakeholderInvite));

        ServiceResult<Void> result = service.inviteStakeholder(invitedUser, 1L);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(STAKEHOLDER_INVITE_TARGET_USER_ALREADY_INVITED));
        verify(stakeholderInviteRepositoryMock, never()).save(any(StakeholderInvite.class));

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
        when(competitionRepositoryMock.findOne(competitionId)).thenReturn(competition);

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
        assertEquals("null/management/competition/setup/stakeholder/" + savedStakeholderInvite.getHash() + "/register",
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
        assertTrue(now().isAfter(savedStakeholderInvite2.getSentOn()));
    }

    @Test
    public void inviteStakeholderSendNotificationFailure() throws Exception {

        long competitionId = 1L;
        String competitionName = "competition1";
        Competition competition = CompetitionBuilder.newCompetition()
                .withId(competitionId)
                .withName(competitionName)
                .build();

        when(userRepositoryMock.findByEmail(invitedUser.getEmail())).thenReturn(Optional.empty());
        when(stakeholderInviteRepositoryMock.findByEmail(invitedUser.getEmail())).thenReturn(Collections.emptyList());
        when(competitionRepositoryMock.findOne(competitionId)).thenReturn(competition);

        StakeholderInvite savedStakeholderInvite = new StakeholderInvite(competition,
                invitedUser.getFirstName() + " " + invitedUser.getLastName(),
                invitedUser.getEmail(),
                generateInviteHash(),
                CREATED);

        when(stakeholderInviteRepositoryMock.save(any(StakeholderInvite.class))).thenReturn(savedStakeholderInvite);
        when(notificationServiceMock.sendNotificationWithFlush(any(Notification.class), eq(EMAIL))).thenReturn(serviceFailure(GENERAL_UNEXPECTED_ERROR));

        ServiceResult<Void> result = service.inviteStakeholder(invitedUser, competitionId);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(GENERAL_UNEXPECTED_ERROR));

        // Verify that the save was called only once. In other words, it was not called after notification send failure.
        verify(stakeholderInviteRepositoryMock).save(any(StakeholderInvite.class));
        verify(notificationServiceMock).sendNotificationWithFlush(any(Notification.class), eq(EMAIL));

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
}

