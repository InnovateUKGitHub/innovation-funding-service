package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.security.ApplicationLookupStrategy;
import org.innovateuk.ifs.application.security.ApplicationPermissionRules;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.AcademicCost;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.security.*;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceRowService;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceRowServiceImpl;
import org.innovateuk.ifs.project.core.security.ProjectLookupStrategy;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceRowBuilder.newApplicationFinanceRow;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing how the secured methods in {@link ApplicationFinanceRowService} interact with Spring Security
 */
public class ApplicationFinanceRowServiceSecurityTest extends BaseServiceSecurityTest<ApplicationFinanceRowService> {

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
    public void update() {
        final Long costId = 1L;
        when(applicationFinanceRowLookupStrategy.getFinanceRow(costId)).thenReturn(newApplicationFinanceRow().with(id(costId)).build());
        assertAccessDenied(
                () -> classUnderTest.update(costId, new AcademicCost(1L)),
                () -> verify(costPermissionsRules)
                        .consortiumCanUpdateACostForTheirApplicationAndOrganisation(isA(ApplicationFinanceRow.class), isA(UserResource.class))
        );
    }

    @Test
    public void delete() {
        final Long costId = 1L;
        when(applicationFinanceRowLookupStrategy.getFinanceRow(costId)).thenReturn(newApplicationFinanceRow().with(id(costId)).build());
        assertAccessDenied(
                () -> classUnderTest.delete(costId),
                () -> verify(costPermissionsRules)
                        .consortiumCanDeleteACostForTheirApplicationAndOrganisation(isA(ApplicationFinanceRow.class), isA(UserResource.class))
        );
    }

    @Test
    public void testAddCostOnLongId() {
        final Long applicationFinanceId = 1L;
        final Long questionId = 2L;
        when(applicationFinanceLookupStrategy.getApplicationFinance(applicationFinanceId)).thenReturn(newApplicationFinanceResource().build());
        assertAccessDenied(
                () -> classUnderTest.create(applicationFinanceId, new AcademicCost(applicationFinanceId)),
                () -> {
                    verify(applicationFinanceRules)
                            .consortiumCanAddACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(isA(ApplicationFinanceResource.class), isA(UserResource.class));
                    verify(applicationFinanceRules)
                            .internalUserCanAddACostToApplicationFinance(isA(ApplicationFinanceResource.class), isA(UserResource.class));
                });
    }

    @Test
    public void testGetCostItem() {
        final Long costId = 1L;

        when(classUnderTestMock.get(costId)).thenReturn(serviceSuccess(new AcademicCost(1L)));

        assertAccessDenied(
                () -> classUnderTest.get(costId),
                () -> verify(costPermissionsRules)
                        .consortiumCanReadACostItemForTheirApplicationAndOrganisation(isA(FinanceRowItem.class), isA(UserResource.class))
        );
    }

    @Override
    protected Class<? extends ApplicationFinanceRowService> getClassUnderTest() {
        return ApplicationFinanceRowServiceImpl.class;
    }
}

