package org.innovateuk.ifs.affiliation.transactional;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.user.resource.AffiliationListResource;
import org.innovateuk.ifs.user.resource.AffiliationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.security.UserLookupStrategies;
import org.innovateuk.ifs.user.security.UserPermissionRules;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.AffiliationListResourceBuilder.newAffiliationListResource;
import static org.innovateuk.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Mockito.*;


/**
 * Tests around the integration of this service and Spring Security
 */
public class AffiliationServiceSecurityTest extends BaseServiceSecurityTest<AffiliationService> {

    private UserPermissionRules userPermissionRules;
    private UserLookupStrategies userLookupStrategies;

    private static final int ARRAY_SIZE_FOR_POST_FILTER_TESTS = 2;

    @Before
    public void lookupPermissionRules() {
        userPermissionRules = getMockPermissionRulesBean(UserPermissionRules.class);
        userLookupStrategies = getMockPermissionEntityLookupStrategiesBean(UserLookupStrategies.class);
    }

    @Test
    public void getUserAffiliations() {
        Long userId = 1L;

        AffiliationListResource affiliationListResource = newAffiliationListResource()
                .withAffiliationList(
                        newAffiliationResource()
                                .build(ARRAY_SIZE_FOR_POST_FILTER_TESTS)
                )
                .build();

        when(classUnderTestMock.getUserAffiliations(userId))
                .thenReturn(serviceSuccess(affiliationListResource));

        classUnderTest.getUserAffiliations(userId);
        verifyNoMoreInteractions(userPermissionRules);

    }

    @Test
    public void updateUserAffiliations() {
        Long userId = 1L;
        List<AffiliationResource> affiliations = newAffiliationResource().build(2);
        AffiliationListResource affiliationListResource = newAffiliationListResource()
                .withAffiliationList(affiliations)
                .build();
        UserResource user = newUserResource().build();
        when(userLookupStrategies.findById(userId)).thenReturn(user);

        assertAccessDenied(() -> classUnderTest.updateUserAffiliations(userId, affiliationListResource), () -> {
            verify(userPermissionRules).usersCanUpdateTheirOwnProfiles(user, getLoggedInUser());
            verify(userPermissionRules).adminsCanUpdateUserDetails(user, getLoggedInUser());
            verifyNoMoreInteractions(userPermissionRules);
        });
    }

    @Override
    protected Class<? extends AffiliationService> getClassUnderTest() {
        return AffiliationServiceImpl.class;
    }
}

