package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.ApplicationKtaInvite;
import org.innovateuk.ifs.invite.repository.ApplicationKtaInviteRepository;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.ApplicationKtaInviteBuilder.newApplicationKtaInvite;
import static org.innovateuk.ifs.invite.builder.ApplicationKtaInviteResourceBuilder.newApplicationKtaInviteResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

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
        given(applicationKtaInviteRepository.findByApplicationId(invite.getApplication())).willReturn(Optional.of(otherInvite));

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
        given(applicationKtaInviteRepository.findByApplicationId(invite.getApplication())).willReturn(Optional.empty());

        UserResource user = newUserResource().withRolesGlobal(singletonList(Role.APPLICANT)).build();
        given(userService.findByEmail("testemail@example.com")).willReturn(serviceSuccess(user));

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
        given(applicationKtaInviteRepository.findByApplicationId(invite.getApplication())).willReturn(Optional.empty());

        UserResource user = newUserResource().withRolesGlobal(singletonList(Role.KNOWLEDGE_TRANSFER_ADVISOR)).build();
        given(userService.findByEmail("testemail@example.com")).willReturn(serviceSuccess(user));

        given(applicationInviteNotificationService.inviteKta(any())).willAnswer(invocation -> serviceSuccess());

        // when
        ServiceResult<Void> result = inviteKtaService.saveKtaInvite(invite);

        // then
        assertTrue(result.isSuccess());
    }
}
