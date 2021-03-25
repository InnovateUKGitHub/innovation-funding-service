package org.innovateuk.ifs.project.spendprofile.transactional;

import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.SbriPilotCostCategoryGenerator;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.transactional.ProjectProcurementMilestoneService;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.financechecks.domain.Cost;
import org.innovateuk.ifs.project.financechecks.domain.CostCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.innovateuk.ifs.finance.resource.cost.SbriPilotCostCategoryGenerator.OTHER_COSTS;
import static org.innovateuk.ifs.finance.resource.cost.SbriPilotCostCategoryGenerator.VAT;
import static org.innovateuk.ifs.project.finance.resource.TimeUnit.MONTH;

@Component
public class ProcurementMilestonesSpendProfileFigureDistributer {

    @Autowired
    private ProjectProcurementMilestoneService projectProcurementMilestoneService;

    @Autowired
    private ProjectFinanceService projectFinanceService;

    /**
     * For procurement projects we store the costs in two separate ways, both of which should have the same totals:
     *
     * Firstly we have costs that represent the totals, per category, for the project duration. These are provided here
     * by the {@link SpendProfileCostCategorySummaries}. The categories for procurement are other costs and vat.
     *
     * Secondly we have milestones, which represent the costs for a particular month. These have no concept of category.
     * These are found here by querying the milestones repository.
     *
     * These sets of figures are stored separately but the totals must have the same value, and this is enforced by the
     * system.
     *
     * We use the values in the milestones to distribute the costs in the spend profile. This is achieved by taking
     * the value for each milestone and breaking it out into other costs and vat.
     *
     * Because we only deal in whole numbers this breakout can be slightly off. e.g.
     *
     * Vat rate = 20%
     * Milestone = £100
     * Other costs = £83.33... => £83
     * Vat = £16.66... => £17
     *
     * For each milestone the broken out other costs and vat will always add up to the milestone total. One figure will
     * be rounded up and the other down.
     *
     * However, because of the rounding, the summation of all the broken out other costs and vat might not quite equal
     * the values, per category, of the cost for the project duration. For example:
     *
     * Vat rate = 20%
     * Other costs = £30
     * Vat = £6
     * Total = £36
     *
     * Milestone      Total       Other cost       Vat
     * Month 1         £16        £13.33 => £13    £2.6666 => £3
     * Month 2         £10        £8.33  =>  £8    £1.6666 => £2
     * Month 3         £10        £8.33  =>  £8    £1.6666 => £2
     * Total           £36                  £29               £7
     *
     * The summation of all figures add up, and always will. But the summation per cost category is slightly out. The
     * maximum amount that a cost category total can be out by is the number of milestones in pounds.
     *
     * However it is preferable that the totals per category are not out. To achieve this we adjust the calculated
     * breakout figures. The adjustments must keep the breakout for a particular milestone summing to the total for that
     * milestone. Thus if we add £1 to the other costs break out, we must subtract a £1 from the vat breakout.
     * For example:
     *
     * Vat rate = 20%
     * Other costs = £30
     * Vat = £6
     * Total = £36
     *
     * Milestone      Total       Other cost            Vat
     * Month 1         £16        £13 (+ £1 adjustment) £3 (- £1 adjustment)
     * Month 2         £10        £8                    £2
     * Month 3         £10        £8                    £2
     * Total           £36        £30                   £6
     *
     *
     * @param costCategorySummaries
     * @param project
     * @param organisation
     * @return
     */
    public List<List<Cost>> distributeCosts(SpendProfileCostCategorySummaries costCategorySummaries, Project project, Organisation organisation) {
        ProjectFinanceResource projectFinance = projectFinanceService.financeChecksDetails(project.getId(), organisation.getId()).getSuccess();
        List<ProjectProcurementMilestoneResource> milestones = projectProcurementMilestoneService.getByProjectIdAndOrganisationId(project.getId(), organisation.getId()).getSuccess();
        Long durationInMonths = project.getDurationInMonths();
        BigDecimal vatRate = projectFinance.getVatRate(); // Zero for non vat registered.
        // Calculate the total costs per month
        List<BigInteger> milestoneTotalsPerMonth = milestoneTotalsPerMonth(durationInMonths.intValue(), milestones);
        // Assert that the milestones and the category totals are the same.
        assertTotalsEqual(milestoneTotalsPerMonth, costCategorySummaries);
        // Generate the raw breakdown per month
        List<OtherAndVat> rawBrokenOutCosts = breakoutCosts(milestoneTotalsPerMonth, vatRate);
        // Adjust so that the totals per category tally.
        List<OtherAndVat> adjustedCosts = adjustedCosts(rawBrokenOutCosts, costCategorySummaries);
        // Convert into actual domain Costs
        CostCategory otherCostCategory = costCategory(costCategorySummaries, OTHER_COSTS);
        CostCategory vatCostCategory = costCategory(costCategorySummaries, VAT);
        return toCosts(adjustedCosts, otherCostCategory, vatCostCategory);
    }

    private List<BigInteger> milestoneTotalsPerMonth(int durationInMonths, List<ProjectProcurementMilestoneResource> milestones) {
        return range(0, durationInMonths).mapToObj(
                index -> milestones.stream()
                        .filter(milestone -> index == milestone.getIndex().intValue()) // There can be multiple milestones per month
                        .map(milestone -> milestone.getPayment())
                        .reduce(BigInteger::add)
                        .orElse(ZERO))
                .collect(toList());
    }

