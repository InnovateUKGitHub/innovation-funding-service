package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.RoleInvite;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantRoleResource;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.resource.AdminRoleType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import java.util.Collections;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.USER_ROLE_INVITE_INVALID;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.USER_ROLE_INVITE_TARGET_USER_ALREADY_INVITED;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InviteUserServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private InviteUserService inviteUserService = new InviteUserServiceImpl();

    private UserResource invitedUser = null;

    @Before
    public void setUp() {

        invitedUser = UserResourceBuilder.newUserResource()
                .withFirstName("A")
                .withLastName("D")
                .withEmail("A.D@gmail.com")
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
    public void saveUserInviteWhenUserRoleDoesNotExist() throws Exception {

        AdminRoleType adminRoleType = AdminRoleType.SUPPORT;

        when(roleRepositoryMock.findOneByName(adminRoleType.getName())).thenReturn(null);

        ServiceResult<Void> result = inviteUserService.saveUserInvite(invitedUser, adminRoleType);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(Role.class, adminRoleType.getName())));

    }

    @Test
    public void saveUserInviteWhenUserAlreadyInvited() throws Exception {

        AdminRoleType adminRoleType = AdminRoleType.SUPPORT;

        Role role = new Role(1L, "support");
        RoleInvite roleInvite = new RoleInvite();

        when(roleRepositoryMock.findOneByName(adminRoleType.getName())).thenReturn(role);
        when(inviteRoleRepositoryMock.findByRoleIdAndEmail(role.getId(), invitedUser.getEmail())).thenReturn(Collections.singletonList(roleInvite));

        ServiceResult<Void> result = inviteUserService.saveUserInvite(invitedUser, adminRoleType);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(USER_ROLE_INVITE_TARGET_USER_ALREADY_INVITED));

    }

    @Test
    public void saveUserInviteSuccess() throws Exception {

        AdminRoleType adminRoleType = AdminRoleType.IFS_ADMINISTRATOR;

        Role role = new Role(1L, "support");

        when(roleRepositoryMock.findOneByName(adminRoleType.getName())).thenReturn(role);
        when(inviteRoleRepositoryMock.findByRoleIdAndEmail(role.getId(), invitedUser.getEmail())).thenReturn(Collections.emptyList());

        ServiceResult<Void> result = inviteUserService.saveUserInvite(invitedUser, adminRoleType);
        assertTrue(result.isSuccess());
        verify(inviteRoleRepositoryMock).save(Mockito.any(RoleInvite.class));

    }
}
