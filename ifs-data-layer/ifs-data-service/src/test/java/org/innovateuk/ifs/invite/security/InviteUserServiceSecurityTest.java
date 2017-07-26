package org.innovateuk.ifs.invite.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.RoleInviteResource;
import org.innovateuk.ifs.invite.transactional.InviteUserService;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

public class InviteUserServiceSecurityTest extends BaseServiceSecurityTest<InviteUserService> {

    private InviteUserPermissionRules inviteUserPermissionRules;


    @Before
    public void lookupPermissionRules() {
        inviteUserPermissionRules = getMockPermissionRulesBean(InviteUserPermissionRules.class);

    }

    @Test
    public void testSaveUserInvite() {

        UserResource invitedUser = UserResourceBuilder.newUserResource().build();

        assertAccessDenied(
                () -> classUnderTest.saveUserInvite(invitedUser, UserRoleType.SUPPORT),
                () -> {
                    verify(inviteUserPermissionRules).ifsAdminCanSaveNewUserInvite(any(UserResource.class), any(UserResource.class));
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
    }
}
