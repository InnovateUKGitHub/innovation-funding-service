package org.innovateuk.ifs.finance.resource.category;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.Overhead;
import org.innovateuk.ifs.finance.resource.cost.OverheadRateType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code DefaultCostCategory} implementation for {@link FinanceRowCostCategory}.
 * Default representation for costs and defaults to summing up the costs.
 */
public class OverheadCostCategory implements FinanceRowCostCategory {
    public static final String ACCEPT_RATE = "Accept Rate";
    public static final Boolean USE_TOTAL_BY_DEFAULT = true;
    public static final String USE_TOTAL_META_FIELD = "use_total";
    public static final String CALCULATION_FILE_FIELD = "file_entry";
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
        if (overhead.getRateType().equals(OverheadRateType.TOTAL)){
            total = new BigDecimal(overhead.getRate());
        }
        else {
            total = labourCostTotal.multiply(new BigDecimal(overhead.getRate()).divide(new BigDecimal(100)));
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
