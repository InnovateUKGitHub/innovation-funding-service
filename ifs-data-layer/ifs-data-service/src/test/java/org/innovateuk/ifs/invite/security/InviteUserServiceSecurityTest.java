package org.innovateuk.ifs.invite.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.RoleInviteResource;
import org.innovateuk.ifs.invite.transactional.InviteUserService;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.resource.UserPageResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.security.UserPermissionRules;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class InviteUserServiceSecurityTest extends BaseServiceSecurityTest<InviteUserService> {

    private InviteUserPermissionRules inviteUserPermissionRules;
    private UserPermissionRules userPermissionRules;


    @Before
    public void lookupPermissionRules() {
        inviteUserPermissionRules = getMockPermissionRulesBean(InviteUserPermissionRules.class);
        userPermissionRules = getMockPermissionRulesBean(UserPermissionRules.class);

    }

    @Test
    public void testSaveUserInvite() {

        UserResource invitedUser = UserResourceBuilder.newUserResource().build();

        assertAccessDenied(
                () -> classUnderTest.saveUserInvite(invitedUser, UserRoleType.SUPPORT),
                () -> {
                    verify(inviteUserPermissionRules).ifsAdminCanSaveNewUserInvite(any(UserResource.class), any(UserResource.class));
                    verifyNoMoreInteractions(inviteUserPermissionRules);
                });
    }

    @Test
    public void testFindPendingInternalUsers() {

        Pageable pageable = new PageRequest(0, 5);

        assertAccessDenied(
                () -> classUnderTest.findPendingInternalUsers(pageable),
                () -> {
                    verify(userPermissionRules).internalUsersCanViewEveryone(any(UserPageResource.class), any(UserResource.class));
                    verifyNoMoreInteractions(userPermissionRules);
                });
    }

    @Override
    protected Class<? extends InviteUserService> getClassUnderTest() {
        return InviteUserServiceSecurityTest.TestInviteUserService.class;
    }

    public static class TestInviteUserService implements InviteUserService {

        @Override
        public ServiceResult<Void> saveUserInvite(UserResource invitedUser, UserRoleType adminRoleType) {
            return null;
        }

        @Override
        public ServiceResult<RoleInviteResource> getInvite(String inviteHash) {
            return null;
        }

        @Override
        public ServiceResult<Boolean> checkExistingUser(String inviteHash) {
            return null;
        }

        @Override
        public ServiceResult<UserPageResource> findPendingInternalUsers(Pageable pageable) {
            return serviceSuccess(new UserPageResource());
        }
    }
}
