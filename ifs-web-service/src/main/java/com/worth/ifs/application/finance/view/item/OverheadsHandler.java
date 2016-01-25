package com.worth.ifs.application.finance.view.item;

import com.worth.ifs.application.finance.model.CostFormField;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.Overhead;
import com.worth.ifs.finance.resource.cost.OverheadRateType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.util.List;

/**
 * Handles the conversion of form fields to overhead
 */
public class OverheadsHandler extends CostHandler {

    @Override
    public CostItem toCostItem(Long id, List<CostFormField> costFormFields) {
        Integer customRate = null;
        Integer agreedRate = null;
        String rateType = OverheadRateType.NONE.name();

        for(CostFormField costFormField : costFormFields) {
            switch (costFormField.getCostName()) {
                case "rateType":
                    rateType = costFormField.getValue();
                    break;
                case "customRate":
                    customRate = Integer.valueOf(costFormField.getValue());
                case "agreedRate":
                    agreedRate = Integer.valueOf(costFormField.getValue());
                    break;
                default:
                    log.info("Unused costField: " + costFormField.getCostName());
                    break;
            }
        }

        Integer rate = handleRate(rateType, customRate, agreedRate);
        return new Overhead(id, OverheadRateType.valueOf(rateType), rate);
    }

    protected Integer handleRate(String rateType, Integer customRate, Integer agreedRate) {
        OverheadRateType overheadRateType = OverheadRateType.valueOf(rateType);
        switch(overheadRateType) {
            case CUSTOM_RATE:
                return customRate;
            case SPECIAL_AGREED_RATE:
                return agreedRate;
            case DEFAULT_PERCENTAGE:
                return OverheadRateType.DEFAULT_PERCENTAGE.getRate();
            case NONE:
            default:
                return 0;
        }
    }
}
