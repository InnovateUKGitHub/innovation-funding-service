package org.innovateuk.ifs.finance.resource.cost;

import java.math.BigDecimal;

/**
 * Abstract class for holding helper methods shared by all finance row item types.
 */
public abstract class AbstractFinanceRowItem implements FinanceRowItem {
    @Override
    public BigDecimal totalDiff(FinanceRowItem other){
        BigDecimal thisRate = getTotal() == null ? new BigDecimal(0) : getTotal();
        BigDecimal otherRate = other.getTotal() == null ? new BigDecimal(0) : other.getTotal();
        return thisRate.subtract(otherRate);
    }
}
