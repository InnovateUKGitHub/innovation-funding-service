package com.worth.ifs.application.finance.view.item;

import com.worth.ifs.application.finance.model.FinanceFormField;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.finance.resource.cost.OtherCost;
import com.worth.ifs.util.NumberUtils;

import java.math.BigDecimal;
import java.util.List;

import static com.worth.ifs.util.NullCheckFunctions.allNull;

/**
 * Handles the conversion of form fields to other costs
 */
public class OtherCostsHandler extends FinanceRowHandler {
    @Override
    public FinanceRowItem toFinanceRowItem(Long id, List<FinanceFormField> financeFormFields) {
        String description = null;
        BigDecimal cost = null;

        for (FinanceFormField financeFormField : financeFormFields) {
            String fieldValue = financeFormField.getValue();
            if (fieldValue != null) {
                switch (financeFormField.getCostName()) {
                    case "description":
                        description = fieldValue;
                        break;
                    case "otherCost":
                        cost = NumberUtils.getBigDecimalValue(fieldValue, 0d);
                        break;
                    default:
                        LOG.info("Unused costField: " + financeFormField.getCostName());
                        break;
                }
            }
        }
        
        if(allNull(id, description, cost)) {
        	return null;
        }
        return new OtherCost(id, description, cost);
    }
}
