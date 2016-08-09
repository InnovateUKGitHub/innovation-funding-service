package com.worth.ifs.finance.resource.category;

import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.finance.resource.cost.LabourCost;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code LabourCostCategory} implementation for {@link FinanceRowCostCategory}. Calculating the Labour costs
 * for an application.
 */
public class LabourCostCategory implements FinanceRowCostCategory {
    public static final String WORKING_DAYS_PER_YEAR = "Working days per year";
    public static final String WORKING_DAYS_KEY = "working-days-per-year";
    private List<FinanceRowItem> costs = new ArrayList<>();
    private BigDecimal total = BigDecimal.ZERO;
    private LabourCost workingDaysPerYearCostItem;

    @Override
    public List<FinanceRowItem> getCosts() {
        return costs;
    }

    @Override
    public void setCosts(List<FinanceRowItem> costItems) {
        costs = costItems;
    }

    @Override
    public BigDecimal getTotal() {
        return total;
    }

    @Override
    public void calculateTotal() {
        if (workingDaysPerYearCostItem != null) {
            total = costs.stream()
                    .map(c -> ((LabourCost) c).getTotal(workingDaysPerYearCostItem.getLabourDays()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            total = BigDecimal.ZERO;
        }
    }

    public Integer getWorkingDaysPerYear() {
        if (workingDaysPerYearCostItem != null) {
            return workingDaysPerYearCostItem.getLabourDays();
        } else {
            return 0;
        }
    }

    public LabourCost getWorkingDaysPerYearCostItem() {
        return workingDaysPerYearCostItem;
    }

    @Override
    public void addCost(FinanceRowItem costItem) {
        if (costItem != null) {
            LabourCost labourCost = (LabourCost) costItem;
            if (WORKING_DAYS_PER_YEAR.equals(labourCost.getDescription())) {
                workingDaysPerYearCostItem = (LabourCost) costItem;
            } else {
                costs.add(costItem);
            }
        }
    }

    @Override
    public boolean excludeFromTotalCost() {
        return false;
    }
}
