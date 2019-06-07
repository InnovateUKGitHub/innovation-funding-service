package org.innovateuk.ifs.project.grantofferletter.viewmodel;

import java.math.BigDecimal;

/**
* Holder of values for the summary finance table, used on the grant offer letter template page
 */

public class SummaryFinanceTableModel extends BaseFinanceTableModel {

    private BigDecimal totalProjectCosts;
    private BigDecimal totalProjectGrant;

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
