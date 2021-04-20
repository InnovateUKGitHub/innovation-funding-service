package org.innovateuk.ifs.project.financecheck.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.project.core.security.ProjectLookupStrategy;
import org.innovateuk.ifs.project.finance.resource.*;
import org.innovateuk.ifs.project.financechecks.security.ProjectFinancePermissionRules;
import org.innovateuk.ifs.project.financechecks.service.FinanceCheckService;
import org.innovateuk.ifs.project.financechecks.service.FinanceCheckServiceImpl;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckPartnerStatusResourceBuilder.FinanceCheckEligibilityResourceBuilder.newFinanceCheckEligibilityResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

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
        assertRolesCanPerform(() -> classUnderTest.getByProjectAndOrganisation(new ProjectOrganisationCompositeId(1L, 2L)), PROJECT_FINANCE, IFS_ADMINISTRATOR, SYSTEM_MAINTAINER);
    }

    @Test
    public void testGetFinanceCheckSummary(){
        assertRolesCanPerform(() -> classUnderTest.getFinanceCheckSummary(1L), PROJECT_FINANCE, IFS_ADMINISTRATOR, SYSTEM_MAINTAINER, EXTERNAL_FINANCE);
    }

    @Test
    public void testGetFinanceCheckOverview() {
        when(projectLookupStrategies.getProjectCompositeId(1l)).thenReturn(ProjectCompositeId.id(1L));
        assertAccessDenied(
                () -> classUnderTest.getFinanceCheckOverview(1L),
                () -> {
                    verify(projectFinancePermissionRules)
                            .partnersCanSeeTheProjectFinanceOverviewsForTheirProject(isA(ProjectCompositeId.class), isA(UserResource.class));
                    verify(projectFinancePermissionRules)
                            .internalUsersCanSeeTheProjectFinanceOverviewsForAllProjects(isA(ProjectCompositeId.class), isA(UserResource.class));
                }
        );
    }

    @Test
    public void getFinanceCheckEligibilityDetails(){
        when(classUnderTestMock.getFinanceCheckEligibilityDetails(1L, 2L))
                .thenReturn(serviceSuccess(newFinanceCheckEligibilityResource().build()));

        assertAccessDenied(
                () -> classUnderTest.getFinanceCheckEligibilityDetails(1L, 2L),
                () -> {
                    verify(projectFinancePermissionRules)
                            .partnersCanSeeTheProjectFinancesForTheirOrganisation(isA(FinanceCheckEligibilityResource.class), isA(UserResource.class));
                    verify(projectFinancePermissionRules)
                            .internalUsersCanSeeTheProjectFinancesForTheirOrganisation(isA(FinanceCheckEligibilityResource.class), isA(UserResource.class));
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
                    verify(projectFinancePermissionRules)
                            .projectFinanceUserCanViewViability(projectOrganisationCompositeId, getLoggedInUser());
                    verify(projectFinancePermissionRules)
                            .competitionFinanceUserCanViewViability(projectOrganisationCompositeId, getLoggedInUser());
                    verifyNoMoreInteractions(projectFinancePermissionRules);
                });
    }

    @Test
    public void testSaveViability() {
        Long projectId = 1L;
        Long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        assertAccessDenied(() -> classUnderTest.saveViability(projectOrganisationCompositeId, ViabilityState.APPROVED, ViabilityRagStatus.RED),
                () -> {
                    verify(projectFinancePermissionRules)
                            .projectFinanceUserCanSaveViability(projectOrganisationCompositeId, getLoggedInUser());
                    verify(projectFinancePermissionRules)
                            .competitionFinanceUserCanSaveViability(projectOrganisationCompositeId, getLoggedInUser());
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
                    verify(projectFinancePermissionRules)
                            .projectFinanceUserCanViewEligibility(projectOrganisationCompositeId, getLoggedInUser());
                    verify(projectFinancePermissionRules)
                            .projectPartnersCanViewEligibility(projectOrganisationCompositeId, getLoggedInUser());
                    verify(projectFinancePermissionRules)
                            .competitionFinanceUserCanViewEligibility(projectOrganisationCompositeId, getLoggedInUser());
                    verifyNoMoreInteractions(projectFinancePermissionRules);
                });
    }

    @Test
    public void testSaveEligibility() {
        long projectId = 1;
        long organisationId = 1;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        assertAccessDenied(() -> classUnderTest.saveEligibility(projectOrganisationCompositeId, EligibilityState.APPROVED, EligibilityRagStatus.RED),
                () -> {
                    verify(projectFinancePermissionRules)
                            .projectFinanceUserCanSaveEligibility(projectOrganisationCompositeId, getLoggedInUser());
                    verify(projectFinancePermissionRules)
                            .competitionFinanceUserCanSaveEligibility(projectOrganisationCompositeId, getLoggedInUser());
                    verifyNoMoreInteractions(projectFinancePermissionRules);
                });
    }

    @Test
    public void testGetFundingRules() {
        Long projectId = 1L;
        Long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        assertAccessDenied(() -> classUnderTest.getFundingRules(projectOrganisationCompositeId),
                () -> {
                    verify(projectFinancePermissionRules)
                            .projectFinanceUserCanViewFundingRules(projectOrganisationCompositeId, getLoggedInUser());
                    verify(projectFinancePermissionRules)
                            .userCanViewTheirOwnFundingRulesStatus(projectOrganisationCompositeId, getLoggedInUser());
                    verifyNoMoreInteractions(projectFinancePermissionRules);
                });
    }

    @Test
    public void testSaveFundingRules() {
        long projectId = 1;
        long organisationId = 1;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        assertAccessDenied(() -> classUnderTest.saveFundingRules(projectOrganisationCompositeId, FundingRules.SUBSIDY_CONTROL),
                () -> {
                    verify(projectFinancePermissionRules)
                            .projectFinanceUserCanSaveFundingRules(projectOrganisationCompositeId, getLoggedInUser());
                    verify(projectFinancePermissionRules)
                            .competitionFinanceUserCanSaveFundingRules(projectOrganisationCompositeId, getLoggedInUser());
                    verifyNoMoreInteractions(projectFinancePermissionRules);
                });
    }

    @Test
    public void testApproveFundingRules() {
        long projectId = 1;
        long organisationId = 1;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        assertAccessDenied(() -> classUnderTest.approveFundingRules(projectOrganisationCompositeId),
                () -> {
                    verify(projectFinancePermissionRules)
                            .projectFinanceUserCanSaveFundingRules(projectOrganisationCompositeId, getLoggedInUser());
                    verify(projectFinancePermissionRules)
                            .competitionFinanceUserCanSaveFundingRules(projectOrganisationCompositeId, getLoggedInUser());
                    verifyNoMoreInteractions(projectFinancePermissionRules);
                });
    }

    @Test
    public void testGetCreditReport() {
        when(projectLookupStrategies.getProjectCompositeId(1L)).thenReturn(ProjectCompositeId.id(1L));
        assertAccessDenied(() -> classUnderTest.getCreditReport(1, 2L),
                () -> {
                    verify(projectFinancePermissionRules)
                            .projectFinanceUserCanViewCreditReport(ProjectCompositeId.id(1L), getLoggedInUser());
                    verify(projectFinancePermissionRules)
                            .competitionFinanceUserCanViewCreditReport(ProjectCompositeId.id(1L), getLoggedInUser());
                    verifyNoMoreInteractions(projectFinancePermissionRules);
                });
    }

    @Test
    public void testSetCreditReport() {
        when(projectLookupStrategies.getProjectCompositeId(1l)).thenReturn(ProjectCompositeId.id(1L));
        assertAccessDenied(() -> classUnderTest.saveCreditReport(1L, 2L, Boolean.TRUE),
                () -> {
                    verify(projectFinancePermissionRules)
                            .projectFinanceUserCanSaveCreditReport(ProjectCompositeId.id(1L), getLoggedInUser());
                    verify(projectFinancePermissionRules)
                            .competitionFinanceUserCanSaveCreditReport(ProjectCompositeId.id(1L), getLoggedInUser());
                    verifyNoMoreInteractions(projectFinancePermissionRules);
                });
    }

    @Override
    protected Class<? extends FinanceCheckService> getClassUnderTest() {
        return FinanceCheckServiceImpl.class;
    }
}