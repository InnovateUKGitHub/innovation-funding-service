package com.worth.ifs.user.transactional;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.security.UserPermissionRules;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.method.P;

import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Tests around the integration of this service and Spring Security
 */
public class UserProfileServiceSecurityTest extends BaseServiceSecurityTest<UserProfileService> {

    private UserPermissionRules rules;

    @Before
    public void lookupPermissionRules() {
        rules = getMockPermissionRulesBean(UserPermissionRules.class);
    }

    @Test
    public void testUpdateProfile() {
        assertAccessDenied(() -> classUnderTest.updateProfile(newUserResource().build()), () -> {
            verify(rules).usersCanUpdateTheirOwnProfiles(isA(UserResource.class), isA(UserResource.class));
            verifyNoMoreInteractions(rules);
        });
    }

    @Override
    protected Class<? extends UserProfileService> getClassUnderTest() {
        return TestUserProfileService.class;
    }

    public static class TestUserProfileService implements UserProfileService {

        @Override
        public ServiceResult<Void> updateProfile(@P("userBeingUpdated") UserResource userBeingUpdated) {
            return null;
        }
    }
}
