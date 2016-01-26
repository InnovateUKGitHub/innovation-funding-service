package com.worth.ifs.finance.resource.category;

import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.Overhead;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * {@code DefaultCostCategory} implementation for {@link CostCategory}.
 * Default representation for costs and defaults to summing up the costs.
 */
public class OverheadCostCategory implements CostCategory {
    private final Log log = LogFactory.getLog(getClass());
    List<CostItem> costs = new ArrayList<>();
    BigDecimal total = BigDecimal.ZERO;
    BigDecimal labourCostTotal = BigDecimal.ZERO;

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
        Optional<CostItem> cost = costs.stream()
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

    public void setLabourCostTotal(BigDecimal labourCostTotal) {
        this.labourCostTotal = labourCostTotal;
    }
}
