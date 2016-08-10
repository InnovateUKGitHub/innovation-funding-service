package com.worth.ifs.application.finance.view.item;

import java.math.BigDecimal;
import java.util.List;

import com.worth.ifs.application.finance.model.FinanceFormField;
import com.worth.ifs.finance.resource.cost.CapitalUsage;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.util.NumberUtils;

import static com.worth.ifs.util.NullCheckFunctions.allNull;

/**
 * Handles the conversion of form fields to a cost item
 */
public class CapitalUsageHandler extends FinanceRowHandler {

    @Override
    public FinanceRowItem toFinanceRowItem(Long id, List<FinanceFormField> financeFormFields) {
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

        if(allNull(id, deprecation, description, existing, npv, residualValue, utilisation)) {
        	return null;
        }
        
        if(id == null || Long.valueOf(0L).equals(id)) {
	        if(npv == null) {
	        	npv = BigDecimal.ZERO;
	        }
	        
	        if(residualValue == null) {
	       	residualValue = BigDecimal.ZERO;
	        }
	        
	        if(utilisation == null) {
	        	utilisation = 0;
	        }
        }
        
        return new CapitalUsage(id, deprecation, description, existing,
                npv, residualValue, utilisation);
    }

}
