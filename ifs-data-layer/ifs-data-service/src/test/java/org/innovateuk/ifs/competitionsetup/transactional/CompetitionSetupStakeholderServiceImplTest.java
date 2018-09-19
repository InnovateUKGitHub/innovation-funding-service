package org.innovateuk.ifs.competitionsetup.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.builder.CompetitionBuilder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.StakeholderInvite;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.StakeholderInviteRepository;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.STAKEHOLDER_INVITE_EMAIL_TAKEN;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.STAKEHOLDER_INVITE_INVALID;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.STAKEHOLDER_INVITE_INVALID_EMAIL;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.STAKEHOLDER_INVITE_TARGET_USER_ALREADY_INVITED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
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
        verify(stakeholderInviteRepositoryMock, never()).save(Mockito.any(StakeholderInvite.class));
    }

    @Test
    public void inviteStakeholderWhenEmailDomainIsIncorrect() throws Exception {

        invitedUser.setEmail("Rayon.Kevin@innovateuk.gov.uk");

        ServiceResult<Void> result = service.inviteStakeholder(invitedUser, 1L);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(STAKEHOLDER_INVITE_INVALID_EMAIL));
        verify(stakeholderInviteRepositoryMock, never()).save(Mockito.any(StakeholderInvite.class));
    }

    @Test
    public void inviteStakeholderWhenEmailAlreadyTaken() throws Exception {

        when(userRepositoryMock.findByEmail(invitedUser.getEmail())).thenReturn(Optional.of(newUser().build()));

        ServiceResult<Void> result = service.inviteStakeholder(invitedUser, 1L);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(STAKEHOLDER_INVITE_EMAIL_TAKEN));
        verify(stakeholderInviteRepositoryMock, never()).save(Mockito.any(StakeholderInvite.class));
    }

    @Test
    public void inviteStakeholderWhenUserAlreadyInvited() throws Exception {
        StakeholderInvite stakeholderInvite = new StakeholderInvite();

        when(userRepositoryMock.findByEmail(invitedUser.getEmail())).thenReturn(Optional.empty());
        when(stakeholderInviteRepositoryMock.findByEmail(invitedUser.getEmail())).thenReturn(Collections.singletonList(stakeholderInvite));

        ServiceResult<Void> result = service.inviteStakeholder(invitedUser, 1L);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(STAKEHOLDER_INVITE_TARGET_USER_ALREADY_INVITED));
        verify(stakeholderInviteRepositoryMock, never()).save(Mockito.any(StakeholderInvite.class));

    }

    @Test
    public void inviteStakeholderSuccess() throws Exception {

        long competitionId = 1L;
        Competition competition = CompetitionBuilder.newCompetition()
                .withId(competitionId)
                .build();

        when(userRepositoryMock.findByEmail(invitedUser.getEmail())).thenReturn(Optional.empty());
        when(stakeholderInviteRepositoryMock.findByEmail(invitedUser.getEmail())).thenReturn(Collections.emptyList());
        when(competitionRepositoryMock.findById(competitionId)).thenReturn(competition);

        ServiceResult<Void> result = service.inviteStakeholder(invitedUser, competitionId);
        assertTrue(result.isSuccess());
        verify(stakeholderInviteRepositoryMock).save(Mockito.any(StakeholderInvite.class));

        // Create a captor and verify that the correct and expected StakeholderInvite was saved
        ArgumentCaptor<StakeholderInvite> captor = ArgumentCaptor.forClass(StakeholderInvite.class);
        verify(stakeholderInviteRepositoryMock).save(captor.capture());
        StakeholderInvite savedStakeholderInvite = captor.getValue();
        assertEquals(competition, savedStakeholderInvite.getTarget());
        assertEquals("Rayon Kevin", savedStakeholderInvite.getName());
        assertEquals("Rayon.Kevin@gmail.com", savedStakeholderInvite.getEmail());
        assertNotNull(savedStakeholderInvite.getHash());
        assertEquals(CREATED, savedStakeholderInvite.getStatus());

    }
}

