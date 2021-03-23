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
import java.util.List;
import java.util.Optional;

import static java.math.BigDecimal.ONE;
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

    public List<List<Cost>> distributeCosts(SpendProfileCostCategorySummaries costCategorySummaries, Project project, Organisation organisation) {


        ProjectFinanceResource projectFinance = projectFinanceService.financeChecksDetails(project.getId(), organisation.getId()).getSuccess();
        List<ProjectProcurementMilestoneResource> milestones = projectProcurementMilestoneService.getByProjectId(project.getId()).getSuccess();
        Long durationInMonths = project.getDurationInMonths();
        BigDecimal vatRate = projectFinance.getVatRate();
        CostCategory otherCostCategory = costCategory(costCategorySummaries, OTHER_COSTS);
        CostCategory vatCostCategory = costCategory(costCategorySummaries, VAT);
        List<BigInteger> milestoneTotalsPerMonth = milestoneTotalsPerMonth(durationInMonths.intValue(), milestones);
        List<OtherAndVat> costs = distributeWithVat(milestoneTotalsPerMonth, otherCostCategory, vatCostCategory, vatRate);
        List<OtherAndVat> adjustedCosts = adjustedCosts(costs, costCategorySummaries);
        return toCosts(adjustedCosts, otherCostCategory, vatCostCategory);
    }

    private List<List<Cost>> toCosts(List<OtherAndVat> costs, CostCategory otherCostCategory, CostCategory vatCostCategory){
        List<Cost> vat = range(0, costs.size())
                .mapToObj(index -> new Cost(new BigDecimal(costs.get(index).vat))
                        .withCategory(vatCostCategory)
                        .withTimePeriod(index, MONTH, 1, MONTH))
                .collect(toList());
        List<Cost> otherCosts = range(0, costs.size())
                .mapToObj(index -> new Cost(new BigDecimal(costs.get(index).vat))
                        .withCategory(otherCostCategory)
                        .withTimePeriod(index, MONTH, 1, MONTH))
                .collect(toList());
        return asList(otherCosts, vat);
    }

    private List<OtherAndVat> adjustedCosts(List<OtherAndVat> costsToAdjust, SpendProfileCostCategorySummaries  costCategorySummaries){
        BigInteger toSumToVat = cost(costCategorySummaries, VAT).getTotal().toBigInteger();
        BigInteger toSumToOtherCosts = cost(costCategorySummaries, OTHER_COSTS).getTotal().toBigInteger();
        BigInteger currentVatTotal = costsToAdjust.stream().map(otherAndVat -> otherAndVat.vat).reduce(BigInteger::add).orElse(ZERO);
        BigInteger currentOtherCostsTotal = costsToAdjust.stream().map(otherAndVat -> otherAndVat.other).reduce(BigInteger::add).orElse(ZERO);
        return range(0, costsToAdjust.size()).mapToObj(index ->
        {
            OtherAndVat current = costsToAdjust.get(index);
            if (index == costsToAdjust.size() - 1){
                return current;
            }
            return new OtherAndVat(current.other.add(toSumToOtherCosts.subtract(currentOtherCostsTotal)),
                                   current.vat.add(toSumToVat.subtract(currentVatTotal)));
        }).collect(toList());
    }

    private SpendProfileCostCategorySummary summaryTotal(SpendProfileCostCategorySummaries costCategorySummaries, SbriPilotCostCategoryGenerator generator){
        return costCategorySummaries.getCosts().stream().filter(cost -> generator.getDisplayName().equals(cost.getCategory().getName())).findFirst().get();
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

    private List<BigInteger> milestoneTotalsPerMonth(int durationInMonths, List<ProjectProcurementMilestoneResource> milestones) {
        return range(0, durationInMonths).mapToObj(
                index -> milestones.stream()
                        .filter(milestone -> index == milestone.getIndex().intValue())
                        .map(milestone -> milestone.getPayment())
                        .reduce(BigInteger::add)
                        .orElse(ZERO))
                .collect(toList());
    }

    private List<OtherAndVat> distributeWithVat(List<BigInteger> milestoneTotalsPerMonth, CostCategory otherCostsCategory, CostCategory vatCategory, BigDecimal vatRate){
        return range(0, milestoneTotalsPerMonth.size())
                .mapToObj(index -> distributeMonthWithVat(milestoneTotalsPerMonth.get(index), index, otherCostsCategory, vatCategory, vatRate))
                .collect(toList());
    }

    private OtherAndVat distributeMonthWithVat(BigInteger milestoneTotal, int index, CostCategory otherCostsCategory, CostCategory vatCategory, BigDecimal vatRate){
        BigInteger otherCosts = new BigDecimal(milestoneTotal).divide(ONE.add(vatRate), 1, BigDecimal.ROUND_HALF_DOWN).toBigInteger();
        BigInteger vat = milestoneTotal.subtract(otherCosts);
        return new OtherAndVat(otherCosts, vat);
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
