package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.ApplicationKtaInvite;
import org.innovateuk.ifs.invite.mapper.ApplicationKtaInviteMapper;
import org.innovateuk.ifs.invite.repository.ApplicationKtaInviteRepository;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.ApplicationKtaInviteBuilder.newApplicationKtaInvite;
import static org.innovateuk.ifs.invite.builder.ApplicationKtaInviteResourceBuilder.newApplicationKtaInviteResource;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationKtaInviteServiceImplTest {

    @Mock
    private ApplicationKtaInviteRepository applicationKtaInviteRepository;

    @Mock
    private ApplicationInviteNotificationService applicationInviteNotificationService;

    @Mock
    private UserService userService;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ApplicationKtaInviteMapper applicationKtaInviteMapper;

    @Mock
    private ProcessRoleRepository processRoleRepository;

    @InjectMocks
    private ApplicationKtaInviteServiceImpl inviteKtaService;

    @Test
    public void saveKtaInviteGivenInvitePresentAlready() {
        // given
        ApplicationKtaInviteResource invite = newApplicationKtaInviteResource()
                .withEmail("testemail@example.com")
                .withApplication(123L).build();

        ApplicationKtaInvite otherInvite = newApplicationKtaInvite()
                .withEmail("testemail2@example.com")
                .build();
        when(applicationKtaInviteRepository.findByApplicationId(invite.getApplication())).thenReturn(Optional.of(otherInvite));

        // when
        ServiceResult<Void> result = inviteKtaService.saveKtaInvite(invite);

        // then
        assertTrue(result.isFailure());
        assertEquals(1, result.getFailure().getErrors().size());
        assertEquals("kta.already.invited", result.getFailure().getErrors().get(0).getErrorKey());
    }

    @Test
    public void saveKtaInviteForNonKtaUser() {
        // given
        ApplicationKtaInviteResource invite = newApplicationKtaInviteResource()
                .withEmail("testemail@example.com")
                .withApplication(123L).build();
        when(applicationKtaInviteRepository.findByApplicationId(invite.getApplication())).thenReturn(Optional.empty());

        UserResource user = newUserResource().withRolesGlobal(singletonList(Role.APPLICANT)).build();
        when(userService.findByEmail("testemail@example.com")).thenReturn(serviceSuccess(user));

        // when
        ServiceResult<Void> result = inviteKtaService.saveKtaInvite(invite);

        // then
        assertTrue(result.isFailure());
        assertEquals(1, result.getFailure().getErrors().size());
        assertEquals("user.not.registered.kta", result.getFailure().getErrors().get(0).getErrorKey());
    }

    @Test
    public void saveKtaInvite() {
        // given
        ApplicationKtaInviteResource invite = newApplicationKtaInviteResource()
                .withEmail("testemail@example.com")
                .withApplication(123L).build();
        when(applicationKtaInviteRepository.findByApplicationId(invite.getApplication())).thenReturn(Optional.empty());

        UserResource user = newUserResource().withRolesGlobal(singletonList(Role.KNOWLEDGE_TRANSFER_ADVISER)).build();
        when(userService.findByEmail("testemail@example.com")).thenReturn(serviceSuccess(user));

        when(applicationInviteNotificationService.inviteKta(any())).thenAnswer(invocation -> serviceSuccess());

        // when
        ServiceResult<Void> result = inviteKtaService.saveKtaInvite(invite);

        // then
        assertTrue(result.isSuccess());
    }

    @Test
    public void getKtaInviteByHash() {
        // given
        String hash = "hash";
        ApplicationKtaInviteResource inviteResource = newApplicationKtaInviteResource().build();
        ApplicationKtaInvite invite = new ApplicationKtaInvite();
        when(applicationKtaInviteRepository.getByHash(hash)).thenReturn(invite);
        when(applicationKtaInviteMapper.mapToResource(invite)).thenReturn(inviteResource);


        // when
        ServiceResult<ApplicationKtaInviteResource> result = inviteKtaService.getKtaInviteByHash(hash);

        // then
        assertTrue(result.isSuccess());
        assertEquals(result.getSuccess(), inviteResource);
    }

    @Test
    public void acceptInvite() {
        // given
        String hash = "hash";
        ApplicationKtaInvite invite = mock(ApplicationKtaInvite.class);
        when(applicationKtaInviteRepository.getByHash(hash)).thenReturn(invite);
        when(invite.getTarget()).thenReturn(newApplication().build());
        when(invite.getUser()).thenReturn(newUser().build());

        // when
        ServiceResult<Void> result = inviteKtaService.acceptInvite(hash);

        // then
        assertTrue(result.isSuccess());

        verify(invite).open();
        verify(processRoleRepository).save(any(ProcessRole.class));
        verify(applicationKtaInviteRepository).save(invite);
    }
}
