package org.innovateuk.ifs.application.finance.view.item;

import org.innovateuk.ifs.application.finance.model.FinanceFormField;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.TravelCost;
import org.innovateuk.ifs.util.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.util.List;

import static org.innovateuk.ifs.util.NullCheckFunctions.allNull;

public class TravelCostHandler extends FinanceRowHandler {

    @Override
    public FinanceRowItem toFinanceRowItem(Long id, List<FinanceFormField> financeFormFields) {
        BigDecimal costPerItem = null;
        String item = null;
        Integer quantity = null;

        for(FinanceFormField financeFormField : financeFormFields) {
            String fieldValue = financeFormField.getValue();
            if(fieldValue != null) {
                switch (financeFormField.getCostName()) {
                    case "item":
                        item = fieldValue;
                        break;
                    case "quantity":
                        quantity = NumberUtils.getIntegerValue(fieldValue, 0);
                        break;
                    case "cost":
                        costPerItem = NumberUtils.getBigDecimalValue(fieldValue, 0d);
                        break;
                    default:
                        LOG.info("Unused costField: " + financeFormField.getCostName());
                        break;
                }
            }
        }
        
        if(allNull(id, costPerItem, item, quantity)) {
        	return null;
        }
        return new TravelCost(id, item, costPerItem, quantity);
    }
}
