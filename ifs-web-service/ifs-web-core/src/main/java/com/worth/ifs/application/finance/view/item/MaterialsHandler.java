package com.worth.ifs.application.finance.view.item;

import com.worth.ifs.application.finance.model.FinanceFormField;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.finance.resource.cost.Materials;
import com.worth.ifs.util.NumberUtils;

import java.math.BigDecimal;
import java.util.List;

import static com.worth.ifs.util.NullCheckFunctions.allNull;

/**
 * Handles the conversion of form fields to material items
 */
public class MaterialsHandler extends FinanceRowHandler {
    @Override
    public FinanceRowItem toFinanceRowItem(Long id, List<FinanceFormField> financeFormFields) {
        String item = null;
        BigDecimal cost = null;
        Integer quantity = null;

        for(FinanceFormField financeFormField : financeFormFields) {
            String fieldValue = financeFormField.getValue();
            if(fieldValue != null) {
                switch (financeFormField.getCostName()) {
                    case "item":
                        item = fieldValue;
                        break;
                    case "cost":
                        cost = NumberUtils.getBigDecimalValue(fieldValue, 0D);
                        break;
                    case "quantity":
                        quantity = NumberUtils.getIntegerValue(fieldValue, 0);
                        break;
                    default:
                        LOG.info("Unused costField: " + financeFormField.getCostName());
                        break;
                }
            }
        }

        if(allNull(id, item, cost, quantity)) {
        	return null;
        }
        return new Materials(id, item, cost, quantity);
    }
}
