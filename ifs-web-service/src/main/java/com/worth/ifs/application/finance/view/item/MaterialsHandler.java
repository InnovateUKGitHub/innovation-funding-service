package com.worth.ifs.application.finance.view.item;

import com.worth.ifs.application.finance.model.CostFormField;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.Materials;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.util.List;

public class MaterialsHandler extends CostHandler {
    @Override
    public CostItem toCostItem(Long id, List<CostFormField> costFormFields) {
        String item = null;
        BigDecimal cost = null;
        Integer quantity = null;

        for(CostFormField costFormField : costFormFields) {
            String fieldValue = costFormField.getValue();
            if(fieldValue != null) {
                switch (costFormField.getCostName()) {
                    case "item":
                        item = fieldValue;
                        break;
                    case "cost":
                        cost = getBigDecimalValue(fieldValue, 0D);
                        break;
                    case "quantity":
                        quantity = getIntegerValue(fieldValue, 0);
                        break;
                    default:
                        log.info("Unused costField: " + costFormField.getCostName());
                        break;
                }
            }
        }

        return new Materials(id, item, cost, quantity);
    }
}
