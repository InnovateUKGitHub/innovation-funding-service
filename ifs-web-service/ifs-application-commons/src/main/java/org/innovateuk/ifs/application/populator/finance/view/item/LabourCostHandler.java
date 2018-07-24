package org.innovateuk.ifs.application.populator.finance.view.item;

import org.innovateuk.ifs.application.populator.finance.model.FinanceFormField;
import org.innovateuk.ifs.finance.resource.category.LabourCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.LabourCost;
import org.innovateuk.ifs.util.NumberUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.innovateuk.ifs.util.NullCheckFunctions.allNull;

/**
 * Handles the conversion of form fields to a labour cost
 */
public class LabourCostHandler extends FinanceRowHandler {

    @Override
    public FinanceRowItem toFinanceRowItem(Long id, List<FinanceFormField> financeFormFields) {
        BigDecimal grossEmployeeCost = null;
        String role = null;
        Integer labourDays = null;
        String description = null;
        String key = null;

        for (FinanceFormField financeFormField : financeFormFields) {
            String fieldValue = financeFormField.getValue();
            if (fieldValue != null) {
                switch (financeFormField.getCostName()) {
                    case "grossEmployeeCost":
                        grossEmployeeCost = NumberUtils.getBigDecimalValue(fieldValue, 0D);
                        break;
                    case "role":
                        role = fieldValue;
                        break;
                    case "labourDays":
                        labourDays = NumberUtils.getIntegerValue(fieldValue, 0);
                        break;
                    case "labourDaysYearly":
                        labourDays = NumberUtils.getIntegerValue(fieldValue, 0);
                        description = LabourCostCategory.WORKING_DAYS_PER_YEAR;
                        key = LabourCostCategory.WORKING_DAYS_PER_YEAR;
                        break;
                    default:
                        LOG.info("Unused costField: " + financeFormField.getCostName());
                        break;
                }
            }
        }

        if (allNull(id, grossEmployeeCost, role, labourDays, description, key)) {
            return null;
        }
        return new LabourCost(id, key, role, grossEmployeeCost, labourDays, description);
    }
}
