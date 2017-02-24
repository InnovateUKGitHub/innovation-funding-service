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

    public static void main(String[] args) {
        System.out.println("Test one");

        //class vars
        BigDecimal firstMonthSpend;
        BigDecimal otherMonthsSpend;

        //params
        BigDecimal total = new BigDecimal(32);
        long projectDurationInMonths = 36;

/*        BigDecimal durationInMonths = BigDecimal.valueOf(projectDurationInMonths);
        BigDecimal monthlyCost = total.divide(durationInMonths, 0, HALF_EVEN);
        BigDecimal remainder = total.subtract(monthlyCost.multiply(durationInMonths)).setScale(0, HALF_EVEN);

        firstMonthSpend = monthlyCost.add(remainder);
        otherMonthsSpend = monthlyCost;*/


        BigDecimal durationInMonths = BigDecimal.valueOf(projectDurationInMonths);
        BigDecimal remainder = total.remainder(durationInMonths);

        BigDecimal perfectlyDivisibleTotal = total.subtract(remainder);
        BigDecimal costPerMonth = perfectlyDivisibleTotal.divide(durationInMonths, 0, HALF_EVEN);

        firstMonthSpend = costPerMonth.add(remainder);
        otherMonthsSpend = costPerMonth;

        System.out.println("First Month spend: " + firstMonthSpend.toString());
        System.out.println("Other Month spend: " + otherMonthsSpend.toString());
    }
}
