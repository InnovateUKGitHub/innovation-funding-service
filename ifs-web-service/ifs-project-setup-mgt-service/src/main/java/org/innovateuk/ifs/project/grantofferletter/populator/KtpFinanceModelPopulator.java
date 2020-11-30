package org.innovateuk.ifs.project.grantofferletter.populator;

import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.KtpFinanceModel;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.KtpFinanceRowModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.innovateuk.ifs.project.grantofferletter.viewmodel.KtpFinanceModel.KtpFinanceModelBuilder.aKtpFinanceModel;

@Component
public class KtpFinanceModelPopulator {

    private static final BigDecimal academicAndSecretarialSupportFixedAnnualRate = new BigDecimal("10500");

    public KtpFinanceModel populate(ProjectResource project, ProjectFinanceResource leadFinances) {
        BigDecimal claimPercentage = leadFinances.getGrantClaimPercentage();

        KtpFundingRowsRunningTotal fundingRunningTotal = new KtpFundingRowsRunningTotal();

        return aKtpFinanceModel()
                .withAssociateEmployment(row(leadFinances.getFinanceOrganisationDetails(FinanceRowType.ASSOCIATE_SALARY_COSTS), claimPercentage, fundingRunningTotal))
                .withAssociateDevelopment(row(leadFinances.getFinanceOrganisationDetails(FinanceRowType.ASSOCIATE_DEVELOPMENT_COSTS), claimPercentage, fundingRunningTotal))
                .withTravelAndSubsistence(row(leadFinances.getFinanceOrganisationDetails(FinanceRowType.KTP_TRAVEL), claimPercentage, fundingRunningTotal))
                .withConsumables(row(leadFinances.getFinanceOrganisationDetails(FinanceRowType.CONSUMABLES), claimPercentage, fundingRunningTotal))
                .withKnowledgeBaseSupervisor(row(leadFinances.getFinanceOrganisationDetails(FinanceRowType.KNOWLEDGE_BASE), claimPercentage, fundingRunningTotal))
                .withAdditionalSupportCosts(row(leadFinances.getFinanceOrganisationDetails(FinanceRowType.ASSOCIATE_SUPPORT), claimPercentage, fundingRunningTotal))
                .withOtherCosts(row(leadFinances.getFinanceOrganisationDetails(FinanceRowType.OTHER_COSTS), claimPercentage, fundingRunningTotal))
                .withAssociateEstateCosts(rowFromRunningTotal(fundingRunningTotal, leadFinances))
                .withAcademicAndSecretarialSupport(row(calculateAcademicAndSecretarialSupport(project), claimPercentage, fundingRunningTotal)) //todo
                .build();
    }

    private BigDecimal calculateAcademicAndSecretarialSupport(ProjectResource project) {
        return BigDecimal.valueOf(project.getDurationInMonths()).multiply(academicAndSecretarialSupportFixedAnnualRate);
    }

    private KtpFinanceRowModel rowFromRunningTotal(KtpFundingRowsRunningTotal fundingRowsRunningTotal, ProjectFinanceResource leadFinances) {
        int cost = leadFinances.getTotal().setScale(0, RoundingMode.HALF_UP).intValue() - fundingRowsRunningTotal.getCostTotal();
        int funding = leadFinances.getTotalFundingSought().setScale(0, RoundingMode.HALF_UP).intValue() - fundingRowsRunningTotal.getFundingTotal();
        return new KtpFinanceRowModel(
                Math.max(cost, 0),
                Math.max(funding, 0)
        );
    }

    private KtpFinanceRowModel row(FinanceRowCostCategory category, BigDecimal claimPercentage, KtpFundingRowsRunningTotal fundingRunningTotal) {
        return row(category.getTotal(), claimPercentage, fundingRunningTotal);
    }

    private KtpFinanceRowModel row(BigDecimal total, BigDecimal claimPercentage, KtpFundingRowsRunningTotal fundingRunningTotal) {
        BigDecimal funding = total.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP).multiply(claimPercentage)
                .setScale(0, RoundingMode.HALF_UP);
        return fundingRunningTotal.addToRunningTotal(new KtpFinanceRowModel(total.setScale(0, RoundingMode.HALF_UP).intValue(),
                funding.intValue()));
    }
    private static class KtpFundingRowsRunningTotal {

        private Integer costTotal = 0;
        private Integer fundingTotal = 0;

        public KtpFinanceRowModel addToRunningTotal(KtpFinanceRowModel add) {
            costTotal = costTotal + add.getCost();
            fundingTotal = fundingTotal + add.getFunding();
            return add;
        }

        public Integer getCostTotal() {
            return costTotal;
        }

        public Integer getFundingTotal() {
            return fundingTotal;
        }
    }
}
