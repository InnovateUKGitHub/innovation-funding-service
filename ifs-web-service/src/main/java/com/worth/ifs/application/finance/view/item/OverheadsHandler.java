package com.worth.ifs.application.finance.view.item;

import com.worth.ifs.application.finance.model.CostFormField;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.Overhead;
import com.worth.ifs.finance.resource.cost.OverheadRateType;

import java.math.BigDecimal;
import java.util.List;

/**
 * Handles the conversion of form fields to overheads
 */
public class OverheadsHandler extends CostHandler {
    @Override
    public CostItem toCostItem(Long id, List<CostFormField> costFormFields) {
        Integer customRate = null;
        BigDecimal agreedRate = null;
        String rateType = OverheadRateType.NONE.name();

        for(CostFormField costFormField : costFormFields) {
            switch (costFormField.getCostName()) {
                case "rateType":
                    rateType = costFormField.getValue();
                    break;
                case "customRate":
                    customRate = Integer.valueOf(costFormField.getValue());
                    break;
                case "agreedRate":
                    agreedRate = new BigDecimal(costFormField.getValue());
                    break;
                default:
                    log.info("Unused costField: " + costFormField.getCostName());
                    break;
            }
        }
        return new Overhead(id, OverheadRateType.valueOf(rateType), customRate, agreedRate);
    }
}
