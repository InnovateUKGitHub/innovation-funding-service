package org.innovateuk.ifs.finance.resource.cost;

import java.math.BigDecimal;

/**
 * Abstract class for holding helper methods shared by all finance row item types.
 */
public abstract class AbstractFinanceRowItem implements FinanceRowItem {
    @Override
    public BigDecimal totalDiff(FinanceRowItem other){
        BigDecimal thisRate = getTotal() == null ? BigDecimal.ZERO : getTotal();
        BigDecimal otherRate = other.getTotal() == null ? BigDecimal.ZERO : other.getTotal();
        return thisRate.subtract(otherRate);
    }
}
