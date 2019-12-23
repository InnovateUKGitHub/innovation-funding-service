package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.GrantClaimPercentage;
import org.innovateuk.ifs.finance.security.ProjectFinanceLookupStrategy;
import org.innovateuk.ifs.finance.security.ProjectFinanceRowLookupStrategy;
import org.innovateuk.ifs.finance.security.ProjectFinanceRowPermissionRules;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceRowService;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceRowServiceImpl;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceRowBuilder.newProjectFinanceRow;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing how the secured methods in {@link ProjectFinanceRowServiceImpl} interact with Spring Security
 */
public class ProjectFinanceRowServiceSecurityTest extends BaseServiceSecurityTest<ProjectFinanceRowService> {

    private ProjectFinanceRowLookupStrategy projectFinanceRowLookupStrategy;
    private ProjectFinanceLookupStrategy projectFinanceLookupStrategy;
    private ProjectFinanceRowPermissionRules projectFinanceRowPermissionRules;

    @Before
    public void lookupPermissionRules() {

        projectFinanceRowLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ProjectFinanceRowLookupStrategy.class);
        projectFinanceLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ProjectFinanceLookupStrategy.class);
        projectFinanceRowPermissionRules = getMockPermissionRulesBean(ProjectFinanceRowPermissionRules.class);
    }

    @Test
    public void get() {
        final Long costId = 1L;
        when(projectFinanceRowLookupStrategy.getProjectFinanceRow(costId)).thenReturn(newProjectFinanceRow().with(id(costId)).build());
        assertAccessDenied(
                () -> classUnderTest.get(costId),
                () -> {
                    verify(projectFinanceRowPermissionRules)
                            .projectFinanceCanCrudProjectFinanceRows(isA(ProjectFinanceRow.class), isA(UserResource.class));
                    verify(projectFinanceRowPermissionRules)
                            .teamMembersCanCrudFinanceRows(isA(ProjectFinanceRow.class), isA(UserResource.class));
                }
        );
    }


    @Test
    public void create() {
        final Long financeId = 1L;
        when(projectFinanceLookupStrategy.getProjectFinance(financeId)).thenReturn(newProjectFinanceResource().with(id(financeId)).build());
        assertAccessDenied(
                () -> classUnderTest.create(new GrantClaimPercentage(financeId)),
                () -> {
                    verify(projectFinanceRowPermissionRules)
                            .projectFinanceCanCrudProjectFinanceRows(isA(ProjectFinanceResource.class), isA(UserResource.class));
                    verify(projectFinanceRowPermissionRules)
                            .teamMembersCanCrudFinanceRows(isA(ProjectFinanceResource.class), isA(UserResource.class));
                }
        );
    }

    @Test
    public void update() {
        final Long costId = 1L;
        when(projectFinanceRowLookupStrategy.getProjectFinanceRow(costId)).thenReturn(newProjectFinanceRow().with(id(costId)).build());
        assertAccessDenied(
                () -> classUnderTest.update(costId, new GrantClaimPercentage(2L)),
                () -> {
                    verify(projectFinanceRowPermissionRules)
                            .projectFinanceCanCrudProjectFinanceRows(isA(ProjectFinanceRow.class), isA(UserResource.class));
                    verify(projectFinanceRowPermissionRules)
                            .teamMembersCanCrudFinanceRows(isA(ProjectFinanceRow.class), isA(UserResource.class));
                }
        );
    }

    @Test
    public void delete() {
        final Long costId = 1L;
        when(projectFinanceRowLookupStrategy.getProjectFinanceRow(costId)).thenReturn(newProjectFinanceRow().with(id(costId)).build());
        assertAccessDenied(
                () -> classUnderTest.delete(costId),
                () -> {
                    verify(projectFinanceRowPermissionRules)
                            .projectFinanceCanCrudProjectFinanceRows(isA(ProjectFinanceRow.class), isA(UserResource.class));
                    verify(projectFinanceRowPermissionRules)
                            .teamMembersCanCrudFinanceRows(isA(ProjectFinanceRow.class), isA(UserResource.class));
                }
        );
    }


    @Override
    protected Class<? extends ProjectFinanceRowService> getClassUnderTest() {
        return ProjectFinanceRowServiceImpl.class;
    }
}

