package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.security.ApplicationLookupStrategy;
import org.innovateuk.ifs.application.security.ApplicationPermissionRules;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResourceId;
import org.innovateuk.ifs.finance.resource.FinanceRowMetaFieldResource;
import org.innovateuk.ifs.finance.resource.cost.AcademicCost;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.security.*;
import org.innovateuk.ifs.finance.transactional.FinanceRowCostsService;
import org.innovateuk.ifs.finance.transactional.FinanceRowCostsServiceImpl;
import org.innovateuk.ifs.project.core.security.ProjectLookupStrategy;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.EnumSet.complementOf;
import static java.util.EnumSet.of;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceRowBuilder.newApplicationFinanceRow;
import static org.innovateuk.ifs.finance.builder.FinanceRowMetaFieldResourceBuilder.newFinanceRowMetaFieldResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

/**
 * Testing how the secured methods in {@link FinanceRowCostsService} interact with Spring Security
 */
public class FinanceRowCostsServiceSecurityTest extends BaseServiceSecurityTest<FinanceRowCostsService> {

    private static final int ARRAY_SIZE_FOR_POST_FILTER_TESTS = 2;
    private FinanceRowMetaFieldPermissionsRules financeRowMetaFieldPermissionsRules;
    private ApplicationFinanceRowPermissionRules costPermissionsRules;
    private ApplicationFinancePermissionRules applicationFinanceRules;
    private ApplicationPermissionRules applicationRules;
    private ApplicationLookupStrategy applicationLookupStrategy;
    private FinanceRowLookupStrategy financeRowLookupStrategy;
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
        financeRowLookupStrategy = getMockPermissionEntityLookupStrategiesBean(FinanceRowLookupStrategy.class);
        financeRowMetaFieldLookupStrategy = getMockPermissionEntityLookupStrategiesBean(FinanceRowMetaFieldLookupStrategy.class);
        applicationFinanceLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ApplicationFinanceLookupStrategy.class);

        projectLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ProjectLookupStrategy.class);
    }

    @Test
    public void testFindAllCostFields() {
        when(classUnderTestMock.findAllCostFields())
                .thenReturn(serviceSuccess(newFinanceRowMetaFieldResource().build(ARRAY_SIZE_FOR_POST_FILTER_TESTS)));

        classUnderTest.findAllCostFields();
        verify(financeRowMetaFieldPermissionsRules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS))
                .loggedInUsersCanReadCostFieldReferenceData(isA(FinanceRowMetaFieldResource.class), isA(UserResource.class));
        verifyNoMoreInteractions(financeRowMetaFieldPermissionsRules);
    }

    @Test
    public void testUpdateCost() {
        final Long costId = 1L;
        when(financeRowLookupStrategy.getFinanceRow(costId)).thenReturn(newApplicationFinanceRow().with(id(costId)).build());
        assertAccessDenied(
                () -> classUnderTest.updateCost(costId, new AcademicCost()),
                () -> verify(costPermissionsRules)
                        .consortiumCanUpdateACostForTheirApplicationAndOrganisation(isA(FinanceRow.class), isA(UserResource.class))
        );
    }

    @Test
    public void testGetCostField() {
        final Long costFieldId = 1L;
        when(financeRowMetaFieldLookupStrategy.getCostField(costFieldId)).thenReturn(newFinanceRowMetaFieldResource().with(id(costFieldId)).build());
        assertAccessDenied(
                () -> classUnderTest.getCostFieldById(costFieldId),
                () -> verify(financeRowMetaFieldPermissionsRules)
                        .loggedInUsersCanReadCostFieldReferenceData(isA(FinanceRowMetaFieldResource.class), isA(UserResource.class))
        );
    }

    @Test
    public void testDeleteCost() {
        final Long costId = 1L;
        when(financeRowLookupStrategy.getFinanceRow(costId)).thenReturn(newApplicationFinanceRow().with(id(costId)).build());
        assertAccessDenied(
                () -> classUnderTest.deleteCost(costId),
                () -> verify(costPermissionsRules)
                        .consortiumCanDeleteACostForTheirApplicationAndOrganisation(isA(FinanceRow.class), isA(UserResource.class))
        );
    }

    @Test
    public void testAddCostOnLongId() {
        final Long applicationFinanceId = 1L;
        final Long questionId = 2L;
        when(applicationFinanceLookupStrategy.getApplicationFinance(applicationFinanceId)).thenReturn(newApplicationFinanceResource().build());
        assertAccessDenied(
                () -> classUnderTest.addCost(applicationFinanceId, questionId, new AcademicCost()),
                () -> {
                    verify(applicationFinanceRules)
                            .consortiumCanAddACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(isA(ApplicationFinanceResource.class), isA(UserResource.class));
                    verify(applicationFinanceRules)
                            .supportCanAddACostToApplicationFinance(isA(ApplicationFinanceResource.class), isA(UserResource.class));
                    verify(applicationFinanceRules)
                            .innovationLeadCanAddACostToApplicationFinance(isA(ApplicationFinanceResource.class), isA(UserResource.class));
                });
    }

    @Test
    public void testAddCostOnApplicationFinanceResourceId() {
        final Long applicationId = 1L;
        final Long organisationId = 2L;
        final ApplicationFinanceResourceId applicationFinanceId = new ApplicationFinanceResourceId(applicationId, organisationId);
        when(applicationFinanceLookupStrategy.getApplicationFinance(applicationFinanceId)).thenReturn(newApplicationFinanceResource().build());
        assertAccessDenied(
                () -> classUnderTest.addCost(applicationFinanceId),
                () -> {
                    verify(applicationFinanceRules)
                            .consortiumCanAddACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(isA(ApplicationFinanceResource.class), isA(UserResource.class));
                    verify(applicationFinanceRules)
                            .supportCanAddACostToApplicationFinance(isA(ApplicationFinanceResource.class), isA(UserResource.class));
                    verify(applicationFinanceRules)
                            .innovationLeadCanAddACostToApplicationFinance(isA(ApplicationFinanceResource.class), isA(UserResource.class));
                });
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
    public void testGetCostItem() {
        final Long costId = 1L;

        when(classUnderTestMock.getCostItem(costId)).thenReturn(serviceSuccess(new AcademicCost()));

        assertAccessDenied(
                () -> classUnderTest.getCostItem(costId),
                () -> verify(costPermissionsRules)
                        .consortiumCanReadACostItemForTheirApplicationAndOrganisation(isA(FinanceRowItem.class), isA(UserResource.class))
        );
    }

    @Test
    public void testGetCosts() {
        final Long costId = 1L;
        final String costTypeName = "academic";
        final Long questionId = 2L;

        when(classUnderTestMock.getCosts(costId, costTypeName, questionId))
                .thenReturn(serviceSuccess(newApplicationFinanceRow().build(ARRAY_SIZE_FOR_POST_FILTER_TESTS)));

        classUnderTest.getCosts(costId, costTypeName, questionId);
        verify(costPermissionsRules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS))
                .consortiumCanReadACostForTheirApplicationAndOrganisation(isA(FinanceRow.class), isA(UserResource.class));
    }

    @Test
    public void testGetCostItems() {
        final Long costId = 1L;
        final String costTypeName = "academic";
        final Long questionId = 2L;

        when(classUnderTestMock.getCostItems(costId, costTypeName, questionId))
                .thenReturn(getCostItems());

        classUnderTest.getCostItems(costId, costTypeName, questionId);
        verify(costPermissionsRules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS))
                .consortiumCanReadACostItemForTheirApplicationAndOrganisation(isA(FinanceRowItem.class), isA(UserResource.class));
    }

    @Test
    public void testGetCostItems2() {
        final Long applicationFinanceId = 1L;
        final Long questionId = 2L;

        when(classUnderTestMock.getCostItems(applicationFinanceId, questionId))
                .thenReturn(getCostItems());

        classUnderTest.getCostItems(applicationFinanceId, questionId);
        verify(costPermissionsRules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS))
                .consortiumCanReadACostItemForTheirApplicationAndOrganisation(isA(FinanceRowItem.class), isA(UserResource.class));
    }

    @Test
    public void testAddProjectCostWithoutPersisting() {
        final long projectFinanceId = 1L;
        final long questionId = 2L;

        EnumSet<Role> nonProjectFinanceRoles = complementOf(of(PROJECT_FINANCE));
        nonProjectFinanceRoles.forEach(role -> {
            setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
            try {
                classUnderTest.addCostWithoutPersisting(projectFinanceId, questionId);
                Assert.fail("Should not have been able to add a project cost without the project finance role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    private ServiceResult<List<FinanceRowItem>> getCostItems() {
        final List<FinanceRowItem> items = new ArrayList<>();
        for (int i = 0; i < ARRAY_SIZE_FOR_POST_FILTER_TESTS; i++) {
            items.add(new AcademicCost());
        }
        return serviceSuccess(items);
    }

    @Override
    protected Class<? extends FinanceRowCostsService> getClassUnderTest() {
        return FinanceRowCostsServiceImpl.class;
    }
}

