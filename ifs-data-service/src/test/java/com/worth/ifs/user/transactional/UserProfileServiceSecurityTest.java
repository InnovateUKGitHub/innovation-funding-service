package com.worth.ifs.user.transactional;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.resource.AffiliationResource;
import com.worth.ifs.user.resource.ContractResource;
import com.worth.ifs.user.resource.ProfileSkillsResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.security.UserLookupStrategies;
import com.worth.ifs.user.security.UserPermissionRules;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

/**
 * Tests around the integration of this service and Spring Security
 */
public class UserProfileServiceSecurityTest extends BaseServiceSecurityTest<UserProfileService> {

    private UserPermissionRules rules;
    private UserLookupStrategies userLookupStrategies;

    private static int ARRAY_SIZE_FOR_POST_FILTER_TESTS = 2;

    @Before
    public void lookupPermissionRules() {
        rules = getMockPermissionRulesBean(UserPermissionRules.class);
        userLookupStrategies = getMockPermissionEntityLookupStrategiesBean(UserLookupStrategies.class);
    }

/*    @Test
    public void updateProfile() {
        when(userLookupStrategies.findById(1L)).thenReturn(newUserResource().build());

        assertAccessDenied(() -> classUnderTest.updateProfile(1L, newProfileResource().build()), () -> {
            verify(rules).usersCanUpdateTheirOwnProfiles(isA(UserResource.class), isA(UserResource.class));
            verifyNoMoreInteractions(rules);
        });
    }*/

    @Test
    public void getUserAffiliations() {
        long userId = 1L;

        classUnderTest.getUserAffiliations(userId);
        verify(rules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS)).usersCanViewTheirOwnAffiliations(isA(AffiliationResource.class), eq(getLoggedInUser()));
        verifyNoMoreInteractions(rules);
    }

    @Test
    public void updateUserAffiliations() {
        Long userId = 1L;
        List<AffiliationResource> affiliations = newAffiliationResource().build(2);

        UserResource user = newUserResource().build();
        when(userLookupStrategies.findById(userId)).thenReturn(user);

        assertAccessDenied(() -> classUnderTest.updateUserAffiliations(userId, affiliations), () -> {
            verify(rules).usersCanUpdateTheirOwnAffiliations(user, getLoggedInUser());
            verifyNoMoreInteractions(rules);
        });
    }

    @Test
    public void updateUserContract() {
        Long userId = 1L;
        ProfileResource profileResource = newProfileResource().build();

        UserResource user = newUserResource().build();
        when(userLookupStrategies.findById(userId)).thenReturn(user);

        assertAccessDenied(() -> classUnderTest.updateUserContract(userId, profileResource), () -> {
            verify(rules).usersCanUpdateTheirSignedContract(user, getLoggedInUser());
            verifyNoMoreInteractions(rules);
        });
    }

    @Override
    protected Class<? extends UserProfileService> getClassUnderTest() {
        return TestUserProfileService.class;
    }

    public static class TestUserProfileService implements UserProfileService {

        @Override
        public ServiceResult<ProfileSkillsResource> getProfileSkills(Long userId) {
            return null;
        }

        @Override
        public ServiceResult<Void> updateProfileSkills(Long userId, ProfileSkillsResource profileResource) {
            return null;
        }

        @Override
        public ServiceResult<Void> updateDetails(UserResource userResource) {
            return null;
        }

        @Override
        public ServiceResult<List<AffiliationResource>> getUserAffiliations(Long userId) {
            return serviceSuccess(newAffiliationResource().build(ARRAY_SIZE_FOR_POST_FILTER_TESTS));
        }

        @Override
        public ServiceResult<Void> updateUserAffiliations(Long userId, List<AffiliationResource> userProfile) {
            return null;
        }

        @Override
        public ServiceResult<Void> updateUserContract(Long userId, ProfileResource profileResource) {
            return null;
        }
    }
}
