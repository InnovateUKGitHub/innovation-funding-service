package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceRowService;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceRowServiceImpl;
import org.innovateuk.ifs.project.financechecks.security.ProjectFinancePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.mockito.ArgumentMatchers.isA;
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

    @Override
    protected Class<? extends ProjectFinanceRowService> getClassUnderTest() {
        return ProjectFinanceRowServiceImpl.class;
    }
}
