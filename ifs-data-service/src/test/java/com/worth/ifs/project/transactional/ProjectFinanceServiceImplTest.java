package com.worth.ifs.project.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.finance.domain.Cost;
import com.worth.ifs.project.finance.domain.CostCategoryType;
import com.worth.ifs.project.finance.domain.SpendProfile;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.user.domain.Organisation;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.List;

import static com.worth.ifs.LambdaMatcher.createLambdaMatcher;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.finance.resource.cost.FinanceRowType.LABOUR;
import static com.worth.ifs.finance.resource.cost.FinanceRowType.MATERIALS;
import static com.worth.ifs.project.builder.ProjectBuilder.newProject;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ProjectFinanceServiceImplTest extends BaseServiceUnitTest<ProjectFinanceServiceImpl> {

    @Mock
    private CostCategoryTypeStrategy costCategoryTypeStrategy;

    @Mock
    private SpendProfileCostCategorySummaryStrategy spendProfileCostCategorySummaryStrategy;

    @Test
    public void testGenerateSpendProfile() {

        Long projectId = 123L;

        Project project = newProject().withDuration(3L).build();
        ProjectResource projectResource = newProjectResource().build();
        Organisation organisation1 = newOrganisation().build();
        Organisation organisation2 = newOrganisation().build();

        CostCategoryType matchingCostCategoryType1 = new CostCategoryType("Type 1", null);
        CostCategoryType matchingCostCategoryType2 = new CostCategoryType("Type 2", null);

        when(projectServiceMock.getProjectById(projectId)).thenReturn(serviceSuccess(projectResource));

        List<ProjectUserResource> projectUsers =
                newProjectUserResource().withOrganisation(organisation1.getId(), organisation2.getId()).build(2);
        when(projectServiceMock.getProjectUsers(projectId)).thenReturn(serviceSuccess(projectUsers));

        when(costCategoryTypeStrategy.getOrCreateCostCategoryTypeForSpendProfile(projectId, organisation1.getId())).
                thenReturn(serviceSuccess(matchingCostCategoryType1));

        when(costCategoryTypeStrategy.getOrCreateCostCategoryTypeForSpendProfile(projectId, organisation2.getId())).
                thenReturn(serviceSuccess(matchingCostCategoryType2));

        when(spendProfileCostCategorySummaryStrategy.getCostCategorySummaries(project.getId(), organisation1.getId())).thenReturn(serviceSuccess(
                asList(
                        new SpendProfileCostCategorySummary(LABOUR, new BigDecimal("100.00"), 1),
                        new SpendProfileCostCategorySummary(MATERIALS, new BigDecimal("200.00"), 1))));

        when(spendProfileCostCategorySummaryStrategy.getCostCategorySummaries(project.getId(), organisation2.getId())).thenReturn(serviceSuccess(
                asList(
                        new SpendProfileCostCategorySummary(LABOUR, new BigDecimal("300.00"), 1),
                        new SpendProfileCostCategorySummary(MATERIALS, new BigDecimal("400.00"), 1))));

        SpendProfile expectedOrganisation1Profile = new SpendProfile(organisation1, project, matchingCostCategoryType1, asList(new Cost("")), null);

        when(spendProfileRepositoryMock.save(spendProfileExpectations(expectedOrganisation1Profile))).thenReturn(null);
        ServiceResult<Void> generateResult = service.generateSpendProfile(projectId);

    }

    private SpendProfile spendProfileExpectations(SpendProfile expectedSpendProfile) {
        return createLambdaMatcher(spendProfile -> {
            assertEquals(expectedSpendProfile.getCostCategoryType(), spendProfile.getCostCategoryType());
        });
    }

    @Override
    protected ProjectFinanceServiceImpl supplyServiceUnderTest() {
        return new ProjectFinanceServiceImpl();
    }
}
