package org.innovateuk.ifs.project.spendprofile.transactional;

import org.innovateuk.ifs.project.financechecks.domain.Cost;
import org.innovateuk.ifs.project.financechecks.domain.CostCategory;
import org.innovateuk.ifs.project.financechecks.repository.CostCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.project.finance.resource.TimeUnit.MONTH;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

public class DefaultSpendProfileFigureDistributer implements SpendProfileFigureDistributer {

    @Autowired
    private CostCategoryRepository costCategoryRepository;

    @Override
    public List<List<Cost>> distributeCosts(SpendProfileCostCategorySummaries summaryPerCategory) {
        return simpleMap(summaryPerCategory.getCosts(), summary -> {
            CostCategory cc = costCategoryRepository.findById(summary.getCategory().getId()).orElse(null);
            BigDecimal durationInMonths = BigDecimal.valueOf(summary.getProjectDurationInMonths());
            BigDecimal remainder = summary.getTotal().remainder(durationInMonths);
            BigDecimal perfectlyDivisibleTotal = summary.getTotal().subtract(remainder);
            BigDecimal costPerMonth = perfectlyDivisibleTotal.divide(durationInMonths, 0, RoundingMode.HALF_EVEN);
            BigDecimal firstMonthSpend = costPerMonth.add(remainder);

            return IntStream.range(0, durationInMonths.intValue()).mapToObj(i -> {
                BigDecimal costValueForThisMonth = i == 0 ? firstMonthSpend : costPerMonth;

                return new Cost(costValueForThisMonth).
                        withCategory(cc).
                        withTimePeriod(i, MONTH, 1, MONTH);

            }).collect(toList());
        });
    }
}
