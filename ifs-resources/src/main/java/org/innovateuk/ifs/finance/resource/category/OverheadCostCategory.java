package org.innovateuk.ifs.finance.resource.category;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.Overhead;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
        costs.stream().findFirst().
                map(Overhead.class::cast).
                filter(overhead -> overhead.getRate()!=null).
                ifPresent(overhead -> setTotalCost(overhead));
    }

    private void setTotalCost(Overhead overhead) {
        if (overhead.getRateType().getRate() != null) {
            total = labourCostTotal.multiply(new BigDecimal(overhead.getRate()).divide(new BigDecimal(100)));
        } else {
            total = new BigDecimal(overhead.getRate());
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
