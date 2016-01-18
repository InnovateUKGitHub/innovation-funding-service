package com.worth.ifs.finance.resource.category;

import com.worth.ifs.finance.resource.cost.CostItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code DefaultCostCategory} implementation for {@link CostCategory}.
 * Default representation for costs and defaults to summing up the costs.
 */
public class DefaultCostCategory implements CostCategory {
    List<CostItem> costs = new ArrayList<>();
    BigDecimal total = BigDecimal.ZERO;

    @Override
    public List<CostItem> getCosts() {
        return costs;
    }

    @Override
    public BigDecimal getTotal() {
        return total;
    }

    @Override
    public void calculateTotal() {
        total = costs.stream()
                .map(c -> c.getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void addCost(CostItem costItem) {
        if(costItem!=null) {
            costs.add(costItem);
        }
    }

    @Override
    public boolean excludeFromTotalCost() {
        return false;
    }

    @Override
    public void setCosts(List<CostItem> costItems) {
        costs = costItems;
    }
}
