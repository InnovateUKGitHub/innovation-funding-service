package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.security.ApplicationLookupStrategy;
import org.innovateuk.ifs.application.security.ApplicationPermissionRules;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.security.*;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceService;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceServiceImpl;
import org.innovateuk.ifs.project.core.security.ProjectLookupStrategy;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ApplicationFinanceServiceSecurityTest extends BaseServiceSecurityTest<ApplicationFinanceService> {

    private static final int ARRAY_SIZE_FOR_POST_FILTER_TESTS = 2;

    private FinanceRowMetaFieldPermissionsRules financeRowMetaFieldPermissionsRules;
    private ApplicationFinanceRowPermissionRules costPermissionsRules;
    private ApplicationFinancePermissionRules applicationFinanceRules;
    private ApplicationPermissionRules applicationRules;
    private ApplicationLookupStrategy applicationLookupStrategy;
    private ApplicationFinanceRowLookupStrategy applicationFinanceRowLookupStrategy;
    private FinanceRowMetaFieldLookupStrategy financeRowMetaFieldLookupStrategy;
    private ApplicationFinanceLookupStrategy applicationFinanceLookupStrategy;

    private ProjectLookupStrategy projectLookupStrategy;

    @Before
    public void lookupPermissionRules() {

        financeRowMetaFieldPermissionsRules = getMockPermissionRulesBean(FinanceRowMetaFieldPermissionsRules.class);
        costPermissionsRules = getMockPermissionRulesBean(ApplicationFinanceRowPermissionRules.class);
        applicationFinanceRules = getMockPermissionRulesBean(ApplicationFinancePermissionRules.class);
        applicationRules = getMockPermissionRulesBean(ApplicationPermissionRules.class);
        applicationLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ApplicationLookupStrategy.class);
        applicationFinanceRowLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ApplicationFinanceRowLookupStrategy.class);
        financeRowMetaFieldLookupStrategy = getMockPermissionEntityLookupStrategiesBean(FinanceRowMetaFieldLookupStrategy.class);
        applicationFinanceLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ApplicationFinanceLookupStrategy.class);

        projectLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ProjectLookupStrategy.class);
    }

    @Test
    public void testUpdateCostOnApplicationFinanceId() {
        final Long applicationFinanceId = 1L;
        final ApplicationFinanceResource applicationFinanceResource = new ApplicationFinanceResource();
        when(applicationFinanceLookupStrategy.getApplicationFinance(applicationFinanceId)).thenReturn(newApplicationFinanceResource().build());
        assertAccessDenied(
                () -> classUnderTest.updateApplicationFinance(applicationFinanceId, applicationFinanceResource),
                () -> verify(applicationFinanceRules)
                        .consortiumCanUpdateACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(isA(ApplicationFinanceResource.class), isA(UserResource.class))
        );
    }

    @Test
    public void collaborativeFundingCriteriaMet() {
        final long applicationId = 1L;
        when(applicationLookupStrategy.getApplicationResource(applicationId)).thenReturn(newApplicationResource().withId(applicationId).build());

        assertAccessDenied(
                () -> classUnderTest.collaborativeFundingCriteriaMet(applicationId),
                () -> verify(applicationRules).consortiumCanCheckCollaborativeFundingCriteriaIsMet(isA(ApplicationResource.class), isA(UserResource.class)));
    }

    @Override
    protected Class<? extends ApplicationFinanceService> getClassUnderTest() {
        return ApplicationFinanceServiceImpl.class;
    }
}
