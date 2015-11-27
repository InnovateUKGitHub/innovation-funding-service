package com.worth.ifs.application.finance;

import com.worth.ifs.application.finance.cost.CostItem;
import com.worth.ifs.application.finance.cost.LabourCost;

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
        total = costs.stream()
                .filter(c -> c.getTotal() != null)
                .map(c -> ((LabourCost)c).getTotal(workingDaysPerYear.getLabourDays()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return total;
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
            } else if (costItem != null) {
                costs.add(costItem);
            }
        }
    }
}
