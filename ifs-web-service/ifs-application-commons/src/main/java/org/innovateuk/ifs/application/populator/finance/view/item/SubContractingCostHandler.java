package org.innovateuk.ifs.application.populator.finance.view.item;

import org.innovateuk.ifs.application.populator.finance.model.FinanceFormField;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.SubContractingCost;
import org.innovateuk.ifs.util.NumberUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.innovateuk.ifs.util.NullCheckFunctions.allNull;

/**
 * Handles the conversion of form fields to subcontracting costs
 */
public class SubContractingCostHandler extends FinanceRowHandler {

    @Override
    public FinanceRowItem toFinanceRowItem(Long id, List<FinanceFormField> financeFormFields) {
        BigDecimal cost = null;
        String country = null;
        String name = null;
        String role = null;

        for (FinanceFormField financeFormField : financeFormFields) {
            String fieldValue = financeFormField.getValue();
            if (fieldValue != null) {
                switch (financeFormField.getCostName()) {
                    case "country":
                        country = fieldValue;
                        break;
                    case "subcontractingCost":
                        cost = NumberUtils.getBigDecimalValue(fieldValue, 0D);
                        break;
                    case "name":
                        name = fieldValue;
                        break;
                    case "role":
                        role = fieldValue;
                        break;
                    default:
                        LOG.info("Unused costField: " + financeFormField.getCostName());
                        break;
                }
            }
        }

        if (allNull(id, cost, country, name, role)) {
            return null;
        }

        if ((id == null || Long.valueOf(0L).equals(id)) && (cost == null)) {
            cost = BigDecimal.ZERO;
        }

        return new SubContractingCost(id, cost, country, name, role);
    }
}
