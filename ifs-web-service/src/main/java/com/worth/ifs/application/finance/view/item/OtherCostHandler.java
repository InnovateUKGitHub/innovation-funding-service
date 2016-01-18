package com.worth.ifs.application.finance.view.item;

import com.worth.ifs.application.finance.cost.OtherCost;
import com.worth.ifs.application.finance.model.CostFormField;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.OtherCost;

import java.math.BigDecimal;
import java.util.List;

public class OtherCostHandler extends CostHandler {
    @Override
    public CostItem toCostItem(Long id, List<CostFormField> costFormFields) {
        String description = null;
        BigDecimal cost = null;

        for (CostFormField costFormField : costFormFields) {
            String fieldValue = costFormField.getValue();
            if (fieldValue != null) {
                switch (costFormField.getCostName()) {
                    case "description":
                        description = fieldValue;
                        break;
                    case "otherCost":
                        cost = getBigDecimalValue(fieldValue, 0d);
                        break;
                    default:
                        log.info("Unused costField: " + costFormField.getCostName());
                        break;
                }
            }
        }
        return new OtherCost(id, cost, description);
    }
}
