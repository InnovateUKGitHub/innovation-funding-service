package com.worth.ifs.user.transactional;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.resource.AffiliationResource;
import com.worth.ifs.user.resource.ProfileContractResource;
import com.worth.ifs.user.resource.ProfileSkillsResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.security.UserLookupStrategies;
import com.worth.ifs.user.security.UserPermissionRules;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static com.worth.ifs.user.builder.ProfileContractResourceBuilder.newProfileContractResource;
import static com.worth.ifs.user.builder.ProfileSkillsResourceBuilder.newProfileSkillsResource;
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

    @Test
    public void getProfileSkills() {
        Long userId = 1L;

        assertAccessDenied(
                () -> classUnderTest.getProfileSkills(userId),
                () -> {
                    verify(rules).usersCanViewTheirOwnProfileSkills(isA(ProfileSkillsResource.class), eq(getLoggedInUser()));
                    verifyNoMoreInteractions(rules);
                }
        );
    }

    @Test
    public void updateProfileSkills() {
        Long userId = 1L;
        ProfileSkillsResource profileSkillsResource = newProfileSkillsResource().build();

        UserResource user = newUserResource().build();
        when(userLookupStrategies.findById(userId)).thenReturn(user);

        assertAccessDenied(() -> classUnderTest.updateProfileSkills(userId, profileSkillsResource), () -> {
            verify(rules).usersCanUpdateTheirOwnProfiles(user, getLoggedInUser());
            verifyNoMoreInteractions(rules);
        });
    }

    @Test
    public void getProfileContract() {
        Long userId = 1L;

        assertAccessDenied(
                () -> classUnderTest.getProfileContract(userId),
                () -> {
                    verify(rules).usersCanViewTheirOwnProfileContract(isA(ProfileContractResource.class), isA(UserResource.class));
                    verifyNoMoreInteractions(rules);
                }
        );
    }

    @Test
    public void updateProfileContract() {
        Long userId = 1L;

        UserResource user = newUserResource().build();
        when(userLookupStrategies.findById(userId)).thenReturn(user);

        assertAccessDenied(() -> classUnderTest.updateProfileContract(userId), () -> {
            verify(rules).usersCanUpdateTheirOwnProfiles(user, getLoggedInUser());
            verifyNoMoreInteractions(rules);
        });
    }

    @Test
    public void updateDetails() {
        UserResource user = newUserResource().build();

        assertAccessDenied(() -> classUnderTest.updateDetails(user), () -> {
            verify(rules).usersCanUpdateTheirOwnProfiles(user, getLoggedInUser());
            verifyNoMoreInteractions(rules);
        });
    }

    @Test
    public void getUserAffiliations() {
        Long userId = 1L;

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
            verify(rules).usersCanUpdateTheirOwnProfiles(user, getLoggedInUser());
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
            return serviceSuccess(newProfileSkillsResource().build());
        }

        @Override
        public ServiceResult<Void> updateProfileSkills(Long userId, ProfileSkillsResource profileResource) {
            return null;
        }

        @Override
        public ServiceResult<ProfileContractResource> getProfileContract(Long userId) {
            return serviceSuccess(newProfileContractResource().build());
        }

        @Override
        public ServiceResult<Void> updateProfileContract(Long userId) {
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
    }
}
