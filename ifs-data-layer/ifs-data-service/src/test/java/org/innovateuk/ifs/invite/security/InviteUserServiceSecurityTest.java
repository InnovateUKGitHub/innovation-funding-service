package org.innovateuk.ifs.invite.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.invite.resource.RoleInvitePageResource;
import org.innovateuk.ifs.invite.transactional.InviteUserService;
import org.innovateuk.ifs.invite.transactional.InviteUserServiceImpl;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class InviteUserServiceSecurityTest extends BaseServiceSecurityTest<InviteUserService> {

    private InviteUserPermissionRules inviteUserPermissionRules;

    @Before
    public void lookupPermissionRules() {
        inviteUserPermissionRules = getMockPermissionRulesBean(InviteUserPermissionRules.class);
    }

    @Test
    public void saveUserInvite() {
        UserResource invitedUser = UserResourceBuilder.newUserResource().build();

        assertAccessDenied(
                () -> classUnderTest.saveUserInvite(invitedUser, SUPPORT, ""),
                () -> {
                    verify(inviteUserPermissionRules)
                            .ifsAdminCanSaveNewUserInvite(any(UserResource.class), any(UserResource.class));
                    verifyNoMoreInteractions(inviteUserPermissionRules);
                });
    }

    @Test
    public void findPendingInternalUserInvites() {
        Pageable pageable = PageRequest.of(0, 5);

        when(classUnderTestMock.findPendingInternalUserInvites("", pageable))
                .thenReturn(serviceSuccess(new RoleInvitePageResource()));

        assertAccessDenied(
                () -> classUnderTest.findPendingInternalUserInvites("", pageable),
                () -> {
                    verify(inviteUserPermissionRules)
                            .internalUsersCanViewPendingInternalUserInvites(any(RoleInvitePageResource.class), any
                                    (UserResource.class));
                    verifyNoMoreInteractions(inviteUserPermissionRules);
                });
    }

    @Test
    public void resendPendingInternalUserInvites() {
        assertRolesCanPerform(() -> classUnderTest.resendInvite(123L),
                IFS_ADMINISTRATOR, SUPER_ADMIN_USER);
    }

    @Override
    protected Class<? extends InviteUserService> getClassUnderTest() {
        return InviteUserServiceImpl.class;
    }
}
