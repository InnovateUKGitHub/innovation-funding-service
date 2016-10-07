package com.worth.ifs.user.transactional;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.builder.UserResourceBuilder;
import com.worth.ifs.user.resource.ProfileResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.security.UserLookupStrategies;
import com.worth.ifs.user.security.UserPermissionRules;
import org.junit.Before;
import org.junit.Test;

import static com.worth.ifs.user.builder.ProfileResourceBuilder.newProfileResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests around the integration of this service and Spring Security
 */
public class UserProfileServiceSecurityTest extends BaseServiceSecurityTest<UserProfileService> {

    private UserPermissionRules rules;
    private UserLookupStrategies userLookupStrategies;

    @Before
    public void lookupPermissionRules() {
        rules = getMockPermissionRulesBean(UserPermissionRules.class);
        userLookupStrategies = getMockPermissionEntityLookupStrategiesBean(UserLookupStrategies.class);
    }

    @Test
    public void testUpdateProfile() {
        when(userLookupStrategies.findById(1L)).thenReturn(newUserResource().build());

        assertAccessDenied(() -> classUnderTest.updateProfile(1L, newProfileResource().build()), () -> {
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
        public ServiceResult<Void> updateProfile(Long userId, ProfileResource profileResource) {
            return null;
        }

        @Override
        public ServiceResult<Void> updateDetails(UserResource userResource) {
            return null;
        }
    }
}