    /**
     * This should be enforced by the system
     * @param milestoneTotalsPerMonth
     * @param costCategorySummaries
     */
    private void assertTotalsEqual(List<BigInteger> milestoneTotalsPerMonth, SpendProfileCostCategorySummaries costCategorySummaries){
        BigInteger milestoneTotal = milestoneTotalsPerMonth.stream().reduce(BigInteger::add).orElse(ZERO);
        BigInteger categoriesTotal = costCategorySummaries.getCosts().stream().map(SpendProfileCostCategorySummary::getTotal).reduce(BigDecimal::add).orElse(BigDecimal.ZERO).toBigIntegerExact();
        if (!milestoneTotal.equals(categoriesTotal)){
            throw new IllegalStateException("Categories Total: " + categoriesTotal + " is not equal to Milestones Total: " + milestoneTotal);
        }
    }

    private List<List<Cost>> toCosts(List<OtherAndVat> costs, CostCategory otherCostCategory, CostCategory vatCostCategory){
        List<Cost> vat = range(0, costs.size())
                .mapToObj(index -> new Cost(new BigDecimal(costs.get(index).vat))
                        .withCategory(vatCostCategory)
                        .withTimePeriod(index, MONTH, 1, MONTH))
                .collect(toList());
        List<Cost> otherCosts = range(0, costs.size())
                .mapToObj(index -> new Cost(new BigDecimal(costs.get(index).other))
                        .withCategory(otherCostCategory)
                        .withTimePeriod(index, MONTH, 1, MONTH))
                .collect(toList());
        return asList(otherCosts, vat);
    }

    private SpendProfileCostCategorySummary cost(SpendProfileCostCategorySummaries summaryPerCategory, SbriPilotCostCategoryGenerator generator){
        return summaryPerCategory.getCosts().stream()
                .filter(cost -> generator.getDisplayName().equals(cost.getCategory().getName()))
                .findFirst()
                .get();
    }

    private CostCategory costCategory(SpendProfileCostCategorySummaries summaryPerCategory, SbriPilotCostCategoryGenerator generator){
        return cost(summaryPerCategory, generator).getCategory();
    }

    private List<OtherAndVat> breakoutCosts(List<BigInteger> milestoneTotalsPerMonth, BigDecimal vatRate){
        return range(0, milestoneTotalsPerMonth.size())
                .mapToObj(index -> breakoutCosts(milestoneTotalsPerMonth.get(index), vatRate))
                .collect(toList());
    }

    private OtherAndVat breakoutCosts(BigInteger milestoneTotal, BigDecimal vatRate){
        BigInteger otherCosts = new BigDecimal(milestoneTotal).divide(BigDecimal.ONE.add(vatRate), 1, BigDecimal.ROUND_HALF_DOWN).toBigInteger();
        BigInteger vat = milestoneTotal.subtract(otherCosts);
        return new OtherAndVat(otherCosts, vat);
    }

    /**
     * There are many different ways in which we could adjust costs but however it is done, the totals per a month must
     * stay the same.
     * This means if we add to other costs whe must take away from vat and vice versa.
     *
     * @param costsToAdjust
     * @param costCategorySummaries
     * @return
     */
    private List<OtherAndVat> adjustedCosts(List<OtherAndVat> costsToAdjust, SpendProfileCostCategorySummaries costCategorySummaries){
        BigInteger correctVatTotal = cost(costCategorySummaries, VAT).getTotal().toBigIntegerExact();
        BigInteger correctOtherCostsTotal = cost(costCategorySummaries, OTHER_COSTS).getTotal().toBigIntegerExact();
        BigInteger currentVatTotal = costsToAdjust.stream().map(otherAndVat -> otherAndVat.vat).reduce(BigInteger::add).orElse(ZERO);
        BigInteger currentOtherCostsTotal = costsToAdjust.stream().map(otherAndVat -> otherAndVat.other).reduce(BigInteger::add).orElse(ZERO);
        if (!correctVatTotal.subtract(currentVatTotal).equals(currentOtherCostsTotal.subtract(correctOtherCostsTotal))){
            throw new IllegalStateException("The absolute amount we have to change other costs and vat by is not the same");
        }
        return adjustedCosts(costsToAdjust, currentVatTotal.subtract(correctVatTotal));
    }

    private List<OtherAndVat> adjustedCosts(List<OtherAndVat> costsToAdjust, BigInteger amountToAddToVat){
        List<OtherAndVat> adjustedCosts = new ArrayList(costsToAdjust);

        while (amountToAddToVat.equals(ZERO)) {
            for (int index = 0; index < adjustedCosts.size() - 1; ) {
                if (!amountToAddToVat.equals(ZERO)) {
                    OtherAndVat unAdjusted = costsToAdjust.get(index);
                    if (unAdjusted.other.compareTo(ZERO) > 0) {
                        costsToAdjust.set(index, new OtherAndVat(unAdjusted.other.subtract(ONE), unAdjusted.vat.add(ONE)));
                        amountToAddToVat = amountToAddToVat.subtract(ONE);
                    }
                }
            }
        }
        return adjustedCosts;
    }


    private static class OtherAndVat{
        private BigInteger other;
        private BigInteger vat;

        public OtherAndVat(BigInteger other, BigInteger vat) {
            this.other = other;
            this.vat = vat;
        }
    }
}
