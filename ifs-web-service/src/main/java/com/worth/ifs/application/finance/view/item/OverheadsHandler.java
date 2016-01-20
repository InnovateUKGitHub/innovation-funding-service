package com.worth.ifs.application.finance.view.item;

import com.worth.ifs.application.finance.model.CostFormField;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.Overhead;

import java.math.BigDecimal;
import java.util.List;

public class OverheadsHandler extends CostHandler {
    @Override
    public CostItem toCostItem(Long id, List<CostFormField> costFormFields) {
        Integer customRate = null;
        String acceptRate = null;

        for(CostFormField costFormField : costFormFields) {
            switch (costFormField.getCostName()) {
                case "acceptRate":
                    acceptRate = costFormField.getValue();
                    break;
                case "customRate":
                    customRate = Integer.valueOf(costFormField.getValue());
                    break;
                default:
                    log.info("Unused costField: " + costFormField.getCostName());
                    break;
            }
        }
        return new Overhead(id, acceptRate, customRate);
    }
}
