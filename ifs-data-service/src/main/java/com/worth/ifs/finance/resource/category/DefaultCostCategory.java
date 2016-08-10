package com.worth.ifs.finance.resource.category;

import com.worth.ifs.finance.resource.cost.FinanceRowItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code DefaultCostCategory} implementation for {@link FinanceRowCostCategory}.
 * Default representation for costs and defaults to summing up the costs.
 */
public class DefaultCostCategory implements FinanceRowCostCategory {
    private List<FinanceRowItem> costs = new ArrayList<>();
    private BigDecimal total = BigDecimal.ZERO;

    @Override
    public List<FinanceRowItem> getCosts() {
        return costs;
    }

    @Override
    public BigDecimal getTotal() {
        return total;
    }

    @Override
    public void calculateTotal() {
        total = costs.stream()
                .map(c -> c.getTotal() == null ? BigDecimal.ZERO : c.getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void addCost(FinanceRowItem costItem) {
        if(costItem!=null) {
            costs.add(costItem);
        }
    }

    @Override
    public boolean excludeFromTotalCost() {
        return false;
    }

    @Override
    public void setCosts(List<FinanceRowItem> costItems) {
        costs = costItems;
    }
}
