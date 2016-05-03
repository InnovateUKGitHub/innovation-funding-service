package com.worth.ifs.application.finance.view.item;

import com.worth.ifs.application.finance.model.FinanceFormField;
import com.worth.ifs.finance.resource.category.LabourCostCategory;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.LabourCost;

import java.math.BigDecimal;
import java.util.List;

/**
 * Handles the conversion of form fields to a labour cost
 */
public class LabourCostHandler extends CostHandler {

    @Override
    public CostItem toCostItem(Long id, List<FinanceFormField> financeFormFields) {
        BigDecimal grossAnnualSalary = null;
        String role = null;
        Integer labourDays = null;
        String description = null;
        String key = null;

        for(FinanceFormField financeFormField : financeFormFields) {
            String fieldValue = financeFormField.getValue();
            if(fieldValue != null) {
                switch (financeFormField.getCostName()) {
                    case "grossAnnualSalary":
                        grossAnnualSalary = getBigDecimalValue(fieldValue, null);
                        break;
                    case "role":
                        role = fieldValue;
                        break;
                    case "labourDays":
                        labourDays = getIntegerValue(fieldValue, null);
                        break;
                    case "labourDaysYearly":
                        labourDays = getIntegerValue(fieldValue, null);
                        description = LabourCostCategory.WORKING_DAYS_PER_YEAR;
                        key = LabourCostCategory.WORKING_DAYS_PER_YEAR;
                        break;
                    default:
                        LOG.info("Unused costField: " + financeFormField.getCostName());
                        break;
                }
            }
        }
        return new LabourCost(id, key, role, grossAnnualSalary, labourDays, description);
    }
}
