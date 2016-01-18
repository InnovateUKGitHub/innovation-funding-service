package com.worth.ifs.application.finance.view.item;

import com.worth.ifs.application.finance.cost.TravelCost;
import com.worth.ifs.application.finance.model.CostFormField;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.TravelCost;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.util.List;

public class TravelCostHandler extends CostHandler {

    @Override
    public CostItem toCostItem(Long id, List<CostFormField> costFields) {
        BigDecimal costPerItem = null;
        String item = null;
        Integer quantity = null;

        for(CostFormField costFormField : costFormFields) {
            String fieldValue = costFormField.getValue();
            if(fieldValue != null) {
                switch (costFormField.getCostName()) {
                    case "travelPurpose":
                        item = fieldValue;
                        break;
                    case "travelNumTimes":
                        quantity = getIntegerValue(fieldValue, 0);
                        break;
                    case "travelCostEach":
                        costPerItem = getBigDecimalValue(fieldValue, 0d);
                        break;
                    default:
                        log.info("Unused costField: " + costFormField.getCostName());
                        break;
                }
            }
        }
        return new TravelCost(id, costPerItem, item, quantity);
    }
}
