package org.innovateuk.ifs.finance.resource.category;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.HecpIndirectCosts;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code DefaultCostCategory} implementation for {@link FinanceRowCostCategory}.
 * Default representation for costs and defaults to summing up the costs.
 */
public class HecpIndirectCostsCostCategory implements FinanceRowCostCategory {
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
                map(HecpIndirectCosts.class::cast).
                filter(hecpIndirectCosts -> hecpIndirectCosts.getRate()!=null).
                ifPresent(hecpIndirectCosts -> setTotalCost(hecpIndirectCosts));
    }

    private void setTotalCost(HecpIndirectCosts hecpIndirectCosts) {
        if (hecpIndirectCosts.getRateType().getRate() != null) {
            total = labourCostTotal.multiply(new BigDecimal(hecpIndirectCosts.getRate()).divide(new BigDecimal(100))).setScale(0, RoundingMode.HALF_UP);
        } else {
            total = new BigDecimal(hecpIndirectCosts.getRate()).setScale(0, RoundingMode.HALF_UP);
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

    public void setCosts(List<FinanceRowItem> costItems) {
        costs = costItems;
    }

    public void setLabourCostTotal(BigDecimal labourCostTotal) {
        this.labourCostTotal = labourCostTotal;
    }
}
