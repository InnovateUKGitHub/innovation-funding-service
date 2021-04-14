package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.saver;

import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.YourProjectCostsForm;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

/*
    Helper class for indirect cost calculation
    this is a calculated cost that is 46% of the sum of the grant amounts of the Academic and Secretarial support and the Associate Salary costs.
    ((AcademicAndSecretarialSupport * grantClaimPercentage) + (AssociateSalaryCosts * grandClaimPercentage)) * indirectCostPercentage
 */
public class IndirectCostsUtil {

    public final static BigDecimal INDIRECT_COST_PERCENTAGE = BigDecimal.valueOf(46);

    public static BigDecimal calculateIndirectCost(ApplicationFinanceResource organisationFinance) {
        BigDecimal totalAssociateSalaryCost = organisationFinance.getFinanceOrganisationDetails().get(FinanceRowType.ASSOCIATE_SALARY_COSTS).getCosts().stream()
                .filter(financeRowItem -> !financeRowItem.isEmpty() && financeRowItem.getTotal() != null)
                .map(FinanceRowItem::getTotal)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        BigDecimal totalGrantAssociateSalaryCost = totalAssociateSalaryCost
                .multiply(organisationFinance.getGrantClaimPercentage())
                .divide(new BigDecimal(100));

        BigDecimal totalAcademicAndSecretarialSupportCost = organisationFinance.getFinanceOrganisationDetails().get(FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT).getCosts().stream()
                .filter(financeRowItem -> !financeRowItem.isEmpty() && financeRowItem.getTotal() != null)
                .map(FinanceRowItem::getTotal)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        BigDecimal totalGrantAcademicAndSecretarialSupportCost = totalAcademicAndSecretarialSupportCost
                .multiply(organisationFinance.getGrantClaimPercentage())
                .divide(new BigDecimal(100));

        return totalGrantAssociateSalaryCost
                .add(totalGrantAcademicAndSecretarialSupportCost)
                .multiply(INDIRECT_COST_PERCENTAGE)
                .divide(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateIndirectCostFromForm(YourProjectCostsForm form) {
        form.recalculateTotals();

        BigDecimal totalAssociateSalaryCost = Optional.of(form.getTotalAssociateSalaryCosts())
                .orElse(BigDecimal.ZERO);

        BigDecimal totalGrantAssociateSalaryCost = totalAssociateSalaryCost
                .multiply(form.getGrantClaimPercentage())
                .divide(new BigDecimal(100));

        BigDecimal totalAcademicAndSecretarialSupportCost = Optional.of(form.getTotalAcademicAndSecretarialSupportCosts())
                .orElse(BigDecimal.ZERO);

        BigDecimal totalGrantAcademicAndSecretarialSupportCost = totalAcademicAndSecretarialSupportCost
                .multiply(form.getGrantClaimPercentage())
                .divide(new BigDecimal(100));

        return getTotalIndirectCosts(totalGrantAssociateSalaryCost, totalGrantAcademicAndSecretarialSupportCost);
    }

    public static BigDecimal calculateIndirectCostWithNewAssociateSalaryCost(YourProjectCostsForm form,
                                                                             BaseFinanceResource finance,
                                                                             BigDecimal newAssociateSalaryTotalCost) {

        BigDecimal totalGrantAssociateSalaryCost = newAssociateSalaryTotalCost
                .multiply(finance.getGrantClaimPercentage())
                .divide(new BigDecimal(100));

        BigDecimal totalGrantAcademicAndSecretarialSupportCost = getAcademicAndSecretarialSupportCostsFromFinances(finance);

        return getTotalIndirectCosts(totalGrantAssociateSalaryCost, totalGrantAcademicAndSecretarialSupportCost);
    }

    public static BigDecimal calculateIndirectCostWithNewAcademicAndSecretarialSupportCost(YourProjectCostsForm form,
                                                                                           BaseFinanceResource finance,
                                                                                           BigDecimal updatedValue) {

        BigDecimal totalAssociateSalaryCost = finance.getFinanceOrganisationDetails(FinanceRowType.ASSOCIATE_SALARY_COSTS).getTotal();

        BigDecimal totalGrantAssociateSalaryCost = totalAssociateSalaryCost
                .multiply(finance.getGrantClaimPercentage())
                .divide(new BigDecimal(100));

        BigDecimal totalGrantAcademicAndSecretarialSupportCost = updatedValue
                .multiply(finance.getGrantClaimPercentage())
                .divide(new BigDecimal(100));

        return getTotalIndirectCosts(totalGrantAssociateSalaryCost, totalGrantAcademicAndSecretarialSupportCost);
    }


    private static BigDecimal getAcademicAndSecretarialSupportCostsFromFinances(BaseFinanceResource finance) {
        BigDecimal totalAcademicAndSecretarialSupportCost =
                Optional.of(finance.getFinanceOrganisationDetails(FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT).getTotal())
                        .orElse(BigDecimal.ZERO);

        return totalAcademicAndSecretarialSupportCost
                .multiply(finance.getGrantClaimPercentage())
                .divide(new BigDecimal(100));
    }

    private static BigDecimal getTotalIndirectCosts(BigDecimal totalGrantAssociateSalaryCost, BigDecimal totalGrantAcademicAndSecretarialSupportCost) {
        return totalGrantAssociateSalaryCost
                .add(totalGrantAcademicAndSecretarialSupportCost)
                .multiply(INDIRECT_COST_PERCENTAGE)
                .divide(new BigDecimal(100))
                .setScale(0, RoundingMode.HALF_UP);
    }

}
