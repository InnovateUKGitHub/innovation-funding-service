package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.security.ApplicationLookupStrategy;
import org.innovateuk.ifs.finance.security.FinanceTotalsPermissionRules;
import org.innovateuk.ifs.finance.totals.service.ApplicationFinanceTotalsSender;
import org.innovateuk.ifs.finance.totals.service.ApplicationFinanceTotalsSenderImpl;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ApplicationFinanceTotalsSenderSecurityTest extends
        BaseServiceSecurityTest<ApplicationFinanceTotalsSender> {
    private FinanceTotalsPermissionRules financeTotalsPermissionRules;
    private ApplicationLookupStrategy applicationLookupStrategy;


    @Before
    public void setUp() {
        financeTotalsPermissionRules = getMockPermissionRulesBean(FinanceTotalsPermissionRules.class);
        applicationLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ApplicationLookupStrategy.class);
    }

    @Override
    protected Class<? extends ApplicationFinanceTotalsSender> getClassUnderTest() {
        return ApplicationFinanceTotalsSenderImpl.class;
    }

    @Test
    public void testSendFinanceTotalsForApplication_canOnlyBeRunByRegistrationUser() {
        when(applicationLookupStrategy.getApplicationResource(1L)).thenReturn(newApplicationResource().build());
        assertAccessDenied(
                () -> classUnderTest.sendFinanceTotalsForApplication(1L),
                () -> verify(financeTotalsPermissionRules).leadApplicantAndInternalUsersCanUpdateTotalsForAnApplication(
                        isA(ApplicationResource.class),
                        isA(UserResource.class))
        );
    }
}