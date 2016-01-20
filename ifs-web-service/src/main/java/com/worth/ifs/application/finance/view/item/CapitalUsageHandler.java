package com.worth.ifs.application.finance.view.item;

import com.worth.ifs.application.finance.model.CostFormField;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.domain.CostValue;
import com.worth.ifs.finance.resource.cost.CapitalUsage;
import com.worth.ifs.finance.resource.cost.CostItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.util.List;

public class CapitalUsageHandler extends CostHandler {
    private final Log log = LogFactory.getLog(getClass());

    @Override
    public CostItem toCostItem(Long id, List<CostFormField> costFormFields) {
        Integer deprecation = null;
        String description = null;
        String existing = null;
        BigDecimal npv = null;
        BigDecimal residualValue = null;
        Integer utilisation = null;

        for(CostFormField costFormField : costFormFields) {
            final String costFormValue = costFormField.getValue();
            switch (costFormField.getCostName()) {
                case "item":
                    description = costFormValue;
                    break;
                case "existing":
                    existing = costFormValue;
                    break;
                case "deprecation_period":
                    deprecation = Integer.valueOf(costFormValue);
                    break;
                case "npv":
                    npv = getBigDecimalValue(costFormValue, 0d);
                    break;
                case "residual_value":
                    residualValue = getBigDecimalValue(costFormValue, 0d);
                    break;
                case "utilisation":
                    utilisation = Integer.valueOf(costFormValue);
                    break;
                default:
                    log.info("Unused costField: " + costFormField.getCostName());
                    break;
            }
        }

        return new CapitalUsage(id, deprecation, description, existing,
                npv, residualValue, utilisation);
    }

}
