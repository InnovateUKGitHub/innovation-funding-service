package org.innovateuk.ifs.project.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckOverviewResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckSummaryResource;
import org.innovateuk.ifs.project.financechecks.service.FinanceCheckService;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static junit.framework.TestCase.fail;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckPartnerStatusResourceBuilder.FinanceCheckEligibilityResourceBuilder.newFinanceCheckEligibilityResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;

public class FinanceCheckServiceSecurityTest extends BaseServiceSecurityTest<FinanceCheckService> {

    private ProjectFinancePermissionRules projectFinancePermissionRules;

    @Before
    public void lookupPermissionRules() {

        projectFinancePermissionRules = getMockPermissionRulesBean(ProjectFinancePermissionRules.class);

    }

    @Test
    public void testGetFinanceCheckByProjectAndOrganisation() {
        assertRolesCanPerform(() -> classUnderTest.getByProjectAndOrganisation(new ProjectOrganisationCompositeId(1L, 2L)), PROJECT_FINANCE);
    }

    @Test
    public void testGetFinanceCheckSummary(){
        assertRolesCanPerform(() -> classUnderTest.getFinanceCheckSummary(1L), PROJECT_FINANCE);
    }

    @Test
    public void testGetFinanceCheckOverview() {
        assertAccessDenied(
                () -> classUnderTest.getFinanceCheckOverview(1L),
                () -> {
                    verify(projectFinancePermissionRules).partnersCanSeeTheProjectFinanceOverviewsForTheirProject(isA(Long.class), isA(UserResource.class));
                    verify(projectFinancePermissionRules).internalUsersCanSeeTheProjectFinanceOverviewsForAllProjects(isA(Long.class), isA(UserResource.class));
                }
        );
    }

    @Test
    public void getFinanceCheckEligibilityDetails(){
        assertAccessDenied(
                () -> classUnderTest.getFinanceCheckEligibilityDetails(1L, 2L),
                () -> {
                    verify(projectFinancePermissionRules).partnersCanSeeTheProjectFinancesForTheirOrganisation(isA(FinanceCheckEligibilityResource.class), isA(UserResource.class));
                    verify(projectFinancePermissionRules).internalUsersCanSeeTheProjectFinancesForTheirOrganisation(isA(FinanceCheckEligibilityResource.class), isA(UserResource.class));
                }
        );
    }

    private void assertRolesCanPerform(Runnable actionFn, UserRoleType... supportedRoles) {
        asList(UserRoleType.values()).forEach(role -> {
            RoleResource roleResource = newRoleResource().withType(role).build();
            UserResource userWithRole = newUserResource().withRolesGlobal(singletonList(roleResource)).build();
            setLoggedInUser(userWithRole);
            if (asList(supportedRoles).contains(role)) {
                actionFn.run();
            } else {
                try {
                    actionFn.run();
                    fail("Should have thrown an AccessDeniedException for any non " + supportedRoles + " users");
                } catch (AccessDeniedException e) {
                    // expected behaviour
                }
            }
        });
    }

    @Override
    protected Class<TestFinanceCheckService> getClassUnderTest() {
        return TestFinanceCheckService.class;
    }

    public static class TestFinanceCheckService implements FinanceCheckService {

        @Override
        public ServiceResult<FinanceCheckResource> getByProjectAndOrganisation(ProjectOrganisationCompositeId key) {
            return null;
        }

        @Override
        public ServiceResult<FinanceCheckSummaryResource> getFinanceCheckSummary(Long projectId) {
            return null;
        }

        @Override
        public ServiceResult<FinanceCheckOverviewResource> getFinanceCheckOverview(Long projectId) {
            return null;
        }

        @Override
        public ServiceResult<Boolean> isQueryActionRequired(Long projectId, Long organisationId) {
            return null;
        }

        @Override
        public ServiceResult<FinanceCheckEligibilityResource> getFinanceCheckEligibilityDetails(Long projectId, Long organisationId) { return serviceSuccess(newFinanceCheckEligibilityResource().build()); }

        @Override public ServiceResult<Long> getTurnoverByOrganisationId(final Long applicationId, Long organisationId) { return null; }

        @Override public ServiceResult<Long> getHeadCountByOrganisationId(final Long applicationId, Long organisationId) { return null; }

    }
}

