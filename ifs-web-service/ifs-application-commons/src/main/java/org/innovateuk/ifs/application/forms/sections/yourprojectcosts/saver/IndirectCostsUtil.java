package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.saver;

import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.YourProjectCostsForm;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.function.BinaryOperator;

/*
    Helper class for indirect cost calculation
    this is a calculated cost that is 46% of the sum of the grant amounts of the Academic and Secretarial support and the Associate Salary costs.
    ((AcademicAndSecretarialSupport * grantClaimPercentage) + (AssociateSalaryCosts * grandClaimPercentage)) * indirectCostPercentage
 */
public class IndirectCostsUtil {

    public final static BigDecimal INDIRECT_COST_PERCENTAGE = BigDecimal.valueOf(46);
    private static final BigDecimal percentageDivisor = new BigDecimal("100");

    private static final BinaryOperator<BigDecimal> calculateIndirectCostPercentage = (grantTotalCost1, grantTotalCost2) ->
            grantTotalCost1.add(grantTotalCost2)
                    .multiply(INDIRECT_COST_PERCENTAGE)
                    .divide(percentageDivisor)
                    .setScale(0, RoundingMode.HALF_UP);

    private static final BinaryOperator<BigDecimal> calculateGrantPercentageAmount = (total, grantClaimPercentage) ->
            total.multiply(grantClaimPercentage).divide(percentageDivisor);

    private static BigDecimal getCostTotalFromFinances(FinanceRowType type, BaseFinanceResource financeResource) {
        return financeResource.getFinanceOrganisationDetails()
                .get(type).getCosts().stream()
                .filter(financeRowItem -> !financeRowItem.isEmpty() && financeRowItem.getTotal() != null)
                .map(FinanceRowItem::getTotal)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    public static BigDecimal calculateIndirectCost(BaseFinanceResource organisationFinance) {
        return calculateIndirectCostPercentage.apply(
                calculateGrantPercentageAmount.apply(
                        getCostTotalFromFinances(FinanceRowType.ASSOCIATE_SALARY_COSTS, organisationFinance),
                        organisationFinance.getGrantClaimPercentage()),
                calculateGrantPercentageAmount.apply(
                        getCostTotalFromFinances(FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT, organisationFinance),
                        organisationFinance.getGrantClaimPercentage()));
    }

    public static BigDecimal calculateIndirectCostFromForm(YourProjectCostsForm form) {
        form.recalculateTotals();

        BigDecimal totalAssociateSalaryCost = Optional.of(form.getTotalAssociateSalaryCosts())
                .orElse(BigDecimal.ZERO);

        BigDecimal totalAcademicAndSecretarialSupportCost = Optional.of(form.getTotalAcademicAndSecretarialSupportCosts())
                .orElse(BigDecimal.ZERO);

        return calculateIndirectCostPercentage.apply(
                calculateGrantPercentageAmount.apply(totalAssociateSalaryCost,
                        form.getGrantClaimPercentage()),
                calculateGrantPercentageAmount.apply(totalAcademicAndSecretarialSupportCost,
                        form.getGrantClaimPercentage()));
    }

    public static BigDecimal calculateIndirectCostWithNewAssociateSalaryCost(YourProjectCostsForm form,
                                                                             BaseFinanceResource finance,
                                                                             BigDecimal newAssociateSalaryTotalCost) {
        return calculateIndirectCostPercentage.apply(
                calculateGrantPercentageAmount.apply(newAssociateSalaryTotalCost,
                        finance.getGrantClaimPercentage()),
                calculateGrantPercentageAmount.apply(
                        getCostTotalFromFinances(FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT, finance),
                        finance.getGrantClaimPercentage()));
    }

    public static BigDecimal calculateIndirectCostWithNewAcademicAndSecretarialSupportCost(YourProjectCostsForm form,
                                                                                           BaseFinanceResource finance,
                                                                                           BigDecimal updatedValue) {
        return calculateIndirectCostPercentage.apply(
                calculateGrantPercentageAmount.apply(
                        getCostTotalFromFinances(FinanceRowType.ASSOCIATE_SALARY_COSTS, finance),
                        finance.getGrantClaimPercentage()),
                calculateGrantPercentageAmount.apply(
                        Optional.ofNullable(updatedValue).orElse(new BigDecimal("0")),
                        finance.getGrantClaimPercentage()));
    }

}
