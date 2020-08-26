package org.innovateuk.ifs.finance.resource.category;

import java.math.BigDecimal;

/**
 * {@code ExcludedCostCategory} implementation for {@link FinanceRowCostCategory}.
 * Same configuration as {@code DefaultCostCategory} but the total doesn't count towards the total costs.
 */
public class ExcludedCostCategory extends DefaultCostCategory {
    @Override
    public boolean excludeFromTotalCost() {
        return true;
    }

    @Override
    public BigDecimal getTotal() {
        return ZERO_COST;
    }
}
