package com.worth.ifs.finance.resource.category;

import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.LabourCost;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code LabourCostCategory} implementation for {@link CostCategory}. Calculating the Labour costs
 * for an application.
 */
public class LabourCostCategory implements CostCategory {
    public static final String WORKING_DAYS_PER_YEAR = "Working days per year";
    private LabourCost workingDaysPerYear;

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
                .map(c -> ((LabourCost)c).getTotal(workingDaysPerYear.getLabourDays()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Integer getWorkingDaysPerYear() {
        if (workingDaysPerYear!=null) {
            return workingDaysPerYear.getLabourDays();
        } else {
            return 0;
        }
    }

    public LabourCost getWorkingDaysPerYearCostItem() {
        return workingDaysPerYear;
    }

    @Override
    public void addCost(CostItem costItem) {
        if(costItem != null) {
            LabourCost labourCost = (LabourCost) costItem;
            if (labourCost.getDescription().equals(WORKING_DAYS_PER_YEAR)) {
                workingDaysPerYear = (LabourCost) costItem;
            } else {
                costs.add(costItem);
            }
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
