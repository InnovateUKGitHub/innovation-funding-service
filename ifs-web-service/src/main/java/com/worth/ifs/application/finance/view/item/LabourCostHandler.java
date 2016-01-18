package com.worth.ifs.application.finance.view.item;

import com.worth.ifs.application.finance.cost.LabourCost;
import com.worth.ifs.application.finance.model.CostFormField;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.LabourCost;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.util.List;

public class LabourCostHandler extends CostHandler {

    @Override
    public CostItem toCostItem(Long id, List<CostFormField> costFormFields) {
        BigDecimal grossAnnualSalary = null;
        String role = null;
        Integer labourDays = null;
        String description = null;

        for(CostFormField costFormField : costFormFields) {
            String fieldValue = costFormField.getValue();
            if(fieldValue != null) {
                switch (costFormField.getCostName()) {
                    case "grossAnnualSalary":
                        grossAnnualSalary = getBigDecimalValue(fieldValue, 0D);
                        break;
                    case "role":
                        role = fieldValue;
                        break;
                    case "labourDays":
                    case "workingDays":
                        labourDays = getIntegerValue(fieldValue, 0);
                        break;
                    default:
                        log.info("Unused costField: " + costFormField.getCostName());
                        break;
                }
            }
        }
        return new LabourCost(id, role, grossAnnualSalary, labourDays, description);
    }
}
