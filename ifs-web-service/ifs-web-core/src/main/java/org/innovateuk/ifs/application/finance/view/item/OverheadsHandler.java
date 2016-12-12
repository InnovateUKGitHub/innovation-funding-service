package org.innovateuk.ifs.application.finance.view.item;

import org.innovateuk.ifs.application.finance.model.FinanceFormField;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.Overhead;
import org.innovateuk.ifs.finance.resource.cost.OverheadRateType;
import org.innovateuk.ifs.util.NumberUtils;

import java.util.List;

import static org.innovateuk.ifs.util.NullCheckFunctions.allNull;

/**
 * Handles the conversion of form fields to overhead
 */
public class OverheadsHandler extends FinanceRowHandler {

    @Override
    public FinanceRowItem toFinanceRowItem(Long id, List<FinanceFormField> financeFormFields) {
        Integer customRate = null;
        String rateType = null;

        for(FinanceFormField financeFormField : financeFormFields) {
            switch (financeFormField.getCostName()) {
                case "type":
                    rateType = financeFormField.getValue();
                    break;
                case "customRate":
                    customRate = NumberUtils.getIntegerValue(financeFormField.getValue(), 0);
                    break;
                default:
                    LOG.info("Unused costField: " + financeFormField.getCostName());
                    break;
            }
        }

        if(allNull(id == null, customRate, rateType)) {
        	return null;
        }
        
        return createOverHead(id, rateType, customRate);
    }

    protected FinanceRowItem createOverHead(Long id, String rateType, Integer customRate) {
        OverheadRateType overheadRateType = null;
        Integer rate = null;

        if(rateType!=null) {
            overheadRateType = OverheadRateType.valueOf(rateType);
            rate = handleRate(rateType, customRate);
        } else {
            if(customRate!=null) {
                rate = customRate;
            }
        }
        return new Overhead(id, overheadRateType, rate);
    }

    private Integer handleRate(String rateType, Integer customRate) {
        OverheadRateType overheadRateType = OverheadRateType.valueOf(rateType);
        switch(overheadRateType) {
            case CUSTOM_RATE:
                return customRate;
            case DEFAULT_PERCENTAGE:
                return OverheadRateType.DEFAULT_PERCENTAGE.getRate();
            case NONE:
            default:
                return 0;
        }
    }
}
