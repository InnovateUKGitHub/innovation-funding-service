package com.worth.ifs.application.finance.view.item;

import com.worth.ifs.application.finance.model.FinanceFormField;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.Materials;
import com.worth.ifs.util.NumberUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * Handles the conversion of form fields to material items
 */
public class MaterialsHandler extends CostHandler {
    @Override
    public CostItem toCostItem(Long id, List<FinanceFormField> financeFormFields) {
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

        if(id == null && item == null && cost == null && quantity == null) {
        	return null;
        }
        return new Materials(id, item, cost, quantity);
    }
}
