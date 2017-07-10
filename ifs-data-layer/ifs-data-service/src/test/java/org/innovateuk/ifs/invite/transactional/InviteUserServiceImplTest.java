package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.RoleInvite;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.resource.AdminRoleType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InviteUserServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private InviteUserService inviteUserService = new InviteUserServiceImpl();

    private UserResource invitedUser = null;

    @Before
    public void setUp() {

        invitedUser = UserResourceBuilder.newUserResource()
                .withFirstName("Astle")
                .withLastName("Pimenta")
                .withEmail("Astle.Pimenta@innovateuk.gov.uk")
                .build();
    }

    @Test
    public void saveUserInviteWhenUserDetailsMissing() throws Exception {

        UserResource invitedUser = UserResourceBuilder.newUserResource().build();

        ServiceResult<Void> result = inviteUserService.saveUserInvite(invitedUser, AdminRoleType.SUPPORT);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(USER_ROLE_INVITE_INVALID));
    }

    @Test
    public void saveUserInviteWhenUserRoleIsNotSpecified() throws Exception {

        ServiceResult<Void> result = inviteUserService.saveUserInvite(invitedUser, null);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(USER_ROLE_INVITE_INVALID));
    }

    @Test
    public void saveUserInviteWhenEmailDomainIsIncorrect() throws Exception {

        AdminRoleType adminRoleType = AdminRoleType.SUPPORT;
        invitedUser.setEmail("Astle.Pimenta@gmail.com");

        ServiceResult<Void> result = inviteUserService.saveUserInvite(invitedUser, adminRoleType);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(USER_ROLE_INVITE_INVALID_EMAIL));
    }

    @Test
    public void saveUserInviteWhenUserAlreadyInvited() throws Exception {

        AdminRoleType adminRoleType = AdminRoleType.SUPPORT;

        Role role = new Role(1L, "support");
        RoleInvite roleInvite = new RoleInvite();

        when(roleRepositoryMock.findOneByName(adminRoleType.getName())).thenReturn(role);
        when(inviteRoleRepositoryMock.findByEmail(invitedUser.getEmail())).thenReturn(Collections.singletonList(roleInvite));

        ServiceResult<Void> result = inviteUserService.saveUserInvite(invitedUser, adminRoleType);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(USER_ROLE_INVITE_TARGET_USER_ALREADY_INVITED));

    }

    @Test
    public void saveUserInviteWhenUserRoleDoesNotExist() throws Exception {

        AdminRoleType adminRoleType = AdminRoleType.SUPPORT;

        when(roleRepositoryMock.findOneByName(adminRoleType.getName())).thenReturn(null);
        when(userRepositoryMock.findByEmail(invitedUser.getEmail())).thenReturn(Optional.empty());

        ServiceResult<Void> result = inviteUserService.saveUserInvite(invitedUser, adminRoleType);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(Role.class, adminRoleType.getName())));

    }

    @Test
    public void saveUserInviteSuccess() throws Exception {

        AdminRoleType adminRoleType = AdminRoleType.IFS_ADMINISTRATOR;

        Role role = new Role(1L, "support");

        when(roleRepositoryMock.findOneByName(adminRoleType.getName())).thenReturn(role);
        when(inviteRoleRepositoryMock.findByEmail(invitedUser.getEmail())).thenReturn(Collections.emptyList());
        when(userRepositoryMock.findByEmail(invitedUser.getEmail())).thenReturn(Optional.empty());

        ServiceResult<Void> result = inviteUserService.saveUserInvite(invitedUser, adminRoleType);
        assertTrue(result.isSuccess());
        verify(inviteRoleRepositoryMock).save(Mockito.any(RoleInvite.class));

    }

    @Test
    public void saveUserInviteWhenEmailAlreadyTaken() throws Exception {

        AdminRoleType adminRoleType = AdminRoleType.IFS_ADMINISTRATOR;

        Role role = new Role(1L, "support");

        when(roleRepositoryMock.findOneByName(adminRoleType.getName())).thenReturn(role);
        when(inviteRoleRepositoryMock.findByEmail(invitedUser.getEmail())).thenReturn(Collections.emptyList());
        when(userRepositoryMock.findByEmail(invitedUser.getEmail())).thenReturn(Optional.of(newUser().build()));

        ServiceResult<Void> result = inviteUserService.saveUserInvite(invitedUser, adminRoleType);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(USER_ROLE_INVITE_EMAIL_TAKEN));
        verify(inviteRoleRepositoryMock, never()).save(Mockito.any(RoleInvite.class));

    }
}
