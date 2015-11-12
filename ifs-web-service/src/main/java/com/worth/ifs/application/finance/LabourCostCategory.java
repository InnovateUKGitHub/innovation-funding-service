package com.worth.ifs.application.finance;

import com.worth.ifs.application.finance.cost.CostItem;
import com.worth.ifs.application.finance.cost.LabourCost;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@code LabourCostCategory} implementation for {@link CostCategory}. Calculating the Labour costs
 * for an application.
 */
public class LabourCostCategory implements CostCategory {
    public static final String WORKING_DAYS_PER_YEAR = "Working days per year";
    private LabourCost workingDaysPerYear;
    private final Log log = LogFactory.getLog(getClass());

    List<CostItem> costs = new ArrayList<>();
    BigDecimal total = new BigDecimal(0);

    @Override
    public List<CostItem> getCosts() {
        return costs;
    }

    @Override
    public BigDecimal getTotal() {
        total = costs.stream()
                .map(c -> ((LabourCost)c).getTotal(workingDaysPerYear.getLabourDays()))
                .reduce(new BigDecimal(0), (num, accumulator) -> accumulator.add(num));
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
        LabourCost labourCost = (LabourCost) costItem;
        if(labourCost.getDescription().equals(WORKING_DAYS_PER_YEAR)) {
            workingDaysPerYear = (LabourCost) costItem;
        }
        else if(costItem!=null){
            costs.add(costItem);
        }
    }
}
