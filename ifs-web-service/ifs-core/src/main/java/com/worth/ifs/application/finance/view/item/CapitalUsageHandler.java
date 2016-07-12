package com.worth.ifs.application.finance.view.item;

import java.math.BigDecimal;
import java.util.List;

import com.worth.ifs.application.finance.model.FinanceFormField;
import com.worth.ifs.finance.resource.cost.CapitalUsage;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.util.NumberUtils;

/**
 * Handles the conversion of form fields to a cost item
 */
public class CapitalUsageHandler extends CostHandler {

    @Override
    public CostItem toCostItem(Long id, List<FinanceFormField> financeFormFields) {
        Integer deprecation = null;
        String description = null;
        String existing = null;
        BigDecimal npv = null;
        BigDecimal residualValue = null;
        Integer utilisation = null;

        for(FinanceFormField financeFormField : financeFormFields) {
            final String costFormValue = financeFormField.getValue();
            switch (financeFormField.getCostName()) {
                case "description":
                    description = costFormValue;
                    break;
                case "existing":
                    existing = costFormValue;
                    break;
                case "deprecation":
                    deprecation = NumberUtils.getIntegerValue(costFormValue, 0);
                    break;
                case "npv":
                    npv = NumberUtils.getBigDecimalValue(costFormValue, 0d);
                    break;
                case "residualValue":
                    residualValue = NumberUtils.getBigDecimalValue(costFormValue, 0d);
                    break;
                case "utilisation":
                    utilisation = NumberUtils.getIntegerValue(costFormValue, 0);
                    break;
                default:
                    LOG.info("Unused costField: " + financeFormField.getCostName());
                    break;
            }
        }

        if(id == null && deprecation == null && description == null && existing == null && npv == null && residualValue == null && utilisation == null) {
        	return null;
        }
        return new CapitalUsage(id, deprecation, description, existing,
                npv, residualValue, utilisation);
    }

}
