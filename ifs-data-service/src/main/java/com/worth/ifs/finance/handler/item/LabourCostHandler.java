package com.worth.ifs.finance.handler.item;

import com.worth.ifs.finance.domain.ApplicationFinanceRow;
import com.worth.ifs.finance.resource.category.LabourCostCategory;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.finance.resource.cost.LabourCost;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the labour costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
public class LabourCostHandler extends FinanceRowHandler {
    private static final Log LOG = LogFactory.getLog(LabourCostHandler.class);
    public static final String COST_KEY = "labour";
    public static final Integer DEFAULT_WORKING_DAYS = 232;

    @Override
    public void validate(FinanceRowItem costItem, BindingResult bindingResult) {
        LabourCost labourCost = (LabourCost) costItem;
        if(StringUtils.isNotEmpty(labourCost.getName()) && labourCost.getName().equals(LabourCostCategory.WORKING_DAYS_KEY)){
            super.validate(costItem, bindingResult, LabourCost.YearlyWorkingDays.class);
        }else{
            super.validate(costItem, bindingResult);
        }
    }

    @Override
    public ApplicationFinanceRow toCost(FinanceRowItem costItem) {
        ApplicationFinanceRow cost = null;
        if (costItem instanceof LabourCost) {
            LabourCost labourCostItem = (LabourCost) costItem;
            cost = new ApplicationFinanceRow(labourCostItem.getId(), labourCostItem.getName(), labourCostItem.getRole(), labourCostItem.getDescription(), labourCostItem.getLabourDays(), labourCostItem.getGrossAnnualSalary(), null, null);
        }
        return cost;
    }

    @Override
    public FinanceRowItem toCostItem(ApplicationFinanceRow cost) {
        return new LabourCost(cost.getId(), cost.getName(), cost.getItem(), cost.getCost(), cost.getQuantity(), cost.getDescription());
    }

    @Override
    public List<ApplicationFinanceRow> initializeCost() {
        ArrayList<ApplicationFinanceRow> costs = new ArrayList<>();
        costs.add(initializeWorkingDays());
        return costs;
    }

    private ApplicationFinanceRow initializeWorkingDays() {
        String description = LabourCostCategory.WORKING_DAYS_PER_YEAR;
        Integer labourDays = DEFAULT_WORKING_DAYS;
        LabourCost costItem = new LabourCost(null, LabourCostCategory.WORKING_DAYS_KEY, null, null, labourDays, description);
        return toCost(costItem);
    }
}
