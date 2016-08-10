package com.worth.ifs.finance.resource.category;

import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.finance.resource.cost.Overhead;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * {@code DefaultCostCategory} implementation for {@link FinanceRowCostCategory}.
 * Default representation for costs and defaults to summing up the costs.
 */
public class OverheadCostCategory implements FinanceRowCostCategory {
    public static final String ACCEPT_RATE = "Accept Rate";
    private List<FinanceRowItem> costs = new ArrayList<>();
    private BigDecimal total = BigDecimal.ZERO;
    private BigDecimal labourCostTotal = BigDecimal.ZERO;

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
        Optional<FinanceRowItem> cost = costs.stream()
                .findFirst();

        if(cost.isPresent()) {
            Overhead overhead = (Overhead)cost.get();
            if(overhead.getRate()!=null) {
                total = labourCostTotal.multiply(new BigDecimal(overhead.getRate()).divide(new BigDecimal(100)));
            } else {
                total = BigDecimal.ZERO;
            }
        } else {
            total = BigDecimal.ZERO;
        }
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

    public void setLabourCostTotal(BigDecimal labourCostTotal) {
        this.labourCostTotal = labourCostTotal;
    }
}
