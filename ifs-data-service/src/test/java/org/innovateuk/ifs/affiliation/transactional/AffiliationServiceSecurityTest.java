package org.innovateuk.ifs.affiliation.transactional;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.AffiliationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.security.UserLookupStrategies;
import org.innovateuk.ifs.user.security.UserPermissionRules;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;


/**
 * Tests around the integration of this service and Spring Security
 */
public class AffiliationServiceSecurityTest extends BaseServiceSecurityTest<AffiliationService> {

    private UserPermissionRules rules;
    private UserLookupStrategies userLookupStrategies;

    private static final int ARRAY_SIZE_FOR_POST_FILTER_TESTS = 2;

    @Before
    public void lookupPermissionRules() {
        rules = getMockPermissionRulesBean(UserPermissionRules.class);
        userLookupStrategies = getMockPermissionEntityLookupStrategiesBean(UserLookupStrategies.class);
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
    protected Class<? extends AffiliationService> getClassUnderTest() {
        return org.innovateuk.ifs.affiliation.transactional.AffiliationServiceSecurityTest.TestAffiliationService.class;
    }

    public static class TestAffiliationService implements AffiliationService {

        @Override
        public ServiceResult<List<AffiliationResource>> getUserAffiliations(Long userId) {
            return serviceSuccess(newAffiliationResource().build(ARRAY_SIZE_FOR_POST_FILTER_TESTS));
        }

        @Override
        public ServiceResult<Void> updateUserAffiliations(long userId, List<AffiliationResource> userProfile) {
            return null;
        }
    }
}

