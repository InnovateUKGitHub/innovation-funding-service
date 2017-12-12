package org.innovateuk.ifs.project.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.finance.resource.*;
import org.innovateuk.ifs.project.financechecks.service.FinanceCheckService;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.Arrays;
import java.util.List;

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
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class FinanceCheckServiceSecurityTest extends BaseServiceSecurityTest<FinanceCheckService> {

    private ProjectFinancePermissionRules projectFinancePermissionRules;
    private ProjectLookupStrategy projectLookupStrategies;

    @Before
    public void lookupPermissionRules() {
        projectFinancePermissionRules = getMockPermissionRulesBean(ProjectFinancePermissionRules.class);
        projectLookupStrategies = getMockPermissionEntityLookupStrategiesBean(ProjectLookupStrategy.class);
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
        when(projectLookupStrategies.getProjectCompositeId(1l)).thenReturn(ProjectCompositeId.id(1L));
        assertAccessDenied(
                () -> classUnderTest.getFinanceCheckOverview(1L),
                () -> {
                    verify(projectFinancePermissionRules).partnersCanSeeTheProjectFinanceOverviewsForTheirProject(isA(ProjectCompositeId.class), isA(UserResource.class));
                    verify(projectFinancePermissionRules).internalUsersCanSeeTheProjectFinanceOverviewsForAllProjects(isA(ProjectCompositeId.class), isA(UserResource.class));
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

    @Test
    public void testGetViability() {
        Long projectId = 1L;
        Long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        assertAccessDenied(() -> classUnderTest.getViability(projectOrganisationCompositeId),
                () -> {
                    verify(projectFinancePermissionRules).projectFinanceUserCanViewViability(projectOrganisationCompositeId, getLoggedInUser());
                    verifyNoMoreInteractions(projectFinancePermissionRules);
                });
    }

    @Test
    public void testSaveViability() {
        Long projectId = 1L;
        Long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        assertAccessDenied(() -> classUnderTest.saveViability(projectOrganisationCompositeId, Viability.APPROVED, ViabilityRagStatus.RED),
                () -> {
                    verify(projectFinancePermissionRules).projectFinanceUserCanSaveViability(projectOrganisationCompositeId, getLoggedInUser());
                    verifyNoMoreInteractions(projectFinancePermissionRules);
                });
    }

    @Test
    public void testGetEligibility() {
        Long projectId = 1L;
        Long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        assertAccessDenied(() -> classUnderTest.getEligibility(projectOrganisationCompositeId),
                () -> {
                    verify(projectFinancePermissionRules).projectFinanceUserCanViewEligibility(projectOrganisationCompositeId, getLoggedInUser());
                    verify(projectFinancePermissionRules).projectPartnersCanViewEligibility(projectOrganisationCompositeId, getLoggedInUser());
                    verifyNoMoreInteractions(projectFinancePermissionRules);
                });
    }

    @Test
    public void testSaveEligibility() {
        Long projectId = 1L;
        Long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        assertAccessDenied(() -> classUnderTest.saveEligibility(projectOrganisationCompositeId, Eligibility.APPROVED, EligibilityRagStatus.RED),
                () -> {
                    verify(projectFinancePermissionRules).projectFinanceUserCanSaveEligibility(projectOrganisationCompositeId, getLoggedInUser());
                    verifyNoMoreInteractions(projectFinancePermissionRules);
                });
    }

    @Test
    public void testGetCreditReport() {
        when(projectLookupStrategies.getProjectCompositeId(1l)).thenReturn(ProjectCompositeId.id(1L));
        assertAccessDenied(() -> classUnderTest.getCreditReport(1L, 2L),
                () -> {
                    verify(projectFinancePermissionRules).projectFinanceUserCanViewCreditReport(ProjectCompositeId.id(1L), getLoggedInUser());
                    verifyNoMoreInteractions(projectFinancePermissionRules);
                });
    }

    @Test
    public void testSetCreditReport() {
        when(projectLookupStrategies.getProjectCompositeId(1l)).thenReturn(ProjectCompositeId.id(1L));
        assertAccessDenied(() -> classUnderTest.saveCreditReport(1L, 2L, Boolean.TRUE),
                () -> {
                    verify(projectFinancePermissionRules).projectFinanceUserCanSaveCreditReport(ProjectCompositeId.id(1L), getLoggedInUser());
                    verifyNoMoreInteractions(projectFinancePermissionRules);
                });
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
                    fail("Should have thrown an AccessDeniedException for any non " + Arrays.toString(supportedRoles) + " users");
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

        @Override
        public ServiceResult<ViabilityResource> getViability(ProjectOrganisationCompositeId projectOrganisationCompositeId) {
            return null;
        }

        @Override
        public ServiceResult<Void> saveViability(ProjectOrganisationCompositeId projectOrganisationCompositeId, Viability viability, ViabilityRagStatus viabilityRagStatus) {
            return null;
        }

        @Override
        public ServiceResult<EligibilityResource> getEligibility(ProjectOrganisationCompositeId projectOrganisationCompositeId) {
            return null;
        }

        @Override
        public ServiceResult<Void> saveEligibility(ProjectOrganisationCompositeId projectOrganisationCompositeId, Eligibility eligibility, EligibilityRagStatus eligibilityRagStatus) {
            return null;
        }

        @Override
        public ServiceResult<Boolean> getCreditReport(Long projectId, Long organisationId) { return null; }

        @Override
        public ServiceResult<Void> saveCreditReport(Long projectId, Long organisationId, boolean creditReportPresent) { return null; }

        @Override
        public ServiceResult<List<ProjectFinanceResource>> getProjectFinances(Long projectId) {
            return null;
        }
    }
}

