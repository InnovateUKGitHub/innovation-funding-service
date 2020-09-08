package org.innovateuk.ifs.project.spendprofile.transactional;

import org.innovateuk.ifs.project.financechecks.domain.CostCategory;

import java.math.BigDecimal;

/**
 * Holder of summary information used to generate Spend Profiles
 */
public class SpendProfileCostCategorySummary {

    private CostCategory category;
    private BigDecimal total;
    private long projectDurationInMonths;

    public SpendProfileCostCategorySummary(CostCategory category, BigDecimal totalWithScale, long projectDurationInMonths) {

        // Set the scale of the roundedTotal to zero to ignore the pence figures.
        BigDecimal roundTotal = totalWithScale.setScale(0, BigDecimal.ROUND_HALF_EVEN);

        this.category = category;
        this.total = roundTotal;
        this.projectDurationInMonths = projectDurationInMonths;
    }

    public CostCategory getCategory() {
        return category;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public long getProjectDurationInMonths() {
        return projectDurationInMonths;
    }
}
