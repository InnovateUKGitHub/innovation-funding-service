package org.innovateuk.ifs.project.grantofferletter.populator;

import org.innovateuk.ifs.finance.builder.GrantClaimCostBuilder;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.KtpFinanceModel;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.KtpFinanceRowModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.innovateuk.ifs.finance.builder.ExcludedCostCategoryBuilder.newExcludedCostCategory;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class KtpFinanceModelPopulatorTest {

    @InjectMocks
    private KtpFinanceModelPopulator populator;

    @Test
    public void populate() {
        ProjectResource project = newProjectResource()
                .withDuration(10L)
                .build();

        ProjectFinanceResource leadFinances = newProjectFinanceResource()
                .withIndustrialCosts()
                .build();
        leadFinances.getFinanceOrganisationDetails().put(FinanceRowType.FINANCE, newExcludedCostCategory().withCosts(
                GrantClaimCostBuilder.newGrantClaimPercentage().withGrantClaimPercentage(new BigDecimal("44.44")).build(1)
        ).build());
        leadFinances.getFinanceOrganisationDetails().forEach((k, c) -> c.calculateTotal());

        KtpFinanceModel ktpFinanceModel = populator.populate(project, leadFinances);

        assertRow(ktpFinanceModel.getAssociateEmployment(), 300, 133, 167);
        assertRow(ktpFinanceModel.getAssociateDevelopment(), 300, 133, 167);
        assertRow(ktpFinanceModel.getTravelAndSubsistence(), 2100, 933, 1167);
        assertRow(ktpFinanceModel.getConsumables(), 229, 102, 127);
        assertRow(ktpFinanceModel.getKnowledgeBaseSupervisor(), 131, 58, 73);
        assertRow(ktpFinanceModel.getOtherCosts(), 400, 178, 222);
        assertRow(ktpFinanceModel.getAdditionalSupportCosts(), 131, 58, 73);
        assertRow(ktpFinanceModel.getAcademicAndSecretarialSupport(), 8750, 3888, 4862);

        //Estate costs contains the correction based on the rounding errors. 44.44% of 17957 is 7980.0908.
        assertRow(ktpFinanceModel.getAssociateEstateCosts(), 17957, 7981, 9976);

        assertEquals((int)ktpFinanceModel.getTable1TotalCost(), leadFinances.getTotal().setScale(0, RoundingMode.HALF_UP).intValue());
        assertEquals((int)ktpFinanceModel.getTable1TotalFunding(), leadFinances.getTotalFundingSought().setScale(0, RoundingMode.HALF_UP).intValue());
        assertEquals((int)ktpFinanceModel.getTable1TotalContribution(), leadFinances.getTotalContribution().setScale(0, RoundingMode.HALF_UP).intValue());

        assertEquals((int)ktpFinanceModel.getTable2TotalCost(), 12079);
        assertEquals((int)ktpFinanceModel.getTable2TotalFunding(), 5367);
        assertEquals((int)ktpFinanceModel.getTable2TotalContribution(), 6712);

        assertEquals(ktpFinanceModel.getContributionToKbPartnerOverheads(), 1849);
        assertEquals(ktpFinanceModel.getMaximumAmountOfGovtGrant(), 7216);
    }

    private void assertRow(KtpFinanceRowModel row, int cost, int funding, int contribution) {
        assertEquals(cost, (int) row.getCost());
        assertEquals(funding, (int) row.getFunding());
        assertEquals(contribution, (int) row.getContribution());
    }
}
