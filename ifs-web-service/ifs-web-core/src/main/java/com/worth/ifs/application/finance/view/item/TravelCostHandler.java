package com.worth.ifs.application.finance.view.item;

import com.worth.ifs.application.finance.model.FinanceFormField;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.finance.resource.cost.TravelCost;
import com.worth.ifs.util.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.util.List;

import static com.worth.ifs.util.NullCheckFunctions.allNull;

public class TravelCostHandler extends FinanceRowHandler {
    private static final Log LOG = LogFactory.getLog(TravelCostHandler.class);

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
