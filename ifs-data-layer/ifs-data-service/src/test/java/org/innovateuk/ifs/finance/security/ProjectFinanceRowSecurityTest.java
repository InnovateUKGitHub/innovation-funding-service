package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.handler.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceRowService;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceRowServiceImpl;
import org.innovateuk.ifs.project.security.ProjectFinancePermissionRules;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
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
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProjectFinanceRowSecurityTest extends BaseServiceSecurityTest<ProjectFinanceRowService> {

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
                    verify(projectFinancePermissionRules)
                            .partnersCanAddEmptyRowWhenReadingProjectCosts(isA(ProjectFinanceResource.class), isA(UserResource.class));
                    verify(projectFinancePermissionRules)
                            .internalUsersCanAddEmptyRowWhenReadingProjectCosts(isA(ProjectFinanceResource.class), isA(UserResource.class));
                }
        );
    }

    @Test
    public void testFinanceChecksDetails(){
        when(classUnderTestMock.financeChecksDetails(1L, 2L))
                .thenReturn(serviceSuccess(newProjectFinanceResource().build()));

        assertAccessDenied(
                () -> classUnderTest.financeChecksDetails(1L, 2L),
                () -> {
                    verify(projectFinancePermissionRules)
                            .internalUserCanSeeProjectFinancesForOrganisations(isA(ProjectFinanceResource.class), isA(UserResource.class));
                    verify(projectFinancePermissionRules)
                            .partnersCanSeeTheProjectFinancesForTheirOrganisation(isA(ProjectFinanceResource.class), isA(UserResource.class));
                }
        );
    }

    /**
     * Comp admin is allowed access to update project inance details costs for same reason as above financeChecksDetails
     * method.
     */
    @Test
    public void testAllInternalUsersCanUpdateFinanceCosts(){
        asList(Role.values()).forEach(role -> {
            UserResource userWithRole = newUserResource().withRolesGlobal(singletonList(role)).build();
            setLoggedInUser(userWithRole);
            if (role == PROJECT_FINANCE || role == COMP_ADMIN) {
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
    protected Class<? extends ProjectFinanceRowService> getClassUnderTest() {
        return ProjectFinanceRowServiceImpl.class;
    }
}
