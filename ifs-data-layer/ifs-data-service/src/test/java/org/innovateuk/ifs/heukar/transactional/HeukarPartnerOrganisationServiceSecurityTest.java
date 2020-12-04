package org.innovateuk.ifs.heukar.transactional;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.security.ApplicationLookupStrategy;
import org.innovateuk.ifs.application.security.ApplicationPermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HeukarPartnerOrganisationServiceSecurityTest extends BaseServiceSecurityTest<HeukarPartnerOrganisationService> {
    private ApplicationPermissionRules applicationRules;
    private ApplicationLookupStrategy applicationLookupStrategy;

    @Before
    public void lookupPermissionRules() {
        applicationRules = getMockPermissionRulesBean(ApplicationPermissionRules.class);
        applicationLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ApplicationLookupStrategy.class);
    }

    @Test
    public void findByApplicationId() {
        final long applicationId = 1L;
        when(applicationLookupStrategy.getApplicationResource(applicationId)).thenReturn(newApplicationResource().build());
        assertAccessDenied(
                () -> classUnderTest.findByApplicationId(applicationId),
                () -> verify(applicationRules).canAddHeukarPartnerOrganisation(isA(ApplicationResource.class), isA(UserResource.class))
        );
    }

    @Override
    protected Class<? extends HeukarPartnerOrganisationService> getClassUnderTest() {
        return HeukarPartnerOrganisationServiceImpl.class;
    }
}