package com.worth.ifs.finance.resource.category;

import com.worth.ifs.finance.resource.cost.CostItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code DefaultCostCategory} implementation for {@link CostCategory}.
 * Default representation for costs and defaults to summing up the costs.
 */
public class GrantClaimCategory implements CostCategory {
    private static final Log LOG = LogFactory.getLog(GrantClaimCategory.class);

    private List<CostItem> costs = new ArrayList<>();
    private BigDecimal total = BigDecimal.ZERO;

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
                .filter(c -> c.getTotal()!=null)
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
        return true;
    }

    @Override
    public void setCosts(List<CostItem> costItems) {
        costs = costItems;
    }
}
