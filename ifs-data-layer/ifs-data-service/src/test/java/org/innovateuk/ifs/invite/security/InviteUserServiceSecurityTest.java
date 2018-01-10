package org.innovateuk.ifs.invite.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.ExternalInviteResource;
import org.innovateuk.ifs.invite.resource.RoleInvitePageResource;
import org.innovateuk.ifs.invite.resource.RoleInviteResource;
import org.innovateuk.ifs.invite.transactional.InviteUserService;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.resource.SearchCategory;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

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
                    verifyNoMoreInteractions(inviteUserPermissionRules);
                });
    }

    @Test
    public void testFindPendingInternalUserInvites() {

        Pageable pageable = new PageRequest(0, 5);

        assertAccessDenied(
                () -> classUnderTest.findPendingInternalUserInvites(pageable),
                () -> {
                    verify(inviteUserPermissionRules).internalUsersCanViewPendingInternalUserInvites(any(RoleInvitePageResource.class), any(UserResource.class));
                    verifyNoMoreInteractions(inviteUserPermissionRules);
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
        public ServiceResult<RoleInvitePageResource> findPendingInternalUserInvites(Pageable pageable) {
            return serviceSuccess(new RoleInvitePageResource());
        }

        @Override
        public ServiceResult<List<ExternalInviteResource>> findExternalInvites(String searchString, SearchCategory searchCategory) {
            return null;
        }
    }
}
