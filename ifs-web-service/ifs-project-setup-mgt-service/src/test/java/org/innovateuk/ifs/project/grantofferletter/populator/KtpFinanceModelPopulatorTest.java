package org.innovateuk.ifs.project.grantofferletter.populator;

import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.AcademicAndSecretarialSupportCostRowForm;
import org.innovateuk.ifs.finance.builder.GrantClaimCostBuilder;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.AcademicAndSecretarialSupport;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.KtpFinanceModel;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.KtpFinanceRowModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Collections;

import static org.innovateuk.ifs.finance.builder.DefaultCostCategoryBuilder.newDefaultCostCategory;
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

        AcademicAndSecretarialSupport academicAndSecretarialSupport = new AcademicAndSecretarialSupport();
        academicAndSecretarialSupport.setId(1L);
        academicAndSecretarialSupport.setCost(new BigInteger("29"));
        academicAndSecretarialSupport.setTargetId(leadFinances.getId());

        leadFinances.getFinanceOrganisationDetails()
                .put(FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT,
                        newDefaultCostCategory()
                                .withCosts(Collections.singletonList(academicAndSecretarialSupport))
                                .build());
        leadFinances.getFinanceOrganisationDetails().forEach((k, c) -> c.calculateTotal());

        KtpFinanceModel ktpFinanceModel = populator.populate(project, leadFinances);

        assertRow(ktpFinanceModel.getAssociateEmployment(), 300, 133, 167);
        assertRow(ktpFinanceModel.getAssociateDevelopment(), 300, 133, 167);
        assertRow(ktpFinanceModel.getTravelAndSubsistence(), 2100, 933, 1167);
        assertRow(ktpFinanceModel.getConsumables(), 229, 102, 127);
        assertRow(ktpFinanceModel.getKnowledgeBaseSupervisor(), 131, 58, 73);
        assertRow(ktpFinanceModel.getOtherCosts(), 400, 178, 222);
        assertRow(ktpFinanceModel.getAdditionalSupportCosts(), 131, 58, 73);
        assertRow(ktpFinanceModel.getAcademicAndSecretarialSupport(), academicAndSecretarialSupport.getCost().intValue(), 13, 16);

        assertRow(ktpFinanceModel.getAssociateEstateCosts(), 17986, 7994, 9992);

        assertEquals((int)ktpFinanceModel.getTable1TotalCost(), leadFinances.getTotal().setScale(0, RoundingMode.HALF_UP).intValue());
        assertEquals((int)ktpFinanceModel.getTable1TotalFunding(), leadFinances.getTotalFundingSought().setScale(0, RoundingMode.HALF_UP).intValue());
        assertEquals((int)ktpFinanceModel.getTable1TotalContribution(), leadFinances.getTotalContribution().setScale(0, RoundingMode.HALF_UP).intValue());

        assertEquals(3358, (int)ktpFinanceModel.getTable2TotalCost());
        assertEquals(1492, (int)ktpFinanceModel.getTable2TotalFunding());
        assertEquals(1866, (int)ktpFinanceModel.getTable2TotalContribution());

        assertEquals(67, ktpFinanceModel.getContributionToKbPartnerOverheads());
        assertEquals(1559, ktpFinanceModel.getMaximumAmountOfGovtGrant());
    }

    private void assertRow(KtpFinanceRowModel row, int cost, int funding, int contribution) {
        assertEquals(cost, (int) row.getCost());
        assertEquals(funding, (int) row.getFunding());
        assertEquals(contribution, (int) row.getContribution());
    }
}
