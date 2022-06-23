package org.innovateuk.ifs.finance.resource.category;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.PersonnelCost;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code PersonnelCostCategory} implementation for {@link FinanceRowCostCategory}. Calculating the Personnel costs
 * for an application.
 */
public class PersonnelCostCategory implements FinanceRowCostCategory {
    public static final String WORKING_DAYS_PER_YEAR = "Working days per year";
    public static final String WORKING_DAYS_KEY = "working-days-per-year";
    private List<FinanceRowItem> costs = new ArrayList<>();
    private BigDecimal total = BigDecimal.ZERO;
    private PersonnelCost workingDaysPerYearCostItem;

    @Override
    public List<FinanceRowItem> getCosts() {
        return costs;
    }

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
                    .map(c -> ((PersonnelCost) c).getTotal(workingDaysPerYearCostItem.getLabourDays()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(0, RoundingMode.HALF_UP);
        } else  {
            total = costs.stream()
                    .map(c -> {
                        PersonnelCost personnelCost = (PersonnelCost) c;
                        return ((PersonnelCost) c).isThirdPartyOfgem() ? personnelCost.getTotalWithoutWorkingDays() : BigDecimal.ZERO;
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(0, RoundingMode.HALF_UP);
        }
    }

    public Integer getWorkingDaysPerYear() {
        if (workingDaysPerYearCostItem != null) {
            return workingDaysPerYearCostItem.getLabourDays();
        } else {
            return 0;
        }
    }

    public PersonnelCost getWorkingDaysPerYearCostItem() {
        return workingDaysPerYearCostItem;
    }

    @Override
    public void addCost(FinanceRowItem costItem) {
        if (costItem != null) {
            PersonnelCost personnelCost = (PersonnelCost) costItem;
            if (WORKING_DAYS_PER_YEAR.equals(personnelCost.getDescription())) {
                workingDaysPerYearCostItem = (PersonnelCost) costItem;
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
