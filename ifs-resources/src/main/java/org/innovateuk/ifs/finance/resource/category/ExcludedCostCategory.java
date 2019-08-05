package org.innovateuk.ifs.finance.resource.category;

/**
 * {@code DefaultCostCategory} implementation for {@link FinanceRowCostCategory}.
 * Default representation for costs and defaults to summing up the costs.
 */
public class ExcludedCostCategory extends DefaultCostCategory {
    @Override
    public boolean excludeFromTotalCost() {
        return true;
    }
}
