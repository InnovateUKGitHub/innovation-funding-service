package org.innovateuk.ifs.finance.handler.item;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.category.LabourCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.LabourCost;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the labour costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
public class LabourCostHandler extends FinanceRowHandler<LabourCost> {
    private static final Log LOG = LogFactory.getLog(LabourCostHandler.class);
    public static final String COST_KEY = "labour";
    public static final Integer DEFAULT_WORKING_DAYS = 232;

    @Override
    public void validate(LabourCost labourCost, BindingResult bindingResult) {
        if (StringUtils.isNotEmpty(labourCost.getName()) && (labourCost.getName().equals(LabourCostCategory.WORKING_DAYS_KEY) || labourCost.getName().equals(LabourCostCategory.WORKING_DAYS_PER_YEAR))) {
            super.validate(labourCost, bindingResult, LabourCost.YearlyWorkingDays.class);
        } else {
            super.validate(labourCost, bindingResult);
        }
    }

    @Override
    public ApplicationFinanceRow toCost(LabourCost labourCostItem) {
        return labourCostItem != null ? new ApplicationFinanceRow(labourCostItem.getId(), labourCostItem.getName(), labourCostItem.getRole(), labourCostItem.getDescription(), labourCostItem.getLabourDays(), labourCostItem.getGrossAnnualSalary(), null, null) : null;

    }

    @Override
    public ProjectFinanceRow toProjectCost(LabourCost costItem) {
        return new ProjectFinanceRow(costItem.getId(), costItem.getName(), costItem.getRole(), costItem.getDescription(), costItem.getLabourDays(), costItem.getGrossAnnualSalary(), null, null);
    }

    @Override
    public FinanceRowItem toCostItem(ApplicationFinanceRow cost) {
        return buildRowItem(cost);
    }

    @Override
    public FinanceRowItem toCostItem(ProjectFinanceRow cost) {
        return buildRowItem(cost);
    }

    private FinanceRowItem buildRowItem(FinanceRow cost){
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
