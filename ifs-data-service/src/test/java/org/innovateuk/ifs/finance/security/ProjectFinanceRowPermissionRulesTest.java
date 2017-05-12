package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.handler.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceRowService;
import org.innovateuk.ifs.project.security.ProjectFinancePermissionRules;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.method.P;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static junit.framework.TestCase.fail;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProjectFinanceRowPermissionRulesTest extends BaseServiceSecurityTest<ProjectFinanceRowService> {

    private ProjectFinancePermissionRules projectFinancePermissionRules;
    private ProjectFinanceLookupStrategy projectFinanceLookupStrategy;

    @Before
    public void lookupPermissionRules() {

        projectFinancePermissionRules = getMockPermissionRulesBean(ProjectFinancePermissionRules.class);
        projectFinanceLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ProjectFinanceLookupStrategy.class);

    }

    @Test
    public void testAddCostWithoutPersisting(){
        when(projectFinanceLookupStrategy.getProjectFinance(1L)).thenReturn(newProjectFinanceResource().build());
        assertAccessDenied(
                () -> classUnderTest.addCostWithoutPersisting(1L, 2L),
                () -> {
                    verify(projectFinancePermissionRules).partnersCanAddEmptyRowWhenReadingProjectCosts(isA(ProjectFinanceResource.class), isA(UserResource.class));
                    verify(projectFinancePermissionRules).internalUsersCanAddEmptyRowWhenReadingProjectCosts(isA(ProjectFinanceResource.class), isA(UserResource.class));
                }
        );
    }

    @Test
    public void testFinanceChecksDetails(){
        assertAccessDenied(
                () -> classUnderTest.financeChecksDetails(1L, 2L),
                () -> {
                    verify(projectFinancePermissionRules).internalUserCanSeeProjectFinancesForOrganisations(isA(ProjectFinanceResource.class), isA(UserResource.class));
                    verify(projectFinancePermissionRules).partnersCanSeeTheProjectFinancesForTheirOrganisation(isA(ProjectFinanceResource.class), isA(UserResource.class));
                }
        );
    }

    /**
     * Comp admin is allowed access to update project inance details costs for same reason as above financeChecksDetails
     * method.
     */
    @Test
    public void testAllInternalUsersCanUpdateFinanceCosts(){
        asList(UserRoleType.values()).forEach(role -> {
            RoleResource roleResource = newRoleResource().withType(role).build();
            UserResource userWithRole = newUserResource().withRolesGlobal(singletonList(roleResource)).build();
            setLoggedInUser(userWithRole);
            if (PROJECT_FINANCE.equals(role) || UserRoleType.COMP_ADMIN.equals(role)) {
                classUnderTest.updateCost(1L, new ProjectFinanceResource());
            } else {
                try{
                    classUnderTest.updateCost(1L, new ProjectFinanceResource());
                    fail("Should have thrown an AccessDeniedException for any non-Finance Team members");
                } catch (AccessDeniedException e) {
                    // expected behaviour
                }
            }
        });
    }

    @Override
    protected Class<TestProjectFinanceRowService> getClassUnderTest() {
        return TestProjectFinanceRowService.class;
    }

    public static class TestProjectFinanceRowService implements ProjectFinanceRowService {

        @Override
        public ServiceResult<List<? extends FinanceRow>> getCosts(Long projectFinanceId, String costTypeName, Long questionId) {
            return null;
        }

        @Override
        public ServiceResult<FinanceRowItem> getCostItem(Long costItemId) {
            return null;
        }

        @Override
        public ServiceResult<List<FinanceRowItem>> getCostItems(Long projectFinanceId, String costTypeName, Long questionId) {
            return null;
        }

        @Override
        public ServiceResult<List<FinanceRowItem>> getCostItems(Long projectFinanceId, Long questionId) {
            return null;
        }

        @Override
        public ServiceResult<FinanceRowItem> addCost(@P("projectFinanceId") Long projectFinanceId, Long questionId, FinanceRowItem newCostItem) {
            return null;
        }

        @Override
        public ServiceResult<FinanceRowItem> updateCost(@P("costId") Long costId, FinanceRowItem newCostItem) {
            return null;
        }

        @Override
        public ServiceResult<FinanceRowItem> addCostWithoutPersisting(@P("projectFinanceId") Long projectFinanceId, Long questionId) {
            return null;
        }

        @Override
        public ServiceResult<Void> deleteCost(@P("projectId") Long projectId, @P("organisationId") Long organisationId, @P("costId") Long costId) {
            return null;
        }

        @Override
        public ServiceResult<ProjectFinanceResource> updateCost(@P("projectFinanceId") Long projectFinanceId, ProjectFinanceResource applicationFinance) {
            return null;
        }

        @Override
        public ServiceResult<ProjectFinanceResource> financeChecksDetails(Long projectId, Long organisationId) {
            return serviceSuccess(newProjectFinanceResource().build());
        }

        @Override
        public ServiceResult<List<ProjectFinanceResource>> financeChecksTotals(Long projectId) {
            return null;
        }

        @Override
        public FinanceRowHandler getCostHandler(FinanceRowItem costItemId) {
            return null;
        }
    }
}
