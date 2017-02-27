package org.innovateuk.ifs.project.finance.transactional;

import org.innovateuk.ifs.project.finance.domain.CostCategory;

import java.math.BigDecimal;

import static java.math.RoundingMode.HALF_EVEN;

/**
 * Holder of summary information used to generate Spend Profiles
 */
class SpendProfileCostCategorySummary {

    private CostCategory category;
    private BigDecimal total;
    private BigDecimal firstMonthSpend;
    private BigDecimal otherMonthsSpend;

    SpendProfileCostCategorySummary(CostCategory category, BigDecimal total, long projectDurationInMonths) {
        this.category = category;
        this.total = total;

        BigDecimal durationInMonths = BigDecimal.valueOf(projectDurationInMonths);
        BigDecimal remainder = total.remainder(durationInMonths);

        BigDecimal perfectlyDivisibleTotal = total.subtract(remainder);
        BigDecimal costPerMonth = perfectlyDivisibleTotal.divide(durationInMonths, 0, HALF_EVEN);

        this.firstMonthSpend = costPerMonth.add(remainder);
        this.otherMonthsSpend = costPerMonth;
    }

    public CostCategory getCategory() {
        return category;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public BigDecimal getFirstMonthSpend() {
        return firstMonthSpend;
    }

    public BigDecimal getOtherMonthsSpend() {
        return otherMonthsSpend;
    }
}
