package org.innovateuk.ifs.procurement.milestone.service;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.security.ApplicationLookupStrategy;
import org.innovateuk.ifs.application.security.ApplicationPermissionRules;
import org.innovateuk.ifs.procurement.milestone.transactional.ApplicationProcurementMilestoneService;
import org.innovateuk.ifs.procurement.milestone.transactional.ApplicationProcurementMilestoneServiceImpl;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

public class ApplicationProcurementMilestoneServiceSecurityTest extends BaseServiceSecurityTest<ApplicationProcurementMilestoneService> {

    private ApplicationPermissionRules applicationRules;
    private ApplicationLookupStrategy applicationLookupStrategy;

    @Before
    public void lookupPermissionRules() {
        applicationRules = getMockPermissionRulesBean(ApplicationPermissionRules.class);
        applicationLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ApplicationLookupStrategy.class);
    }

    @Test
    public void findMaxMilestoneMonth() {
        final Long applicationId = 1L;
        ApplicationResource applicationResource = new ApplicationResource();
        when(applicationLookupStrategy.getApplicationResource(applicationId)).thenReturn(applicationResource);
        assertAccessDenied(
                () -> classUnderTest.findMaxMilestoneMonth(applicationId),
                () -> {
                    verify(applicationRules).canViewApplication(ArgumentMatchers.isA(ApplicationResource.class), isA(UserResource.class));
                    verifyNoMoreInteractions(applicationRules);
                }
        );
    }

    @Override
    protected Class<? extends ApplicationProcurementMilestoneService> getClassUnderTest() {
        return ApplicationProcurementMilestoneServiceImpl.class;
    }
}
