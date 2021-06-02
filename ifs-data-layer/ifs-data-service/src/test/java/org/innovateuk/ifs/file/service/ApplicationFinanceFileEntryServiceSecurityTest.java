package org.innovateuk.ifs.file.service;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.file.transactional.ApplicationFinanceFileEntryService;
import org.innovateuk.ifs.file.transactional.ApplicationFinanceFileEntryServiceImpl;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.security.ApplicationFinanceLookupStrategy;
import org.innovateuk.ifs.finance.security.ApplicationFinancePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing how the secured methods in FileEntryService interact with Spring Security
 */
public class ApplicationFinanceFileEntryServiceSecurityTest extends BaseServiceSecurityTest<ApplicationFinanceFileEntryService> {

    private ApplicationFinancePermissionRules applicationFinanceRules;
    private ApplicationFinanceLookupStrategy applicationFinanceLookupStrategy;

    @Before
    public void lookupPermissionRules() {
        applicationFinanceRules = getMockPermissionRulesBean(ApplicationFinancePermissionRules.class);
        applicationFinanceLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ApplicationFinanceLookupStrategy.class);
    }


    @Test
    public void testGetFileEntryByApplicationFinanceId() {
        final Long applicationFinanceId = 1L;
        when(applicationFinanceLookupStrategy.getApplicationFinance(applicationFinanceId)).thenReturn(newApplicationFinanceResource().build());
        assertAccessDenied(
                () -> classUnderTest.getFileEntryByApplicationFinanceId(applicationFinanceId),
                () -> {
                    verify(applicationFinanceRules).canViewApplication(isA(ApplicationFinanceResource.class), isA(UserResource.class));
                });
    }

    @Test
    public void testGetFECFileEntryByApplicationFinanceId() {
        final Long applicationFinanceId = 1L;
        when(applicationFinanceLookupStrategy.getApplicationFinance(applicationFinanceId)).thenReturn(newApplicationFinanceResource().build());
        assertAccessDenied(
                () -> classUnderTest.getFECCertificateFileEntryByApplicationFinanceId(applicationFinanceId),
                () -> {
                    verify(applicationFinanceRules).canViewApplication(isA(ApplicationFinanceResource.class), isA(UserResource.class));
                });
    }

    @Override
    protected Class<? extends ApplicationFinanceFileEntryService> getClassUnderTest() {
        return ApplicationFinanceFileEntryServiceImpl.class;
    }
}

