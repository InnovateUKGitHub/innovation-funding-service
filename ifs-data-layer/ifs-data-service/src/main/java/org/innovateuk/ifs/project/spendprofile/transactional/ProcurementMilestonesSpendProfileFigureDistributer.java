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
        boolean isVatRegistered = projectFinance.isVatRegistered();

        CostCategory otherCostCategory = costCategory(costCategorySummaries, OTHER_COSTS).get();
        List<BigInteger> milestoneTotalsPerMonth = milestoneTotalsPerMonth(durationInMonths.intValue(), milestones);
        if (isVatRegistered){
            BigDecimal vatRate = projectFinance.getVatRate();
            CostCategory vatCostCategory = costCategory(costCategorySummaries, VAT).get();
            return distributeWithVat(milestoneTotalsPerMonth, otherCostCategory, vatCostCategory, vatRate);
        } else {
            return ditributeWithNoVat(milestoneTotalsPerMonth, otherCostCategory);
        }


    }

    private Optional<CostCategory> costCategory(SpendProfileCostCategorySummaries summaryPerCategory, SbriPilotCostCategoryGenerator generator){
        return summaryPerCategory.getCosts().stream()
                .map(cost -> cost.getCategory())
                .filter(costCategory -> generator.getDisplayName().equals(costCategory.getName()))
                .findFirst();
    }

    private List<BigInteger> milestoneTotalsPerMonth(int durationInMonths, List<ProjectProcurementMilestoneResource> milestones) {
        return range(0, durationInMonths).mapToObj(
                index -> milestones.stream()
                        .filter(milestone -> index == milestone.getIndex().intValue())
                        .map(milestone -> milestone.getPayment())
                        .reduce(BigInteger::add)
                        .orElse(BigInteger.ZERO))
                .collect(toList());
    }

    private List<List<Cost>> ditributeWithNoVat(List<BigInteger> milestoneTotalsPerMonth, CostCategory otherCostsCategory){
        List<Cost> otherCosts = range(0, milestoneTotalsPerMonth.size() - 1)
                .mapToObj(index ->
                        new Cost(new BigDecimal(milestoneTotalsPerMonth.get(index)))
                                .withCategory(otherCostsCategory)
                                .withTimePeriod(index,MONTH, 1, MONTH))
                .collect(toList());
        return asList(otherCosts);
    }

    private List<List<Cost>> distributeWithVat(List<BigInteger> milestoneTotalsPerMonth, CostCategory otherCostsCategory, CostCategory vatCategory, BigDecimal vatRate){
        List<OtherAndVat> costs = range(0, milestoneTotalsPerMonth.size() - 1)
                .mapToObj(index -> distributeMonthWithVat(milestoneTotalsPerMonth.get(index), index, otherCostsCategory, vatCategory, vatRate))
                .collect(toList());
        List<Cost> other = costs.stream().map(otherAndVat -> otherAndVat.other).collect(toList());
        List<Cost> vat = costs.stream().map(otherAndVat -> otherAndVat.vat).collect(toList());
        return asList(other, vat);
    }

    private OtherAndVat distributeMonthWithVat(BigInteger milestoneTotal, int index, CostCategory otherCostsCategory, CostCategory vatCategory, BigDecimal vatRate){
        BigInteger otherCostsValue = new BigDecimal(milestoneTotal).divide(ONE.add(vatRate), 1, BigDecimal.ROUND_HALF_DOWN).toBigInteger();
        BigInteger vatValue = milestoneTotal.subtract(otherCostsValue);
        Cost otherCosts = new Cost(new BigDecimal(otherCostsValue)).withCategory(otherCostsCategory).withTimePeriod(index, MONTH, 1, MONTH);
        Cost vatCost = new Cost(new BigDecimal(vatValue)).withCategory(vatCategory).withTimePeriod(index, MONTH, 1, MONTH);
        return new OtherAndVat(otherCosts, vatCost);
    }

    private static class OtherAndVat{
        private Cost other;
        private Cost vat;

        public OtherAndVat(Cost other, Cost vat) {
            this.other = other;
            this.vat = vat;
        }
    }
}
