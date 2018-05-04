package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.security.ApplicationLookupStrategy;
import org.innovateuk.ifs.application.security.ApplicationPermissionRules;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.security.*;
import org.innovateuk.ifs.finance.transactional.FinanceService;
import org.innovateuk.ifs.finance.transactional.FinanceServiceImpl;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.core.security.ProjectLookupStrategy;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

public class FinanceServiceSecurityTest extends BaseServiceSecurityTest<FinanceService> {

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
    public void testFindApplicationFinanceByApplicationIdAndOrganisation() {
        final Long applicationId = 1L;
        final Long organisationId = 2L;

        when(classUnderTestMock.findApplicationFinanceByApplicationIdAndOrganisation(applicationId, organisationId))
                .thenReturn(serviceSuccess(newApplicationFinanceResource().build()));

        assertAccessDenied(
                () -> classUnderTest.findApplicationFinanceByApplicationIdAndOrganisation(applicationId, organisationId),
                () -> verifyApplicationFinanceResourceReadRulesCalled()
        );
    }

    @Test
    public void testFindApplicationFinanceByApplication() {
        final Long applicationId = 1L;

        when(classUnderTestMock.findApplicationFinanceByApplication(applicationId))
                .thenReturn(serviceSuccess(newApplicationFinanceResource().build(ARRAY_SIZE_FOR_POST_FILTER_TESTS)));

        ServiceResult<List<ApplicationFinanceResource>> applicationFinanceByApplication =
                classUnderTest.findApplicationFinanceByApplication(applicationId);
        assertTrue(applicationFinanceByApplication.getSuccess().isEmpty());
        verifyApplicationFinanceResourceReadRulesCalled(ARRAY_SIZE_FOR_POST_FILTER_TESTS);
    }


    @Test
    public void testGetApplicationFinanceById() {
        final Long applicationId = 1L;

        when(classUnderTestMock.getApplicationFinanceById(applicationId))
                .thenReturn(serviceSuccess(newApplicationFinanceResource().build()));

        assertAccessDenied(
                () -> classUnderTest.getApplicationFinanceById(applicationId),
                () -> verifyApplicationFinanceResourceReadRulesCalled()
        );
    }

    @Test
    public void testFinanceDetails() {
        final Long applicationId = 1L;
        final Long organisationId = 1L;

        when(classUnderTestMock.financeDetails(applicationId, organisationId))
                .thenReturn(serviceSuccess(newApplicationFinanceResource().build()));

        assertAccessDenied(
                () -> classUnderTest.financeDetails(applicationId, organisationId),
                () -> verifyApplicationFinanceResourceReadRulesCalled()
        );
    }

    @Test
    public void testFinanceTotals() {
        final Long applicationId = 1L;
        when(applicationLookupStrategy.getApplicationResource(applicationId)).thenReturn(newApplicationResource().withId(applicationId).build());

        assertAccessDenied(
                () -> classUnderTest.financeTotals(applicationId),
                () -> {
                    verify(applicationRules)
                            .internalUserCanSeeApplicationFinancesTotals(isA(ApplicationResource.class), isA(UserResource.class));
                    verify(applicationRules)
                            .consortiumCanSeeTheApplicationFinanceTotals(isA(ApplicationResource.class), isA(UserResource.class));
                    verify(applicationRules)
                            .assessorCanSeeTheApplicationFinancesTotals(isA(ApplicationResource.class), isA(UserResource.class));
                });
    }

    @Test
    public void testGetResearchParticipationPercentage() {
        final Long applicationId = 1L;
        when(applicationLookupStrategy.getApplicationResource(applicationId)).thenReturn(newApplicationResource().withId(applicationId).build());
        assertAccessDenied(
                () -> classUnderTest.getResearchParticipationPercentage(applicationId),
                () -> {
                    verify(applicationRules)
                            .assessorCanSeeTheResearchParticipantPercentageInApplicationsTheyAssess(isA(ApplicationResource.class), isA(UserResource.class));
                    verify(applicationRules)
                            .internalUsersCanSeeTheResearchParticipantPercentageInApplications(isA(ApplicationResource.class), isA(UserResource.class));
                    verify(applicationRules)
                            .consortiumCanSeeTheResearchParticipantPercentage(isA(ApplicationResource.class), isA(UserResource.class));
                });
    }

    @Test
    public void testOrganisationSeeksFunding() {
        final Long projectId = 1L;
        final Long applicationId = 1L;
        final Long organisationId = 1L;

        when(projectLookupStrategy.getProjectResource(projectId)).thenReturn(newProjectResource().withId(projectId).build());

        assertAccessDenied(
                () -> classUnderTest.organisationSeeksFunding(projectId, applicationId, organisationId),
                () -> {
                    verify(costPermissionsRules).projectPartnersCanCheckFundingStatusOfTeam(isA(ProjectResource.class), isA(UserResource.class));
                    verify(costPermissionsRules).projectPartnersCanCheckFundingStatusOfTeam(isA(ProjectResource.class), isA(UserResource.class));
                });
    }

    private void verifyApplicationFinanceResourceReadRulesCalled() {
        verifyApplicationFinanceResourceReadRulesCalled(1);
    }

    private void verifyApplicationFinanceResourceReadRulesCalled(int nTimes) {
        verify(applicationFinanceRules, times(nTimes))
                .consortiumCanSeeTheApplicationFinancesForTheirOrganisation(isA(ApplicationFinanceResource.class), isA(UserResource.class));
        verify(applicationFinanceRules, times(nTimes))
                .assessorCanSeeTheApplicationFinanceForOrganisationsInApplicationsTheyAssess(isA(ApplicationFinanceResource.class), isA(UserResource.class));
        verify(applicationFinanceRules, times(nTimes))
                .internalUserCanSeeApplicationFinancesForOrganisations(isA(ApplicationFinanceResource.class), isA(UserResource.class));
    }

    @Override
    protected Class<? extends FinanceService> getClassUnderTest() {
        return FinanceServiceImpl.class;
    }
}
