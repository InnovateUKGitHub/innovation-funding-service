package org.innovateuk.ifs.project.spendprofile.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.Vat;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.transactional.ProjectProcurementMilestoneService;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.financechecks.domain.Cost;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigInteger.valueOf;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.finance.builder.VATCategoryBuilder.newVATCategory;
import static org.innovateuk.ifs.finance.resource.cost.ProcurementCostCategoryGenerator.OTHER_COSTS;
import static org.innovateuk.ifs.finance.resource.cost.ProcurementCostCategoryGenerator.VAT;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.procurement.milestone.builder.ProjectProcurementMilestoneResourceBuilder.newProjectProcurementMilestoneResource;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.financecheck.builder.CostCategoryBuilder.newCostCategory;
import static org.innovateuk.ifs.project.spendprofile.builder.SpendProfileCostCategorySummariesBuilder.newSpendProfileCostCategorySummaries;
import static org.innovateuk.ifs.project.spendprofile.builder.SpendProfileCostCategorySummaryBuilder.newSpendProfileCostCategorySummary;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.mockito.Mockito.when;

public class ProcurementMilestonesSpendProfileFigureDistributerMockTest  extends BaseServiceUnitTest<ProcurementMilestonesSpendProfileFigureDistributer> {

    @Mock
    private ProjectFinanceService projectFinanceService;

    @Mock
    private ProjectProcurementMilestoneService projectProcurementMilestoneService;

    @Test
    public void testDistributeCosts() {
        Project project = newProject().withDuration(5l).build();
        Vat vat = new Vat();
        vat.setRegistered(true);
        vat.setRate(new BigDecimal("0.20"));
        Organisation organisation = newOrganisation().build();
        // Note 102 + 20 = 122
        SpendProfileCostCategorySummaries costCategorySummaries = newSpendProfileCostCategorySummaries()
                .withCosts(
                        asList(
                                newSpendProfileCostCategorySummary()
                                        .withTotal(new BigDecimal("102"))
                                        .withCategory(
                                                newCostCategory()
                                                        .withName(OTHER_COSTS.getDisplayName())
                                                        .build())
                                        .build(),
                                newSpendProfileCostCategorySummary()
                                        .withTotal(new BigDecimal("20"))
                                        .withCategory(
                                                newCostCategory()
                                                        .withName(VAT.getDisplayName()).build())
                                        .build()))
                .build();
        ProjectFinanceResource projectFinance = newProjectFinanceResource()
                .withFinanceOrganisationDetails(
                        asMap(FinanceRowType.VAT, newVATCategory().withCosts(asList(vat))
                                .build()))
                .build();
        // Note 27 + 2 + 25 + 40 + 28 = 122
        List<ProjectProcurementMilestoneResource> milestones = asList(
                newProjectProcurementMilestoneResource().withMonth(1).withPayment(valueOf(27)).build(),
                newProjectProcurementMilestoneResource().withMonth(1).withPayment(valueOf(2)).build(), // Note also month 1
                newProjectProcurementMilestoneResource().withMonth(3).withPayment(valueOf(25)).build(),
                newProjectProcurementMilestoneResource().withMonth(4).withPayment(valueOf(40)).build(),
                newProjectProcurementMilestoneResource().withMonth(5).withPayment(valueOf(28)).build());

        when(projectFinanceService.financeChecksDetails(project.getId(), organisation.getId())).thenReturn(serviceSuccess(projectFinance));
        when(projectProcurementMilestoneService.getByProjectIdAndOrganisationId(project.getId(), organisation.getId())).thenReturn(serviceSuccess(milestones));

        // Method under test
        List<List<Cost>> costs = service.distributeCosts(costCategorySummaries, project, organisation);

        // Assertions;
        Assert.assertEquals(2, costs.size());
        Assert.assertEquals(5, costs.get(0).size());
        Assert.assertEquals(5, costs.get(1).size());

        List<Cost> otherCosts = costs.stream().filter(costsList-> costsList.get(0).getCostCategory().getName().equals(OTHER_COSTS.getDisplayName())).findFirst().get();
        List<Cost> vatCosts = costs.stream().filter(costsList-> costsList.get(0).getCostCategory().getName().equals(VAT.getDisplayName())).findFirst().get();

        Assert.assertEquals(new BigDecimal("25"), otherCosts.get(0).getValue());
        Assert.assertEquals(new BigDecimal("0"), otherCosts.get(1).getValue());
        Assert.assertEquals(new BigDecimal("21"), otherCosts.get(2).getValue());
        Assert.assertEquals(new BigDecimal("33"), otherCosts.get(3).getValue());
        Assert.assertEquals(new BigDecimal("23"), otherCosts.get(4).getValue());

        Assert.assertEquals(new BigDecimal("4"), vatCosts.get(0).getValue());
        Assert.assertEquals(new BigDecimal("0"), vatCosts.get(1).getValue());
        Assert.assertEquals(new BigDecimal("4"), vatCosts.get(2).getValue());
        Assert.assertEquals(new BigDecimal("7"), vatCosts.get(3).getValue());
        Assert.assertEquals(new BigDecimal("5"), vatCosts.get(4).getValue());
    }

    @Override
    protected ProcurementMilestonesSpendProfileFigureDistributer supplyServiceUnderTest() {
        return new ProcurementMilestonesSpendProfileFigureDistributer();
    }
}
