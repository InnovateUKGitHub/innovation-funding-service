package com.worth.ifs.application.finance.view.item;

import com.worth.ifs.application.finance.model.FinanceFormField;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.Overhead;
import com.worth.ifs.finance.resource.cost.OverheadRateType;

import java.util.List;

/**
 * Handles the conversion of form fields to overhead
 */
public class OverheadsHandler extends CostHandler {

    @Override
    public CostItem toCostItem(Long id, List<FinanceFormField> financeFormFields) {
        Integer customRate = null;
        Integer agreedRate = null;
        String rateType = null;

        for(FinanceFormField financeFormField : financeFormFields) {
            switch (financeFormField.getCostName()) {
                case "rateType":
                    rateType = financeFormField.getValue();
                    break;
                case "customRate":
                    customRate = getIntegerValue(financeFormField.getValue(), 0);
                    agreedRate = getIntegerValue(financeFormField.getValue(), 0);
                    break;
                case "agreedRate":
                    agreedRate = getIntegerValue(financeFormField.getValue(), 0);
                    break;
                default:
                    log.info("Unused costField: " + financeFormField.getCostName());
                    break;
            }
        }

        return createOverHead(id, rateType, customRate, agreedRate);
    }

    protected CostItem createOverHead(Long id, String rateType, Integer customRate, Integer agreedRate) {
        OverheadRateType overheadRateType = null;
        Integer rate = null;

        if(rateType!=null) {
            overheadRateType = OverheadRateType.valueOf(rateType);
            rate = handleRate(rateType, customRate, agreedRate);
        } else {
            if(agreedRate!=null) {
                rate = agreedRate;
            } else if(customRate!=null) {
                rate = customRate;
            }
        }
        return new Overhead(id, overheadRateType, rate);
    }

    private Integer handleRate(String rateType, Integer customRate, Integer agreedRate) {
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
