package org.innovateuk.ifs.project.grantofferletter.populator;

import org.innovateuk.ifs.finance.builder.GrantClaimCostBuilder;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.AcademicAndSecretarialSupport;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.KtpFinanceModel;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.KtpFinanceRowModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Before;
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
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class KtpFinanceModelPopulatorTest {

    @InjectMocks
    private KtpFinanceModelPopulator populator;

    private ProjectResource project;

    private ProjectFinanceResource leadFinances;

    @Before
    public void setup() {
        project = newProjectResource()
                .withDuration(10L)
                .build();

        leadFinances = newProjectFinanceResource()
                .withIndustrialCosts()
                .build();

        leadFinances.getFinanceOrganisationDetails().put(FinanceRowType.FINANCE, newExcludedCostCategory().withCosts(
                GrantClaimCostBuilder.newGrantClaimPercentage().withGrantClaimPercentage(new BigDecimal("44.44")).build(1)
        ).build());

        // Remove cost categories that are no longer needed for KTP
        leadFinances.getFinanceOrganisationDetails().remove(FinanceRowType.LABOUR);
        leadFinances.getFinanceOrganisationDetails().remove(FinanceRowType.OVERHEADS);
        leadFinances.getFinanceOrganisationDetails().remove(FinanceRowType.MATERIALS);
        leadFinances.getFinanceOrganisationDetails().remove(FinanceRowType.CAPITAL_USAGE);
        leadFinances.getFinanceOrganisationDetails().remove(FinanceRowType.SUBCONTRACTING_COSTS);
        leadFinances.getFinanceOrganisationDetails().remove(FinanceRowType.TRAVEL);
        leadFinances.getFinanceOrganisationDetails().remove(FinanceRowType.VAT);
        leadFinances.getFinanceOrganisationDetails().remove(FinanceRowType.PROCUREMENT_OVERHEADS);
    }

    @Test
    public void populate() {
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

        assertRow(ktpFinanceModel.getAssociateEstateCosts(), 131, 59, 72);

        assertEquals(leadFinances.getTotal().setScale(0, RoundingMode.HALF_UP).intValue(), (int)ktpFinanceModel.getTable1TotalCost());
        assertEquals(leadFinances.getTotalFundingSought().setScale(0, RoundingMode.HALF_UP).intValue(), (int)ktpFinanceModel.getTable1TotalFunding());
        assertEquals(leadFinances.getTotalContribution().setScale(0, RoundingMode.HALF_UP).intValue(), (int)ktpFinanceModel.getTable1TotalContribution());

        assertEquals(12079, (int)ktpFinanceModel.getTable2TotalCost());
        assertEquals(5367, (int)ktpFinanceModel.getTable2TotalFunding());
        assertEquals(6712, (int)ktpFinanceModel.getTable2TotalContribution());

        assertEquals(1849, ktpFinanceModel.getContributionToKbPartnerOverheads());
        assertEquals(7216, ktpFinanceModel.getMaximumAmountOfGovtGrant());

        assertNull(ktpFinanceModel.getFecModelEnabled());
        assertFalse(ktpFinanceModel.isFecModelDisabled());
    }

    @Test
    public void populateNonFec() {
        leadFinances.setFecModelEnabled(false);

        // Remove cost categories that are no longer needed for KTP NON-FEC
        leadFinances.getFinanceOrganisationDetails().remove(FinanceRowType.ASSOCIATE_SUPPORT);
        leadFinances.getFinanceOrganisationDetails().remove(FinanceRowType.ESTATE_COSTS);
        leadFinances.getFinanceOrganisationDetails().remove(FinanceRowType.KNOWLEDGE_BASE);

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
        assertRow(ktpFinanceModel.getOtherCosts(), 400, 178, 222);
        assertRow(ktpFinanceModel.getAcademicAndSecretarialSupport(), academicAndSecretarialSupport.getCost().intValue(), 13, 16);

        ProjectFinanceResource filteredLeadFinances = leadFinances;
        filteredLeadFinances.getFinanceOrganisationDetails().remove(FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT);

        assertEquals(filteredLeadFinances.getTotal().setScale(0, RoundingMode.HALF_UP).intValue(), (int)ktpFinanceModel.getTable1TotalCost());
        assertEquals(filteredLeadFinances.getTotalFundingSought().setScale(0, RoundingMode.HALF_UP).intValue(), (int)ktpFinanceModel.getTable1TotalFunding());
        assertEquals(filteredLeadFinances.getTotalContribution().setScale(0, RoundingMode.HALF_UP).intValue(), (int)ktpFinanceModel.getTable1TotalContribution());

        assertEquals(3358, (int)ktpFinanceModel.getTable2TotalCost());
        assertEquals(1492, (int)ktpFinanceModel.getTable2TotalFunding());
        assertEquals(1866, (int)ktpFinanceModel.getTable2TotalContribution());

        assertEquals(67, ktpFinanceModel.getContributionToKbPartnerOverheads());
        assertEquals(1559, ktpFinanceModel.getMaximumAmountOfGovtGrant());

        assertNull(ktpFinanceModel.getKnowledgeBaseSupervisor());
        assertNull(ktpFinanceModel.getAdditionalSupportCosts());
        assertNull(ktpFinanceModel.getAssociateEstateCosts());

        assertFalse(ktpFinanceModel.getFecModelEnabled());
        assertTrue(ktpFinanceModel.isFecModelDisabled());
    }

    private void assertRow(KtpFinanceRowModel row, int cost, int funding, int contribution) {
        assertEquals(cost, (int) row.getCost());
        assertEquals(funding, (int) row.getFunding());
        assertEquals(contribution, (int) row.getContribution());
    }
}
