package org.innovateuk.ifs.project.grantofferletter.viewmodel;

import java.math.BigDecimal;

/**
 *  Holder of values for the summary finance table, used on the grant offer letter template page
 */
public class SummaryFinanceTableModel {

    private final BigDecimal totalEligibleCosts;
    private final BigDecimal totalProjectGrant;
    private final BigDecimal totalGrantPercentage;
    private final BigDecimal totalOtherFunding;

    public SummaryFinanceTableModel(
            BigDecimal totalEligibleCosts,
            BigDecimal totalProjectGrant,
            BigDecimal totalGrantPercentage,
            BigDecimal totalOtherFunding
    ) {
        this.totalEligibleCosts = totalEligibleCosts;
        this.totalProjectGrant = totalProjectGrant;
        this.totalGrantPercentage = totalGrantPercentage;
        this.totalOtherFunding = totalOtherFunding;
    }

    public BigDecimal getTotalEligibleCosts() {
        return totalEligibleCosts;
    }

    public BigDecimal getTotalProjectGrant() {
        return totalProjectGrant;
    }

    public BigDecimal getTotalGrantPercentage() {
        return totalGrantPercentage;
    }

    public BigDecimal getTotalOtherFunding() {
        return totalOtherFunding;
    }

    public boolean showOtherFundingRow() {
        return totalOtherFunding.compareTo(BigDecimal.ZERO) > 0;
    }
}

