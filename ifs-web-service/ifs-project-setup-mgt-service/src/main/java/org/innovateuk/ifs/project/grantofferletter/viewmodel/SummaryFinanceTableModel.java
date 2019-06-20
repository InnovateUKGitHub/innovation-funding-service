package org.innovateuk.ifs.project.grantofferletter.viewmodel;

import java.math.BigDecimal;

/**
 *  Holder of values for the summary finance table, used on the grant offer letter template page
 */
public class SummaryFinanceTableModel {

    private final BigDecimal totalProjectCosts;
    private final BigDecimal totalProjectGrant;

    public SummaryFinanceTableModel(
            BigDecimal totalProjectCosts,
            BigDecimal totalProjectGrant
    ) {
        this.totalProjectCosts = totalProjectCosts;
        this.totalProjectGrant = totalProjectGrant;
    }

    public BigDecimal getTotalProjectCosts() {
        return totalProjectCosts;
    }

    public BigDecimal getTotalProjectGrant() {
        return totalProjectGrant;
    }
}

