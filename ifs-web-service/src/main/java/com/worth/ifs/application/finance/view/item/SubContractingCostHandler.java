package com.worth.ifs.application.finance.view.item;

import com.worth.ifs.application.finance.model.CostFormField;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.domain.CostValue;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.SubContractingCost;

import java.math.BigDecimal;
import java.util.List;

public class SubContractingCostHandler extends CostHandler {

    @Override
    public CostItem toCostItem(Long id, List<CostFormField> costFormFields) {
        BigDecimal cost = null;
        String country = null;
        String name = null;
        String role = null;

        for(CostFormField costFormField : costFormFields) {
            String fieldValue = costFormField.getValue();
            if (fieldValue != null) {
                switch (costFormField.getCostName()) {
                    case "country":
                        country = fieldValue;
                        break;
                    case "subcontractingCost":
                        cost = getBigDecimalValue(fieldValue, 0D);
                        break;
                    case "name":
                        name = fieldValue;
                        break;
                    case "role":
                        role = fieldValue;
                        break;
                    default:
                        log.info("Unused costField: " + costFormField.getCostName());
                        break;
                }
            }
        }

        return new SubContractingCost(id, cost, country, name, role);
    }
}
